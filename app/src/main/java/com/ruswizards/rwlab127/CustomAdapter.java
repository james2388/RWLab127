package com.ruswizards.rwlab127;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 16.12.2014
 * Vladimir Farafonov
 */
public class CustomAdapter extends BaseAdapter {

    private Activity activity_;
    private List<CustomViewForList> items_;
    private LayoutInflater layoutInflater_;

    CustomAdapter(Activity activity, List<CustomViewForList> items){
        activity_ = activity;
        items_ = items;
    }

    @Override
    public int getCount() {
        return items_.size();
    }

    @Override
    public Object getItem(int position) {
        return items_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater_ == null){
            layoutInflater_ = LayoutInflater.from(activity_);
        }
        if (convertView == null){
            convertView = layoutInflater_.inflate(R.layout.custom_view_for_list, parent, false);
        }
        CustomViewForList chooseItem = items_.get(position);

        TextView textView = (TextView)convertView.findViewById(R.id.text_view_list_title);
        textView.setText(chooseItem.getTitle());
        textView = (TextView)convertView.findViewById(R.id.text_view_list_details);
        textView.setText(chooseItem.getDetails());
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imagePicture);
        imageView.setImageResource(chooseItem.getImageResource());

        return convertView;
    }
}
