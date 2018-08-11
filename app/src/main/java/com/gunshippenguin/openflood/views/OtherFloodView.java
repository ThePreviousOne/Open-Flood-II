package com.gunshippenguin.openflood.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;

import com.gunshippenguin.openflood.R;

import java.util.Random;

public class OtherFloodView extends View {

    private SharedPreferences sp;
    private int boardSizeX;
    private int[][] board;
    private int boardSizeY;
    private int cellSize;
    private Paint paints[];

    public OtherFloodView(Context context) {
        super(context);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        boardSizeX = sp.getInt("board_size", 18);
        setDrawingInfo();
    }

    public void initBoard() {
        initPaints();
        final String SEED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder seed = new StringBuilder();
        for(int i=0;i<rand.nextInt(9);i++) {
            seed.append(SEED_CHARS.charAt(rand.nextInt(SEED_CHARS.length())));
        }
        board = new int[boardSizeY][boardSizeX];
        Random r = new Random(seed.hashCode());
        for (int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                board[y][x] = r.nextInt(sp.getInt("num_colors", 6));
            }
        }
    }

    private void initPaints() {
        int[] color;
        if (sp.getBoolean("use_old_colors", false)){
            color = getResources().getIntArray(R.array.oldBoardColorScheme);
        } else {
            color = getResources().getIntArray(R.array.boardColorScheme);
        }

        paints = new Paint[color.length];
        for (int i = 0; i < color.length; i++) {
            paints[i] = new Paint();
            paints[i].setColor(color[i]);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setDrawingInfo();
    }

    private void setDrawingInfo() {
        int dimension;
        final DisplayMetrics display = getContext().getResources().getDisplayMetrics();
        final int pixelsX = display.widthPixels;
        final int pixelsY = display.heightPixels;
        if (pixelsX > pixelsY) {
            dimension = pixelsY;
        } else {
            dimension = pixelsX;
        }
        dimension -= (dimension % boardSizeX);
        cellSize = dimension / boardSizeX;
        boardSizeY = (int) Math.floor(pixelsY / cellSize);
    }

    @Override
    protected void onDraw(Canvas c) {
        // Draw colors
        for (int y = 0; y < boardSizeY ; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                c.drawRect(x * cellSize, y * cellSize,
                        (x + 1) * cellSize, (y + 1) * cellSize, paints[board[y][x]]);
            }
        }
    }
}
