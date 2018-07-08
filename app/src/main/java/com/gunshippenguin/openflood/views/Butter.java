package com.gunshippenguin.openflood.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.RelativeLayout.LayoutParams;
import static android.widget.RelativeLayout.LayoutParams.*;

/**
 * Make Toast (new Butter)
 *
 * add Ingredients (set...)
 * top with delicious Jam (addJam())
 *
 */
public class Butter extends Toast {

    private Context context;
    private int resource;
    private Typeface font;

    private int duration = Toast.LENGTH_SHORT;
    private int textSize = 18;

    public Butter(Context context, @StringRes int resource) {
        super(context);

        this.context = context;
        this.resource = resource;
    }

    public Butter setFont(String font) {
        this.font = Typeface.createFromAsset(context.getAssets(), font);
        return this;
    }

    public Butter setFontSize(int size) {
        textSize = size;
        return this;
    }

    public Butter setButteredToastDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public Butter addJam() {
        //Initialize Views
        RelativeLayout rl= new RelativeLayout(context);
        TextView textView = new TextView(context);

        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, MATCH_PARENT);
        RoundRectShape rect = new RoundRectShape(
                new float[] {20, 20, 20, 20, 20, 20, 20, 20}, null, null);
        ShapeDrawable background = new ShapeDrawable(rect);
        LayoutParams textLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        // Set params
        background.getPaint().setColor(0xFFFFFAF6);
        background.getPaint().setStyle(Paint.Style.FILL);
        rl.setLayoutParams(lp);
        textView.setPadding(10, 0,10, 0);
        if (font == null) font = Typeface.createFromAsset(context.getAssets(), "fonts/PierceRoman.otf");
        textView.setTypeface(font);
        textView.setTextSize(textSize);
        textView.setBackground(background);
        textView.setLayoutParams(textLayout);
        textView.setText(context.getString(resource));
        rl.addView(textView);
        setDuration(duration);
        setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        setView(rl);
        return this;
    }
}
