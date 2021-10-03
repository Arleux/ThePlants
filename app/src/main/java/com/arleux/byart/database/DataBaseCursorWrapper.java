package com.arleux.byart.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.arleux.byart.Plant;
import com.arleux.byart.Species;
import com.arleux.byart.database.DataBaseScheme.PlantsTable;
import com.arleux.byart.database.DataBaseScheme.AccountTable;
import com.arleux.byart.database.DataBaseScheme.SpeciesTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

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
        String species_id = getString(getColumnIndex(SpeciesTable.Cols.SPECIES));
        int defaultWatering = getInt(getColumnIndex(SpeciesTable.Cols.DEFAULT_WATERING));

        mSpecies = new Species(Integer.valueOf(species_id));
        mSpecies.setDefaultWateringInterval(defaultWatering);
        return mSpecies;
    }
    public Plant getPlant(){
        String accountId = getString(getColumnIndex(PlantsTable.Cols.ACCOUNT_ID));
        String id = getString(getColumnIndex(PlantsTable.Cols.PLANT_ID));
        String name = getString(getColumnIndex(PlantsTable.Cols.NAME));
        String photo = getString(getColumnIndex(PlantsTable.Cols.PHOTO));
        String species = getString(getColumnIndex(PlantsTable.Cols.SPECIES));
        int defaultWatering = getInt(getColumnIndex(PlantsTable.Cols.DEFAULT_WATERING));
        LocalDate dayForWatering = (LocalDate) deserialize(getBlob(getColumnIndex(PlantsTable.Cols.DAY_FOR_WATERING)));
        int isDefault = getInt(getColumnIndex(PlantsTable.Cols.IS_DEFAULT));
        ArrayList<LocalDate> wateringDays = (ArrayList<LocalDate>) deserialize(getBlob(getColumnIndex(PlantsTable.Cols.WATERING_DAYS)));

        plant = new Plant(UUID.fromString(id)); //чтобы не наплодить одинаковых объектов
        plant.setAccountId(accountId);
        plant.setName(name);
        plant.setSpecies(species);
        plant.setDefaultWatering(defaultWatering);
        plant.setPhoto(photo);
        plant.setDayForWatering(dayForWatering);
        plant.setIsDefault(isDefault);
        plant.setDaysWatering(wateringDays);

        return plant;
    }

    public static Object deserialize(byte[] bytes){ // для расшифровки массивов byte[]
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
