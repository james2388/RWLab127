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

	private List<CustomViewForList> listItems_;

	/**
	 * ViewHolder class.
	 */
	public static class ViewHolder extends RecyclerView.ViewHolder {

		public TextView textViewTitle;
		public TextView textViewDetails;
		public ImageView imageViewIcon;

		public ViewHolder(View itemView) {
			super(itemView);
			textViewTitle = (TextView) itemView.findViewById(R.id.text_view_list_title);
			textViewDetails = (TextView) itemView.findViewById(R.id.text_view_list_details);
			imageViewIcon = (ImageView) itemView.findViewById(R.id.imagePicture);
		}
	}

	public CustomRecyclerViewAdapter(List<CustomViewForList> listItems) {
		listItems_ = listItems;
	}

	/**
	 * Creates view holder.
	 */
	@Override
	public CustomRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.custom_view_for_list, viewGroup, false);
		ViewHolder viewHolder = new ViewHolder(v);
		return viewHolder;
	}

	/**
	 * Fills in views in a view holder on bind
	 * @param viewHolder
	 * @param i item number
	 */
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		CustomViewForList item = listItems_.get(i);
		viewHolder.textViewTitle.setText(item.getTitle());
		viewHolder.textViewDetails.setText(item.getDetails());
		viewHolder.imageViewIcon.setImageResource(item.getImageResource());
	}

	/**
	 * Counts elements
	 */
	@Override
	public int getItemCount() {
		return listItems_.size();
	}
}
