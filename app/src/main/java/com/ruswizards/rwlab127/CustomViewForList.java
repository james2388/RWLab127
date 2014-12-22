/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 16.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Class for custom view of an RecyclerView's items
 */
public class CustomViewForList extends LinearLayout implements Serializable {

	private String title_;
	private String details_;
	private int imageViewResource_;

	public CustomViewForList(Context context) {
		this(context, null);
	}

	public CustomViewForList(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Calls super class' constructor and inflates layout for views
	 */
	public CustomViewForList(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.custom_view_for_list, this);
	}


	/**
	 * Calls {@link #CustomViewForList(android.content.Context, android.util.AttributeSet)}, saves
	 * {@link #title_}, {@link #details_}, {@link #imageViewResource_} values and fills views in
	 * a view holder
	 */
	public CustomViewForList(Context context, String title, String details, int imageNum) {
		this(context, null);
		title_ = title;
		TextView textViewTitle = (TextView) findViewById(R.id.text_view_list_title);
		textViewTitle.setText(title);

		details_ = details;
		TextView textViewDetails = (TextView) findViewById(R.id.text_view_list_details);
		textViewDetails.setText(details);

		ImageView imageView = (ImageView) findViewById(R.id.imagePicture);
		switch (imageNum) {
			case 0:
				imageViewResource_ = android.R.drawable.btn_star_big_on;
				break;
			case 1:
				imageViewResource_ = R.drawable.bg_images_sprite;
				break;
			case 2:
				imageViewResource_ = R.drawable.android_wrench;
				break;
			case 3:
				imageViewResource_ = android.R.drawable.ic_dialog_alert;
				break;
			default:
				imageViewResource_ = android.R.drawable.btn_star_big_on;
				break;
		}
		imageView.setImageResource(imageViewResource_);
	}

	/**
	 * Gets a title field
	 * @return Title
	 */
	public String getTitle() {
		return title_;
	}

	/**
	 * Gets a details field
	 * @return Details
	 */
	public String getDetails() {
		return details_;
	}

	/**
	 * Gets an image's resource number
	 * @return Image's resource number
	 */
	public int getImageResource() {
		return imageViewResource_;
	}
}
