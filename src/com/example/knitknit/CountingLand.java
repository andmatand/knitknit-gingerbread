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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CountingLand extends Activity {
	private static final String TAG = "bunny-knitknit-CountingLand";
	private Long mProjectID;
	private DatabaseHelper mDatabaseHelper;
	private ArrayList<Counter> mCounters;

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

		// Add an onCLickListener to the whole screen
		LinearLayout wrapper = (LinearLayout)
			findViewById(R.id.countingland_wrapper);
		wrapper.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w(TAG, "tapped");
				increment();
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.w(TAG, "in onSaveInstanceState");
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
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.saveState();
		}
	}

	private void fillData() {
		// Set activity title to project name
		getWindow().setTitle(
			mDatabaseHelper.getProjectName(mProjectID));


		// Create an array of counter objects
		mCounters = new ArrayList<Counter>();

		// Get a cursor over the list of counters in this project
		Cursor counterCursor =
			mDatabaseHelper.fetchCounters(mProjectID);

		// Loop over each row with the cursor
		do {
			// Add a new counter object to the counter ArrayList
			mCounters.add(new Counter(this, counterCursor));
		} while (counterCursor.moveToNext());
		counterCursor.close();

	}

	// Adds (or subtracts, depending on counter setting) to all counters
	private void increment() {
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.increment();
		}
	}

	private void sizeCounters() {
		// Set the text size of the counters based on the available
		// height divided by the number of counters
		View wrapper = (View)
			findViewById(R.id.countingland_counterwrapper);
		int counterSize =
			(int) ((wrapper.getHeight() / mCounters.size()) * .5);
		Log.w(TAG, "counterSize: " + counterSize);
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.setSize(counterSize);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) sizeCounters();
	}
}
