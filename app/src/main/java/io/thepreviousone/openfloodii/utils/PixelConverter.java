package io.thepreviousone.openfloodii.utils;

import static io.thepreviousone.openfloodii.activities.BaseActivity.metrics;
import static io.thepreviousone.openfloodii.activities.BaseActivity.screenWidth;

public class PixelConverter {

    public static int dip2px(float dipValue) {
        if (screenWidth >= 3.5f) {
            return (int)  (dipValue * metrics.density * 1.5f);
        }
        return (int) (dipValue * metrics.density + 0.5f);
    }

    public static int sip2px(float sipValue) {
        return (int) (sipValue * metrics.scaledDensity);

    }
}
