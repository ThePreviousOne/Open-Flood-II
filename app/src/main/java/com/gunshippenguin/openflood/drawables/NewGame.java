package com.gunshippenguin.openflood.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

import com.github.megatronking.svg.support.SVGRenderer;

public class NewGame extends SVGRenderer {

    public NewGame(Context context, float size) {
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

        mPath.moveTo(4f, 20f);
        mPath.rLineTo(11f, -8f);
        mPath.lineTo(4f, 4f);
        mPath.rMoveTo(0f, 16f);
        mPath.close();
        mPath.moveTo(20f, 4f);
        mPath.rLineTo(0f, 16f);
        mPath.rLineTo(-3f, 0f);
        mPath.rLineTo(0f, -16f);
        mPath.rLineTo(3f, 0f);
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