package io.thepreviousone.openfloodii.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    public static DisplayMetrics metrics = new DisplayMetrics();
    public static float screenWidth;


    @Override
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        screenWidth = metrics.widthPixels / metrics.densityDpi;
    }
}
