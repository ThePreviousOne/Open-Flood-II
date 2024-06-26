package io.thepreviousone.openfloodii.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.github.megatronking.svg.support.SVGDrawable;

import io.thepreviousone.openfloodii.R;
import io.thepreviousone.openfloodii.drawables.*;
import io.thepreviousone.openfloodii.utils.PixelConverter;
import io.thepreviousone.openfloodii.fragments.InfoDialogFragment;
import io.thepreviousone.openfloodii.views.OtherFloodView;
import io.thepreviousone.openfloodii.fragments.SettingsDialogFragment;
import io.thepreviousone.openfloodii.views.SVGImageViewS;

public class MainActivity extends BaseActivity {

    private OtherFloodView background;
    private ObjectAnimator animatorRotation, animatorScale;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FrameLayout outline = findViewById(R.id.outline);
        final View space = findViewById(R.id.space);
        final RelativeLayout frame = findViewById(R.id.mainLayout);
        final SVGImageViewS play = findViewById(R.id.regularPlayButton);
        final SVGImageViewS endless = findViewById(R.id.endlessPlayButton);
        final ImageView settings = findViewById(R.id.settingsButton);
        final ImageView info = findViewById(R.id.infoButton);
        background = new OtherFloodView(this);

        setRotationAnimator(endless);
        setScaleAnimator(play);

        play.setImageDrawable(new SVGDrawable(new Playoutline(this, 124f)));
        setupButton(play, 0);

        endless.setImageDrawable(new SVGDrawable(new Endless(this, 124f)));
        setupButton(endless, 1);

        info.setImageDrawable(new SVGDrawable(new Info(this)));
        info.setColorFilter(darkerGrey);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationPause();
                new InfoDialogFragment().show(getSupportFragmentManager(), "InfoDialog");
            }
        });

        settings.setImageDrawable(new SVGDrawable(new Settings(this)));
        settings.setColorFilter(darkerGrey);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationPause();
                new SettingsDialogFragment().show(getSupportFragmentManager(), "SettingsDialog");
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

        frame.removeAllViews();
        outline.removeAllViews();
        frame.addView(background);
        outline.addView(play);
        frame.addView(space);
        frame.addView(settings);
        frame.addView(info);
        frame.addView(outline);
        frame.addView(endless);
    }

    private void setupButton(SVGImageViewS view, final int mode) {
        view.setColorFilter(darkerGrey);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putInt("sp_gameMode", mode);
                editor.apply();
                finish();
                Intent launchPlayIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(launchPlayIntent);
            }
        });
    }

    private void setScaleAnimator(final SVGImageViewS view) {
        animatorScale = ObjectAnimator.ofInt(view, "svgScale",
                PixelConverter.dip2px(136), PixelConverter.dip2px(128));
        animatorScale.setDuration(1750);
        animatorScale.setRepeatCount(ValueAnimator.INFINITE);
        animatorScale.setRepeatMode(ValueAnimator.REVERSE);
        animatorScale.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorScale.start();
        animatorScale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.requestLayout();
            }
        });
        view.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
                view.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        });

    }

    private void setRotationAnimator(final SVGImageViewS view) {
        animatorRotation = ObjectAnimator.ofFloat(view, "svgRotation", 0, 360);
        animatorRotation.setDuration(3500);
        animatorRotation.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorRotation.setRepeatCount(ValueAnimator.INFINITE);
        animatorRotation.start();
        view.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
                view.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        });
    }

    private void animationPause() {
        animatorRotation.pause();
        animatorScale.pause();
    }

    public void animationResume() {
        animatorRotation.resume();
        animatorScale.resume();
    }

    public void redraw() {
        background.invalidate();
        background.initBoard();
    }
}
