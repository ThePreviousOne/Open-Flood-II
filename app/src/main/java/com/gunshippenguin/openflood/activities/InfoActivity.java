package com.gunshippenguin.openflood.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gunshippenguin.openflood.R;
import com.gunshippenguin.openflood.views.Butter;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Activity displaying information about the application.
 */
public class InfoActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Set up the first line of the info
        TextView versionTextView = findViewById(R.id.infoVersionTextView);
        String appName = getResources().getString(R.string.app_name);

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            versionTextView.setText(appName + " " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versionTextView.setText(appName);
        }

        // Set up the source link
        TextView sourceTextView = findViewById(R.id.infoSourceTextView);
        sourceTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Set up the back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
