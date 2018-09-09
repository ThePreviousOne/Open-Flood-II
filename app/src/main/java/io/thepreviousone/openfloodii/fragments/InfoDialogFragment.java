package io.thepreviousone.openfloodii.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.thepreviousone.openfloodii.R;

import org.greenrobot.eventbus.EventBus;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.thepreviousone.openfloodii.activities.MainActivity;

/**
 * Activity displaying information about the application.
 */
public class InfoDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_info, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        // Set up the first line of the info
        TextView versionTextView = layout.findViewById(R.id.infoVersionTextView);
        String appName = getResources().getString(R.string.app_name);

        PackageInfo pInfo;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            versionTextView.setText(String.format("%s %s", appName, pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            versionTextView.setText(appName);
        }

        // Set up the source link
        TextView sourceTextView = layout.findViewById(R.id.infoSourceTextView);
        sourceTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Set up the back button
        Button backButton = layout.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).animationResume();
    }
}
