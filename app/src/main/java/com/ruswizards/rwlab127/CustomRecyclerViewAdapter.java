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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter based on RecyclerView.Adapter
 */
public class CustomRecyclerViewAdapter extends
		RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

	private final List<CustomViewForList> listItems_;

	public CustomRecyclerViewAdapter(List<CustomViewForList> listItems) {
		listItems_ = listItems;
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
		CustomViewForList item = listItems_.get(i);
		View frontView = viewHolder.itemView.findViewById(R.id.front_layout);
		frontView.setTranslationX(0);
		frontView.setAlpha(1);
		viewHolder.titleTextView.setText(item.getTitle());
		viewHolder.detailsTextView.setText(item.getDetails());
		viewHolder.iconImageView.setImageResource(item.getImageResource());
	}

	/**
	 * Counts elements in a list
	 */
	@Override
	public int getItemCount() {
		return listItems_.size();
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
