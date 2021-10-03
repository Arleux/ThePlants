package com.arleux.byart;

import java.util.ArrayList;
import java.util.List;

public class PlantSpecies {
    private static List<Integer> species;

    public static List<Integer> getPlantSpecies(){
        species = new ArrayList<>();
        species.add(R.string.kaktus);
        species.add(R.string.zamik);
        return species;
    }
}
