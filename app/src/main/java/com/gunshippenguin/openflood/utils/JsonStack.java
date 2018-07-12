package com.gunshippenguin.openflood.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayDeque;
import java.util.Iterator;

public class JsonStack {

    private ArrayDeque<JsonArray> undoRecord = new ArrayDeque<>();

    public void push(int[][] game) {
        JsonArray storeY = new JsonArray();
        for (int[] position : game) {
            JsonArray storeX = new JsonArray();
                for (int xPosition : position) {
                    storeX.add(xPosition);
            }
            storeY.add(storeX);
        }
        undoRecord.push(storeY);
    }

    public int[][] pop() {
        return arrayFromJson(undoRecord.pop());
    }

    public int[][] peek() {
        return arrayFromJson(undoRecord.peek());
    }

    public boolean isEmpty() {
        return undoRecord.isEmpty();
    }

    private int[][] arrayFromJson(JsonArray json) {
        if (json != null) {
            int[][] array = new int[json.size()][json.size()];
            for (int y = 0; y < json.size(); y++) {
                Iterator<JsonElement> iter = json.get(y).getAsJsonArray().iterator();
                for (int x = 0; x < json.size(); x++) {
                    array[y][x] = iter.next().getAsInt();
                }
            }
            return array;
        } else {
            return new int[][] {};
        }
    }

}