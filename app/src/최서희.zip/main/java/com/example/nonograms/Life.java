package com.example.nonograms;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Life {

    private int life;
    private final int maxLife;
    private final LinearLayout lifeContainer;
    private final Context context;

    public Life(int initialLife, LinearLayout lifeContainer, Context context) {
        this.life = initialLife;
        this.maxLife = initialLife;
        this.lifeContainer = lifeContainer;
        this.context = context;
        updateLifeUI();
    }

    public void decreaseLife() {
        if (life > 0) {
            life--;
            updateLifeUI();
        }
    }

    public void increaseLife() {
        if (life < maxLife) {
            life++;
            updateLifeUI();
        }
    }

    public int getLife() {
        return life;
    }

    private void updateLifeUI() {
        lifeContainer.removeAllViews();

        for (int i = 0; i < life; i++) {
            ImageView heart = new ImageView(context);
            heart.setImageResource(R.drawable.heart_full);
            heart.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            lifeContainer.addView(heart);
        }

        for (int i = life; i < maxLife; i++) {
            ImageView emptyHeart = new ImageView(context);
            emptyHeart.setImageResource(R.drawable.heart_empty);
            emptyHeart.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            lifeContainer.addView(emptyHeart);
        }
    }

    public boolean isGameOver() {
        return life == 0;
    }
}
