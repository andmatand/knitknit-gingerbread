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
import android.content.Context;
import android.database.Cursor;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class Counter {
	private static final String TAG = "bunny-knitknit-Counter";
	private long mID;
	private String mName;
	private int mValue;
	private boolean mCountUp;
	private boolean mPatternOn;
	private int mPatternLength;

	private TextView mView;
	private boolean mPressed = false;
	private DatabaseHelper mDatabaseHelper;

	Counter(Context context, Cursor cursor) {
		// Get all the member variables from the database cursor
		mID = cursor.getLong(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_ID));
		mName = cursor.getString(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_NAME));
		mValue = cursor.getInt(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_VALUE));
		mCountUp = cursor.getInt(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_COUNTUP))
			> 0;
		mPatternOn = cursor.getInt(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_PATTERNON))
			> 0;
		mPatternLength = cursor.getInt(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_PATTERNLENGTH));

		// Inflate a new TextView based on the template layout
		LayoutInflater inflater =
			(LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mView =
			(TextView) inflater.inflate(
				R.layout.countingland_counter, null, false);

		// Register the view for a context menu
		((Activity) context).registerForContextMenu(mView);
		mView.setClickable(false);

		// Fill mView with its current contents
		render();

		// Find the parent view
		LinearLayout parentView =
			(LinearLayout) ((Activity) context).findViewById(
				R.id.countingland_counterwrapper);

		// Add this view to the parent view
		parentView.addView(mView);

		// Open database
		mDatabaseHelper = new DatabaseHelper(context);
		mDatabaseHelper.open();
	}

	public long getID() {
		return mID;
	}

	public int getValue() {
		return mValue;
	}

	public int getY() {
		// Create an array of two integers
		int[] xy = new int[2];

		// Fill the array the the x and y coordinates
		mView.getLocationOnScreen(xy);

		// Return the y coordinate
		return xy[1];
	}

	public int getHeight() {
		return mView.getHeight();
	}

	// Adds or subtracts 1, depending on countUp setting
	public void increment() {
		if (mCountUp) {
			mValue++;
			if (mPatternOn) {
				if (mValue > mPatternLength - 1) {
					mValue = 0;
				}
			}
		} else {
			mValue--;
			if (mPatternOn) {
				if (mValue < 0) {
					mValue = mPatternLength - 1;
				}
			}
		}

		render();
	}

	public void longClick() {
		mView.performLongClick();
		mPressed = false;
	}

	// Highlight the counter to show user that a longClick is imminent
	public void highlight() {
		// mView.setPressed(true);
		Log.w(TAG, "mView: " + mView);
		Log.w(TAG, "mView text: " + mView.getText());
		mView.setTextColor(R.color.counter_pressed);
		mPressed = true;
	}

	public boolean getHighlighted() {
		return mPressed;
	}

	public void render() {
		// Update the TextView with the counter's current value
		mView.setText(String.valueOf(this.getValue()));
		mView.setTextColor(R.color.counter);
	}

	public void setSize(int size) {
		mView.setTextSize(size);
	}

	public void saveState() {
		// Save the current value in the database
		mDatabaseHelper.updateCounter(getID(), getValue());
	}
}
