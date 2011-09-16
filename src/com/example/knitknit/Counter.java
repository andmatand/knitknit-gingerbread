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

import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;
import android.view.View;

public class Counter {
	private long mID;
	private String mName;
	private int mValue;
	private boolean mCountUp;

	private TextView mTextView;
	private DatabaseHelper mDatabaseHelper;

	Counter(Context context, View view, Cursor cursor) {
		// Get all the member variables from the cursor
		mID = cursor.getLong(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_ID));
		mName = cursor.getString(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_NAME));
		mValue = cursor.getInt(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_VALUE));
		mCountUp = cursor.getInt(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_COUNTUP))
			> 0;

		// Get a handle on the only number textView for now
		mTextView = (TextView)
			view.findViewById(R.id.countingland_counter1);

		// Open database
		mDatabaseHelper = new DatabaseHelper(context);
		mDatabaseHelper.open();
	}

	long getID() {
		return mID;
	}

	int getValue() {
		return mValue;
	}

	// Adds or subtracts 1, depending on countUp setting
	public void increment() {
		if (mCountUp) {
			mValue++;
		} else {
			mValue--;
		}

		render();
	}

	// Update the TextView with the counter's current value
	public void render() {
		mTextView.setText(String.valueOf(this.getValue()));
	}

	public void saveState() {
		// Save the current value in the database
		mDatabaseHelper.updateCounter(getID(), getValue());
	}
}
