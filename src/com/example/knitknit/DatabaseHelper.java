/*
 * Copyright 2011 Andrew Anderson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 * 
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
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
	private static final int DATABASE_VERSION = 8;

	private static final String PROJECT_TABLE = "projects";
	public static final String PROJECT_KEY_ID = "_id";
	public static final String PROJECT_KEY_NAME = "name";
	public static final String PROJECT_KEY_DATECREATED = "dateCreated";
	public static final String PROJECT_KEY_DATEACCESSED = "dateAccessed";
	public static final String PROJECT_KEY_TOTALROWS = "totalRows";

	private static final String COUNTER_TABLE = "counters";
	public static final String COUNTER_KEY_ID = "_id";
	public static final String COUNTER_KEY_PROJECTID = "projects_id";
	public static final String COUNTER_KEY_NAME = "name";
	public static final String COUNTER_KEY_VALUE = "value";
	public static final String COUNTER_KEY_COUNTUP = "countUp";
	public static final String COUNTER_KEY_PATTERNON = "patternOn";
	public static final String COUNTER_KEY_PATTERNLENGTH = "patternLength";

	private static final String SETTINGS_TABLE = "settings";
	public static final String SETTINGS_KEY_ID = "_id";
	public static final String SETTINGS_KEY_KEEPSCREEENON = "keepScreenOn";
	public static final String SETTINGS_KEY_SHOWCOUNTERNAMES =
		"showCounterNames";

	private static final String NOTE_TABLE = "notes";

	private DatabaseOpenHelper mOpenHelper;
	private SQLiteDatabase mDB;
	private Context mContext;

	private static class DatabaseOpenHelper extends SQLiteOpenHelper {
		DatabaseOpenHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create projects table
			db.execSQL(
				"create table " + PROJECT_TABLE +
				"(" + PROJECT_KEY_ID + " integer " +
					"primary key autoincrement, " +
				PROJECT_KEY_NAME + " tinytext not null, " +
				PROJECT_KEY_DATECREATED + " datetime " +
					"not null," +
				PROJECT_KEY_DATEACCESSED + " datetime null, " +
				PROJECT_KEY_TOTALROWS + ");");

			// Create counters table
			db.execSQL(
				"create table " + COUNTER_TABLE +
				"(" + COUNTER_KEY_ID + " integer " +
					"primary key autoincrement, " +
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

			// Create settings table
			db.execSQL(
				"create table " + SETTINGS_TABLE +
				"(" + SETTINGS_KEY_ID + " integer " +
					"primary key, " +
				SETTINGS_KEY_KEEPSCREEENON + " bool not null " +
					"default 0, " +
				SETTINGS_KEY_SHOWCOUNTERNAMES + " bool " +
					"not null default 0);");

			// Insert the sole row into the settings table
			ContentValues vals = new ContentValues();
			vals.put(SETTINGS_KEY_ID, 0);
			db.insert(SETTINGS_TABLE, null, vals);
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
		this.mContext = ctx;
	}

	public DatabaseHelper open() throws SQLException {
		// Open the notes database.  If it cannot be opened, try to
		// create a new instance of the database.  If it cannot be
		// created, throw an exception to signal the failure
		mOpenHelper = new DatabaseOpenHelper(mContext);
		mDB = mOpenHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mOpenHelper.close();
	}

	/* Project ***********************************************************/
	public boolean accessProject(long projectID) {
		// Get current date
		SimpleDateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

		// Update project's dateAccessed
		ContentValues values = new ContentValues();
		values.put(PROJECT_KEY_DATEACCESSED,
			dateFormat.format(new Date()));

		return mDB.update(
			PROJECT_TABLE,
			values,
			PROJECT_KEY_ID + "=" + projectID,
			null) > 0;
	}

	public boolean deleteProject(long projectID) {
		// Get a cursor over the list of counters in this project
		Cursor cursor = fetchAllCounters(projectID);

		// Delete each counter
		do {
			if (!deleteCounter(cursor.getLong(
				cursor.getColumnIndexOrThrow(COUNTER_KEY_ID)))) 
			{
				return false;
			}

		} while (cursor.moveToNext());
		cursor.close();

		// Delete the project
		return mDB.delete(
			PROJECT_TABLE,
			PROJECT_KEY_ID + "=" + projectID,
			null) > 0;
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

	public int getProjectTotalRows(long projectID) {
		Cursor cursor = mDB.query(
			true,
			PROJECT_TABLE,
			new String[] {PROJECT_KEY_TOTALROWS},
			PROJECT_KEY_ID + "=" + projectID,
			null, null, null, null, null);

		cursor.moveToFirst();

		return cursor.getInt(
			cursor.getColumnIndexOrThrow(PROJECT_KEY_TOTALROWS));
	}

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

	public boolean renameProject(long projectID, String name) {
		ContentValues values = new ContentValues();
		values.put(PROJECT_KEY_NAME, name);

		return mDB.update(
			PROJECT_TABLE,
			values,
			PROJECT_KEY_ID + "=" + projectID,
			null) > 0;
	}

	public boolean updateProject(long projectID, int totalRows) {
		ContentValues values = new ContentValues();
		values.put(PROJECT_KEY_TOTALROWS, totalRows);

		// Update project's current totalRows
		return mDB.update(
			PROJECT_TABLE,
			values,
			PROJECT_KEY_ID + "=" + projectID,
			null) > 0;
	}

	/* Counter ***********************************************************/
	public boolean deleteCounter(long counterID) {
		return mDB.delete(
			COUNTER_TABLE,
			COUNTER_KEY_ID + "=" + counterID,
			null) > 0;
	}

	public Cursor fetchAllCounters(long projectID) throws SQLException {
		Log.w(TAG, "in fetchAllCounters");

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

		if (cursor.getCount() == 0) {
			Log.w(TAG, "counterCursor was empty");
		}

		return cursor;
	}

	public Cursor fetchCounter(long projectID, long counterID)
		throws SQLException
	{
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
			COUNTER_KEY_PROJECTID + "=" + projectID + " and " +
				COUNTER_KEY_ID + "=" + counterID,
			null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		return cursor;
	}

	public long insertCounter(long projectID) {
		ContentValues counterValues = new ContentValues();
		counterValues.put(COUNTER_KEY_PROJECTID, projectID);

		return mDB.insert(COUNTER_TABLE, null, counterValues);
	}

	public boolean updateCounter(long counterID, String name, int value,
	                             boolean countUp, boolean patternOn,
	                             int patternLength)
	{
		ContentValues vals = new ContentValues();
		vals.put(COUNTER_KEY_NAME, name);
		vals.put(COUNTER_KEY_VALUE, value);
		vals.put(COUNTER_KEY_COUNTUP, countUp);
		vals.put(COUNTER_KEY_PATTERNON, patternOn);
		vals.put(COUNTER_KEY_PATTERNLENGTH, patternLength);

		return mDB.update(
			COUNTER_TABLE,
			vals,
			COUNTER_KEY_ID + "=" + counterID,
			null) > 0;
	}

	/* Settings ***********************************************************/
	public boolean updateSettings(boolean keepScreenOn,
	                             boolean showCounterNames)
	{
		ContentValues vals = new ContentValues();
		vals.put(SETTINGS_KEY_KEEPSCREEENON, keepScreenOn);
		vals.put(SETTINGS_KEY_SHOWCOUNTERNAMES, showCounterNames);

		return mDB.update(SETTINGS_TABLE, vals,
		                  SETTINGS_KEY_ID + "=0", null) > 0;
	}
}
