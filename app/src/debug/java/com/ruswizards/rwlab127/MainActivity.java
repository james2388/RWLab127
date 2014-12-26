/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 15.92.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.lucasr.dspec.DesignSpec;
import org.lucasr.dspec.DesignSpecFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main activity class
 */
public class MainActivity extends ActionBarActivity {

	private static final String STATE_LIST = "ListView";
	private static final String LOG_TAG = "MainActivity";
	private CustomRecyclerViewAdapter customRecyclerViewAdapter_;
	private List<CustomViewForList> itemsList_;
	private DesignSpecFrameLayout designSpecFrameLayout_;
	private TouchListener touchListenerRecycler_;
	private MenuItem searchAction_;
	private boolean isSearchOpened_;
	private RecyclerView recyclerView_;
	private List<CustomViewForList> tempList_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		designSpecFrameLayout_ = (DesignSpecFrameLayout) findViewById(R.id.design_spec_layout);
		// Retain state
		if (savedInstanceState != null) {
			itemsList_ = (List<CustomViewForList>) savedInstanceState.getSerializable(STATE_LIST);
		} else {
			itemsList_ = new ArrayList<>();
			tempList_ = new ArrayList<>();
//			Random generator = new Random();
			for (int i = 0; i < 5; i++) {
				addRandomItem(i);
				/*CustomViewForList customViewForList = new CustomViewForList(
						this, randomString(3), randomString(5), generator.nextInt(4));
				itemsList_.add(customViewForList);*/
			}
		}
		//Set up RecyclerView
		recyclerView_ = (RecyclerView) findViewById(R.id.recycler_view);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView_.setLayoutManager(linearLayoutManager);
		recyclerView_.setItemAnimator(new DefaultItemAnimator());
		//Set up item touch listener
		touchListenerRecycler_ = new TouchListener(this);
		recyclerView_.addOnItemTouchListener(touchListenerRecycler_);
		// Specify and set up an adapter
		customRecyclerViewAdapter_ = new CustomRecyclerViewAdapter(itemsList_, this);
		recyclerView_.setAdapter(customRecyclerViewAdapter_);
		recyclerView_.addItemDecoration(
				new DividersItemDecoration(getResources().getDrawable(R.drawable.divider),
						(int) getResources().getDimension(R.dimen.divider_padding_left))
		);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Puts array with items in out state bundle
		outState.putSerializable(STATE_LIST, (java.io.Serializable) itemsList_);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean result = super.dispatchTouchEvent(ev);
		// Check if items of RecyclerView should be deleted if touch is outside RecyclerView
		if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
			touchListenerRecycler_.checkForDeletion();
		}
		return result;
	}

	/**
	 * Generates random string
	 *
	 * @param length Length of the string
	 * @return Generated string
	 */
	private String randomString(int length) {
		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		char tempChar;
		for (int i = 0; i < length; i++) {
			tempChar = (char) (generator.nextInt(96) + 32);
			randomStringBuilder.append(tempChar);
		}
		return randomStringBuilder.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu only in DEBUG mode.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		searchAction_ = menu.findItem(R.id.search_action);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Switch DesignSpec view elements visibility
		DesignSpec designSpec = designSpecFrameLayout_.getDesignSpec();
		switch (item.getItemId()) {
			case R.id.baseline_switch_action:
				designSpec.setBaselineGridVisible(!designSpec.isBaselineGridVisible());
				item.setChecked(!item.isChecked());
				break;
			case R.id.spacing_switch_action:
				designSpec.setSpacingsVisible(!designSpec.areSpacingsVisible());
				item.setChecked(!item.isChecked());
				break;
			case R.id.keyline_switch_action:
				designSpec.setKeylinesVisible(!designSpec.areKeylinesVisible());
				item.setChecked(!item.isChecked());
				break;
			case R.id.search_action:
				if (isSearchOpened_){
					closeSearch();
					changeItems(tempList_);
				} else {
					openSearch();
					tempList_.clear();
					tempList_.addAll(itemsList_);
				}
		}

		return super.onOptionsItemSelected(item);
	}

	private void closeSearch() {
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(false);
		actionBar.setCustomView(R.layout.search_bar);

		searchAction_.setIcon(getResources().getDrawable(R.drawable.abc_ic_search_api_mtrl_alpha));

		isSearchOpened_ = false;
	}

	private void openSearch() {
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.search_bar);

		final EditText searchEditText = (EditText)actionBar.getCustomView().findViewById(R.id.search_edit_text);
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				customRecyclerViewAdapter_.getFilter().filter(searchEditText.getText());
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		searchAction_.setIcon(getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));
		isSearchOpened_ = true;
	}

	/**
	 * Just shows a toast. It is an implemented method similar to onClick but from
	 * {@link com.ruswizards.rwlab127.CustomViewForList} class
	 *
	 * @param v View
	 */
	public void onSpecialClick(View v) {
		Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.floating_action_button:
				// Add element into RecycleView if floating button was pressed
				RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
				int position = 0;
				recyclerView.getLayoutManager().scrollToPosition(position);
				addRandomItem(position);
				customRecyclerViewAdapter_.notifyItemInserted(position);
				break;
		}
	}

	/**
	 * Adds random item to list
	 *
	 * @param position Position of inserting
	 */
	private void addRandomItem(int position) {
		CustomViewForList customViewForList = new CustomViewForList(
				this, randomString(3), randomString(5), (int) new Random().nextInt(1000)/250);
		itemsList_.add(position, customViewForList);
	}

	/**
	 * Deletes item from RecyclerView and notifies RecycleViewAdapter
	 *
	 * @param position Item position in RecyclerView
	 */
	public void deleteItem(int position) {
		// Check if animation of items deleting were in progress while user swiped to
		// delete last item in the list. Needed to ensure correct item deleting and
		// avoid ArrayIndexOutOfBoundsException
		if (position == itemsList_.size()) {
			position = itemsList_.size() - 1;
		}
		itemsList_.remove(position);
		customRecyclerViewAdapter_.notifyItemRemoved(position);
	}

	public void changeItems(List<CustomViewForList> newItems) {

		itemsList_.clear();
		itemsList_.addAll(newItems);

		customRecyclerViewAdapter_.notifyDataSetChanged();


		/*customRecyclerViewAdapter_ = new CustomRecyclerViewAdapter(itemsList_, this);
		customRecyclerViewAdapter_.notifyDataSetChanged();
		int n = 0;
		for (int i = 0; i < newItems.size(); i++) {
			Log.d(LOG_TAG, "i: " + String.valueOf(i));
			Log.d(LOG_TAG, "n: " + String.valueOf(i));
			CustomViewForList initialItem = itemsList_.get(n);
			CustomViewForList newItem = newItems.get(i);

			while (!(initialItem.getTitle().equals(newItem.getTitle())
					&& initialItem.getDetails().equals(newItem.getDetails())) && n < itemsList_.size()){
				customRecyclerViewAdapter_.notifyItemRemoved(n);
				n++;
			}


			*//*if (!(initialItem.getTitle().equals(newItem.getTitle())
					&& initialItem.getDetails().equals(newItem.getDetails()))){
				customRecyclerViewAdapter_.notifyItemRemoved(n);
			} else {
				n++;
			}*//*
		}*/

	}
}
