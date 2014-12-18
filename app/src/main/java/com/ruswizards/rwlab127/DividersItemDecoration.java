package com.ruswizards.rwlab127;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 18.12.2014
 * Vladimir Farafonov
 */
public class DividersItemDecoration extends RecyclerView.ItemDecoration {

	private Drawable dividerImage_;
	private int leftPadding_;

	public DividersItemDecoration(Context context, AttributeSet attributeSet){
		final TypedArray typedArray = context.obtainStyledAttributes(attributeSet, new int[]{android.R.attr.listDivider});
		dividerImage_ = typedArray.getDrawable(0);
		typedArray.recycle();
	}

	public DividersItemDecoration(Drawable dividerImage, int leftPaddingDp){
		dividerImage_ = dividerImage;
		leftPadding_ = leftPaddingDp;
	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		if (dividerImage_ == null) {
			super.onDrawOver(c, parent, state);
			return;
		}
		if (getOrientation(parent) == LinearLayoutManager.VERTICAL){
			final int left = leftPadding_;
			final int right = parent.getWidth() - parent.getPaddingRight();
			final int childCount = parent.getChildCount();

			for (int i = 1; i < childCount; i++){
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)child.getLayoutParams();
				final int size = dividerImage_.getIntrinsicHeight();
				final int top = child.getTop();
				final int bottom = top + size;
				dividerImage_.setBounds(left, top, right, bottom);
				dividerImage_.draw(c);
			}
		} else {
			final int top = leftPadding_;
			final int bottom = parent.getHeight() - parent.getPaddingBottom();
			final int childCount = parent.getChildCount();

			for (int i=1; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
				final int size = dividerImage_.getIntrinsicWidth();
				final int left = child.getLeft() - params.leftMargin;
				final int right = left + size;
				dividerImage_.setBounds(left, top, right, bottom);
				dividerImage_.draw(c);
			}
		}
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		if (dividerImage_ == null){
			return;
		}
		if (parent.getChildPosition(view) < 1){
			return;
		}
		if (getOrientation(parent) == LinearLayoutManager.VERTICAL){
			outRect.top = dividerImage_.getIntrinsicHeight();
		} else {
			outRect.left = dividerImage_.getIntrinsicWidth();
		}
	}

	private int getOrientation(RecyclerView parent) {
		if (parent.getLayoutManager() instanceof LinearLayoutManager){
			LinearLayoutManager layoutManager = (LinearLayoutManager)parent.getLayoutManager();
			return layoutManager.getOrientation();
		} else {
			throw new IllegalStateException("DividersItemDecoration can only be used with a LinearLayoutManager.");
		}
	}
}
