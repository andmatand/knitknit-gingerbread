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
	private boolean mLongClick = false;

	public CountingLandWrapper(Context context) {
		super(context);
		mContext = context;
		this.setClickable(true);

		this.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.w(TAG, "in onTouch");
				return false;
			}
		});
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
				mLongClick = false;

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
		// onInterceptTouchEvent or if we have just finished performing
		// a longclick
		if (mTouchDown == null || mLongClick) return true;

		Log.w(TAG, "in onTouchEvent");

		switch(event.getAction()) {
		/*
		case MotionEvent.ACTION_MOVE:
			Log.w(TAG, "got move event");

			// If the touch has lasted a little longer than
			// normal
			if (event.getEventTime() -
				mTouchDown.getEventTime() >= 250)
			{
				// Change the color of the counter which
			       	// overlaps with the touch-event's y-position
				((CountingLand) mContext).highlightCounter(
					event.getY());
			}

			// If the touch has lasted long enough
			if (event.getEventTime() -
				mTouchDown.getEventTime() >= 500)
			{
				// Perform long-click on child view
				mLongClick = true;
				Log.w(TAG, "long-click");

				// Perform long-click on the counter which
				// overlaps with the touch-event's y-position
				((CountingLand) mContext).longClickCounter(
					event.getY());

				// Finish touchEvent
				mTouchDown = null;
				return false;
			}
			break;
			*/
		case MotionEvent.ACTION_UP:
			Log.w(TAG, "got up event");
			// Increment the counters
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

			((CountingLand) mContext).
				pushCounter(mTouchDown.getY());

			// Allow timer to continue
			//return true;
		} else {
			mTouchTimer.cancel();
		}

		// The touch is no longer being held down; stop the timer
		//return false;
	}
}
