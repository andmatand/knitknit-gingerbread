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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;

public class CountingLand extends Activity {
	private static final String TAG = "bunny-knitknit-CountingLand";
	private Long mProjectID;
	private DatabaseHelper mDatabaseHelper;
	private TextView mCounter1;
	private List<Counter> mCounters;

	private class Counter {
		private TextView textView;

		protected void onCreate() {
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Open database
		mDatabaseHelper = new DatabaseHelper(this);
		mDatabaseHelper.open();

		setContentView(R.layout.countingland);

		// Get projectID from savedInstanceState
		mProjectID = (savedInstanceState == null ?
			null :
			(Long) savedInstanceState.getSerializable(
				DatabaseHelper.PROJECT_KEY_ID));

		// If we still don't have projectID, get it from intent extras
		if (mProjectID == null) {
			Bundle extras = getIntent().getExtras();
			mProjectID = (extras != null ?
				extras.getLong(DatabaseHelper.PROJECT_KEY_ID) :
				null);
		}

		// If projectID is still null, there is a problem and we need
		// to get out of here since we can't do anything
		if (mProjectID == null) {
			finish();
		}

		Log.w(TAG, "in onCreate, mProjectID: " + mProjectID);

		// Fill the onscreen objects with data
		fillData();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.w("bunny-CountingLand", "in onSaveInstanceState");
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(DatabaseHelper.PROJECT_KEY_ID,
			mProjectID);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		Log.w(TAG, "in onResume");
		super.onResume();
		fillData();
	}

	private void saveState() {
	}

	private void fillData() {
		Cursor counterCursor =
			mDatabaseHelper.fetchCounters(mProjectID);
		/*
		do {

		} while (counterCursor.moveToNext());
		*/
		int value = counterCursor.getInt(
			counterCursor.getColumnIndexOrThrow(
				DatabaseHelper.COUNTER_KEY_VALUE));

		counterCursor.close();
		Log.w(TAG, "Got value from cursor");
		/*
		startManagingCursor(counters);
		mTitleText.setText(
			note.getString(
				note.getColumnIndexOrThrow(
				NotesDbAdapter.KEY_TITLE)));
		mBodyText.setText(note.getString(
			note.getColumnIndexOrThrow(
			NotesDbAdapter.KEY_BODY)));
		*/

		//mCounters = new ArrayList<Counter>();

		//for ( )
		mCounter1 = (TextView)
			this.findViewById(R.id.countingland_counter1);

		mCounter1.setText(String.valueOf(value));
		
	}
}
