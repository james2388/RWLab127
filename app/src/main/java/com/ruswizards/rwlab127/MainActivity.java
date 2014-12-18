package com.ruswizards.rwlab127;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.lucasr.dspec.DesignSpec;
import org.lucasr.dspec.DesignSpecFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

	private static final String LOG_TAG = "MainActivity";
	private static final String STATE_LIST = "ListView";
	private List<CustomViewForList> customArray_;
	private DesignSpecFrameLayout designSpecFrameLayout_;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		designSpecFrameLayout_ = (DesignSpecFrameLayout) findViewById(R.id.design_spec_layout);

		if (savedInstanceState != null) {
			customArray_ = (List<CustomViewForList>) savedInstanceState.getSerializable(STATE_LIST);
		} else {
			customArray_ = new ArrayList<>();
			Random generator = new Random();
			for (int i = 0; i < 20; i++) {
				CustomViewForList customViewForList = new CustomViewForList(this, randomString(5), randomString(15), generator.nextInt(4));
				customArray_.add(customViewForList);
			}
		}

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setBackgroundColor(getResources().getColor(R.color.abc_background_cache_hint_selector_material_dark));

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);

		// specify an adapter
		CustomRecyclerViewAdapter customRecyclerViewAdapter = new CustomRecyclerViewAdapter(customArray_);
		recyclerView.setAdapter(customRecyclerViewAdapter);
		recyclerView.addItemDecoration(
				new DividersItemDecoration(getResources().getDrawable(R.drawable.divider),
						(int)getResources().getDimension(R.dimen.list_padding_left))
		);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(STATE_LIST, (java.io.Serializable) customArray_);
		super.onSaveInstanceState(outState);
	}

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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
        */
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

	public void onSpecialClick(View v) {
		Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show();
	}
}
