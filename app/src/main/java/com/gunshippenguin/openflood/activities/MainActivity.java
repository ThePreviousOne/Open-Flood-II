package com.gunshippenguin.openflood.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.megatronking.svg.support.SVGDrawable;
import com.github.megatronking.svg.support.extend.SVGImageView;
import com.gunshippenguin.openflood.drawables.*;
import com.gunshippenguin.openflood.utils.PixelConverter;
import com.gunshippenguin.openflood.views.OtherFloodView;

public class MainActivity extends AppCompatActivity {

    OtherFloodView background;
    PixelConverter pixelConverter;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int darkerGrey = 0xEE272727;

        pixelConverter = new PixelConverter(this);
        final RelativeLayout frame = new RelativeLayout(this.getBaseContext());
        final View space = new View(this);
        final RelativeLayout outline = new RelativeLayout(this);
        final SVGImageView play = new SVGImageView(this);
        final SVGImageView endless = new SVGImageView(this);
        final ImageView settings = new ImageView(this);
        final ImageView info = new ImageView(this);
        background = new OtherFloodView(this);
        setContentView(frame);
        space.setId(0x7f992243);

        final AnimatorSet animatorSet = new AnimatorSet();
        final ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(endless, "svgRotation", 0, 360);
        final ObjectAnimator animatorX = ObjectAnimator.ofInt(play, "svgWidth",
                pixelConverter.dip2px(136), pixelConverter.dip2px(128));
        final ObjectAnimator animatorY = ObjectAnimator.ofInt(play, "svgHeight",
                pixelConverter.dip2px(136), pixelConverter.dip2px(128));
        animatorX.setDuration(1750);
        animatorY.setDuration(1750);
        animatorRotation.setDuration(3500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorX.setRepeatCount(ValueAnimator.INFINITE);
        animatorX.setRepeatMode(ValueAnimator.REVERSE);
        animatorY.setRepeatCount(ValueAnimator.INFINITE);
        animatorY.setRepeatMode(ValueAnimator.REVERSE);
        animatorRotation.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.play(animatorX).with(animatorY).with(animatorRotation);
        animatorSet.start();
        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // There is a bug in ImageView(ImageButton), in this case, we must call requestLayout() here.
                play.requestLayout();
            }
        });
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // There is a bug in ImageView(ImageButton), in this case, we must call requestLayout() here.
                play.requestLayout();
            }
        });

        play.setImageDrawable(new SVGDrawable(new Playoutline(this, 124f)));
        play.setColorFilter(darkerGrey);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchPlayIntent = new Intent(MainActivity.this, RegularGameActivity.class);
                startActivity(launchPlayIntent);
            }
        });

        endless.setImageDrawable(new SVGDrawable(new Endless(this, 124f)));
        endless.setColorFilter(darkerGrey);
        endless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchPlayIntent = new Intent(MainActivity.this, EndlessGameActivity.class);
                startActivity(launchPlayIntent);
            }
        });

        info.setImageDrawable(new SVGDrawable(new Info(this)));
        info.setColorFilter(darkerGrey);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchSettingsIntent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(launchSettingsIntent);
            }
        });

        settings.setImageDrawable(new SVGDrawable(new Settings(this)));
        settings.setColorFilter(darkerGrey);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchSettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(launchSettingsIntent);
            }
        });

        RelativeLayout.LayoutParams backgroundLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        backgroundLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        backgroundLP.addRule(RelativeLayout.ALIGN_PARENT_END);
        background.setLayoutParams(backgroundLP);
        background.initBoard();

        RelativeLayout.LayoutParams outlineLP = new RelativeLayout.LayoutParams(
                pixelConverter.dip2px(142), pixelConverter.dip2px(142));
        outlineLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        outlineLP.addRule(RelativeLayout.ABOVE, space.getId());
        outline.setLayoutParams(outlineLP);

        RelativeLayout.LayoutParams playLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        playLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        play.setLayoutParams(playLP);

        RelativeLayout.LayoutParams spaceLP = new RelativeLayout.LayoutParams(
                pixelConverter.dip2px(100), pixelConverter.dip2px(2)
        );
        spaceLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        space.setLayoutParams(spaceLP);

        RelativeLayout.LayoutParams endlessLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        endlessLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        endlessLP.addRule(RelativeLayout.BELOW, space.getId());
        endless.setLayoutParams(endlessLP);

        RelativeLayout.LayoutParams infoLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        infoLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        infoLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        info.setId(143635);
        info.setLayoutParams(infoLP);

        RelativeLayout.LayoutParams settingsLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        settingsLP.addRule(RelativeLayout.START_OF, info.getId());
        settings.setLayoutParams(settingsLP);

        frame.addView(background);
        outline.addView(play);
        frame.addView(space);
        frame.addView(settings);
        frame.addView(info);
        frame.addView(outline);
        frame.addView(endless);
    }

    @Override
    public void onPause() {
        super.onPause();
        background.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        background.initBoard();
    }
}
