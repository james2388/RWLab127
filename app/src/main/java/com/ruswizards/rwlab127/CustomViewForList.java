/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 16.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
	private int iconResource_;
	private Drawable iconDrawable_;
	private int targetSdkVersion_ = 0;

	/**
	 * Calls super class' constructor and inflates layout for views
	 */
	public CustomViewForList(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.custom_view_for_list, this);
	}

	/**
	 * Calls {@link #CustomViewForList(android.content.Context, android.util.AttributeSet, int)},
	 * saves {@link #title_}, {@link #details_} values and fills title and details views in a view
	 * holder
	 */
	public CustomViewForList(Context context, String title, String details) {
		this(context, null, 0);
		title_ = title;
		TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
		titleTextView.setText(title);

		details_ = details;
		TextView detailsTextView = (TextView) findViewById(R.id.details_text_view);
		detailsTextView.setText(details);
	}

	/**
	 * Calls {@link #CustomViewForList(android.content.Context, String, String)}, saves
	 * {@link #iconResource_} value and fills in icon view
	 */
	public CustomViewForList(Context context, String title, String details, int imageNumber) {
		this(context, title, details);
		ImageView iconImageView = (ImageView) findViewById(R.id.iconImageView);
		switch (imageNumber) {
			case 0:
				iconResource_ = android.R.drawable.btn_star_big_on;
				break;
			case 1:
				iconResource_ = R.drawable.bg_images_sprite;
				break;
			case 2:
				iconResource_ = R.drawable.android_wrench;
				break;
			case 3:
				iconResource_ = android.R.drawable.ic_dialog_alert;
				break;
			default:
				iconResource_ = android.R.drawable.btn_star_big_on;
				break;
		}
		iconImageView.setImageResource(iconResource_);
	}

	/**
	 * Calls {@link #CustomViewForList(android.content.Context, String, String)}, saves
	 * {@link #iconResource_} value and fills in icon view
	 */
	public CustomViewForList(Context context, String title, String details, Drawable icon) {
		this(context, title, details);
		iconDrawable_ = icon;
		ImageView iconImageView = (ImageView) findViewById(R.id.iconImageView);
		iconImageView.setImageDrawable(iconDrawable_);
	}

	public CustomViewForList(Context context, String title, String details, Drawable icon
			, int targetSdkVersion) {
		this(context, title, details, icon);
		targetSdkVersion_ = targetSdkVersion;
	}

	/**
	 * Gets a target SDK version
	 *
	 * @return Target SDK version code
	 */
	public int getTargetSdk() {
		return targetSdkVersion_;
	}

	/**
	 * Gets a title field content
	 *
	 * @return Title
	 */
	public String getTitle() {
		return title_;
	}

	/**
	 * Gets a details field content
	 *
	 * @return Details
	 */
	public String getDetails() {
		return details_;
	}

	/**
	 * Gets an icon's resource id
	 *
	 * @return Icon's resource id
	 */
	public int getIconResource() {
		return iconResource_;
	}

	/**
	 * Gets an icon's drawable
	 *
	 * @return Icon's drawable
	 */
	public Drawable getIconDrawable() {
		return iconDrawable_;
	}

	/**
	 * Check what kind of source is used for icon
	 *
	 * @return True if used Drawable and false otherwise
	 */
	public boolean isIconDrawable() {
		return iconDrawable_ != null;
	}

	/**
	 * Compares all data of an instance object with comparison item data
	 *
	 * @param comparison Object to compare with
	 * @return True if objects are equals, false otherwise
	 */
	public boolean isEqual(CustomViewForList comparison) {
		return (title_.equals(comparison.title_) && details_.equals(comparison.details_)
				&& iconResource_ == comparison.iconResource_);
	}
}
