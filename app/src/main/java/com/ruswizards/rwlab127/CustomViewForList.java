package com.ruswizards.rwlab127;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 16.12.2014
 * Vladimir Farafonov
 */
public class CustomViewForList extends LinearLayout {

    private TextView textViewTitle_;
    private TextView textViewDetails_;
    private ImageView imageView_;

    public CustomViewForList(Context context) {
        this(context, null);
    }

    public CustomViewForList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewForList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.custom_view_for_list, this);
        textViewTitle_ = (TextView)findViewById(R.id.text_view_list_title);
        textViewDetails_ = (TextView)findViewById(R.id.text_view_list_details);
        imageView_ = (ImageView)findViewById(R.id.imagePicture);
    }

    public void customViewSetTitle(String title){
        textViewTitle_.setText(title);
    }

    public void customViewSetDetails(String details){
        textViewDetails_.setText(details);
    }
}
