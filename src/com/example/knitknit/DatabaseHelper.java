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
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper {
	private static final String TAG = "bunny-knitknit-DatabaseHelper";
	private static final String DATABASE_NAME = "knitknit.db";
	private static final int DATABASE_VERSION = 6;

	private static final String PROJECT_TABLE = "projects";
	public static final String PROJECT_KEY_ID = "_id";
	public static final String PROJECT_KEY_NAME = "name";
	public static final String PROJECT_KEY_DATECREATED = "dateCreated";
	public static final String PROJECT_KEY_DATEACCESSED = "dateAccessed";

	private static final String COUNTER_TABLE = "counters";
	public static final String COUNTER_KEY_ID = "_id";
	public static final String COUNTER_KEY_PROJECTID = "projects_id";
	public static final String COUNTER_KEY_NAME = "name";
	public static final String COUNTER_KEY_VALUE = "value";
	public static final String COUNTER_KEY_COUNTUP = "countUp";
	public static final String COUNTER_KEY_PATTERNON = "patternOn";
	public static final String COUNTER_KEY_PATTERNLENGTH = "patternLength";

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
				PROJECT_KEY_DATECREATED + " datetime " +
					"not null," +
				PROJECT_KEY_DATEACCESSED + " datetime null);");

			// Create counters table
			db.execSQL(
				"create table " + COUNTER_TABLE +
				"(_id integer primary key autoincrement, " +
				COUNTER_KEY_PROJECTID + " integer not null, " +
				"showNotes bool not null default 0, " +
				COUNTER_KEY_NAME + " tinytext null, " +
				"state integer not null default 0, " +
				COUNTER_KEY_COUNTUP + " bool not null " +
					"default 1, " +
				COUNTER_KEY_VALUE + " integer not null " +
					"default 0, " +
				COUNTER_KEY_PATTERNON + " bool not null " +
					"default 0, " +
				COUNTER_KEY_PATTERNLENGTH + " integer " +
					"not null default 10, " +
				"targetEnabled bool not null default 0, " +
				"targetRows integer null);");
			
			// Create notes table
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion)
		{
			Log.w(TAG, "Upgrading database from " +
				"version " + oldVersion + " to " + newVersion +
				", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + COUNTER_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);

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
		// Get current date
		SimpleDateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

		ContentValues projectValues = new ContentValues();
		projectValues.put(PROJECT_KEY_NAME, name);
		projectValues.put(PROJECT_KEY_DATECREATED,
			dateFormat.format(new Date()));

		long projectID = mDB.insert(PROJECT_TABLE, null, projectValues);

		// Exit if there was a database error
		if (projectID == -1) {
			return -1;
		} else {
			// Insert 1 counter to start
			Log.w(TAG, "insertCounter returned:" +
				insertCounter(projectID));
			//insertCounter(projectID);

			return projectID;
		}
	}

	public long insertCounter(long projectID) {
		ContentValues counterValues = new ContentValues();
		counterValues.put(COUNTER_KEY_PROJECTID, projectID);

		return mDB.insert(COUNTER_TABLE, null, counterValues);
	}

	public Cursor fetchProjects() {
		// Returns a Cursor over the list of all projects in the
		// database
		return mDB.query(
			PROJECT_TABLE,
			new String[] {PROJECT_KEY_ID, PROJECT_KEY_NAME},
			null, null, null, null, null);
	}

	public String getProjectName(long projectID) {
		Cursor cursor = mDB.query(
			true,
			PROJECT_TABLE,
			new String[] {PROJECT_KEY_NAME},
			PROJECT_KEY_ID + "=" + projectID,
			null, null, null, null, null);

		cursor.moveToFirst();

		return cursor.getString(
			cursor.getColumnIndexOrThrow(PROJECT_KEY_NAME));
	}

	public boolean deleteProject(long projectID) {
		return mDB.delete(
			PROJECT_TABLE,
			PROJECT_KEY_ID + "=" + projectID,
			null) > 0;
	}

	public boolean renameProject(Long projectID, String name) {
		ContentValues args = new ContentValues();
		args.put(PROJECT_KEY_NAME, name);

		return mDB.update(
			PROJECT_TABLE,
			args,
			PROJECT_KEY_ID + "=" + projectID,
			null) > 0;
	}

	public Cursor fetchCounters(long projectID) throws SQLException {
		Log.w(TAG, "in fetchCounters");

		Cursor cursor = mDB.query(
			true,
			COUNTER_TABLE,
			new String[] {
				COUNTER_KEY_ID,
				COUNTER_KEY_NAME,
				COUNTER_KEY_VALUE,
				COUNTER_KEY_COUNTUP,
				COUNTER_KEY_PATTERNON,
				COUNTER_KEY_PATTERNLENGTH},
			COUNTER_KEY_PROJECTID + "=" + projectID,
			null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		// TEMP hack to add a second counter
		/*
		if (cursor.getCount() < 2) {
			insertCounter(projectID);
		}
		*/

		if (cursor.getCount() == 0) {
			Log.w(TAG, "counterCursor was empty");
		}

		return cursor;
	}

	public boolean updateCounter(long counterID, int value) {
		ContentValues args = new ContentValues();
		args.put(COUNTER_KEY_VALUE, value);

		return mDB.update(
			COUNTER_TABLE,
			args,
			COUNTER_KEY_ID + "=" + counterID,
			null) > 0;
	}

	public boolean deleteCounter(long counterID) {
		return mDB.delete(
			COUNTER_TABLE,
			COUNTER_KEY_ID + "=" + counterID,
			null) > 0;
	}
}
