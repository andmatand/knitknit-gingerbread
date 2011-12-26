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
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import android.view.Gravity;
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
	private int mNumRepeats;
	private boolean mShowRepeats;

	private LinearLayout mParentView;
	private LinearLayout mView;
	private TextView mValueView;
	private TextView mRepeatsView;
	private Resources mResources;
	private boolean mPressed = false;
	private boolean mSingleMode; // true if this is the only counter
	private DatabaseHelper mDatabaseHelper;

	Counter(Context context, Cursor cursor) {
		// Get all the member variables from the database cursor
		mID = cursor.getLong(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_ID));
		mName = cursor.getString(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_NAME));
		if (mName == null) mName = "";
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
		mNumRepeats = cursor.getInt(cursor.getColumnIndexOrThrow(
			DatabaseHelper.COUNTER_KEY_NUMREPEATS));

		// Inflate a new RelativeLayout based on the template layout
		LayoutInflater inflater =
			(LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mView =
			(LinearLayout) inflater.inflate(
				R.layout.countingland_counter, null, false);
		mValueView = (TextView) mView.getChildAt(0);
		mRepeatsView = (TextView) mView.getChildAt(1);

		// Register the view for a context menu
		((Activity) context).registerForContextMenu(mView);
		mView.setClickable(false);

		// Get the resources (for setting text colors)
		mResources = context.getResources();
		Log.w(TAG, "mResources: " + mResources);

		// Fill mView with its current contents
		refresh();

		// Find the parent view
		mParentView = (LinearLayout)
			((Activity) context).findViewById(
				R.id.countingland_counterwrapper);

		// Add the view to the parent
		//addView();

		// Open database
		mDatabaseHelper = new DatabaseHelper(context);
		mDatabaseHelper.open();
	}


	/* Simple Accessor methods *******************************************/
	public boolean getCountUp() {
		return mCountUp;
	}

	public int getDisplayValue() {
		return (CountingLand.getZeroMode() ? mValue : mValue + 1);
	}

	public int getHeight() {
		return mView.getHeight();
	}

	public boolean getHighlighted() {
		return mPressed;
	}

	public long getID() {
		return mID;
	}

	public String getName() {
		return mName;
	}

	public int getNumRepeats() {
		return mNumRepeats;
	}

	public int getPatternLength() {
		return mPatternLength;
	}

	public boolean getPatternOn() {
		return mPatternOn;
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

	public void setCountUp(boolean upDown) {
		mCountUp = upDown;
	}

	public void setName(String name) {
		mName = name;
	}

	public void setPatternLength(int length) {
		mPatternLength = length;
	}

	public void setPatternOn(boolean onOff) {
		mPatternOn = onOff;
	}

	public void setSingleMode(boolean onOff) {
		mSingleMode = onOff;
		if (onOff) mShowRepeats = false;
	}

	public void setSize(int size) {
		mValueView.setTextSize(size);
	}

	public void setValue(int value) {
		mValue = value;
	}

	/* Other Methods *****************************************************/
	public void addView() {
		// Add this view to the parent view
		mParentView.addView(mView);
	}

	public void increment() {
		// Add or subtract 1, depending on countUp setting
		if (mCountUp) {
			mValue++;
			if (mPatternOn) {
				if (mValue > mPatternLength - 1) {
					mNumRepeats++;
					mValue = 0;
				}
			}
		} else {
			mValue--;
			if (mPatternOn) {
				if (mValue < 0) {
					mNumRepeats++;
					mValue = mPatternLength - 1;
				}
			}
		}

		refresh();
	}

	public void decrement() {
		// Subtract or add 1, depending on countUp setting
		if (mCountUp) {
			mValue--;
			if (mPatternOn) {
				if (mValue < 0) {
					mNumRepeats--;
					mValue = mPatternLength - 1;
				}
			}
		} else {
			mValue++;
			if (mPatternOn) {
				if (mValue > mPatternLength - 1) {
					mNumRepeats--;
					mValue = 0;
				}
			}
		}

		refresh();
	}

	public void longClick() {
		// Use the post method so it runs in the main UI thread intead
		// of the timer thread
		mView.post(new Runnable() {
			public void run() {
				mView.performLongClick();
			}
		});

		mPressed = false;
	}

	public void highlight() {
		// Highlight the counter to show user that a longClick is
		// imminent

		// Use the post method so it runs in the main UI thread intead
		// of the timer thread
		mView.post(new Runnable() {
			public void run() {
				// Set the color
				mValueView.setTextColor(
				mResources.getColor(R.color.counter_pressed));
			}
		});

		mPressed = true;
	}

	public void refresh() {
		// Use the post method so it runs in the main UI thread intead
		// of the timer thread
		mView.post(new Runnable() {
			public void run() {
				// Update the TextView with the counter's
				// current value
				mValueView.setText(String.valueOf(
					getDisplayValue()));

				// Set the color
				mValueView.setTextColor(
					mResources.getColor(R.color.counter));
				
				// If this is the only counter
				if (mSingleMode) {
					// Center it
					mValueView.setGravity(Gravity.CENTER);
				} else {
					// Otherwise right align
					mValueView.setGravity(Gravity.RIGHT);
				}

				if (mShowRepeats) {
					Log.w(TAG, "set repeats to VISIBLE");
					// Show numRepeats
					mRepeatsView.setVisibility(
						View.VISIBLE);

					// Update the TextView with the
					// counter's current value
					mRepeatsView.setText(
						String.format("%1$-3s",
						String.valueOf(mNumRepeats)));

				} else {
					Log.w(TAG, "set repeats to GONE");
					// Hide numRepeats
					mRepeatsView.setVisibility(View.GONE);
				}
			}
		});
	}

	public void removeView() {
		//((LinearLayout) mView.getParent()).removeView(mView);
		mParentView.removeView(mView);
	}

	public void reset() {
		if (mCountUp) {
			mValue = 0;
		} else {
			mValue = mPatternLength - 1;
		}
		mNumRepeats = 0;
	}

	public void saveState() {
		// Save the current value in the database
		mDatabaseHelper.updateCounter(
			mID,
			mName,
			mValue,
			mCountUp,
			mPatternOn,
			mPatternLength,
			mNumRepeats);
	}

	public void setShowRepeats(boolean visible) {
		mShowRepeats = visible;
	}

	public void delete() {
		// Delete the counter from the database
		mDatabaseHelper.deleteCounter(mID);

		// Delete the view
		removeView();
	}
}
