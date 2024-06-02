package io.thepreviousone.openfloodii.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import io.thepreviousone.openfloodii.logic.Game;

/**
 * View that displays the game board to the user.
 */
public class FloodView extends View {
    private Game gameToDraw;
    private int boardSize;
    private int cellSize;
    private int xOffset;
    private Paint textPaint;
    private Paint paints[];

    public FloodView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setDrawingInfo();
    }

    private void setDrawingInfo(){
        int dimension;
        if (getWidth() > getHeight()) {
            dimension = getHeight();
        } else {
            dimension = getWidth();
        }

        dimension -= (dimension % boardSize);
        cellSize = dimension / boardSize;
        xOffset = (getWidth() - dimension) / 2 ;
        textPaint.setTextSize(cellSize); }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
        setDrawingInfo();
    }

    public void drawGame(Game game) {
        gameToDraw = game;
        invalidate();
    }

    public void setPaints(Paint[] paints) {
        this.paints = paints;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas c) {
        if (gameToDraw != null) {
            // Draw colors
            for (int y = 0; y < gameToDraw.getBoardDimensions(); y++) {
                for (int x = 0; x < gameToDraw.getBoardDimensions(); x++) {
                    c.drawRect(x * cellSize + xOffset, y * cellSize,
                            (x + 1) * cellSize + xOffset, (y + 1) * cellSize, paints[gameToDraw.getColor(x, y)]);
                }
            }

            // Draw numbers if color blind mode is on
            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("color_blind_mode", false)) {
                for (int y = 0; y < gameToDraw.getBoardDimensions(); y++) {
                    for (int x = 0; x < gameToDraw.getBoardDimensions(); x++) {
                        c.drawText(Integer.toString(gameToDraw.getColor(x, y) + 1),
                                (x * cellSize + xOffset) + (cellSize / 2),
                                (y * cellSize) + (cellSize * 0.85f), textPaint
                        );
                    }
                }
            }
        }
    }
}
