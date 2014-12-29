/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 24.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

/**
 * Class to implement RecyclerView.OnItemTouchListener interface
 */
public class TouchListener implements RecyclerView.OnItemTouchListener {
	private static final int ANIMATION_DURATION = 500;
	private static final int VELOCITY_MIN = 1000;            // Min velocity for swipe
	private static final float SHIFT_PERCENTAGE = 0.4f;        // Min shift when not swiping
	private static final float ANIMATION_STABILIZATION = 5000;

	private boolean canDelete_;                                // Flag if item can be deleted
	private MainActivity activity_;
	private float initialX_;                                // First touch coordinates
	private float initialY_;                                // First touch coordinates
	private View frontLayoutChildView_;
	private View childView_;
	private VelocityTracker velocityTracker_;
	private int slop_;                                        // Touch slop
	private Direction swipeDirection_;
	private View deletedView_;
	private RecyclerView recyclerView_;

	TouchListener(MainActivity activity) {
		activity_ = activity;
		slop_ = ViewConfiguration.get(activity_).getScaledTouchSlop();
	}

	/**
	 * Monitors touches inside Recycler View. Checks gestures for deleting item gesture and calls
	 * MainActivity.deleteItem method or cancels deleting if needed
	 *
	 * @param rv    Recycler View object
	 * @param event Motion event
	 * @return False to handle motion event to super. True to handle motion event
	 * to onTouchEvent
	 */
	@Override
	public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent event) {
		if (recyclerView_ == null) {
			recyclerView_ = rv;
		}
		int action = event.getActionMasked();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				// Save touch and initial parameters
				initialX_ = event.getX();
				initialY_ = event.getY();
				childView_ = rv.findChildViewUnder(initialX_, initialY_);
				if (childView_ == null) {                        // Touch is in the recycler view,
					break;                                        // but out of any child view
				}
				frontLayoutChildView_ = childView_.findViewById(R.id.front_layout);
				swipeDirection_ = Direction.STAND;
				velocityTracker_ = VelocityTracker.obtain();
				velocityTracker_.addMovement(event);
				// Check if user tapped on the same tile he wants to delete
				canDelete_ = !(deletedView_ != null && deletedView_ == childView_);
				break;

			case MotionEvent.ACTION_MOVE:
				if (childView_ == null || swipeDirection_ == Direction.VERTICAL
						|| childView_ == deletedView_) {
					break;
				}
				velocityTracker_.addMovement(event);
				float deltaX = event.getX() - initialX_;
				float deltaY = event.getY() - initialY_;
				// Check if swiping just started and saves direction if movement is strong enough
				if (swipeDirection_ == Direction.STAND) {
					if (Math.abs(deltaX) > slop_) {
						if (deltaX > 0) {
							swipeDirection_ = Direction.RIGHT;
						} else {
							swipeDirection_ = Direction.LEFT;
						}
					}
					if (Math.abs(deltaY) > slop_) {
						swipeDirection_ = Direction.VERTICAL;
					}
				}
				// Move view along gesture and change its transparency
				int childWidth;
				if (swipeDirection_ == Direction.RIGHT || swipeDirection_ == Direction.LEFT) {
					frontLayoutChildView_.setTranslationX(deltaX - slop_);
					childWidth = frontLayoutChildView_.getWidth();
					frontLayoutChildView_.setAlpha(1 - deltaX / childWidth);
				}
				break;

			case MotionEvent.ACTION_UP:
				if (childView_ == null) {
					break;
				}
				velocityTracker_.addMovement(event);
				velocityTracker_.computeCurrentVelocity(1000);
				float velocityX = velocityTracker_.getXVelocity();
				velocityTracker_.recycle();
				// Reset some variables and perform break if gesture is not horizontal
				if (swipeDirection_ == Direction.VERTICAL || swipeDirection_ == Direction.STAND) {
					childView_ = null;
					break;
				}
				// Animates view depends on gestures' strength. Prepares view for deleting
				deltaX = event.getX() - initialX_;
				childWidth = frontLayoutChildView_.getWidth();
				if (Math.abs(deltaX) < childWidth * SHIFT_PERCENTAGE
						&& Math.abs(velocityX) < VELOCITY_MIN) {
					frontLayoutChildView_.animate()
							.translationX(0)
							.setDuration(ANIMATION_DURATION)
							.alpha(1);
				} else {
					deletedView_ = childView_;
					canDelete_ = true;
					int duration = ANIMATION_DURATION;
					// If swiping calculate duration from velocity
					if (Math.abs(velocityX) >= VELOCITY_MIN) {
						duration = (int) ((childWidth - deltaX) / Math.abs(velocityX) *
								ANIMATION_STABILIZATION);
					}
					// Finish move animation
					if (swipeDirection_ == Direction.RIGHT) {
						frontLayoutChildView_.animate()
								.translationX(childWidth)
								.setDuration(duration)
								.alpha(0);
					} else {
						frontLayoutChildView_.animate()
								.translationX(-childWidth)
								.setDuration(duration)
								.alpha(0);
					}
					// Register receiver for undo button
					TextView undoTextView = (TextView) childView_.findViewById(R.id.undo_text_view);
					undoTextView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							View frontDeletedView = deletedView_.findViewById(R.id.front_layout);
							frontDeletedView.animate()
									.translationX(0)
									.setDuration(ANIMATION_DURATION)
									.alpha(1);
							deletedView_ = null;
						}
					});
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

	/**
	 * Checks if item can be deleted and calls MainActivity.deleteItem method to do this
	 */
	public void checkForDeletion() {
		if (deletedView_ != null && canDelete_) {
			activity_.deleteItem(recyclerView_.getChildPosition(deletedView_));
			canDelete_ = false;
			deletedView_ = null;
		}
	}

	private static enum Direction {
		LEFT, STAND, RIGHT, VERTICAL
	}
}
