package com.ruswizards.rwlab127;

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
	private List<CustomViewForList> initialList_;

	RecyclerFilter(List<CustomViewForList> initialList){
		initialList_ = initialList;
		//TODO: add link to customadapter here
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults filteredResults = new FilterResults();

		constraint = constraint.toString().toLowerCase();
		List<CustomViewForList> filteredItemsList = new ArrayList<>();
		for (int i = 0; i < initialList_.size(); i++) {
			CustomViewForList currentItem = initialList_.get(i);
			if (currentItem.getTitle().contains(constraint) || currentItem.getDetails().contains(constraint)){
				filteredItemsList.add(currentItem);
			}
		}
		filteredResults.count = filteredItemsList.size();
		filteredResults.values = filteredItemsList;
		return filteredResults;
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		// TODO: use link to adapter to publish

	}
}
