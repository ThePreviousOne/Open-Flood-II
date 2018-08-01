package com.gunshippenguin.openflood.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.megatronking.svg.support.SVGDrawable;
import com.gunshippenguin.openflood.drawables.*;
import com.gunshippenguin.openflood.views.OtherFloodView;

public class MainActivity extends AppCompatActivity {

    OtherFloodView background;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout frame = new RelativeLayout(this.getBaseContext());
        ImageView play = new ImageView(this);
        ImageView settings = new ImageView(this);
        ImageView info = new ImageView(this);
        background = new OtherFloodView(this);
        setContentView(frame);

        RelativeLayout.LayoutParams backgroundLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        backgroundLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        backgroundLP.addRule(RelativeLayout.ALIGN_PARENT_END);
        background.setLayoutParams(backgroundLP);
        background.initBoard();

        play.setImageDrawable(new SVGDrawable(new Playoutline(this, 128f)));
        play.setColorFilter(0xFF272727);    //Darker Grey
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchPlayIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(launchPlayIntent);
            }
        });

        info.setImageDrawable(new SVGDrawable(new Info(this)));
        info.setColorFilter(0xFF272727);    //Darker Grey
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchSettingsIntent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(launchSettingsIntent);
            }
        });

        settings.setImageDrawable(new SVGDrawable(new Settings(this)));
        settings.setColorFilter(0xFF272727);    //Darker Grey
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchSettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(launchSettingsIntent);
            }
        });

        RelativeLayout.LayoutParams playLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        playLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        play.setLayoutParams(playLP);

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
        settingsLP.addRule(RelativeLayout.LEFT_OF, info.getId());
        settings.setLayoutParams(settingsLP);

        frame.addView(background);
        frame.addView(settings);
        frame.addView(info);
        frame.addView(play);
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
