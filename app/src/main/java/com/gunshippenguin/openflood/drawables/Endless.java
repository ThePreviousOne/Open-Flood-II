package com.gunshippenguin.openflood.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.megatronking.svg.support.SVGRenderer;


public class Endless extends SVGRenderer {

    public Endless(Context context, float size) {
        super(context);
        mAlpha = 1.0f;
        mWidth = dip2px(size);
        mHeight = dip2px(size);
    }

    @Override
    public void render(Canvas canvas, int w, int h, ColorFilter filter) {
        final float scaleX = w / 24f;
        final float scaleY = h / 24f;

        mPath.reset();
        mRenderPath.reset();

        mFinalPathMatrix.setValues(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f});
        mFinalPathMatrix.postScale(scaleX, scaleY);

        mPath.addCircle(12, 12, 8f, Path.Direction.CW);
        mPath.addCircle(12, 12, 6f, Path.Direction.CCW);

        mRenderPath.addPath(mPath, mFinalPathMatrix);
        if (mFillPaint == null) {
            mFillPaint = new Paint();
            mFillPaint.setStyle(Paint.Style.FILL);
            mFillPaint.setAntiAlias(true);
        }
        mFillPaint.setColor(applyAlpha(-16777216, 1.0f));
        mFillPaint.setColorFilter(filter);
        canvas.drawPath(mRenderPath, mFillPaint);
        mPath.reset();
        mRenderPath.reset();

        mFinalPathMatrix.setValues(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f});
        mFinalPathMatrix.postScale(scaleX, scaleY);

        mPath.moveTo(3.7f, 3.9f);
        mPath.rLineTo(6.8f, 0.7f);
        mPath.rLineTo(-0.7f, 7f);
        mPath.close();

        mRenderPath.addPath(mPath, mFinalPathMatrix);
        if (mFillPaint == null) {
            mFillPaint = new Paint();
            mFillPaint.setStyle(Paint.Style.FILL);
            mFillPaint.setAntiAlias(true);
        }
        mFillPaint.setColor(applyAlpha(-16777216, 1.0f));
        mFillPaint.setColorFilter(filter);
        canvas.drawPath(mRenderPath, mFillPaint);
    }

}