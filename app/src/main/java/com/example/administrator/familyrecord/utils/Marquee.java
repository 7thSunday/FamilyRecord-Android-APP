package com.example.administrator.familyrecord.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class Marquee extends android.support.v7.widget.AppCompatTextView {
    public Marquee(Context con) {
        super(con);
    }

    public Marquee(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Marquee(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean isFocused() {
        return true;
    }

}