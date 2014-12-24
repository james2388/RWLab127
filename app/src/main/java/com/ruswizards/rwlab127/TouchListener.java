/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 24.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.app.Activity;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Class to implement RecyclerView.OnItemTouchListener interface
 */
public class TouchListener implements RecyclerView.OnItemTouchListener {

	private static enum Direction {
		LEFT, STAND, RIGHT, VERTICAL
	}

	private static final int ANIMATION_DURATION = 500;
	private static final float SHIFT_PERCENTAGE = 0.3f;
	private static final String LOG_TAG = "TouchListener";
	private MainActivity activity_;
	private float initialX_;                            // First touch coordinates
	private float initialY_;                            // First touch coordinates
	private float summX_;                                // Movement along X axis
	private float summY_;                                // Movement along Y axis
	private float tempX_;
	private float tempY_;
	private View childView_;
	private VelocityTracker velocityTracker_;
	private int slop;
	private Direction swipeDirection;


	private GestureDetectorCompat gestureDetector_;

	TouchListener(MainActivity activity) {
		activity_ = activity;
		slop = ViewConfiguration.get(activity_).getScaledTouchSlop();
	}

	/**
	 * Monitors touches inside Recycler View. If got an action up motion event, checks if
	 * previously was swiping right gesture. After detecting right swipe gesture calls
	 * MainActivity.deleteItem to delete first touched item.
	 *
	 * @param rv    Recycler View object
	 * @param event Motion event
	 * @return False to handle motion event to super. True to handle motion event
	 * to onTouchEvent
	 */
	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
		/*int action = event.getActionMasked();
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
		}*/
		//gestureDetector_.onTouchEvent(event);
		int action = event.getActionMasked();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				// Save coordinates of touch
				initialX_ = event.getX();
				initialY_ = event.getY();
				// Set initial values
				summX_ = 0;
				summY_ = 0;
				tempX_ = event.getX();
				tempY_ = event.getY();

				swipeDirection = Direction.STAND;
				childView_ = rv.findChildViewUnder(initialX_, initialY_);
				velocityTracker_ = VelocityTracker.obtain();
				velocityTracker_.addMovement(event);
				break;
			case MotionEvent.ACTION_MOVE:
				if (childView_ == null || swipeDirection == Direction.VERTICAL){
					break;
				}

				velocityTracker_.addMovement(event);
				float deltaX = event.getX() - initialX_;
				float deltaY = event.getY() - initialY_;

				// Check if swiping started
				if (swipeDirection == Direction.STAND) {
//					swipeDirection = Direction.RIGHT;
					if (Math.abs(deltaX) > slop){
						if (deltaX > 0){
							swipeDirection = Direction.RIGHT;
						} else {
							swipeDirection = Direction.LEFT;
						}
					}
					if (Math.abs(deltaY) > slop){
						swipeDirection = Direction.VERTICAL;
					}
				}


				childView_.setTranslationX(deltaX - slop);
				int childWidth = childView_.getWidth();
				childView_.setAlpha(1 - deltaX / childWidth);
				break;
			case MotionEvent.ACTION_UP:
				velocityTracker_.recycle();
				if (childView_ == null){
					break;
				}
				if (swipeDirection == Direction.STAND || swipeDirection == Direction.VERTICAL){
					childView_.animate().translationX(0).setDuration(ANIMATION_DURATION).alpha(1);
					break;
				}

				deltaX = event.getRawX() - initialX_;
				childWidth = childView_.getWidth();
				if (Math.abs(deltaX) < childWidth * SHIFT_PERCENTAGE) {
					childView_.animate().translationX(0).setDuration(ANIMATION_DURATION).alpha(1);
				} else {
					if (swipeDirection == Direction.RIGHT) {
						childView_.animate().translationX(childWidth).setDuration(ANIMATION_DURATION).alpha(0);
					} else {
						childView_.animate().translationX(-childWidth).setDuration(ANIMATION_DURATION).alpha(0);
					}
				}
				break;
			default:
				break;
		}
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
	}
}
