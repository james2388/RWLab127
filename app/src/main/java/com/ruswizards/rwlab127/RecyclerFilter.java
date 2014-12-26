package com.ruswizards.rwlab127;

import android.nfc.NfcEvent;
import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 25.12.2014
 * Vladimir Farafonov
 */
public class RecyclerFilter extends Filter {
	private static final String LOG_TAG = "RecyclerFilter";
	private List<CustomViewForList> initialList_;
	private CustomRecyclerViewAdapter clientAdapter_;
	private MainActivity activity_;
	private List<CustomViewForList> filteredItemsList_;

	RecyclerFilter(CustomRecyclerViewAdapter customRecyclerViewAdapter, MainActivity activity){
		clientAdapter_ = customRecyclerViewAdapter;
		activity_ = activity;
		Log.d(LOG_TAG, "CONSTRUCTOR");
		filteredItemsList_ = new ArrayList<>();
		initialList_ = new ArrayList<>();
		initialList_.addAll(clientAdapter_.getItems());
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		Log.d(LOG_TAG, "Filtering: |" + constraint + "|");
		Log.d(LOG_TAG, "Initial size: |" + String.valueOf(initialList_.size()) + "|");

		FilterResults filteredResults = new FilterResults();
		constraint = constraint.toString().toLowerCase();
		filteredItemsList_.clear();
		for (int i = 0; i < initialList_.size(); i++) {
			CustomViewForList currentItem = initialList_.get(i);
			if (currentItem.getTitle().toLowerCase().contains(constraint) || currentItem.getDetails().toLowerCase().contains(constraint)){
				filteredItemsList_.add(currentItem);
			}
		}
		filteredResults.count = filteredItemsList_.size();
		filteredResults.values = filteredItemsList_;
		return filteredResults;
		//TODO: add support of changing list (items could be added/deleted);
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		activity_.changeItems(filteredItemsList_);
	}
}
