package com.gunshippenguin.openflood.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.megatronking.svg.support.SVGDrawable;
import com.gunshippenguin.openflood.utils.ColorButton;
import com.gunshippenguin.openflood.Game;
import com.gunshippenguin.openflood.R;
import com.gunshippenguin.openflood.drawables.Replay;
import com.gunshippenguin.openflood.drawables.Playoutline;
import com.gunshippenguin.openflood.drawables.Undo;
import com.gunshippenguin.openflood.utils.JsonStack;
import com.gunshippenguin.openflood.utils.PixelConverter;
import com.gunshippenguin.openflood.views.Butter;
import com.gunshippenguin.openflood.views.EndGameDialogFragment;
import com.gunshippenguin.openflood.views.FloodView;
import com.gunshippenguin.openflood.views.SeedDialogFragment;

import java.util.TimerTask;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Activity allowing the user to play the actual game.
 */
abstract class AbstractGameActivity extends AppCompatActivity
        implements EndGameDialogFragment.EndGameDialogFragmentListener,
        SeedDialogFragment.SeedDialogFragmentListener {

    protected Game game;
    protected JsonStack undoList;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor spEditor;

    protected FloodView floodView;
    protected TextView stepsTextView;

    protected int lastColor;

    protected boolean gameFinished;
    protected boolean launchedFromBundle;
    protected boolean returnBundle;

    // Paints to be used for the board
    protected Paint paints[];
    protected PixelConverter pixelConverter;

    protected final int darkerGrey = 0xFF272727;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pixelConverter = new PixelConverter(this);
        // Get the FloodView
        floodView = findViewById(R.id.floodView);

        // Initialize the paints array and pass it to the FloodView
        initPaints();
        floodView.setPaints(paints);

        final ImageView undoButton = findViewById(R.id.undoButton);
        undoButton.setImageDrawable(new SVGDrawable(new Undo(this, 48f)));
        undoButton.setColorFilter(darkerGrey);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });

        final ImageView newGameButton = findViewById(R.id.newGameButton);
        newGameButton.setImageDrawable(new SVGDrawable(new Playoutline(this, 48f)));
        newGameButton.setColorFilter(darkerGrey);
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

        // Get the steps text view
        stepsTextView = findViewById(R.id.stepsTextView);

        View separator = findViewById(R.id.separator);
        RelativeLayout.LayoutParams slp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                pixelConverter.dip2px(1)
        );
        slp.addRule(RelativeLayout.BELOW, floodView.getId());

        DisplayMetrics matrix = this.getResources().getDisplayMetrics();
        if (matrix.xdpi <= matrix.ydpi) {
            View header = findViewById(R.id.header);
            RelativeLayout.LayoutParams flp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    pixelConverter.sip2px(400)
            );
            flp.addRule(RelativeLayout.BELOW, newGameButton.getId());
            flp.setMargins(0, pixelConverter.dip2px(15), 0, 0);
            floodView.setLayoutParams(flp);

            slp.setMargins(0, pixelConverter.dip2px(15), 0, 0);
            separator.setLayoutParams(slp);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    pixelConverter.dip2px(1)
            );
            lp.addRule(RelativeLayout.BELOW, R.id.appNameTextView);
            lp.setMargins(0, 8, 0, pixelConverter.dip2px(15));
            header.setLayoutParams(lp);
        } else {
            slp.setMargins(0, 0, 0, 0);
            separator.setLayoutParams(slp);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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

    protected void initPaints() {
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

    protected void newGame() {
        game = new Game(getBoardSize(), getNumColors());
        initGame();
    }

    protected void newGame(String seed) {
        game = new Game(getBoardSize(), getNumColors(), seed);
        initGame();
    }

    protected void restoreGame(int[][] board, String seed, int steps) {
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
                    v.startAnimation(AnimationUtils.loadAnimation(AbstractGameActivity.this, R.anim.button_anim));
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
        new Butter(this, R.string.game_seed_copied, Toast.LENGTH_LONG).setFontSize(10).addJam().show();
    }

    public void onNewGameFromSeedClick(String seed) {
        newGame(seed);
    }

    protected abstract void restoreGame();

    protected abstract void setGameFinished(boolean state);

    protected abstract void doColor(int color);

    protected abstract void setTextView();

    protected abstract void showToast();

    protected abstract void showEndGameDialog();

    protected class DelayFragmentTimer extends TimerTask {

        @Override
        public void run() {
            AbstractGameActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showEndGameDialog();
                }
            });
        }
    }

}
