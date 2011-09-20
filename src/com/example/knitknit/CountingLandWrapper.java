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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import java.util.Timer;
import java.util.TimerTask;

public class CountingLandWrapper extends LinearLayout {
	private static final String TAG = "bunny-knitknit-CountingLandWrapper";
	private Context mContext;

	protected MotionEvent mTouchDown = null;
	protected MotionEvent mTouchUp = null;
	private Timer mTouchTimer;
	private boolean mHighlightedCounter = false;

	// 0 = not clicking, 1 = holding
	private int clickStep = 0;

	public CountingLandWrapper(Context context) {
		super(context);
		mContext = context;
		this.setClickable(true);
	}

	public CountingLandWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.setClickable(true);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.w(TAG, "intercepted down event");
				mTouchDown = MotionEvent.obtain(event);
				mHighlightedCounter = false;

				// Set a timer callback to check if the
				// touch is still being held down
				mTouchTimer = new Timer();
				mTouchTimer.scheduleAtFixedRate(
					new TimerTask() {
						public void run() {
							checkTouch();
						}
					}, 200, 250);

				// Intercept the event
				return true;
		}

		// Otherwise let the event pass through to the children
		Log.w(TAG, "gave event to children");
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Exit if we haven't had a touch down event from
		// onInterceptTouchEvent
		if (mTouchDown == null) return true;

		Log.w(TAG, "in onTouchEvent");

		switch(event.getAction()) {
		case MotionEvent.ACTION_UP:
			Log.w(TAG, "got up event");

			// Increment all counters
			((CountingLand) mContext).increment();

			// Reset the touchDown event
			mTouchDown = null;

			// Cancel the timer
			mTouchTimer.cancel();

			return false;
		}

		return true;
	}

	public void checkTouch() {
		Log.w(TAG, "in checkTouch");
		if (mTouchDown != null) {
			Log.w(TAG, "pushing counter...");

			if (mHighlightedCounter == false) {
				((CountingLand) mContext).
					highlightCounter(mTouchDown.getY());
				mHighlightedCounter = true;
			} else {
				((CountingLand) mContext).
					longClickCounter(mTouchDown.getY());
				mTouchDown = null;

				Log.w(TAG, "canceling timer");
				mTouchTimer.cancel();
			}
		} else {
			// The touch is no longer being held down; stop the
			// timer
			Log.w(TAG, "canceling timer");
			mTouchTimer.cancel();

			// Un-highlight all counters
			((CountingLand) mContext).refreshCounters();
		}
	}
}
