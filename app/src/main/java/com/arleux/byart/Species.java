package com.arleux.byart;

public class Species {
    private Integer mSpecies;
    private int mDefaultWateringInterval;
    private int mCustomWatering;

    public Species(Integer species){
        mSpecies = species;
    }
    public Integer getSpecies() {
        return mSpecies;
    }

    public void setSpecies(Integer species) {
        mSpecies = species;
    }

    public int getDefaultWateringInterval() {
        return mDefaultWateringInterval;
    }

    public void setDefaultWateringInterval(int defaultWateringInterval) {
        mDefaultWateringInterval = defaultWateringInterval;
    }

    public int getCustomWatering() {
        return mCustomWatering;
    }

    public void setCustomWatering(int customWatering) {
        mCustomWatering = customWatering;
    }
}
