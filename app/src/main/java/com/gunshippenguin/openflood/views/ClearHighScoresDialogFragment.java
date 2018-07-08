package com.gunshippenguin.openflood.views;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gunshippenguin.openflood.HighScoreManager;
import com.gunshippenguin.openflood.R;
import com.gunshippenguin.openflood.views.Butter;

public class ClearHighScoresDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_highscores_clear, null);
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        dialog = builder.create();

        Button confirmButton = dialogView.findViewById(R.id.confirmHighScoresClearButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HighScoreManager highScoreManager = new HighScoreManager(
                        PreferenceManager.getDefaultSharedPreferences(getContext()));
                for (int boardSize : getResources().getIntArray(R.array.boardSizeChoices)) {
                    for (int numColors : getResources().getIntArray(R.array.numColorsChoices)) {
                        highScoreManager.removeHighScore(boardSize, numColors);
                    }
                }
                dialog.dismiss();

                new Butter(getContext(), R.string.settings_clear_high_scores_toast)
                        .setButteredToastDuration(Toast.LENGTH_LONG).addJam().show();
            }
        });

        Button cancelButton = dialogView.findViewById(R.id.cancelHighScoresClearButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
