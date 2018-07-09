package com.gunshippenguin.openflood.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import com.gunshippenguin.openflood.ColorButton;
import com.gunshippenguin.openflood.Game;
import com.gunshippenguin.openflood.R;
import com.gunshippenguin.openflood.drawables.info;
import com.gunshippenguin.openflood.drawables.playoutline;
import com.gunshippenguin.openflood.drawables.settings;
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

    private final int UPDATE_SETTINGS = 1;

    private Game game;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private Timer timer = new Timer();

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

        ImageView infoButton = findViewById(R.id.infoButton);
        infoButton.setImageDrawable(new SVGDrawable(new info(this)));
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchSettingsIntent = new Intent(GameActivity.this, InfoActivity.class);
                startActivity(launchSettingsIntent);
            }
        });

        ImageView settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setImageDrawable(new SVGDrawable(new settings(this)));
        settingsButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
              Intent launchSettingsIntent = new Intent(GameActivity.this, SettingsActivity.class);
              startActivityForResult(launchSettingsIntent, UPDATE_SETTINGS);
              }
        });

        ImageView newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setImageDrawable(new SVGDrawable(new playoutline(this)));
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        spEditor.putBoolean("state_saved", true);
        spEditor.putString("state_board", new Gson().toJson(game.getBoard()));
        spEditor.putInt("state_steps", game.getSteps());
        spEditor.putString("state_seed", game.getSeed());
        spEditor.apply();
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
        lastColor = game.getColor(0, 0);

        layoutColorButtons();

        stepsTextView.setText(game.getSteps() + " / " + game.getMaxSteps());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                // Only start a new game if the settings have been changed
                if (extras.getBoolean("gameSettingsChanged")) {
                    newGame();
                }
                if (extras.getBoolean("colorSettingsChanged")) {
                    initPaints();
                    floodView.setPaints(paints);
                    layoutColorButtons();
                }
            }
        }
    }

    private void doColor(int color) {
        if (!gameFinished && game.getSteps() <= game.getMaxSteps()) {
            game.flood(color);
            floodView.drawGame(game);
            lastColor = color;
            stepsTextView.setText(game.getSteps() + " / " + game.getMaxSteps());
        }

        if (game.checkWin() || game.getSteps() == game.getMaxSteps()) {
            setGameFinished(true);

            showToast();

            timer.schedule(new DelayTimer(), 2250);
        }
    }

    public void onNewGameClick() {
        newGame();
    }

    public void onReplayClick() {
        newGame(game.getSeed());
    }

    public void onLaunchSeedDialogClick() {
        SeedDialogFragment seedDialogFragment = new SeedDialogFragment();
        seedDialogFragment.show(getSupportFragmentManager(), "SeedDialog");
    }

    public void onGetSeedClick() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("seed", game.getSeed());
        clipboard.setPrimaryClip(clip);
        new Butter(this, R.string.game_seed_copied).setButteredToastDuration(Toast.LENGTH_LONG).addJam().show();
    }

    public void onNewGameFromSeedClick(String seed) {
        newGame(seed);
    }

    public void showToast() {
        if (game.checkWin()) {
            new Butter(this, R.string.endgame_win_toast)
                    .setFont("fonts/Yahfie-Heavy.ttf").setFontSize(24).addJam().show();
        } else {
            new Butter(this, R.string.endgame_lose_toast)
                    .setFont("fonts/Yahfie-Heavy.ttf").setFontSize(24).addJam().show();
        }
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
