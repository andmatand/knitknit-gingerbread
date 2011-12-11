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
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class CounterEdit extends Activity {
	private static final String TAG = "bunny-knitknit-CounterEdit";
	private Counter mCounter;
	private Resources mResources;

	// UI Elements
	private EditText mNameText;
	private RadioButton mUpdownUp;
	private RadioButton mUpdownDown;
	private CheckBox mPatternCheckBox;
	private EditText mPatternNumber;
	private TextView mPatternEndText;

	/* Activity Lifecycle ************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.counteredit);

		mCounter = CountingLand.mSelectedCounter;

		Log.w(TAG, "in onCreate, mCounter: " + mCounter);

		// If counter ID is still null, there is a problem and we need
		// to get out of here since we can't do anything
		if (mCounter == null) {
			finish();
		}

		mResources = getResources();

		setupUI();
	}

	@Override
	protected void onPause() {
		Log.w(TAG, "in onPause");
		super.onPause();
		saveState();
	}

	private void saveState() {
		mCounter.setPatternLength(
			Integer.parseInt(mPatternNumber.getText().toString()));

		mCounter.setName(mNameText.getText().toString());
	}

	private void setupUI() {
		// Find counter name box
		mNameText = (EditText)
			findViewById(R.id.counteredit_name_text);

		Log.w(TAG, "mNameText: " + mNameText);

		// Fill name box with counter's name
		mNameText.append(mCounter.getName());


		// Create an OnClickListener for the updown radio buttons
		OnClickListener updown_listener = new OnClickListener() {
		    public void onClick(View v) {
			Log.w(TAG, "clicked radiobutton");

			RadioButton rb = (RadioButton) v;
			if (rb.getId() == R.id.counteredit_updown_up) {
				Log.w(TAG, "clicked UP");
				mCounter.setCountUp(true);
			} else {
				Log.w(TAG, "clicked DOWN");
				mCounter.setCountUp(false);
			}
		    }
		};

		// Find updown RadioButtons
		mUpdownUp = (RadioButton)
			findViewById(R.id.counteredit_updown_up);
		mUpdownDown = (RadioButton)
			findViewById(R.id.counteredit_updown_down);

		// Attach OnClickListeners
		mUpdownUp.setOnClickListener(updown_listener);
		mUpdownDown.setOnClickListener(updown_listener);

		// Select the correct radiobutton based on the current setting
		if (mCounter.getCountUp()) {
			mUpdownUp.setChecked(true);
		} else {
			mUpdownDown.setChecked(true);
		}


		// Find pattern checkbox
		mPatternCheckBox = (CheckBox)
			findViewById(R.id.counteredit_pattern_checkbox);

		// Check/uncheck the checkbox based on the current setting
		mPatternCheckBox.setChecked(mCounter.getPatternOn());

		// Callback function for when the checkbox changes
		mPatternCheckBox.setOnCheckedChangeListener(
			new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(
					CompoundButton v, boolean checked)
				{
					Log.w(TAG, "checkbox changed");
					
					// Make text normal/dim
					refreshPattern();

					// Enable/disable pattern mode
					mCounter.setPatternOn(checked);
				}
			});


		// Find pattern length number
		mPatternNumber = (EditText)
			findViewById(R.id.counteredit_pattern_number);

		// Set the number based on the current setting
		mPatternNumber.append(
			Integer.toString(mCounter.getPatternLength()));

		// Find end of pattern text (" rows")
		mPatternEndText = (TextView)
			findViewById(R.id.counteredit_pattern_endtext);

		refreshPattern();
	}

	private void refreshPattern() {
		// Set color to normal or dim depending on the toggle state
		int color = mResources.getColor(
			mPatternCheckBox.isChecked() ?
			R.color.counteredit_text :
			R.color.counteredit_disabled);

		// Set color of checkbox text
		mPatternCheckBox.setTextColor(color);

		// Enable/disable pattern number EditText
		mPatternNumber.setEnabled(mPatternCheckBox.isChecked());
		
		// Set color of end text
		mPatternEndText.setTextColor(color);
	}
}
