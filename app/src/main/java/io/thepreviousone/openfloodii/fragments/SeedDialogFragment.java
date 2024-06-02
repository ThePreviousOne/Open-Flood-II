package io.thepreviousone.openfloodii.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import io.thepreviousone.openfloodii.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Dialog allowing the user to enter a seed to start a new game from.
 */
public class SeedDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.dialog_seed, null);
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(layout);
        dialog = builder.create();

        final EditText seedEditText = layout.findViewById(R.id.seedEditText);
        seedEditText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        seedEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        EventBus.getDefault().post(seedEditText.getText().toString());
                        dialog.dismiss();
                        break;
                }
                return true;
            }
        });

        Button startGameButton = layout.findViewById(R.id.startGameFromSeedButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(seedEditText.getText().toString());
                dialog.dismiss();
            }
        });

        Button cancelButton = layout.findViewById(R.id.cancelStartGameFromSeedButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
