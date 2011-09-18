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

public class CountingLandWrapper extends LinearLayout {
	private static final String TAG = "bunny-knitknit-CountingLandWrapper";
	private Context mContext;

	protected MotionEvent mTouchDown = null;
	protected MotionEvent mTouchUp = null;

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
				break;
			case MotionEvent.ACTION_UP:
				Log.w(TAG, "intercepted up event");
				mTouchUp = MotionEvent.obtain(event);

				//handleTouch();
				((CountingLand) mContext).increment();

				// Intercept the event
				return true;
		}

		// Otherwise let the event pass through to the children
		Log.w(TAG, "gave event to children");
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.w(TAG, "in onTouchEvent");

		switch(event.getAction()) {
			case MotionEvent.ACTION_UP:
				Log.w(TAG, "got up event");
				//mTouchUp = MotionEvent.obtain(event);
				((CountingLand) mContext).increment();
				break;
		}

		//handleTouch();

		return false;
	}

	private void handleTouch() {
		// If there has been a down and and up
		if (mTouchDown != null && mTouchUp != null) {
			// If the touch event has been for a short time
			if (mTouchUp.getEventTime() -
				mTouchDown.getEventTime() < 500 &&
				mTouchUp.getEventTime() >
				mTouchDown.getEventTime())
			{
				// Capture the event
				Log.w(TAG, "down and then up was short");

				((CountingLand) mContext).increment();
				//return true;
			} else {
				// Find which child was dwell-clicked
				//mTouchDown.getY();

				// Generate dwell click on child
			}

			// Reset the mTouchDown and mTouchUp events
			mTouchDown = null;
			mTouchUp = null;
		}
	}
}
