package com.arleux.byart;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Plant {
    private ArrayList<Plant> mPlants;
    private String mName;
    private String mPhoto;
    private Species mSpecies;
    private int mDefaultWateringInterval;
    private String mAccountId;
    private UUID mId;
    private boolean mIsDefault; //дефолтныое изображение цветка или нет
    private LocalDate mDayForWatering; //день для полива

    private ArrayList<LocalDate> daysWatering = new ArrayList<>(); // список со всеми днями полива цветка

    public Plant(){
        this(UUID.randomUUID());
    }
    public Plant(UUID id){
        mId = id;
    }
    public ArrayList<Plant> getPlants() {
        return mPlants;
    }

    public void setDaysWatering(ArrayList<LocalDate> daysWatering) {
        this.daysWatering = daysWatering;
    }
    public ArrayList<LocalDate> getDaysWatering() {
        return daysWatering;
    }

    public void setPlants(ArrayList<Plant> plants) {
        mPlants = plants;
    }

    public UUID getId() {
        return mId;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setAccountId(String accountId) {
        mAccountId = accountId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public Species getSpecies() {
        return mSpecies;
    }

    public void setSpecies(Species species) {
        mSpecies = species;
    }

    public int getDefaultWateringInterval() {
        return mDefaultWateringInterval;
    }

    public void setDefaultWateringInterval(int defaultWateringInterval) {
        mDefaultWateringInterval = defaultWateringInterval;
    }

    public LocalDate getDayForWatering() {
        return mDayForWatering;
    }

    public void setDayForWatering(LocalDate dayForWatering) {
        mDayForWatering = dayForWatering;
    }

    public boolean isDefault() {
        return mIsDefault;
    }

    public void setIsDefault(boolean isDefault) {
        mIsDefault = isDefault;
    }
}
