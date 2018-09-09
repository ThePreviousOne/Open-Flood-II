package io.thepreviousone.openfloodii.utils;

import io.thepreviousone.openfloodii.OpenFloodApplication;

public class PixelConverter {

    public static int dip2px(float dipValue) {
        return (int) (dipValue * OpenFloodApplication.matrics.density + 0.5f);
    }

    public static int sip2px(float sipValue) {
        return (int) (sipValue * OpenFloodApplication.matrics.scaledDensity);

    }
}
