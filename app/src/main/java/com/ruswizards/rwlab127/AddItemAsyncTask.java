package com.ruswizards.rwlab127;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 12.01.2015
 * Vladimir Farafonov
 */
public class AddItemAsyncTask extends AsyncTask<Void, Integer, Void> {

	private MainActivity activity_;
	private CustomViewForList item_;
	private static int itemId_ = 0;

	AddItemAsyncTask(MainActivity mainActivity){
		activity_ = mainActivity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		int position = 0;
		item_ = new CustomViewForList(
				activity_,
				"Async# " + String.valueOf(itemId_),
				"Countdown..",
				new Random().nextInt(1000) / 250
		);
		itemId_ ++;
		activity_.addItem(position, item_);
		RecyclerView recyclerView = (RecyclerView) activity_.findViewById(R.id.recycler_view);
		recyclerView.getLayoutManager().scrollToPosition(position);
		recyclerView.getAdapter().notifyItemInserted(position);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		activity_.updateItemsDetail(item_, String.valueOf(values[0]));
		item_.setDetails(String.valueOf(values[0]));
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (int i = 1; i <= 10; i++) {
			someMethodForThreads();
			publishProgress(i);
		}
		return null;
	}

	private void someMethodForThreads() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		activity_.updateItemsDetail(item_,
				activity_.getResources().getString(R.string.count_finished));
		item_.setDetails(activity_.getResources().getString(R.string.count_finished));
	}
}
