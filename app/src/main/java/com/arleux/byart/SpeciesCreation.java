package com.arleux.byart;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SpeciesCreation {
    private List<Species> mSpeciesList;

    public SpeciesCreation(Context context){
        mSpeciesList = new ArrayList<>();

        Species zamik = new Species(context.getResources().getString(R.string.zamik));
        zamik.setDefaultWateringInterval(25);
        mSpeciesList.add(zamik);

        Species kaktus = new Species(context.getResources().getString(R.string.kaktus));
        kaktus.setDefaultWateringInterval(30);
        mSpeciesList.add(kaktus);
    }
    public List<Species> getSpeciesList(){
        return mSpeciesList;
    }
}
