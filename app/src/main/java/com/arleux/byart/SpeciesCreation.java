package com.arleux.byart;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SpeciesCreation {
    private List<Species> mSpeciesList;
    private InputStream mInputStream;

    public SpeciesCreation(Context context) {
        mSpeciesList = new ArrayList<>();

        Species kaktus = new Species(context.getResources().getString(R.string.kaktus));
        kaktus.setImage(uploadAssetImage(context, "plantsPictures/kaktus.png"));
        kaktus.setDefaultWateringInterval(20);
        mSpeciesList.add(kaktus);

        Species zamik = new Species(context.getResources().getString(R.string.zamik));
        zamik.setDefaultWateringInterval(5);
        zamik.setImage(uploadAssetImage(context, "plantsPictures/zamik.png"));
        mSpeciesList.add(zamik);
    }

    public List<Species> getSpeciesList() {
        return mSpeciesList;
    }

    public static Species getDefaultSpecies(Context context){
        InputStream inputStream = null;
        Species defaultPlant = new Species(context.getResources().getString(R.string.defaultPlant));
        try {
            inputStream = context.getAssets().open("default/default.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultPlant.setImage(Drawable.createFromStream(inputStream, null));

        return defaultPlant;
    }

    private Drawable uploadAssetImage(Context context, String fileName) {
        try {
            mInputStream = context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Drawable.createFromStream(mInputStream, null);
    }
}
