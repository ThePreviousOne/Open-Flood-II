package io.thepreviousone.openfloodii.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import io.thepreviousone.openfloodii.utils.PixelConverter;

import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.LayoutParams.*;

/**
 * Make Toast (new Butter)
 *
 * add Ingredients (set...)
 * top with delicious Jam (addJam())
 *
 */
public class Butter extends Toast {

    private Context context;
    private int string;
    private Typeface font;
    private int textSize;
    private int duration;
    private int color;

    public Butter(Context context, @StringRes int string) {
        this(context, string, Toast.LENGTH_SHORT);
    }
    public Butter(Context context, @StringRes int string, int duration) {
        super(context);

        this.context = context;
        this.string = string;
        this.duration = duration;
        this.textSize = PixelConverter.dip2px(10);
        this.color = 0x77FFFAF6;
    }

    public Butter setFont(String font) {
        this.font = Typeface.createFromAsset(context.getAssets(), font);
        return this;
    }

    public Butter setFontSize(float size) {
        textSize = PixelConverter.dip2px(size);
        return this;
    }

    public Butter setBackgroundColor(int color) {
        this.color = color;
        return this;
    }

    public Butter addJam() {
        //Initialize Views
        int boarder = PixelConverter.dip2px(5);
        //RelativeLayout rl= new RelativeLayout(context);
        TextView textView = new TextView(context);
        textView.setPadding( boarder, boarder, boarder, boarder);

        LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        RoundRectShape rect = new RoundRectShape(
                new float[] {20, 20, 20, 20, 20, 20, 20, 20}, null, null);
        ShapeDrawable background = new ShapeDrawable(rect);
        LayoutParams textLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        // Set params
        background.getPaint().setColor(color);
        background.getPaint().setStyle(Paint.Style.FILL);
        //rl.setLayoutParams(lp);
        if (font == null) font = Typeface.createFromAsset(context.getAssets(), "fonts/Yahfie-Heavy.ttf");
        textView.setTypeface(font);
        textView.setTextSize(textSize);
        textView.setBackground(background);
        textView.setLayoutParams(textLayout);
        textView.setText(context.getString(string));
        //rl.addView(textView);
        setDuration(duration);
        setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        setView(textView);
        return this;
    }

}
