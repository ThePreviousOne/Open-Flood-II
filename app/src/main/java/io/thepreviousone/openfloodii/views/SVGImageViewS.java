package io.thepreviousone.openfloodii.views;

/*
 * Copyright (C) 2017, Megatron King
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.github.megatronking.svg.support.R;
import com.github.megatronking.svg.support.SVGDrawable;

/**
 * Support width, height, alpha, tint color for svg images.<br>
 *
 * @author Megatron King
 * @since 2016/10/10 19:11
 */
public class SVGImageViewS extends AppCompatImageView {

    private ColorStateList mSvgColor;
    private float mSvgAlpha;
    private int mSvgWidth;
    private int mSvgHeight;
    private float mSvgRotation;
    private int mSvgScale;

    public SVGImageViewS(Context context) {
        this(context, null);
    }

    public SVGImageViewS(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVGImageViewS(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SVGView);
        mSvgColor = a.getColorStateList(R.styleable.SVGView_svgColor);
        mSvgAlpha = a.getFloat(R.styleable.SVGView_svgAlpha, 1.0f);
        mSvgWidth = a.getDimensionPixelSize(R.styleable.SVGView_svgWidth, -1);
        mSvgHeight = a.getDimensionPixelSize(R.styleable.SVGView_svgHeight, -1);
        mSvgRotation = a.getFloat(R.styleable.SVGView_svgRotation, 0) % 360;
        a.recycle();
        resetImageDrawable();
    }

    public void setSvgColor(ColorStateList svgColor) {
        this.mSvgColor = svgColor;
        resetImageDrawable();
    }

    public void setSvgColor(int color) {
        setSvgColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getSvgColor() {
        return mSvgColor;
    }

    public void setSvgWidth(int width) {
        this.mSvgWidth = width;
        resetImageDrawable();
    }

    public final int getSvgScale() {
        return this.mSvgScale;
    }

    public final void setSvgScale(int scale) {
        this.mSvgWidth = scale;
        this.mSvgHeight = scale;
        this.mSvgScale = scale;
        this.resetImageDrawable();
    }

    public int getSvgWidth() {
        return mSvgWidth;
    }

    public void setSvgHeight(int height) {
        this.mSvgHeight = height;
        resetImageDrawable();
    }

    public int getSvgHeight() {
        return mSvgHeight;
    }

    public void setSvgSize(int width, int height) {
        this.mSvgWidth = width;
        this.mSvgHeight = height;
        resetImageDrawable();
    }

    public void setSvgAlpha(float alpha) {
        this.mSvgAlpha = alpha;
        resetImageDrawable();
    }

    public float getSvgAlpha() {
        return mSvgAlpha;
    }

    public void setSvgRotation(float rotation) {
        this.mSvgRotation = rotation;
        resetImageDrawable();
    }

    public float getSvgRotation() {
        return mSvgRotation;
    }

    private void resetImageDrawable() {
        Drawable drawable = getDrawable();
        boolean isNeedReset = drawable != null && (drawable.getIntrinsicWidth() != mSvgWidth
                || drawable.getIntrinsicHeight() != mSvgHeight);
        resetDrawable(drawable);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && isNeedReset) {
            super.setImageDrawable(null);
            super.setImageDrawable(drawable);
        } else {
            invalidate();
        }
    }

    private void resetDrawable(Drawable drawable) {
        if (drawable instanceof SVGDrawable) {
            drawable.mutate();
            drawable.setTintList(mSvgColor);
            if (mSvgAlpha > 0 && mSvgAlpha <= 1.0f) {
                drawable.setAlpha((int) (mSvgAlpha * 0xFF));
            }
            if (mSvgWidth > 0) {
                ((SVGDrawable)drawable).setWidth(mSvgWidth);
            }
            if (mSvgHeight > 0) {
                ((SVGDrawable)drawable).setHeight(mSvgHeight);
            }
            if (mSvgRotation != 0) {
                ((SVGDrawable)drawable).setRotation(mSvgRotation);
            }
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        resetImageDrawable();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        resetImageDrawable();
    }
}
