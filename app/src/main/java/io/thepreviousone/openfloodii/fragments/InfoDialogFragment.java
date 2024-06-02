package io.thepreviousone.openfloodii.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import io.thepreviousone.openfloodii.R;

import io.thepreviousone.openfloodii.activities.MainActivity;

/**
 * Activity displaying information about the application.
 */
public class InfoDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
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
            pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            versionTextView.setText(String.format(pInfo.versionName));
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
        ((MainActivity) requireActivity()).animationResume();
    }
}
