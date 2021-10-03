package com.arleux.byart;

import java.util.ArrayList;
import java.util.List;

public class SpeciesCreation {
    private List<Species> mSpeciesList;

    public SpeciesCreation(){
        mSpeciesList = new ArrayList<>();

        Species zamik = new Species(Integer.valueOf(R.string.zamik));
        zamik.setDefaultWateringInterval(25);
        mSpeciesList.add(zamik);

        Species kaktus = new Species(Integer.valueOf(R.string.kaktus));
        kaktus.setDefaultWateringInterval(30);
        mSpeciesList.add(kaktus);
    }
    public List<Species> getSpeciesList(){
        return mSpeciesList;
    }
}
