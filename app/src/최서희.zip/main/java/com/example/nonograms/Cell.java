package com.example.nonograms;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import java.util.Random;

public class Cell extends AppCompatButton {

    private boolean blackSquare;
    private boolean checked;
    private static int numBlackSquares = 0;
    private static final Random random = new Random();

    public Cell(@NonNull Context context) {
        super(context);
        blackSquare = random.nextBoolean();
        this.setTextColor(getResources().getColor(android.R.color.black));
        this.setBackgroundResource(R.drawable.cell_selector);

        if (blackSquare) {
            this.setContentDescription("Black square cell");
            numBlackSquares++;
        } else {
            this.setContentDescription("White square cell");
        }
    }

    public boolean isBlackSquare() {
        return blackSquare;
    }

    public static int getNumBlackSquares() {
        return numBlackSquares;
    }

    public static void resetNumBlackSquares() {
        numBlackSquares = 0;
    }

    public boolean markBlackSquare() {
        if (checked) return true;

        if (blackSquare) {
            setBackgroundColor(getResources().getColor(android.R.color.black));
            setClickable(false);
            this.setContentDescription("Correctly marked black square");
            numBlackSquares--;
            return true;
        } else {
            toggleX();
            this.setContentDescription("Incorrectly marked cell");
            return false;
        }
    }

    public void toggleX() {
        if (checked) {
            setText("");
            this.setContentDescription("Empty cell");
        } else {
            setText("X");
            setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            this.setContentDescription("Marked with X");
        }
        checked = !checked;
    }
}
