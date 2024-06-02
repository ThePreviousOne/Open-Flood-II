package io.thepreviousone.openfloodii.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public static DisplayMetrics metrics = new DisplayMetrics();
    public static float screenWidth;
    protected int darkerGrey = 0xFF272727;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        screenWidth = (float) metrics.widthPixels / metrics.densityDpi;
    }
}
