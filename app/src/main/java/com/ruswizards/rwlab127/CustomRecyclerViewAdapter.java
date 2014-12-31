/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 17.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter based on RecyclerView.Adapter
 */
public class CustomRecyclerViewAdapter extends
		RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> implements Filterable {

	private final List<CustomViewForList> itemsList_;
	private final MainActivity activity_;
	private RecyclerFilter filter_;

	public CustomRecyclerViewAdapter(List<CustomViewForList> itemsList, MainActivity activity) {
		itemsList_ = itemsList;
		activity_ = activity;
	}

	/**
	 * Creates view holder.
	 */
	@Override
	public CustomRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.custom_view_for_list, viewGroup, false);
		return new ViewHolder(itemView);
	}

	/**
	 * Fills in views in a view holder on bind
	 *
	 * @param i Item number
	 */
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		// Reverse all changes to layout made by previous gestures
		View tempView = viewHolder.itemView.findViewById(R.id.front_layout);
		tempView.setTranslationX(0);
		tempView.setAlpha(1);
		tempView = viewHolder.itemView.findViewById(R.id.actions_frame_layout);
		tempView.setLayoutParams(
				new FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
		);
		tempView = viewHolder.itemView.findViewById(R.id.action_image_view);
		tempView.setVisibility(ImageView.GONE);
		// Hide details and icon_sdk for some items
		boolean isDetailsVisible = true;
		boolean isIconSdkVisible = true;
		if ((i & 1) == 0) {
			isDetailsVisible = false;
		}
		if (i % 3 == 0) {
			isIconSdkVisible = false;
		}
		RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		if (!isDetailsVisible) {
			titleLayoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}
		titleLayoutParams.leftMargin = (int) activity_.getResources()
				.getDimension(R.dimen.recycler_view_item_inner_layout_left_padding);
		titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.iconImageView);
		titleLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.icon_sdk);
		titleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		// Set layout params for title_text_view
		tempView = viewHolder.itemView.findViewById(R.id.title_text_view);
		tempView.setLayoutParams(titleLayoutParams);
		// Set visibility of icon_sdk
		tempView = viewHolder.itemView.findViewById(R.id.icon_sdk);
		if (isIconSdkVisible) {
			tempView.setVisibility(View.VISIBLE);
		} else {
			tempView.setVisibility(View.GONE);
		}
		// Set visibility of details_text_view
		tempView = viewHolder.itemView.findViewById(R.id.details_text_view);
		if (isDetailsVisible) {
			tempView.setVisibility(View.VISIBLE);
		} else {
			tempView.setVisibility(View.GONE);
		}
		// Fill in views
		CustomViewForList item = itemsList_.get(i);
		viewHolder.titleTextView.setText(item.getTitle());
		viewHolder.detailsTextView.setText(item.getDetails());
		if (item.isIconDrawable()) {
			viewHolder.iconImageView.setImageDrawable(item.getIconDrawable());
		} else {
			viewHolder.iconImageView.setImageResource(item.getIconResource());
		}
		if (item.getTargetSdk() != 0) {
			viewHolder.iconSdk.setText(String.valueOf(item.getTargetSdk()));
		} else {
			viewHolder.iconSdk.setText(activity_.getString(R.string.custom_view_empty_icon_sdk_text));
		}
	}

	/**
	 * Counts elements in a list
	 */
	@Override
	public int getItemCount() {
		return itemsList_.size();
	}

	@Override
	public Filter getFilter() {
		if (filter_ == null) {
			filter_ = new RecyclerFilter(this, activity_);
		}
		return filter_;
	}

	/**
	 * Updates initial items list in a filter.
	 */
	public void updateFilter(List<CustomViewForList> tempList) {
		filter_.updateInitialItems(tempList);
	}

	/**
	 * Method to get an items list from adapter
	 *
	 * @return Items list
	 */
	public List<CustomViewForList> getItems() {
		return itemsList_;
	}

	/**
	 * ViewHolder class.
	 */
	public static class ViewHolder extends RecyclerView.ViewHolder {

		public final TextView titleTextView;
		public final TextView detailsTextView;
		public final ImageView iconImageView;
		public final TextView iconSdk;

		public ViewHolder(View itemView) {
			super(itemView);
			titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
			detailsTextView = (TextView) itemView.findViewById(R.id.details_text_view);
			iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
			iconSdk = (TextView) itemView.findViewById(R.id.icon_sdk);
		}
	}
}
