package com.gunshippenguin.openflood.utils;

import android.content.Context;

public class PixelConverter {

    private Context context;

    public PixelConverter(Context context) {
        this.context = context;
    }

    public int dip2px(float dipValue) {
        return (int) (dipValue * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public int sip2px(float sipValue) {
        return (int) (sipValue * context.getResources().getDisplayMetrics().scaledDensity);

    }
}
