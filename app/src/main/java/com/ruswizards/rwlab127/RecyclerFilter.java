/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 25.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter class for RecyclerView
 */
public class RecyclerFilter extends Filter {
	private List<CustomViewForList> initialList_;
	private MainActivity activity_;

	RecyclerFilter(CustomRecyclerViewAdapter customRecyclerViewAdapter, MainActivity activity) {
		activity_ = activity;
		initialList_ = new ArrayList<>();
		initialList_.addAll(customRecyclerViewAdapter.getItems());
	}

	/**
	 * Filters items list to find items matched search criteria. Filter scans title and detail
	 * fields
	 *
	 * @param constraint Search sequence
	 * @return FilterResults object
	 */
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults filteredResults = new FilterResults();
		constraint = constraint.toString().toLowerCase();
		List<CustomViewForList> filteredItemsList = new ArrayList<>();
		for (int i = 0; i < initialList_.size(); i++) {
			CustomViewForList currentItem = initialList_.get(i);
			if (currentItem.getTitle().toLowerCase().contains(constraint)
					|| currentItem.getDetails().toLowerCase().contains(constraint)) {
				filteredItemsList.add(currentItem);
			}
		}
		filteredResults.count = filteredItemsList.size();
		filteredResults.values = filteredItemsList;

		return filteredResults;
	}

	/**
	 * Calls MainActivity.changeItems to apply filter to RecyclerView
	 *
	 * @param constraint Search sequence
	 * @param results    FilterResults object with search results
	 */
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		activity_.changeItems((List<CustomViewForList>) results.values);
	}

	/**
	 * Synchronize initial items list when some items were deleted or new items were added
	 *
	 * @param tempList List to synchronize to.
	 */
	public void updateInitialItems(List<CustomViewForList> tempList) {
		initialList_.clear();
		initialList_.addAll(tempList);
	}
}
