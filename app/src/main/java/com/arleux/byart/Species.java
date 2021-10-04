package com.arleux.byart;

import android.graphics.drawable.Drawable;

import java.io.IOException;

public class Species {
    private String mSpecies;
    private int mDefaultWateringInterval;
    private int mCustomWateringInterval;
    private Drawable mImage;

    public Species(String species){
        mSpecies = species;
    }

    public String species() {
        return mSpecies;
    }

    public Drawable getImage() {
        return mImage;
    }

    public void setImage(Drawable image) {
        mImage = image;
    }

    public int getDefaultWateringInterval() {
        return mDefaultWateringInterval;
    }

    public void setDefaultWateringInterval(int defaultWateringInterval) {
        mDefaultWateringInterval = defaultWateringInterval;
    }

    public int getCustomWateringInterval() {
        return mCustomWateringInterval;
    }

    public void setCustomWateringInterval(int customWateringInterval) {
        mCustomWateringInterval = customWateringInterval;
    }
}
