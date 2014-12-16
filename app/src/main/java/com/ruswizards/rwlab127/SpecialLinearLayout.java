package com.ruswizards.rwlab127;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 16.12.2014
 * Vladimir Farafonov
 */
public class SpecialLinearLayout extends LinearLayout {

    public SpecialLinearLayout(Context context) {
        this(context, null);
    }

    public SpecialLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mapSpecialActionToMainActivity(context, attrs, defStyleAttr);
    }

    private void mapSpecialActionToMainActivity(
            Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.SpecialLinearLayout, defStyleAttr, 0);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++){
            int atr = typedArray.getIndex(i);
            switch (atr){
                case R.styleable.SpecialLinearLayout_onSpecialClick:
                    if (context.isRestricted()){
                        throw new IllegalStateException("The android:onSpecialClick attribute" +
                                " cannot be used within a restricted context");
                    }
                    final String handlerName = typedArray.getString(atr);
                    if (handlerName != null){
                        setOnClickListener(new OnClickListener() {
                            private Method handler_;
                            @Override
                            public void onClick(View v) {
                                if (handler_ == null){
                                    try {
                                        handler_ = getContext().getClass().getMethod(handlerName,
                                                View.class);
                                    } catch (NoSuchMethodException e) {
                                        throw new IllegalStateException("Could not find a method " +
                                                handlerName + "(View) in the activity " +
                                                getContext().getClass() + " for onClick handler " +
                                                "on View" + SpecialLinearLayout.this.getClass(), e);
                                    }
                                }
                                try {
                                    handler_.invoke(getContext(), SpecialLinearLayout.this);
                                } catch (IllegalAccessException e){
                                    throw new IllegalStateException("Could not executive non " +
                                            "public method of the activity", e);
                                } catch (InvocationTargetException e){
                                    throw new IllegalStateException("Could not execute method of" +
                                            " the activity", e);
                                }
                            }
                        });
                    }
                    break;
            }
            typedArray.recycle();
        }
    }
}
