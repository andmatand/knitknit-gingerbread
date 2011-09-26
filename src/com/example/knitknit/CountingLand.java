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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CountingLand extends Activity {
	private static final String TAG = "bunny-knitknit-CountingLand";
	private static final int ACTIVITY_EDIT = 1;

	private Long mProjectID;
	private DatabaseHelper mDatabaseHelper;
	private ArrayList<Counter> mCounters;
	private CountingLandWrapper mWrapper;
	private LinearLayout mCounterWrapper;

	// The counter whose context menu is open
	public static Counter mSelectedCounter;

	// Options menu
	private static final int MENU_ADD = Menu.FIRST + 10;
	private static final int MENU_DECREASE = Menu.FIRST + 11;
	private static final int MENU_RESET = Menu.FIRST + 12;

	// Context menu
	private static final int MENU_EDIT = Menu.FIRST;
	private static final int MENU_COUNTER_INCREASE = Menu.FIRST + 1;
	private static final int MENU_COUNTER_DECREASE = Menu.FIRST + 2;
	private static final int MENU_DELETE = Menu.FIRST + 3;

	/* Activity Lifecycle ************************************************/
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

		Log.w(TAG, "in onCreate, mProjectID: " + mProjectID);

		// If projectID is still null, there is a problem and we need
		// to get out of here since we can't do anything
		if (mProjectID == null) {
			finish();
		}

		// Find the wrapper view (the whole screen)
		mWrapper =
			(CountingLandWrapper)
			findViewById(R.id.countingland_wrapper);

		Log.w(TAG, "mWrapper: " + mWrapper);

		// Find the counter wrapper view
		mCounterWrapper =
			(LinearLayout) findViewById(
				R.id.countingland_counterwrapper);

		loadCounters();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.w(TAG, "in onSaveInstanceState");
		super.onSaveInstanceState(outState);

		// Save the project ID to the bundle
		outState.putSerializable(DatabaseHelper.PROJECT_KEY_ID,
			mProjectID);
	}

	@Override
	protected void onPause() {
		Log.w(TAG, "in onPause");
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		Log.w(TAG, "in onResume");
		super.onResume();
		fillData();
	}

	@Override
	protected void onRestart() {
		Log.w(TAG, "in onRestart");
		super.onRestart();
	}

	private void saveState() {
		saveCounters();
	}

	/* Single Counter functions ******************************************/
	private void addCounter() {
		// Save all current counters
		saveCounters();

		mDatabaseHelper.insertCounter(mProjectID);

		// Reload all counters from database
		loadCounters();

		// Redraw all counters
		refreshCounters();
	}

	public void deleteCounter(Counter c) {
		// Delete counter from database
		c.delete();

		// Delete counter object
		mCounters.remove(c);

		/*
		// Redraw all counters
		sizeCounters();
		refreshCounters();
		*/
	}

	private Counter findCounterByYPosition(float y) {
		// Loop through each counter
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();

			// Get the counter's y position
			int couterY = c.getY();

			// If y overlaps with this counter
			if (y >= (float) c.getY() &&
				y <= (float) c.getY() + (c.getHeight() - 1))
			{
				Log.w(TAG, "Counter value: " + c.getValue());
				Log.w(TAG, "Counter y1: " + c.getY());
				Log.w(TAG, "Counter y2: " +
					(c.getY() + c.getHeight()));
				Log.w(TAG, "Touch y: " + y);
				return c;
			}
		}

		return null;
	}

	public void highlightCounter(float y) {
		Counter c = findCounterByYPosition(y);
		
		if (c != null) c.highlight();
	}

	public void longClickCounter(float y) {
		Counter c = findCounterByYPosition(y);
		
		if (c != null) {
			mSelectedCounter = c;
			c.longClick();
		}
	}


	/* Aggregate Counter functions ***************************************/
	private void loadCounters() {
		// Remove any previous counter views
		mCounterWrapper.removeAllViews();

		// Create an ArrayList of counter objects
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

	private void saveCounters() {
		// Save all the counters to the database
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.saveState();
		}
	}

	private void fillData() {
		// Set activity title to project name
		getWindow().setTitle(
			mDatabaseHelper.getProjectName(mProjectID));

		refreshCounters();
	}

	// Adds (or subtracts, depending on counter setting) to all counters
	public void increment() {
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.increment();
		}
	}

	// Adds (or subtracts, depending on counter setting) to all counters
	public void decrement() {
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.decrement();
		}
	}

	private void sizeCounters() {
		// Set the text size of the counters based on the available
		// height divided by the number of counters
		int counterSize =
			(int) ((mWrapper.getHeight() / mCounters.size()) * .5);
		Log.w(TAG, "counterSize: " + counterSize);
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.setSize(counterSize);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			sizeCounters();
			refreshCounters();
		}
	}

	/* Options Menu ******************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ADD, 0, R.string.countingland_add);
		menu.add(0, MENU_DECREASE, 0, R.string.countingland_decrease);
		menu.add(0, MENU_RESET, 0, R.string.countingland_reset);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureID, MenuItem item) {
		switch(item.getItemId()) {
		case MENU_ADD:
			addCounter();
			return true;
		case MENU_DECREASE:
			decrement();
			return true;
		case MENU_RESET:
			resetCounters();
			return true;
		}

		return super.onMenuItemSelected(featureID, item);
	}

	public static boolean getZeroMode() {
		return false;
	}

	/* Context Menu ******************************************************/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, MENU_EDIT, 0, R.string.counter_edit);
		menu.add(0, MENU_COUNTER_INCREASE, 0,
			R.string.counter_increase);
		menu.add(0, MENU_COUNTER_DECREASE, 0,
			R.string.counter_decrease);

		// Only show delete button if there is more than 1 counter
		if (mCounters.size() > 1) {
			menu.add(0, MENU_DELETE, 0, R.string.counter_delete);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Get info for the selected list item
		AdapterContextMenuInfo info =
			(AdapterContextMenuInfo) item.getMenuInfo();

		switch(item.getItemId()) {
		case MENU_EDIT:
			// Create a new intent to start the CounterEdit
			// activity, attaching the selected counter object
			Intent i = new Intent(this, CounterEdit.class);
			/*
			i.putExtra(Counter.COUNTER_KEY_ID,
				mSelectedCounter.getID());
				*/

			Log.w(TAG, "counter stored: " +
				mSelectedCounter.getID());

			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		case MENU_COUNTER_INCREASE:
			mSelectedCounter.increment();
			return true;
		case MENU_COUNTER_DECREASE:
			mSelectedCounter.decrement();
			return true;
		case MENU_DELETE:
			deleteCounter(mSelectedCounter);
			return true;
		}

		return super.onContextItemSelected(item);
	}

	public void refreshCounters() {
		// Put counterwrapper in front of the background image
		//mCounterWrapper.bringToFront();

		// Deselect the currently selected counter
		mSelectedCounter = null;

		// Set text of counter views, and reset to default color
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.refresh();
		}
	}

	private void resetCounters() {
		// Reset all counters to 0
		for (Iterator it = mCounters.iterator(); it.hasNext(); ) {
			Counter c = (Counter) it.next();
			c.setValue(0);
		}
	}
}
