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

public class DBAdapter {
	private static final String TAG = "knitknitDBAdapter";
	private static final String DATABASE_NAME = "knitknit.db";
	private static final int DATABASE_VERSION = 1;

	private static final String PROJECT_TABLE = "projects";
	public static final String PROJECT_KEY_ID = "_id";
	public static final String PROJECT_KEY_NAME = "name";
	public static final String PROJECT_KEY_DATE_CREATED = "dateCreated";
	public static final String PROJECT_KEY_DATE_ACCESSED = "dateAccessed";

	private static final String COUNTER_TABLE = "counters";
	private static final String NOTE_TABLE = "notes";

	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDB;
	private Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
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
	public DBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public DBAdapter open() throws SQLException {
		// Open the notes database.  If it cannot be opened, try to
		// create a new instance of the database.  If it cannot be
		// created, throw an exception to signal the failure
		mDBHelper = new DatabaseHelper(mCtx);
		mDB = mDBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDBHelper.close();
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
