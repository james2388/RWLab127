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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class to implement RecyclerView.OnItemTouchListener interface
 */
public class TouchListener implements RecyclerView.OnItemTouchListener {
	private static final int ANIMATION_DURATION = 500;
	private static final int VELOCITY_MIN = 1;            // Min velocity for swipe
	private static final float LEFT_SHIFT_PERCENTAGE = 0.4f;        // Min shift when not swiping
	private static final float RIGHT_SHIFT_PERCENTAGE = 0.5f; // Shift from which action 2 is started
	private static final float ANIMATION_SPEED_MULTIPLIER = 0.5f;
	private static final int VELOCITY_SEC_COUNT = 1;
	private final MainActivity activity_;
	private final int slop_;                                        // Touch slop
	private boolean canDelete_;                                // Flag if item can be deleted
	private float initialX_;                                // First touch coordinates
	private float initialY_;                                // First touch coordinates
	private View frontLayoutChildView_;
	private View childView_;
	private VelocityTracker velocityTracker_;
	private Direction swipeDirection_;
	private View deletedView_;
	private RecyclerView recyclerView_;
	public static boolean isTouched;

	TouchListener(MainActivity activity) {
		activity_ = activity;
		slop_ = ViewConfiguration.get(activity_).getScaledTouchSlop();
		swipeDirection_ = Direction.STAND;
	}

	/**
	 * Monitors touches inside Recycler View. Checks gestures for any actions. Calls
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
				isTouched = true;
				// Save touch and initial parameters
				initialX_ = event.getX();
				initialY_ = event.getY();
				childView_ = rv.findChildViewUnder(initialX_, initialY_);
				if (childView_ == null) {                        // Touch is in the recycler view,
					return false;                                 // but out of any child view
				}
				frontLayoutChildView_ = childView_.findViewById(R.id.front_layout);
				swipeDirection_ = Direction.STAND;
				velocityTracker_ = VelocityTracker.obtain();
				velocityTracker_.addMovement(event);
				// Check if user tapped again on the same tile he wants to delete
				canDelete_ = deletedView_ != null && !(deletedView_ == childView_);
//				break;
				return false;
			case MotionEvent.ACTION_MOVE:
				if (childView_ == null || swipeDirection_ == Direction.VERTICAL
						|| childView_ == deletedView_) {
					return false;
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
				final int childWidth;
				childWidth = frontLayoutChildView_.getWidth();
				if (swipeDirection_ == Direction.LEFT) {
					// Change position ans transparency while sliding left
					frontLayoutChildView_.setTranslationX(deltaX - slop_);
					frontLayoutChildView_.setAlpha(1 - deltaX / childWidth);
				}
				if (swipeDirection_ == Direction.RIGHT) {
					// Changes position of front and width, background and icon of left layout
					frontLayoutChildView_.setTranslationX(deltaX - slop_);
					FrameLayout actionsFrame =
							(FrameLayout) childView_.findViewById(R.id.actions_frame_layout);
					actionsFrame.setLayoutParams(
							new FrameLayout.LayoutParams((int) deltaX - slop_
									, ViewGroup.LayoutParams.MATCH_PARENT)
					);
					ImageView actionImageView =
							(ImageView) childView_.findViewById(R.id.action_image_view);
					if (actionImageView.getVisibility() == ImageView.GONE) {
						actionImageView.setVisibility(ImageView.VISIBLE);
					}
					if (deltaX > childWidth / 2) {
						actionImageView.setImageResource(android.R.drawable.ic_dialog_email);
						actionsFrame.setBackgroundColor(
								childView_.getResources().getColor(R.color.accentDark));
					} else if (deltaX < childWidth / 2) {
						actionImageView.setImageResource(android.R.drawable.ic_dialog_info);
						actionsFrame.setBackgroundColor(
								childView_.getResources().getColor(R.color.ripple_color));
					}
				}
//				break;
				return false;
			case MotionEvent.ACTION_UP:
				isTouched = false;
				if (childView_ == null) {
					return false;
				}
				velocityTracker_.addMovement(event);
				velocityTracker_.computeCurrentVelocity(VELOCITY_SEC_COUNT);
				float velocityX = velocityTracker_.getXVelocity();
				velocityTracker_.recycle();
				// Reset some variables and perform break if gesture is not horizontal
				if (swipeDirection_ == Direction.VERTICAL || swipeDirection_ == Direction.STAND) {
					childView_ = null;
					return false;
				}
				// Check gesture direction. If left: animate view depends on gestures' strength and
				// prepare view for deleting. If right: make toast for appropriate action ( 1 or 2)
				// and delete item
				deltaX = event.getX() - initialX_;
				childWidth = frontLayoutChildView_.getWidth();
				if (swipeDirection_ == Direction.LEFT &&
						(Math.abs(velocityX) >= VELOCITY_MIN
								|| Math.abs(deltaX) > childWidth * LEFT_SHIFT_PERCENTAGE)) {
					deletedView_ = childView_;
					canDelete_ = true;
					int duration = ANIMATION_DURATION;
					// Calculate duration from velocity if swiping
					if (Math.abs(velocityX) >= VELOCITY_MIN) {
						duration = (int) ((childWidth - Math.abs(deltaX)) / Math.abs(velocityX) /
								ANIMATION_SPEED_MULTIPLIER);
					}
					frontLayoutChildView_.animate()
							.translationX(-childWidth)
							.setDuration(duration)
							.alpha(0);
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
							canDelete_ = false;
							childView_ = null;
						}
					});
				} else if (swipeDirection_ == Direction.RIGHT && deltaX > slop_) {
					if (deltaX < childWidth * RIGHT_SHIFT_PERCENTAGE) {
						// Action 1
						Toast.makeText(activity_, "Action 1", Toast.LENGTH_SHORT).show();
					} else {
						// Action 2
						Toast.makeText(activity_, "Action 2", Toast.LENGTH_SHORT).show();
					}
					activity_.deleteItem(recyclerView_.getChildPosition(childView_));
				} else {
					frontLayoutChildView_.animate()
							.translationX(0)
							.setDuration(ANIMATION_DURATION)
							.alpha(1);
					View tempView = childView_.findViewById(R.id.action_image_view);
					tempView.setVisibility(ImageView.GONE);
					tempView = childView_.findViewById(R.id.actions_frame_layout);
					tempView.setLayoutParams(
							new FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
					);
				}
				return false;
			default:
				return false;
		}
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
	}

	/**
	 * Checks if item can be deleted and calls MainActivity.deleteItem method to do this
	 */
	public void checkForDeletion() {
		if (deletedView_ != null && canDelete_) {
			canDelete_ = false;
			int position = recyclerView_.getChildPosition(deletedView_);
			deletedView_ = null;
			activity_.deleteItem(position);
		}
	}

	private static enum Direction {
		LEFT, STAND, RIGHT, VERTICAL
	}
}
