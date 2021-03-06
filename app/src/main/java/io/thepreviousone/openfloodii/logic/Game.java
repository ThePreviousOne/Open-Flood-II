package io.thepreviousone.openfloodii.logic;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Class representing a game in progress.
 */
public class Game {
    private int board[][];
    private int boardSize;

    private int numColors;

    private int steps = 0;
    private int maxSteps;

    private String seed;
    private static final String SEED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private static final int SEED_LENGTH_LOWER = 5;
    private static final int SEED_LENGTH_UPPER = 15;

    public Game(int boardSize, int numColors) {
        // Initialize board
        this.boardSize = boardSize;
        this.numColors = numColors;
        this.seed = generateRandomSeed();
        initBoard();
        initMaxSteps();
    }

    public Game(int boardSize, int numColors, String seed) {
        // Initialize board
        this.boardSize = boardSize;
        this.numColors = numColors;
        this.seed = seed;
        initBoard();
        initMaxSteps();
    }

    public Game(int[][] board, int boardSize, int numColors, int steps, String seed) {
        // Restore board
        this.board = board;
        this.boardSize = boardSize;
        this.numColors = numColors;
        this.steps = steps;
        this.seed = seed;
        initMaxSteps();
    }

    private String generateRandomSeed() {
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder currSeed = new StringBuilder();
        for(int i=0;i<rand.nextInt((SEED_LENGTH_UPPER-SEED_LENGTH_LOWER)+1)+SEED_LENGTH_LOWER;i++) {
            currSeed.append(SEED_CHARS.charAt(rand.nextInt(SEED_CHARS.length())));
        }
        return currSeed.toString();
    }

    public int[][] getBoard() {
        return board;
    }

    public int getColor(int x, int y) {
        return board[y][x];
    }

    public int getBoardDimensions() {
        return boardSize;
    }

    public String getSeed() {
        return seed;
    }

    public int getSteps() {
        return steps;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    private void initBoard() {
        board = new int[boardSize][boardSize];
        Random r = new Random(seed.hashCode());
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                board[y][x] = r.nextInt(numColors);
            }
        }
    }

    private void initMaxSteps() {
        maxSteps = 30 * (boardSize * numColors) / (17 * 6);
    }

    public void flood(int replacementColor) {
        int targetColor = board[0][0];
        if (targetColor == replacementColor) {
            return;
        }

        Queue<BoardPoint> queue = new LinkedList<>();

        queue.add(new BoardPoint(0, 0));

        BoardPoint currPoint;
        while (!queue.isEmpty()) {
            currPoint = queue.remove();
            if (board[currPoint.getY()][currPoint.getX()] == targetColor) {
                board[currPoint.getY()][currPoint.getX()] = replacementColor;
                if (currPoint.getX() != 0) {
                    queue.add(new BoardPoint(currPoint.getX() - 1, currPoint.getY()));
                }
                if (currPoint.getX() != boardSize - 1) {
                    queue.add(new BoardPoint(currPoint.getX() + 1, currPoint.getY()));
                }
                if (currPoint.getY() != 0 ) {
                    queue.add(new BoardPoint(currPoint.getX(), currPoint.getY() - 1));
                }
                if (currPoint.getY() != boardSize - 1 ) {
                    queue.add(new BoardPoint(currPoint.getX(), currPoint.getY() + 1));
                }
            }
        }
        steps++;
    }

    public boolean checkWin() {
        int lastColor = board[0][0];
        for (int[] aBoard : board) {
            for (int x = 0; x < board.length; x++) {
                if (lastColor != aBoard[x]) {
                        return false;
                    }
                lastColor = aBoard[x];
            }
        }
        return true;
    }

    private class BoardPoint {
        private int x, y;

        BoardPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!BoardPoint.class.isAssignableFrom(obj.getClass())) {
                return false;
            }
            BoardPoint bp = (BoardPoint) obj;
            return (this.x == bp.getX()) && (this.y == bp.getY());
        }
    }
}
