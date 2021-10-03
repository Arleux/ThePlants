package com.arleux.byart.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.widget.GridLayout;

import com.arleux.byart.Plant;
import com.arleux.byart.PlantsLab;
import com.arleux.byart.Species;
import com.arleux.byart.database.DataBaseScheme.PlantsTable;
import com.arleux.byart.database.DataBaseScheme.AccountTable;
import com.arleux.byart.database.DataBaseScheme.SpeciesTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

public class DataBaseCursorWrapper extends CursorWrapper {
    private Plant plant;
    private Species mSpecies;

    public DataBaseCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public boolean isAnyUserLogInApplication(){
        return getCount() != 0;
    } //считает какой элемент по счету для данного курсора(у меня будет курсор всего смещён на одну позицию)
    public String getIdLogInUser(){
        return getString(getColumnIndex(AccountTable.Cols.ACCOUNT_UID)); // возвращаю значение из таблички для данного курсора
    }
    public Species getSpecies(){
        Species species = (Species) deserialize(getBlob(getColumnIndex(SpeciesTable.Cols.SPECIES)));
        int defaultWatering = getInt(getColumnIndex(SpeciesTable.Cols.DEFAULT_WATERING_INTERVAL));
        int customWateringInterval = getInt(getColumnIndex(SpeciesTable.Cols.CUSTOM_WATERING_INTERVAL));

        return species;
    }
    public Plant getPlant(){
        String accountId = getString(getColumnIndex(PlantsTable.Cols.ACCOUNT_ID));
        String id = getString(getColumnIndex(PlantsTable.Cols.PLANT_ID));
        String name = getString(getColumnIndex(PlantsTable.Cols.NAME));
        String photo = getString(getColumnIndex(PlantsTable.Cols.PHOTO));
//        Species species = (Species) deserialize(getBlob(getColumnIndex(PlantsTable.Cols.SPECIES))); //не могу так сделать, потому что Object не кастится к нестандартным джававским классам
        String species = getString(getColumnIndex(PlantsTable.Cols.SPECIES));
        for (Species s: PlantsLab.getSpeciesList()){
            if (s.species().equals(species))
                mSpecies = s;
        }

        int defaultWateringInterval = getInt(getColumnIndex(PlantsTable.Cols.DEFAULT_WATERING_INTERVAL));
        LocalDate dayForWatering = (LocalDate) deserialize(getBlob(getColumnIndex(PlantsTable.Cols.DAY_FOR_WATERING)));
        boolean isDefault = (boolean) deserialize(getBlob(getColumnIndex(PlantsTable.Cols.IS_DEFAULT)));
        ArrayList<LocalDate> wateringDays = (ArrayList<LocalDate>) deserialize(getBlob(getColumnIndex(PlantsTable.Cols.WATERING_DAYS)));

        plant = new Plant(UUID.fromString(id)); //чтобы не наплодить одинаковых объектов
        plant.setAccountId(accountId);
        plant.setName(name);
        plant.setSpecies(mSpecies);
        plant.setDefaultWateringInterval(defaultWateringInterval);
        plant.setPhoto(photo);
        plant.setDayForWatering(dayForWatering);
        plant.setIsDefault(isDefault);
        plant.setDaysWatering(wateringDays);

        return plant;
    }

    private static Object deserialize(byte[] bytes){ // для расшифровки массивов byte[]
        Object obj = new Object();
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                obj = o.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
