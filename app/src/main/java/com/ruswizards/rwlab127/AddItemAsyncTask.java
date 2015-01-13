/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 12.01.2015
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * AsyncTask for item adding
 */
class AddItemAsyncTask extends AsyncTask<Void, Integer, Void> {
	public static int activeCount = 0;
	private static int itemId_ = 0;
	private static MainActivity activity_;
	private CustomViewForList item_;

	AddItemAsyncTask(MainActivity mainActivity) {
		activity_ = mainActivity;
	}

	/**
	 * Links to MainActivity. Used when config changes
	 */
	public static void linkToActivity(MainActivity mainActivity) {
		activity_ = mainActivity;
	}

	/**
	 * Unlinks from MainActivity. Used when config changes
	 */
	public static void unlinkFromActivity() {
		activity_ = null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		increaseCount();
		// Create and add new item for list
		int position = 0;
		item_ = new CustomViewForList(
				activity_,
				"Async# " + String.valueOf(itemId_),
				"Countdown..",
				new Random().nextInt(1000) / 250
		);
		itemId_++;
		activity_.addItem(position, item_);
		TextView asyncButton = (TextView)activity_.findViewById(R.id.asynctask_floating_button);
		activity_.disableButton(asyncButton);
	}

	private synchronized void increaseCount() {
		activeCount ++;
	}

	private synchronized void decreaseCount() {
		activeCount --;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		// Update created items' details while counting
		activity_.updateItemsDetail(item_, String.valueOf(values[0]));
		item_.setDetails(String.valueOf(values[0]));
	}

	@Override
	protected Void doInBackground(Void... params) {
		// Do some hard work and periodically publish progress
		RunnableForThreads someLongTimeAction = new RunnableForThreads();
		for (int i = 1; i <= 10; i++) {
			someLongTimeAction.run();
			publishProgress(i);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		// Change items' details when finished
		activity_.updateItemsDetail(item_,
				activity_.getResources().getString(R.string.count_finished));
		item_.setDetails(activity_.getResources().getString(R.string.count_finished));
		TextView asyncButton = (TextView)activity_.findViewById(R.id.asynctask_floating_button);
		activity_.enableButton(asyncButton);
		decreaseCount();
	}

	/**
	 * Runnable for hard work
	 */
	public static class RunnableForThreads implements Runnable {
		@Override
		public void run() {
			// Just sleep for a second
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
