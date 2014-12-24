package com.ruswizards.rwlab127;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 24.12.2014
 * Vladimir Farafonov
 */
public class TouchListener implements RecyclerView.OnItemTouchListener {

	private MainActivity activity_;
	private float initialX_;							// First touch coordinates
	private float initialY_;							// First touch coordinates
	private float summX_;								// Movement along X axis
	private float summY_;								// Movement along Y axis
	private float tempX_;
	private float tempY_;

	TouchListener(MainActivity activity){
		activity_ = activity;
	}

	/**
	* Monitors touches inside Recycler View. If got an action up motion event, checks if
	* previously was swiping right gesture. After detecting right swipe gesture calls
	* MainActivity.deleteItem to delete first touched item.
	* @param rv Recycler View object
	* @param event Motion event
	* @return False to handle motion event to super. True to handle motion event
	* to onTouchEvent
	*/
	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
		int action = event.getActionMasked();
		if (action == MotionEvent.ACTION_DOWN) {
			// Save coordinates of touch
			initialX_ = event.getX();
			initialY_ = event.getY();
			// Set initial values
			summX_ = 0;
			summY_ = 0;
			tempX_ = event.getX();
			tempY_ = event.getY();
			return false;
		} else if (action == MotionEvent.ACTION_MOVE) {
			// Counts overall movement while touch continues based on previous step
			// coordinates
			summX_ = summX_ + event.getX() - tempX_;
			summY_ = summY_ + event.getY() - tempY_;
			//Saves last coordinates
			tempX_ = event.getX();
			tempY_ = event.getY();
			return false;
		} else if (action == MotionEvent.ACTION_UP) {
			// Delete item if swiped to the right
			if (summX_ > 0 && summX_ > Math.abs(summY_)) {
				activity_.deleteItem(rv, initialX_, initialY_);
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
	}
}
