/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 17.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter based on RecyclerView.Adapter
 */
public class CustomRecyclerViewAdapter extends
		RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> implements Filterable {

	private final List<CustomViewForList> listItems_;
	private RecyclerFilter filter_;
	private final MainActivity activity_;
	private boolean showDetails_;

	public CustomRecyclerViewAdapter(List<CustomViewForList> listItems, MainActivity activity) {
		listItems_ = listItems;
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
		// Hide even items details text view
		tempView = viewHolder.itemView.findViewById(R.id.details_text_view);
		if ((i & 1) == 0) {
			tempView.setVisibility(View.GONE);
		} else {
			tempView.setVisibility(View.VISIBLE);
		}
		// Fill in views
		CustomViewForList item = listItems_.get(i);
		viewHolder.titleTextView.setText(item.getTitle());
		viewHolder.detailsTextView.setText(item.getDetails());
		if (item.isIconDrawable()){
			viewHolder.iconImageView.setImageDrawable(item.getIconDrawable());
		} else {
			viewHolder.iconImageView.setImageResource(item.getIconResource());
		}
	}

	/**
	 * Counts elements in a list
	 */
	@Override
	public int getItemCount() {
		return listItems_.size();
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
	 * @return Items list
	 */
	public List<CustomViewForList> getItems() {
		return listItems_;
	}

	/**
	 * ViewHolder class.
	 */
	public static class ViewHolder extends RecyclerView.ViewHolder {

		public final TextView titleTextView;
		public final TextView detailsTextView;
		public final ImageView iconImageView;

		public ViewHolder(View itemView) {
			super(itemView);
			titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
			detailsTextView = (TextView) itemView.findViewById(R.id.details_text_view);
			iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
		}
	}
}
