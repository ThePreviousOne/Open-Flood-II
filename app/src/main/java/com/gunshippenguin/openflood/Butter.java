package com.gunshippenguin.openflood;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.RelativeLayout.LayoutParams;
import static android.widget.RelativeLayout.LayoutParams.*;

public class Butter extends Toast {
    public Butter(Context context, int resource) {
        super(context);

        //Initialize View
        RelativeLayout rl= new RelativeLayout(context);
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, MATCH_PARENT);
        LayoutParams textLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        TextView textView = new TextView(context);
        RoundRectShape rect = new RoundRectShape(
                new float[] {20, 20, 20, 20, 20, 20, 20, 20}, null, null);
        ShapeDrawable background = new ShapeDrawable(rect);

        // Set params
        background.getPaint().setColor(0xFFFFFAEF);
        background.getPaint().setStyle(Paint.Style.FILL);
        rl.setLayoutParams(lp);
        textView.setPadding(10, 0,10, 0);
        textView.setTextSize(24);
        textView.setBackground(background);
        textView.setLayoutParams(textLayout);
        textView.setText(context.getString(resource));
        rl.addView(textView);
        setDuration(Toast.LENGTH_SHORT);
        setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        setView(rl);
    }

}
