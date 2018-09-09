package io.thepreviousone.openfloodii.activities;

import android.animation.ObjectAnimator;
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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.megatronking.svg.support.SVGDrawable;
import com.github.megatronking.svg.support.SVGRenderer;
import com.github.megatronking.svg.support.extend.SVGImageView;
import com.google.gson.Gson;
import io.thepreviousone.openfloodii.drawables.Back;
import io.thepreviousone.openfloodii.drawables.Endless;
import io.thepreviousone.openfloodii.drawables.Regular;
import io.thepreviousone.openfloodii.fragments.EndGameDialogFragment;
import io.thepreviousone.openfloodii.views.ColorButton;
import io.thepreviousone.openfloodii.logic.Game;
import io.thepreviousone.openfloodii.R;
import io.thepreviousone.openfloodii.drawables.Replay;
import io.thepreviousone.openfloodii.drawables.Playoutline;
import io.thepreviousone.openfloodii.drawables.Undo;
import io.thepreviousone.openfloodii.utils.JsonStack;
import io.thepreviousone.openfloodii.utils.PixelConverter;
import io.thepreviousone.openfloodii.views.Butter;
import io.thepreviousone.openfloodii.views.FloodView;
import io.thepreviousone.openfloodii.fragments.SeedDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Activity allowing the user to play the actual game.
 */
public class GameActivity extends AppCompatActivity {

    private Game game;
    private JsonStack undoList;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private FloodView floodView;
    private TextView stepsTextView;
    private SVGImageView modeSwitchButton;

    private int lastColor;

    private boolean gameFinished;
    private int gameMode;

    // Paints to be used for the board
    private Paint paints[];

    @Override
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spEditor = sp.edit();
        gameMode = getIntent().getIntExtra("gameMode", 0);
        final int darkerGrey = 0xFF272727;

        // Get the FloodView
        floodView = findViewById(R.id.floodView);

        // Initialize the paints array and pass it to the FloodView
        initPaints();
        floodView.setPaints(paints);

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

        modeSwitchButton = new SVGImageView(this);
        RelativeLayout.LayoutParams buttonLP = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonLP.addRule(RelativeLayout.START_OF, R.id.undoButton);
        buttonLP.addRule(RelativeLayout.ABOVE, R.id.floodView);
        modeSwitchButton.setLayoutParams(buttonLP);

        modeSwitchButton.setImageDrawable(new SVGDrawable(setRenderer()));
        modeSwitchButton.setColorFilter(darkerGrey);
        modeSwitchButton.setOnClickListener(new View.OnClickListener() {
            final  ObjectAnimator animatorRotation = setRotationAnimation(modeSwitchButton);
            @Override
            public void onClick(View v) {
                saveGame();
                animatorRotation.start();
                new Timer().schedule(new DelayModeSwitchTimer(), 1700);
                switchGameMode();
            }
        });

        View separator = findViewById(R.id.separator);
        RelativeLayout.LayoutParams slp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                PixelConverter.dip2px(1)
        );
        slp.addRule(RelativeLayout.BELOW, floodView.getId());

        DisplayMetrics matrix = this.getResources().getDisplayMetrics();
        if (matrix.xdpi <= matrix.ydpi) {
            View header = findViewById(R.id.header);
            RelativeLayout.LayoutParams flp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    PixelConverter.sip2px(400)
            );
            flp.addRule(RelativeLayout.BELOW, newGameButton.getId());
            flp.setMargins(0, PixelConverter.dip2px(15), 0, 0);
            floodView.setLayoutParams(flp);

            slp.setMargins(0, PixelConverter.dip2px(15), 0, 0);
            separator.setLayoutParams(slp);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    PixelConverter.dip2px(1)
            );
            lp.addRule(RelativeLayout.BELOW, R.id.appNameTextView);
            lp.setMargins(0, 8, 0, PixelConverter.dip2px(15));
            header.setLayoutParams(lp);
        } else {
            slp.setMargins(0, 0, 0, 0);
            separator.setLayoutParams(slp);
        }
        stepsTextView = findViewById(R.id.stepsTextView);
        RelativeLayout layout = findViewById(R.id.relativeLayout);
        layout.addView(modeSwitchButton);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (sp.contains("state_saved")) {
            if (!sp.getBoolean("state_finished0", false) && gameMode == 0) restoreGame();
            else if (!sp.getBoolean("state_finished1", false) && gameMode == 1) restoreGame();
        }
        else newGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (game.getSteps() != 0 && !gameFinished) saveGame();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        finish();
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
        if (seed == null) {
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
        int board[][] = new Gson().fromJson(sp.getString("state_board" + gameMode, null), int[][].class);
        if (getBoardSize() == board.length && sp.getInt("state_colors",
                    getResources().getInteger(R.integer.default_num_colors)) == getNumColors()) {
            int steps = sp.getInt("state_steps" + gameMode, 0);
            String seed = sp.getString("state_seed" + gameMode, null);
            restoreGame(board, seed, steps);
        } else {
            newGame();
        }
    }

    private void saveGame() {
        spEditor.putInt("state_colors", getNumColors());
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
