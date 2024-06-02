package io.thepreviousone.openfloodii.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

import com.github.megatronking.svg.support.SVGRenderer;

import io.thepreviousone.openfloodii.utils.PixelConverter;

public class Back extends SVGRenderer {

    public Back(Context context) {
        super(context);
        mAlpha = 1.0f;
        mWidth = PixelConverter.dip2px(32.0f);
        mHeight = PixelConverter.dip2px(32.0f);
    }

    @Override
    public void render(Canvas canvas, int w, int h, ColorFilter filter) {
        final float scaleX = w / 18.0f;
        final float scaleY = h / 18.0f;

        mPath.reset();
        mRenderPath.reset();

        mFinalPathMatrix.setValues(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f});
        mFinalPathMatrix.postScale(scaleX, scaleY);

        mPath.moveTo(15.41f, 7.41f);
        mPath.lineTo(14.0f, 6.0f);
        mPath.rLineTo(-6.0f, 6.0f);
        mPath.rLineTo(6.0f, 6.0f);
        mPath.rLineTo(1.41f, -1.41f);
        mPath.lineTo(10.83f, 12.0f);
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
