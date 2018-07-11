package com.gunshippenguin.openflood.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.megatronking.svg.support.SVGDrawable;
import com.gunshippenguin.openflood.utils.HighScoreManager;
import com.gunshippenguin.openflood.R;
import com.gunshippenguin.openflood.drawables.highscore;

/**
 * Dialog Fragment that is displayed to the user upon a win or loss.
 */
public class EndGameDialogFragment extends DialogFragment {

    public interface EndGameDialogFragmentListener {
        void onReplayClick();
        void onNewGameClick();
        void onGetSeedClick();
    }

    EndGameDialogFragmentListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get steps and maxSteps from the arguments
        int steps = getArguments().getInt("steps");
        boolean gameWon = getArguments().getBoolean("game_won");

        // Inflate layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_endgame, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        // Set up dialog's other text views
        TextView endgameTextView = layout.findViewById(R.id.endGameText);
        TextView highScoreTextView = layout.findViewById(R.id.highScoreText);
        ImageView highScoreMedalImageView = layout.findViewById(R.id.highScoreMedalImageView);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        HighScoreManager highScoreManager = new HighScoreManager(sp);
        highScoreMedalImageView.setImageDrawable(new SVGDrawable(new highscore(getContext())));

        int boardSize = sp.getInt("board_size", -1);
        int numColors = sp.getInt("num_colors", -1);

        if (gameWon) {
            String stepsString = String.format(getString(R.string.endgame_win_text), steps);
            endgameTextView.setText(stepsString);

            if (highScoreManager.isHighScore(boardSize, numColors, steps)) {
                highScoreManager.setHighScore(boardSize, numColors, steps);
                highScoreTextView.setText(getString(R.string.endgame_new_highscore_text));
            } else {
                highScoreTextView.setText(String.format(getString(R.string.endgame_old_highscore_text),
                        highScoreManager.getHighScore(boardSize, numColors)));
                highScoreMedalImageView.setVisibility(View.GONE);
            }

        } else {
            endgameTextView.setVisibility(View.GONE);
            highScoreMedalImageView.setVisibility(View.GONE);
            if (highScoreManager.highScoreExists(boardSize, numColors)) {
                highScoreTextView.setText(String.format(getString(R.string.endgame_old_highscore_text),
                        highScoreManager.getHighScore(boardSize, numColors)));
            } else {
                highScoreTextView.setVisibility(View.GONE);
            }
        }

        // Set up the get seed button
        TextView seedTextView  = layout.findViewById(R.id.seedTextView);
        seedTextView.setText(String.format(getString(R.string.endgame_seed),
                getArguments().getString("seed")));
        seedTextView.setTextColor(Color.BLUE);
        seedTextView.setPaintFlags(seedTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        seedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGetSeedClick();
            }
        });

        // Show the replay button
        Button replayButton = layout.findViewById(R.id.replayButton);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReplayClick();
                dismiss();
            }
        });

        // Set up the new game button callback
        Button newGameButton = layout.findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNewGameClick();
                dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EndGameDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EndGameDialogListener");
        }
    }
}