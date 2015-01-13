/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 15.92.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
	private static final String STATE_TEMP_LIST = "TempList";
	private static final String STATE_IS_SEARCHING = "isSearching";
	private static final String STATE_SEARCH_TEXT = "SearchText";
	public static final float ALPHA_DISABLE_BUTTON = 0.5f;
	private static final long ANIMATION_DURATION = 500;
	private static final String STATE_ACTIONS_OPENED = "isActionsOpened";

	private CustomRecyclerViewAdapter customRecyclerViewAdapter_;
	private List<CustomViewForList> itemsList_;
	private DesignSpecFrameLayout designSpecFrameLayout_;
	private TouchListener touchListener_;
	private MenuItem searchAction_;
	private boolean isSearchOpened_;
	private List<CustomViewForList> tempList_;
	private android.os.Handler handlerUiThread_;
	private boolean isActionsOpened_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		designSpecFrameLayout_ = (DesignSpecFrameLayout) findViewById(R.id.design_spec_layout);
		// Retain state
		if (savedInstanceState != null) {
			itemsList_ = (List<CustomViewForList>) savedInstanceState.getSerializable(STATE_LIST);
			isSearchOpened_ = savedInstanceState.getBoolean(STATE_IS_SEARCHING);
			if (isSearchOpened_) {
				tempList_ =
						(List<CustomViewForList>) savedInstanceState.getSerializable(STATE_TEMP_LIST);
				openSearch();
			}
			isActionsOpened_ = savedInstanceState.getBoolean(STATE_ACTIONS_OPENED);
			// Restore action buttons state
			if (isActionsOpened_) {
				float actionButtonSize = getResources().getDimension(R.dimen.fab_size);
				float defaultMarging = getResources().getDimension(R.dimen.fab_margin);
				View tempView = findViewById(R.id.thread_floating_button);
				tempView.setTranslationX(-3 * (actionButtonSize + defaultMarging));
				tempView = findViewById(R.id.asynctask_floating_button);
				tempView.setTranslationX(-2 * (actionButtonSize + defaultMarging));
				tempView = findViewById(R.id.floating_action_button);
				tempView.setTranslationX(-(actionButtonSize + defaultMarging));
				tempView = findViewById(R.id.open_actions_button);
				tempView.setRotation(180);
			}
			AddItemAsyncTask.linkToActivity(this);
			TextView actionButton = (TextView) findViewById(R.id.asynctask_floating_button);
			if (AddItemAsyncTask.activeCount != 0) {
				disableButton(actionButton);
			} else {
				enableButton(actionButton);
			}
			actionButton = (TextView) findViewById(R.id.thread_floating_button);
			if (AddItemThread.activeCount != 0) {
				disableButton(actionButton);
			} else {
				enableButton(actionButton);
			}
		} else {
			itemsList_ = new ArrayList<>();
			tempList_ = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				itemsList_.add(generateRandomItem());
			}
		}
		//Set up RecyclerView
		final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		//Set up item touch listener
		touchListener_ = new TouchListener(this);
		recyclerView.addOnItemTouchListener(touchListener_);
		// Specify and set up an adapter
		customRecyclerViewAdapter_ = new CustomRecyclerViewAdapter(itemsList_, this);
		recyclerView.setAdapter(customRecyclerViewAdapter_);
		recyclerView.addItemDecoration(
				new DividersItemDecoration(getResources().getDrawable(R.drawable.divider),
						(int) getResources().getDimension(R.dimen.divider_padding_left))
		);
		// Create handler to work with AddItemThread
		handlerUiThread_ = new android.os.Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case AddItemThread.STATUS_STARTED:
						int position = 0;
						addItem(position, (CustomViewForList) msg.obj);
						// Disable button
						TextView threadButton = (TextView) findViewById(R.id.thread_floating_button);
						disableButton(threadButton);
						break;
					case AddItemThread.STATUS_MODIFY:
						updateItemsDetail((CustomViewForList) msg.obj, String.valueOf(msg.arg1));
						break;
					case AddItemThread.STATUS_FINISHED:
						updateItemsDetail((CustomViewForList) msg.obj,
								getResources().getString(R.string.count_finished));
						// Enable button
						threadButton = (TextView) findViewById(R.id.thread_floating_button);
						enableButton(threadButton);
						break;
				}
			}
		};
		// Retain data what could not be retained earlier
		if (savedInstanceState != null) {
			AddItemThread.linkToActivity(this, handlerUiThread_);
			if (isSearchOpened_) {
				customRecyclerViewAdapter_.getFilter();
				customRecyclerViewAdapter_.updateFilter(tempList_);
				final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
				searchEditText.setText(savedInstanceState.getString(STATE_SEARCH_TEXT));
			}
		}
	}

	public void enableButton(TextView actionButton) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			actionButton.setBackground(getResources().getDrawable(R.drawable.ab_shape));
		}
		actionButton.setAlpha(1);
		actionButton.setClickable(true);
	}

	public void disableButton(TextView actionButton) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			actionButton.setBackground(getResources().getDrawable(R.drawable.ab_shape_busy));
		}
		actionButton.setAlpha(MainActivity.ALPHA_DISABLE_BUTTON);
		actionButton.setClickable(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(STATE_LIST, (java.io.Serializable) itemsList_);
		outState.putBoolean(STATE_IS_SEARCHING, isSearchOpened_);
		if (isSearchOpened_) {
			outState.putSerializable(STATE_TEMP_LIST, (java.io.Serializable) tempList_);
			final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
			outState.putString(STATE_SEARCH_TEXT, searchEditText.getText().toString());
		}
		outState.putBoolean(STATE_ACTIONS_OPENED, isActionsOpened_);
		AddItemAsyncTask.unlinkFromActivity();
		AddItemThread.unlinkFromActivity();
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
		boolean result = super.dispatchTouchEvent(ev);
		// Check if items of RecyclerView should be deleted if touch is outside RecyclerView
		if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
			touchListener_.checkForDeletion();
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
			tempChar = (char) (generator.nextInt(10) + 72);
			randomStringBuilder.append(tempChar);
		}
		return randomStringBuilder.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		searchAction_ = menu.findItem(R.id.search_action);
		// Change search icon. Needed for retaining state
		if (isSearchOpened_) {
			searchAction_.setIcon(getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));
		}
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
				if (isSearchOpened_) {
					closeSearch();
					changeItems(tempList_);
				} else {
					openSearch();
					tempList_.clear();
					tempList_.addAll(itemsList_);
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Closes previously opened search field
	 */
	private void closeSearch() {
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(false);
		actionBar.setCustomView(R.layout.search_bar);
		searchAction_.setIcon(getResources().getDrawable(R.drawable.abc_ic_search_api_mtrl_alpha));
		isSearchOpened_ = false;
	}

	/**
	 * Opens search field and registers onTextChanged listener for the EditText field within
	 * search area
	 */
	private void openSearch() {
		// Register onTextChanged listener for the EditText view
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.search_bar);
		final EditText searchEditText = (EditText) actionBar.getCustomView()
				.findViewById(R.id.search_edit_text);
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
		// Change search icon
		if (searchAction_ != null) {
			searchAction_.setIcon(getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));
		}
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
				int position = 0;
				addItem(position, generateRandomItem());
				break;
			case R.id.asynctask_floating_button:
				// Start AsyncTask
//				new AddItemAsyncTask(this).execute();
				AddItemAsyncTask addItemAsyncTask = new AddItemAsyncTask(this);
				addItemAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				break;
			case R.id.thread_floating_button:
				AddItemThread addItemThread = new AddItemThread(handlerUiThread_, this);
				addItemThread.start();
				break;
			case R.id.open_actions_button:
				animateButtons();
				isActionsOpened_ = !isActionsOpened_;
				break;
		}
	}

	/**
	 * Animates action buttons movement
	 */
	private void animateButtons() {
		float actionButtonSize = getResources().getDimension(R.dimen.fab_size);
		float defaultMarging = getResources().getDimension(R.dimen.fab_margin);
		if (isActionsOpened_) {
			View tempView = findViewById(R.id.thread_floating_button);
			tempView.animate().translationX(0).setDuration(ANIMATION_DURATION);
			tempView = findViewById(R.id.asynctask_floating_button);
			tempView.animate().translationX(0).setDuration(ANIMATION_DURATION);
			tempView = findViewById(R.id.floating_action_button);
			tempView.animate().translationX(0).setDuration(ANIMATION_DURATION);
			tempView = findViewById(R.id.open_actions_button);
			tempView.animate().rotation(0).setDuration(ANIMATION_DURATION);
		} else {
			View tempView = findViewById(R.id.thread_floating_button);
			tempView.animate().translationX(-3 * (actionButtonSize + defaultMarging)).setDuration(ANIMATION_DURATION);
			tempView = findViewById(R.id.asynctask_floating_button);
			tempView.animate().translationX(-2 * (actionButtonSize + defaultMarging)).setDuration(ANIMATION_DURATION);
			tempView = findViewById(R.id.floating_action_button);
			tempView.animate().translationX(-(actionButtonSize + defaultMarging)).setDuration(ANIMATION_DURATION);
			tempView = findViewById(R.id.open_actions_button);
			tempView.animate().rotation(180).setDuration(ANIMATION_DURATION);
		}
	}

	/**
	 * Generates random item for list
	 */
	CustomViewForList generateRandomItem() {
		return new CustomViewForList(
				this, randomString(3), randomString(5), new Random().nextInt(1000) / 250);
	}

	/**
	 * Adds item to list
	 *
	 * @param position Position of inserting
	 * @param item     CustomViewForList item
	 */
	public void addItem(int position, CustomViewForList item) {
		itemsList_.add(position, item);
		if (isSearchOpened_) {
			tempList_.add(position, item);
			customRecyclerViewAdapter_.updateFilter(tempList_);
			EditText searchEditText = (EditText) getSupportActionBar()
					.getCustomView()
					.findViewById(R.id.search_edit_text);
			customRecyclerViewAdapter_.getFilter().filter(searchEditText.getText());
		}
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.getLayoutManager().scrollToPosition(position);
		customRecyclerViewAdapter_.notifyItemInserted(position);
	}

	/**
	 * Deletes item from RecyclerView and notifies RecycleViewAdapter. If search filter is active
	 * also synchronizes items list with filters' copy
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
		// Delete item from tempList and filters' copy of items list. Finds absolute position of
		// deleted item to do this.
		if (isSearchOpened_) {
			try {
				int absolutePosition = findItem(tempList_, itemsList_.get(position));
				tempList_.remove(absolutePosition);
				customRecyclerViewAdapter_.updateFilter(tempList_);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Delete item from visible list
		itemsList_.remove(position);
		customRecyclerViewAdapter_.notifyItemRemoved(position);
	}

	/**
	 * Searches CustomViewForList object in a list
	 *
	 * @param tempList     List where object should be found
	 * @param deletedView_ Wanted object
	 * @throws NullPointerException Exception to throw if object not found
	 */
	int findItem(List<CustomViewForList> tempList, CustomViewForList deletedView_) throws
			NullPointerException {
		for (int i = 0; i < tempList.size(); i++) {
			if (tempList.get(i).isEqual(deletedView_)) {
				return i;
			}
		}
		throw new NullPointerException(getString(R.string.item_not_found_exception));
	}

	/**
	 * Refreshes list visibility while filtering
	 *
	 * @param newItems Items which are visible with search sequence
	 */
	public void changeItems(List<CustomViewForList> newItems) {
		itemsList_.clear();
		itemsList_.addAll(newItems);
		customRecyclerViewAdapter_.notifyDataSetChanged();
	}

	/**
	 * Finds item in a list and sets details to specified string
	 *
	 * @param item Item to update
	 * @param s    Details string
	 */
	public void updateItemsDetail(CustomViewForList item, String s) {
		// Update item in tempList and filters' copy of items list. Finds absolute position of
		// updated item to do this.
		if (isSearchOpened_) {
			try {
				int absolutePosition = findItem(tempList_, item);
				tempList_.get(absolutePosition).setDetails(s);
				customRecyclerViewAdapter_.updateFilter(tempList_);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Update item in visible list
		try {
			int position = findItem(itemsList_, item);
			itemsList_.get(position).setDetails(s);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		customRecyclerViewAdapter_.notifyDataSetChanged();
	}
}
