package com.ruswizards.rwlab127;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 13.01.2015
 * Vladimir Farafonov
 */
public class AddItemThread extends Thread {
	public static final int STATUS_STARTED = 0;
	public static final int STATUS_MODIFY = 1;
	public static final int STATUS_FINISHED = 2;

	private Handler handlerUiThread_;
	private Activity activity_;

	private static int itemId_ = 0;

	AddItemThread(Handler handler, Activity activity){
		handlerUiThread_ = handler;
		activity_ = activity;
	}

	@Override
	public void run() {
		// Add item to list when thread started
		CustomViewForList newItem_ = new CustomViewForList(activity_,
				"Thread# " + String.valueOf(itemId_),
				"Countdown..",
				new Random().nextInt(1000) / 250);
		itemId_ ++;
		Message message = handlerUiThread_.obtainMessage(STATUS_STARTED, 0, 0, newItem_);
		handlerUiThread_.sendMessage(message);
		// Perform long time actions
		for (int i = 1; i <= 10; i++) {
			//TODO: change to someMethodForThreads();
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			message = handlerUiThread_.obtainMessage(STATUS_MODIFY, i, 0, newItem_);
			handlerUiThread_.sendMessage(message);
			newItem_.setDetails(String.valueOf(i));
		}
		// Send finish message
		message = handlerUiThread_.obtainMessage(STATUS_FINISHED, 0, 0, newItem_);
		handlerUiThread_.sendMessage(message);
		newItem_.setDetails(activity_.getResources().getString(R.string.count_finished));
	}
}
