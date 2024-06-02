package io.thepreviousone.openfloodii.activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.github.megatronking.svg.support.SVGDrawable;
import com.github.megatronking.svg.support.SVGRenderer;
import com.github.megatronking.svg.support.extend.SVGImageView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.thepreviousone.openfloodii.drawables.Back;
import io.thepreviousone.openfloodii.drawables.Endless;
import io.thepreviousone.openfloodii.drawables.Regular;
import io.thepreviousone.openfloodii.fragments.EndGameDialogFragment;
import io.thepreviousone.openfloodii.fragments.SettingsDialogFragment;
import io.thepreviousone.openfloodii.views.ColorButton;
import io.thepreviousone.openfloodii.logic.Game;
import io.thepreviousone.openfloodii.R;
import io.thepreviousone.openfloodii.drawables.Replay;
import io.thepreviousone.openfloodii.drawables.Playoutline;
import io.thepreviousone.openfloodii.drawables.Undo;
import io.thepreviousone.openfloodii.utils.JsonStack;
import io.thepreviousone.openfloodii.views.Butter;
import io.thepreviousone.openfloodii.views.FloodView;
import io.thepreviousone.openfloodii.fragments.SeedDialogFragment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity allowing the user to play the actual game.
 */
public class GameActivity extends BaseActivity {

    private Game game;
    private JsonStack undoList;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private FloodView floodView;
    private TextView stepsTextView;
    //Button for switching GameModes
    private SVGImageView modeSwitchButton;

    private int lastColor;

    private boolean gameFinished;
    //0 == regular; 1 == endless
    private int gameMode;

    // Paints to be used for the board
    private Paint[] paints;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spEditor = sp.edit();
        gameMode = sp.getInt("sp_gameMode", gameMode);
        final int darkerGrey = 0xEE272727;

        // Get the FloodView
        floodView = findViewById(R.id.floodView);
        floodView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new SettingsDialogFragment().show(getSupportFragmentManager(), "SettingsDialog");
                return true;
            }
        });

        // Initialize the paints array and pass it to the FloodView
        initPaints();
        floodView.setPaints(paints);

        //Back to Main Menu (MainActivity)
        final ImageView backButton = findViewById(R.id.backButton);
        backButton.setImageDrawable(new SVGDrawable(new Back(this)));
        backButton.setColorFilter(0xFF666666);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent launchMainIntent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(launchMainIntent);
            }
        });

        final ImageView undoButton = findViewById(R.id.undoButton);
        undoButton.setImageDrawable(new SVGDrawable(new Undo(this, 48f)));
        undoButton.setColorFilter(darkerGrey);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });

        final SVGImageView newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setImageDrawable(new SVGDrawable(new Playoutline(this, 48f)));
        newGameButton.setColorFilter(darkerGrey);
        newGameButton.setOnClickListener(new View.OnClickListener() {
        final ObjectAnimator animatorRotation = setRotationAnimation(newGameButton);

            @Override
            public void onClick(View v) {
                animatorRotation.start();
                spEditor.remove("state_saved" + gameMode);
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
        restartButton.setColorFilter(darkerGrey);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame(game.getSeed());
            }
        });

        modeSwitchButton = findViewById(R.id.modeSwitchButton);
        modeSwitchButton.setImageDrawable(new SVGDrawable(setRenderer()));
        modeSwitchButton.setColorFilter(darkerGrey);
        modeSwitchButton.setOnClickListener(new View.OnClickListener() {
            final ObjectAnimator animatorRotation = setRotationAnimation(modeSwitchButton);

            @Override
            public void onClick(View v) {
                animatorRotation.start();
                saveGame();
                new Timer().schedule(new DelayModeSwitchTimer(), 1700);
                switchGameMode();
            }
        });
        stepsTextView = findViewById(R.id.stepsTextView);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (sp.contains("state_saved" + gameMode)) {
            if (!sp.getBoolean("state_finished" + gameMode, false)) restoreGame();
        }
        else newGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!gameFinished) saveGame();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        spEditor.apply();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GameActivity.this, MainActivity.class));
        super.onBackPressed();
    }

    @Subscribe
    public void onEvent(ClipData clip) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(clip);
        new Butter(this, R.string.game_seed_copied, Toast.LENGTH_LONG)
                .setFontSize(10).setBackgroundColor(0xFFFAF7EE).addJam().show();
    }

    @Subscribe
    public void onEvent(String seed) {
        if (seed.isEmpty()) {
            newGame();
        } else {
            newGame(seed);
        }
    }

    private ObjectAnimator setRotationAnimation(SVGImageView view) {
        ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(view, "svgRotation", 0, 360);
        animatorRotation.setDuration(1500);
        animatorRotation.setInterpolator(new AccelerateDecelerateInterpolator());
        return animatorRotation;
    }
    private void setTextView() {
        if (gameMode == 0) stepsTextView.setText(String.format("%d / %d", game.getSteps(), game.getMaxSteps()));
        else stepsTextView.setText(String.valueOf(game.getSteps()));
    }

    private SVGRenderer setRenderer() {
        if (gameMode == 0) return new Endless(this, 48);
        else return new Regular(this, 48);
    }

    private void switchGameMode() {
        if (gameMode == 0) gameMode = 1;
        else gameMode = 0;
    }
    private void endGameTimer() {
        new Timer().schedule(new DelayFragmentTimer(), 2250);
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

        setTextView();
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

    private void restoreGame(int[][] board, String seed, int steps) {
        if (board == null || seed == null) {
            newGame();
        } else {
            game = new Game(board, getBoardSize(), getNumColors(), steps, seed);
            initGame();
        }
    }

    private void restoreGame() {
        int[][] board = new Gson().fromJson(sp.getString("state_board" + gameMode, null), int[][].class);
        if (getBoardSize() == board.length && sp.getInt("sp_colors",
                    getResources().getInteger(R.integer.default_num_colors)) == getNumColors()) {
            int steps = sp.getInt("state_steps" + gameMode, 0);
            String seed = sp.getString("state_seed" + gameMode, null);
            restoreGame(board, seed, steps);
        } else {
            newGame();
        }
    }

    private void saveGame() {
        spEditor.putInt("sp_colors", getNumColors());
        spEditor.putInt("sp_gameMode", gameMode);
        spEditor.putBoolean("state_saved" + gameMode, true);
        spEditor.putString("state_board" + gameMode, new Gson().toJson(game.getBoard()));
        spEditor.putInt("state_steps" + gameMode, game.getSteps());
        spEditor.putString("state_seed" + gameMode, game.getSeed());
        spEditor.apply();
    }

    private void setGameFinished(boolean state) {
        gameFinished = state;
        spEditor.putBoolean("state_finished" + gameMode, state);
        spEditor.apply();
    }

    private void doStep(int color) {
        if (gameMode == 0) {
            if (!gameFinished && game.getSteps() <= game.getMaxSteps()) {
                doColor(color);
            }

            if (game.checkWin()) {
                setGameFinished(true);
                new Butter(this, R.string.endgame_win_toast)
                        .setFontSize(14).addJam().show();
                endGameTimer();
            } else if (game.getSteps() == game.getMaxSteps()) {
                new Butter(this, R.string.endgame_lose_toast)
                        .setFontSize(14).addJam().show();
                endGameTimer();
            }
        } else {
            if (!gameFinished) {
                doColor(color);
            }

            if (game.checkWin()) {
                setGameFinished(true);
                if (game.getSteps() <= game.getMaxSteps())
                    new Butter(this, R.string.endgame_win_toast)
                            .setFontSize(14).addJam().show();
                else new Butter(this, R.string.endgame_lose_toast)
                        .setFontSize(14).addJam().show();

                endGameTimer();
            }
        }
    }

    private void doColor(int color) {
        game.flood(color);
        floodView.drawGame(game);
        lastColor = color;
        setTextView();
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

    private void undo() {
        if(!undoList.isEmpty()) {
            int color = undoList.peek()[0][0];
            game = new Game(undoList.pop(), getBoardSize(), getNumColors(),
                    game.getSteps() -  1, game.getSeed());
            doStep(color);
        } else {
            new Butter(this, R.string.undo_toast)
                    .setFont(R.font.lenka).addJam().show();
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
                        doStep(localI);
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

    class DelayModeSwitchTimer extends TimerTask {
        @Override
        public void run() {
            GameActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    modeSwitchButton.setImageDrawable(new SVGDrawable(setRenderer()));
                    setTextView();
                }
            });
        }
    }
    class DelayFragmentTimer extends TimerTask {

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
