package io.thepreviousone.openfloodii.drawables;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

import com.github.megatronking.svg.support.SVGRenderer;

import io.thepreviousone.openfloodii.utils.PixelConverter;

public class Undo extends SVGRenderer {

    public Undo(Context context, float size) {
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

        mPath.moveTo(12.5f, 8.0f);
        mPath.rCubicTo(-2.65f, 0.0f, -5.05f, 0.99f, -6.9f, 2.6f);
        mPath.lineTo(2.0f, 7.0f);
        mPath.rLineTo(0f, 9.0f);
        mPath.rLineTo(9.0f, 0f);
        mPath.rLineTo(-3.62f, -3.62f);
        mPath.rCubicTo(1.39f, -1.16f, 3.16f, -1.88f, 5.12f, -1.88f);
        mPath.rCubicTo(3.54f, 0.0f, 6.55f, 2.31f, 7.6f, 5.5f);
        mPath.rLineTo(2.37f, -0.78f);
        mPath.cubicTo(21.08f, 11.03f, 17.15f, 8.0f, 12.5f, 8.0f);
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