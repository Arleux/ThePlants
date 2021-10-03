package com.arleux.byart;

public class Species {
    private String mSpecies;
    private int mDefaultWateringInterval;
    private int mCustomWateringInterval;

    public Species(String species){
        mSpecies = species;
    }

    public String species() {
        return mSpecies;
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
