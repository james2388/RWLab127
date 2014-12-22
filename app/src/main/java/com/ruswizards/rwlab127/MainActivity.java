/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 15.92.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
	private List<CustomViewForList> customArray_;
	private DesignSpecFrameLayout designSpecFrameLayout_;
	CustomRecyclerViewAdapter customRecyclerViewAdapter_;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		designSpecFrameLayout_ = (DesignSpecFrameLayout) findViewById(R.id.design_spec_layout);

		// Retain state
		if (savedInstanceState != null) {
			customArray_ = (List<CustomViewForList>) savedInstanceState.getSerializable(STATE_LIST);
		} else {
			customArray_ = new ArrayList<>();
			Random generator = new Random();
			for (int i = 0; i < 5; i++) {
				CustomViewForList customViewForList = new CustomViewForList(
						this, randomString(5), randomString(15), generator.nextInt(4));
				customArray_.add(customViewForList);
			}
		}

		//Set up Recycler View
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		//Set up item touch listener
		recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
			public static final String LOG_TAG = "MainActivity";
			private float initialX_;
			private float initialY_;
			private float summX_;
			private float summY_;
			private float tempX_;
			private float tempY_;

			/**
			 * Monitors touches inside Recycler View. If got an action up motion event, checks if
			 * previously was swiping right gesture. After detecting right swipe gesture calls
			 * {@link #deleteItem(android.support.v7.widget.RecyclerView)} to delete first touched
			 * item.
			 * @param rv Recycler View object
			 * @param e motion event
			 * @return false to handle motion event to super. True to handle motion event
			 * to onTouchEvent
			 */
			@Override
			public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
				int action = e.getActionMasked();
				if (action == MotionEvent.ACTION_DOWN){
					// Save coordinates of touch
					initialX_ = e.getX();
					initialY_ = e.getY();
					summX_ = 0;
					summY_ = 0;
					tempX_ = e.getX();
					tempY_ = e.getY();
					return false;
				} else if (action == MotionEvent.ACTION_MOVE){
					// Counts overall movement while touch continues
					summX_ = summX_ + e.getX() - tempX_;
					summY_ = summY_ + e.getY() - tempY_;
					tempX_ = e.getX();
					tempY_ = e.getY();
					return false;
				} else if (action == MotionEvent.ACTION_UP){
					// Delete item if swiped to the right
					if (summX_ > 0 && summX_ > Math.abs(summY_)){
						deleteItem(rv);
					}
					return false;
				} else {
					return false;
				}
			}

			/**
			 * Deletes item from RecyclerView and notifies RecycleViewAdapter
			 * @param rv Recycler View object
			 */
			private void deleteItem(RecyclerView rv) {
				View itemView = rv.findChildViewUnder(initialX_, initialY_);
				if (itemView != null) {
					int position = rv.getChildPosition(itemView);
					if (position == customArray_.size()){
						position = customArray_.size() - 1;
					}
					customRecyclerViewAdapter_.notifyItemRemoved(position);
					customArray_.remove(position);
				}
			}

			@Override
			public void onTouchEvent(RecyclerView rv, MotionEvent e) {
			}
		});

		// Specify an adapter
		customRecyclerViewAdapter_ = new CustomRecyclerViewAdapter(customArray_);
		recyclerView.setAdapter(customRecyclerViewAdapter_);
		recyclerView.addItemDecoration(
				new DividersItemDecoration(getResources().getDrawable(R.drawable.divider),
						(int)getResources().getDimension(R.dimen.list_padding_left))
		);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Put array with items in out state bundle
		outState.putSerializable(STATE_LIST, (java.io.Serializable) customArray_);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Generates random string
	 * @param length Length of the string
	 * @return Generated string
	 */
	private String randomString(int length) {
		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		char temp;
		for (int i = 0; i < length; i++) {
			temp = (char) (generator.nextInt(96) + 32);
			randomStringBuilder.append(temp);
		}
		return randomStringBuilder.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Switch DesignSpec view elements visibility
		DesignSpec designSpec = designSpecFrameLayout_.getDesignSpec();
		switch (item.getItemId()) {
			case R.id.action_switch_baseline:
				designSpec.setBaselineGridVisible(!designSpec.isBaselineGridVisible());
				break;
			case R.id.action_switch_spacing:
				designSpec.setSpacingsVisible(!designSpec.areSpacingsVisible());
				break;
			case R.id.action_switch_keyline:
				designSpec.setKeylinesVisible(!designSpec.areKeylinesVisible());
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Implemented method similar to onClick but from {@link com.ruswizards.rwlab127.CustomViewForList}
	 * class
	 * @param v View
	 */
	public void onSpecialClick(View v) {
		Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show();
	}

	public void onClick(View v){
		switch (v.getId()){
			// Add element into RecycleView if floating button was pressed
			case R.id.fab_button:
				RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
				recyclerView.getLayoutManager().scrollToPosition(0);
				addRandomItem(0);
				customRecyclerViewAdapter_.notifyItemInserted(0);
				break;
		}
	}

	/**
	 * Adds random item to list
	 * @param position Position of inserting
	 */
	private void addRandomItem(int position) {
		CustomViewForList customViewForList = new CustomViewForList(
				this, randomString(5), randomString(15), new Random().nextInt(4));
		customArray_.add(position, customViewForList);
	}
}
