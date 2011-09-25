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
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class CounterEdit extends Activity {
	private static final String TAG = "bunny-knitknit-CounterEdit";
	private Counter mCounter;
	private DatabaseHelper mDatabaseHelper;

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

		setupUI();
	}

	private void setupUI() {
		// Find the form elements
		final CheckBox patternCheckBox = (CheckBox)
			findViewById(R.id.counteredit_pattern_checkbox);

		patternCheckBox.setOnCheckedChangeListener(
			new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(
					CompoundButton v, boolean checked)
				{
					// Grey out text
					Log.w(TAG, "checkbox changed");

					// Enable/disable pattern mode
					mCounter.setPatternOn(checked);
				}
			});
	}
}
