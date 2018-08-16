package com.gunshippenguin.openflood.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.megatronking.svg.support.SVGDrawable;
import com.github.megatronking.svg.support.extend.SVGImageView;
import com.google.gson.Gson;
import com.gunshippenguin.openflood.R;
import com.gunshippenguin.openflood.drawables.Regular;
import com.gunshippenguin.openflood.utils.ArrayHelper;
import com.gunshippenguin.openflood.views.Butter;
import com.gunshippenguin.openflood.views.EndGameDialogFragment;

import java.util.Timer;
import java.util.TimerTask;

public class EndlessGameActivity extends AbstractGameActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Initialize the SharedPreferences and SharedPreferences editor
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spEditor = sp.edit();
        super.onCreate(savedInstanceState);

        ImageView newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.remove("state_saved1");
                spEditor.apply();
                newGame();
            }
        });

        final SVGImageView regularGameMode = new SVGImageView(this);
        RelativeLayout.LayoutParams buttonLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLP.addRule(RelativeLayout.START_OF, R.id.undoButton);
        buttonLP.addRule(RelativeLayout.ABOVE, R.id.floodView);
        regularGameMode.setLayoutParams(buttonLP);

        final ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(regularGameMode, "svgRotation", 0, 360);
        animatorRotation.setDuration(1500);
        animatorRotation.setRepeatCount(1);
        animatorRotation.setInterpolator(new AccelerateDecelerateInterpolator());
        regularGameMode.setImageDrawable(new SVGDrawable(new Regular(this, 48f)));
        regularGameMode.setColorFilter(darkerGrey);
        regularGameMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatorRotation.start();
                new Timer().schedule(new EndlessGameActivity.DelayActivityTimer(), 1600);
            }
        });
        RelativeLayout layout = findViewById(R.id.relativeLayout);
        layout.addView(regularGameMode);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null && bundle.containsKey("bundle_board")) {
            int[][] board = ((ArrayHelper) bundle.getSerializable("bundle_board")).getArray();
            int steps = bundle.getInt("bundle_steps");
            String seed = bundle.getString("bundle_seed");
            returnBundle = bundle.getBoolean("bundle_return");

            restoreGame(board, seed, steps);
            setIntent(new Intent());
            launchedFromBundle = true;
        } else if (sp.contains("state_saved1") && !sp.getBoolean("state_finished1", false)) {
            //Restore the previous game
            restoreGame();
        } else {
            // Set up a new game
            newGame();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (launchedFromBundle && !returnBundle) {
            spEditor.putBoolean("state_saved0", true);
            spEditor.putString("state_board0", new Gson().toJson(game.getBoard()));
            spEditor.putInt("state_steps0", game.getSteps());
            spEditor.putString("state_seed0", game.getSeed());
            spEditor.apply();
        } else if (game.getSteps() != 0 && !gameFinished) {
            spEditor.putBoolean("state_saved1", true);
            spEditor.putString("state_board1", new Gson().toJson(game.getBoard()));
            spEditor.putInt("state_steps1", game.getSteps());
            spEditor.putString("state_seed1", game.getSeed());
            spEditor.apply();
        }
    }

    @Override
    protected void setGameFinished(boolean state) {
        gameFinished = state;
        spEditor.putBoolean("state_finished1", state);
        spEditor.apply();
    }

    @Override
    protected void restoreGame() {
        int board[][] = new Gson().fromJson(sp.getString("state_board1", null), int[][].class);
        int steps = sp.getInt("state_steps1", 0);
        String seed = sp.getString("state_seed1", null);
        restoreGame(board, seed, steps);
    }

    @Override
    protected void doColor(int color) {
        if (!gameFinished) {
            game.flood(color);
            floodView.drawGame(game);
            lastColor = color;
            setTextView();
        }

        if (game.checkWin()) {
            setGameFinished(true);
            showToast();

            new Timer().schedule(new DelayFragmentTimer(), 2250);
        }
    }

    @Override
    protected void showToast() {
        if (game.checkWin() && game.getSteps() <= game.getMaxSteps())
                new Butter(this, R.string.endgame_win_toast)
                        .setFontSize(14).addJam().show();
        else new Butter(this, R.string.endgame_lose_toast)
                .setFontSize(14).addJam().show();
    }

    @Override
    protected void showEndGameDialog() {
        DialogFragment endGameDialog = new EndGameDialogFragment();
        Bundle args = new Bundle();
        args.putInt("steps", game.getSteps());
        args.putBoolean("game_won", game.checkWin());
        args.putString("seed", game.getSeed());
        endGameDialog.setArguments(args);
        endGameDialog.show(getSupportFragmentManager(), "EndGameDialog");
    }

    @Override
    protected void setTextView() {
        stepsTextView.setText(String.valueOf(game.getSteps()));
    }

    protected class DelayActivityTimer extends TimerTask {

        @Override
        public void run() {
            EndlessGameActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Intent launchPlayIntent = new Intent(EndlessGameActivity.this, RegularGameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bundle_board", new ArrayHelper(game.getBoard()));
                    bundle.putInt("bundle_steps", game.getSteps());
                    bundle.putString("bundle_seed", game.getSeed());
                    bundle.putBoolean("bundle_return", launchedFromBundle);
                    launchPlayIntent.putExtras(bundle);
                    launchPlayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    launchPlayIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(launchPlayIntent);
                }
            });
        }
    }

}
