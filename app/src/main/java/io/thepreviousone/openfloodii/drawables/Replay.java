package io.thepreviousone.openfloodii.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

import com.github.megatronking.svg.support.SVGRenderer;

import io.thepreviousone.openfloodii.utils.PixelConverter;

public class Replay extends SVGRenderer {

    public Replay(Context context, float size) {
        super(context);
        mAlpha = 1.0f;
        mWidth = PixelConverter.dip2px(size);
        mHeight = PixelConverter.dip2px(size);
    }

    @Override
    public void render(Canvas canvas, int w, int h, ColorFilter filter) {
        final float scaleX = w / 24.0f;
        final float scaleY = h / 24.0f;

        mPath.reset();
        mRenderPath.reset();

        mFinalPathMatrix.setValues(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f});
        mFinalPathMatrix.postScale(scaleX, scaleY);

        mPath.moveTo(6, 6);
        mPath.rLineTo(2, 0);
        mPath.rLineTo(0, 12);
        mPath.rLineTo(-2, 0);
        mPath.close();
        mPath.moveTo(9.5f, 12);
        mPath.rLineTo(8.5f, 6);
        mPath.lineTo(18, 6);
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