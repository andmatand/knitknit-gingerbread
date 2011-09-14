/*
 * Copyright 2011 Andrew Anderson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 	-Redistributions of source code must retain the above copyright notice,
 * 	 this list of conditions and the following disclaimer.
 * 
 * 	-Redistributions in binary form must reproduce the above copyright
 * 	 notice, this list of conditions and the following disclaimer in the
 * 	 documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.knitknit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper {
	private static final String TAG = "knitknitDatabaseHelper";
	private static final String DATABASE_NAME = "knitknit.db";
	private static final int DATABASE_VERSION = 1;

	private static final String PROJECT_TABLE = "projects";
	public static final String PROJECT_KEY_ID = "_id";
	public static final String PROJECT_KEY_NAME = "name";
	public static final String PROJECT_KEY_DATE_CREATED = "dateCreated";
	public static final String PROJECT_KEY_DATE_ACCESSED = "dateAccessed";

	private static final String COUNTER_TABLE = "counters";
	private static final String NOTE_TABLE = "notes";

	private DatabaseOpenHelper mOpenHelper;
	private SQLiteDatabase mDB;
	private Context mCtx;

	private static class DatabaseOpenHelper extends SQLiteOpenHelper {
		DatabaseOpenHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create projects table
			db.execSQL(
				"create table " + PROJECT_TABLE +
				"(_id integer primary key autoincrement, " +
				PROJECT_KEY_NAME + " tinytext not null, " +
				PROJECT_KEY_DATE_CREATED + " datetime " +
					"not null," +
				PROJECT_KEY_DATE_ACCESSED + " datetime null);");

			// Create counters table
			db.execSQL(
				"create table " + COUNTER_TABLE +
				"(_id integer primary key autoincrement, " +
				"project_id int not null, " +
				"showNotes bool not null, " +
				"name tinytext not null, " +
				"state int not null, " +
				"countUp bool not null, " +
				"value int not null, " +
				"patternRepeat bool not null, " +
				"patternLength int not null, " +
				"targetEnabled bool not null, " +
				"targetRows int not null);");
			
			// Create notes table
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion)
		{
			/*
			Log.w(TAG, "Upgrading database from
				version " + oldVersion + " to " + newVersion +
				", which will destroy all old data");
			*/
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	// Constructor
	public DatabaseHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public DatabaseHelper open() throws SQLException {
		// Open the notes database.  If it cannot be opened, try to
		// create a new instance of the database.  If it cannot be
		// created, throw an exception to signal the failure
		mOpenHelper = new DatabaseOpenHelper(mCtx);
		mDB = mOpenHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mOpenHelper.close();
	}

	// Projects
	public long insertProject(String name) {
		Log.w(TAG, "cretateProject '" + name + "'");

		// Get current date
		SimpleDateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		//Date date = new Date();

		ContentValues initialValues = new ContentValues();
		initialValues.put(PROJECT_KEY_NAME, name);
		initialValues.put(PROJECT_KEY_DATE_CREATED,
			dateFormat.format(new Date()));

		return mDB.insert(PROJECT_TABLE, null, initialValues);
	}

	public Cursor selectAllProjects() {
		// Returns a Cursor over the list of all notes in the database
		return mDB.query(
			PROJECT_TABLE,
			new String[] {PROJECT_KEY_ID, PROJECT_KEY_NAME},
			null, null, null, null, null);
	}
}
