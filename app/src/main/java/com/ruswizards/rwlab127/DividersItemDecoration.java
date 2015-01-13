/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 18.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class to draw decoration (dividers) to RecyclerView
 */
class DividersItemDecoration extends RecyclerView.ItemDecoration {

	private final Drawable dividerImage_;
	private final int leftPadding_;

	/**
	 * Class constructor
	 *
	 * @param dividerImage Drawable for divider
	 * @param leftPadding  Left padding in dp
	 */
	public DividersItemDecoration(Drawable dividerImage, int leftPadding) {
		dividerImage_ = dividerImage;
		leftPadding_ = leftPadding;
	}

	/**
	 * Draws a divider between items in the RecyclerView
	 *
	 * @param canvas Canvas to draw into
	 * @param parent RecyclerView where this ItemDecoration is drawn into
	 * @param state  The current state of RecyclerView.
	 */
	@Override
	public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
		if (dividerImage_ == null) {
			super.onDrawOver(canvas, parent, state);
			return;
		}
		//Picks up dimensions of canvas and draws canvas to child of RecyclerView depends on current
		// orientation
		if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
			int left = leftPadding_;
			int right = parent.getWidth() - parent.getPaddingRight();
			int childCount = parent.getChildCount();

			for (int i = 1; i < childCount; i++) {
				View child = parent.getChildAt(i);
				int size = dividerImage_.getIntrinsicHeight();
				int top = child.getTop();
				int bottom = top + size;
				dividerImage_.setBounds(left, top, right, bottom);
				dividerImage_.draw(canvas);
			}
		} else {
			int top = leftPadding_;
			int bottom = parent.getHeight() - parent.getPaddingBottom();
			int childCount = parent.getChildCount();

			for (int i = 1; i < childCount; i++) {
				View child = parent.getChildAt(i);
				RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
				int size = dividerImage_.getIntrinsicWidth();
				final int left = child.getLeft() - params.leftMargin;
				final int right = left + size;
				dividerImage_.setBounds(left, top, right, bottom);
				dividerImage_.draw(canvas);
			}
		}
	}

	/**
	 * Checks if parent view uses LinearLayoutManager and returns orientation from it. Otherwise
	 * IllegalStateException will be thrown
	 *
	 * @param parent The RecyclerView to decorate
	 * @return Orientation from LinearLayoutManager
	 */
	private int getOrientation(RecyclerView parent) {
		if (parent.getLayoutManager() instanceof LinearLayoutManager) {
			LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
			return layoutManager.getOrientation();
		} else {
			throw new IllegalStateException(parent.getContext().getString(
					R.string.not_linear_layout_manager_exception));
		}
	}
}
