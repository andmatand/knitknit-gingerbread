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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity {
	private static final String TAG = "bunny-knitknit-Settings";
	public static final String PREFS_GLOBAL = "globalSettings";
	public static final String PREF_KEEPSCREENON = "keepScreenOn";
	private SharedPreferences.Editor mPrefEditor;
	private CheckBoxPreference mKeepScreenOn;

	/* Activity Lifecycle ************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);

		// Restore preferences
		SharedPreferences prefs =
			getSharedPreferences(PREFS_GLOBAL, 0);

		mPrefEditor = prefs.edit();

		// Find preferences and set their default values
		mKeepScreenOn = (CheckBoxPreference)
			findPreference("keepScreenOn");
		mKeepScreenOn.setChecked(
			prefs.getBoolean(PREF_KEEPSCREENON, true));
	}

	@Override
	protected void onPause() {
		Log.w(TAG, "in onPause");
		super.onPause();
		saveState();
	}

	private void saveState() {
		mPrefEditor.putBoolean(PREF_KEEPSCREENON,
		                       mKeepScreenOn.isChecked());

		mPrefEditor.apply();
	}
}
