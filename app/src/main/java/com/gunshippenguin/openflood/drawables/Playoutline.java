package com.gunshippenguin.openflood.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.megatronking.svg.support.SVGRenderer;

public class Playoutline extends SVGRenderer {

    public Playoutline(Context context, float size) {
        super(context);
        mAlpha = 1.0f;
        mWidth = dip2px(size);
        mHeight = dip2px(size);
    }

    @Override
    public void render(Canvas canvas, int w, int h, ColorFilter filter) {
        final float scaleX = w / 24.0f;
        final float scaleY = h / 24.0f;

        mPath.reset();
        mRenderPath.reset();

        mFinalPathMatrix.setValues(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f});
        mFinalPathMatrix.postScale(scaleX, scaleY);

        mPath.addCircle(12, 12, 10.21f, Path.Direction.CW);
        mPath.addCircle(12, 12, 8.1f, Path.Direction.CCW);
        mPath.moveTo(10.0f, 16.5f);
        mPath.rLineTo(6.0f, -4.5f);
        mPath.rLineTo(-6.0f, -4.5f);
        mPath.rLineTo(0f, 9.0f);
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