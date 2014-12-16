/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 16.12.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Custom view with additional attribute onSpecialClick which same as onClick for other views
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

    /**
     * Checks if custom layouts' attributes have onSpecialClick. If onSpecialClick is there,
     * registers OnClickListener with action to start method in context activity. Method name must
     * be same as to content of onSpecialClick attribute.
     * @param context Contex
     * @param attrs AttributeSet
     * @param defStyleAttr Reference to a style resource that supplies defaults values
     */
    private void mapSpecialActionToMainActivity(
            Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.SpecialLinearLayout, defStyleAttr, 0);
        final int N = typedArray.getIndexCount();
        // Check attributes if the have onSpecialClick one
        for (int i = 0; i < N; i++){
            int atr = typedArray.getIndex(i);
            switch (atr){
                case R.styleable.SpecialLinearLayout_onSpecialClick:
//                    Check if context is restricted
                    if (context.isRestricted()){
                        throw new IllegalStateException("The android:onSpecialClick attribute" +
                                " cannot be used within a restricted context");
                    }
                    final String handlerName = typedArray.getString(atr);
//                    Set up OnClickListener if there is a method name inside onSpecialClick attribute
                    if (handlerName != null){
                        setOnClickListener(new OnClickListener() {
                            private Method handler_;
                            @Override
                            public void onClick(View v) {
                                if (handler_ == null){
//                                    Try to find method in context activity
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
//                                Try to call method from context activity
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
