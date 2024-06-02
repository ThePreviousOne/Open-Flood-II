package io.thepreviousone.openfloodii.utils;

import java.io.Serializable;

public class ArrayHelper implements Serializable {

        private int[][] array;

        public ArrayHelper(int[][] array) {
            this.array = array;
        }

    public int[][] getArray() {
        return array;
    }
}
