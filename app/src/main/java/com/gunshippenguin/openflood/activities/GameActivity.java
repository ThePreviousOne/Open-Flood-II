package com.gunshippenguin.openflood.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.megatronking.svg.support.SVGDrawable;
import com.google.gson.Gson;
import com.gunshippenguin.openflood.utils.ColorButton;
import com.gunshippenguin.openflood.Game;
import com.gunshippenguin.openflood.R;
import com.gunshippenguin.openflood.drawables.Replay;
import com.gunshippenguin.openflood.drawables.Playoutline;
import com.gunshippenguin.openflood.drawables.Undo;
import com.gunshippenguin.openflood.utils.JsonStack;
import com.gunshippenguin.openflood.views.Butter;
import com.gunshippenguin.openflood.views.EndGameDialogFragment;
import com.gunshippenguin.openflood.views.FloodView;
import com.gunshippenguin.openflood.views.SeedDialogFragment;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Activity allowing the user to play the actual game.
 */
public class GameActivity extends AppCompatActivity
        implements EndGameDialogFragment.EndGameDialogFragmentListener,
        SeedDialogFragment.SeedDialogFragmentListener {

    private Game game;
    private JsonStack undoList;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private FloodView floodView;
    private TextView stepsTextView;

    private int lastColor;

    private boolean gameFinished;

    // Paints to be used for the board
    private Paint paints[];

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize the SharedPreferences and SharedPreferences editor
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spEditor = sp.edit();

        // Get the FloodView
        floodView = findViewById(R.id.floodView);

        // Initialize the paints array and pass it to the FloodView
        initPaints();
        floodView.setPaints(paints);

        final ImageView undoButton = findViewById(R.id.undoButton);
        undoButton.setImageDrawable(new SVGDrawable(new Undo(this, 48f)));
        undoButton.setColorFilter(0xFF272727);    //Darker Grey
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });

        ImageView newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setImageDrawable(new SVGDrawable(new Playoutline(this, 48f)));
        newGameButton.setColorFilter(0xFF272727);    //Darker Grey
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor.remove("state_saved");
                spEditor.apply();
                newGame();
            }
        });

        newGameButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SeedDialogFragment seedDialogFragment = new SeedDialogFragment();
                seedDialogFragment.show(getSupportFragmentManager(), "SeedDialog");
                return true;
            }
        });

        final ImageView restartButton = findViewById(R.id.restartButton);
        restartButton.setImageDrawable(new SVGDrawable(new Replay(this, 48f)));
        restartButton.setColorFilter(0xFF272727);    //Darker Grey
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame(game.getSeed());
            }
        });
        // Get the steps text view
        stepsTextView = findViewById(R.id.stepsTextView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sp.contains("state_saved") && !sp.getBoolean("state_finished", false)) {
            //Restore the previous game
            restoreGame();
        } else {
            // Set up a new game
            newGame();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (game.getSteps() != 0 && !gameFinished) {
            spEditor.putBoolean("state_saved", true);
            spEditor.putString("state_board", new Gson().toJson(game.getBoard()));
            spEditor.putInt("state_steps", game.getSteps());
            spEditor.putString("state_seed", game.getSeed());
            spEditor.apply();
        }
    }

    private void setGameFinished(boolean state) {
        gameFinished = state;
        spEditor.putBoolean("state_finished", state);
        spEditor.apply();
    }

    private int getBoardSize() {
        int defaultBoardSize = getResources().getInteger(R.integer.default_board_size);
        if (!sp.contains("board_size")) {
            spEditor.putInt("board_size", defaultBoardSize);
            spEditor.apply();
        }
        return sp.getInt("board_size", defaultBoardSize);
    }

    private int getNumColors() {
        int defaultNumColors = getResources().getInteger(R.integer.default_num_colors);
        if (!sp.contains("num_colors")) {
            spEditor.putInt("num_colors", defaultNumColors);
            spEditor.apply();
        }
        return sp.getInt("num_colors", defaultNumColors);
    }

    private void initPaints() {
        int[] colors;
        if (sp.getBoolean("use_old_colors", false)){
            colors = getResources().getIntArray(R.array.oldBoardColorScheme);
        } else {
            colors = getResources().getIntArray(R.array.boardColorScheme);
        }

        paints = new Paint[colors.length];
        for (int i = 0; i < colors.length; i++) {
            paints[i] = new Paint();
            paints[i].setColor(colors[i]);
        }
    }

    private void initGame() {
        setGameFinished(false);
        undoList = new JsonStack();
        lastColor = game.getColor(0, 0);

        layoutColorButtons();

        stepsTextView.setText(String.format("%d / %d", game.getSteps(), game.getMaxSteps()));
        floodView.setBoardSize(getBoardSize());
        floodView.drawGame(game);
    }

    private void newGame() {
        game = new Game(getBoardSize(), getNumColors());
        initGame();
    }

    private void newGame(String seed) {
        game = new Game(getBoardSize(), getNumColors(), seed);
        initGame();
    }

    private void restoreGame() {
        int board[][] = new Gson().fromJson(sp.getString("state_board", null), int[][].class);
        int steps = sp.getInt("state_steps", 0);
        String seed = sp.getString("state_seed", null);

        if (board == null || seed == null) {
            newGame();
        } else {
            game = new Game(board, getBoardSize(), getNumColors(), steps, seed);
            initGame();
        }
    }

    private void undo() {
        if(!undoList.isEmpty()) {
            int color = undoList.peek()[0][0];
            game = new Game(undoList.pop(), getBoardSize(), getNumColors(),
                    game.getSteps() -  1, game.getSeed());
            doColor(color);
        } else {
           new Butter(this, R.string.undo_toast)
                   .setFont("fonts/Lenka.ttf").addJam().show();
        }
    }

    private void layoutColorButtons() {
        // Add color buttons
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        buttonLayout.removeAllViews();
        int buttonPadding = (int) getResources().getDimension(R.dimen.color_button_padding);
        for (int i = 0; i < getNumColors(); i++) {
            final int localI = i;
            ColorButton newButton = new ColorButton(this);
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.button_anim));
                    if (localI != lastColor) {
                            undoList.push(game.getBoard());
                            doColor(localI);
                    }
                }
            });
            newButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            newButton.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
            newButton.setColorBlindText(Integer.toString(i + 1));
            newButton.setColor(paints[i].getColor());
            buttonLayout.addView(newButton);
        }
    }

    private void doColor(int color) {
        if (!gameFinished && game.getSteps() <= game.getMaxSteps()) {
            game.flood(color);
            floodView.drawGame(game);
            lastColor = color;
            stepsTextView.setText(String.format("%d / %d", game.getSteps(), game.getMaxSteps()));
        }

        if (game.checkWin() || game.getSteps() == game.getMaxSteps()) {
            setGameFinished(true);
            showToast();

            new Timer().schedule(new DelayTimer(), 2250);
        }
    }

    public void onNewGameClick() {
        newGame();
    }

    public void onReplayClick() {
        newGame(game.getSeed());
    }

    public void onGetSeedClick() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("seed", game.getSeed());
        clipboard.setPrimaryClip(clip);
        new Butter(this, R.string.game_seed_copied, Toast.LENGTH_LONG).addJam().show();
    }

    public void onNewGameFromSeedClick(String seed) {
        newGame(seed);
    }

    public void showToast() {
        if (game.checkWin()) new Butter(this, R.string.endgame_win_toast)
                .setFontSize(14).addJam().show();
        else new Butter(this, R.string.endgame_lose_toast)
                .setFontSize(14).addJam().show();
    }

    private void showEndGameDialog() {
        DialogFragment endGameDialog = new EndGameDialogFragment();
        Bundle args = new Bundle();
        args.putInt("steps", game.getSteps());
        args.putBoolean("game_won", game.checkWin());
        args.putString("seed", game.getSeed());
        endGameDialog.setArguments(args);
        endGameDialog.show(getSupportFragmentManager(), "EndGameDialog");
    }

    class DelayTimer extends TimerTask {

        @Override
        public void run() {
            GameActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showEndGameDialog();
                }
            });
        }
    }

}
