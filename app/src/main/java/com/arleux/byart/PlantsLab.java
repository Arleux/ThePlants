package com.arleux.byart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arleux.byart.database.DataBaseCursorWrapper;
import com.arleux.byart.database.DataBasePlantsHelper;
import com.arleux.byart.database.DataBaseScheme;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlantsLab {
    private static SQLiteDatabase mPlantsDatabase;
    private List<Plant> mPlants;
    private static PlantsLab mPlantsLab;
    private static List<Species> mSpeciesList;

    public static PlantsLab get(Context context){ // Синглтон
        if(mPlantsLab == null)
            mPlantsLab = new PlantsLab(context);
        return mPlantsLab;
    }
    private PlantsLab(Context context){
        mPlantsDatabase = new DataBasePlantsHelper(context).getWritableDatabase();

/*        SpeciesCreation speciesCreation = new SpeciesCreation(context); //Здесь храню виды растений в бд, но сейчас это в конструкторе описано и будет добавлять туда виды каждый раз при запуске.
//логичнее просто брать из ресурсов виды растений, а не из бд
        mSpeciesList = speciesCreation.getSpeciesList();
        for (Species species: mSpeciesList){
            addSpecies(species); //добавляю в бд все виды растений
        }
 */
        SpeciesCreation speciesCreation = new SpeciesCreation(context);
        mSpeciesList = speciesCreation.getSpeciesList();
    }
    private static ContentValues getPlantsContentValues(Plant plant){
        ContentValues values = new ContentValues(); //класс, который предназначен для хранения типов данных, которые могут содержаться в базах данных SQLite
        values.put(DataBaseScheme.PlantsTable.Cols.ACCOUNT_ID, plant.getAccountId());
        values.put(DataBaseScheme.PlantsTable.Cols.PLANT_ID, plant.getId().toString());
        values.put(DataBaseScheme.PlantsTable.Cols.NAME, plant.getName());
        values.put(DataBaseScheme.PlantsTable.Cols.PHOTO, plant.getPhoto());
        values.put(DataBaseScheme.PlantsTable.Cols.SPECIES, plant.getSpecies().species());
        values.put(DataBaseScheme.PlantsTable.Cols.DEFAULT_WATERING_INTERVAL, plant.getDefaultWateringInterval());
        values.put(DataBaseScheme.PlantsTable.Cols.DAY_FOR_WATERING, serialize(plant.getDayForWatering())); // следующий день полива
        values.put(DataBaseScheme.PlantsTable.Cols.IS_DEFAULT, serialize(plant.isDefault()));
        values.put(DataBaseScheme.PlantsTable.Cols.WATERING_DAYS, serialize(plant.getDaysWatering()));
        return values;
        //потом я эти значения values заношу прямо в БД, "ключ" должен соответствовать столбцу БД
    }

    private static ContentValues getAccountContentValues(String account_id){
        ContentValues values = new ContentValues(); //класс, который предназначен для хранения типов данных, которые могут содержаться в базах данных SQLite
        values.put(DataBaseScheme.AccountTable.Cols.ACCOUNT_UID, account_id);
        return values;
        //потом я эти значения values заношу прямо в БД, "ключ" должен соответствовать столбцу БД
    }
    private static ContentValues getSpeciesContentValues(Species species){
        ContentValues values = new ContentValues();
        values.put(DataBaseScheme.SpeciesTable.Cols.SPECIES, serialize(species));
        values.put(DataBaseScheme.SpeciesTable.Cols.DEFAULT_WATERING_INTERVAL, species.getDefaultWateringInterval());
        values.put(DataBaseScheme.SpeciesTable.Cols.CUSTOM_WATERING_INTERVAL, species.getCustomWateringInterval());
        return values;
    }
    public static DataBaseCursorWrapper queryDbPlants(Context context, String whereClause, String[] whereArgs){//Делаю запрос в БД с помощью класса Cursor
        mPlantsDatabase = new DataBasePlantsHelper(context).getWritableDatabase();
        Cursor cursor = mPlantsDatabase.query( //Данный класс позволяет сделать запрос, но чтобы постоянно не писать один и тот же код для других запросов мы используем класс-обертку, которому и передаём cursor
                DataBaseScheme.PlantsTable.NAME,
                null, //если null, то все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new DataBaseCursorWrapper(cursor); //Использую класс-обертку
    }
    public static DataBaseCursorWrapper queryDbAccount(Context context, String whereClause, String[] whereArgs){//Делаю запрос в БД с помощью класса Cursor
        mPlantsDatabase = new DataBasePlantsHelper(context).getWritableDatabase();
        Cursor cursor = mPlantsDatabase.query( //Данный класс позволяет сделать запрос, но чтобы постоянно не писать один и тот же код для других запросов мы используем класс-обертку, которому и передаём cursor
                DataBaseScheme.AccountTable.NAME,
                null, //если null, то все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new DataBaseCursorWrapper(cursor); //Использую класс-обертку
    }
    public static DataBaseCursorWrapper queryDbSpecies(Context context, String whereClause, String[] whereArgs){//Делаю запрос в БД с помощью класса Cursor
        mPlantsDatabase = new DataBasePlantsHelper(context).getWritableDatabase();
        Cursor cursor = mPlantsDatabase.query( //Данный класс позволяет сделать запрос, но чтобы постоянно не писать один и тот же код для других запросов мы используем класс-обертку, которому и передаём cursor
                DataBaseScheme.SpeciesTable.NAME,
                null, //если null, то все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new DataBaseCursorWrapper(cursor); //Использую класс-обертку
    }
    public List<Plant> getPlants(Context context){
        mPlants = new ArrayList<>();
        DataBaseCursorWrapper cursor = queryDbPlants(context, null, null);
        try {
            if(cursor.getCount()==0)
                return null;
            else {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    mPlants.add(cursor.getPlant());
                    cursor.moveToNext();
                }
            }
        }
        finally {
            cursor.close();
        }
        return mPlants;
    }
    public List<Plant> getPlants(Context context, String accountId) {
        mPlants = new ArrayList<>();
        DataBaseCursorWrapper cursor = queryDbPlants(context, DataBaseScheme.PlantsTable.Cols.ACCOUNT_ID +" = ?",
                new String[]{accountId});
        try {
            if (cursor.getCount() == 0)
                return null;
            else {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    mPlants.add(cursor.getPlant());
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        return mPlants;
    }

    public void addPlant(Plant plant){
        ContentValues values = getPlantsContentValues(plant);
        mPlantsDatabase.insert(DataBaseScheme.PlantsTable.NAME, null, values);
    }
    public void updatePlant(Plant plant){ //обновление данных в БД
        ContentValues values = getPlantsContentValues(plant);
        mPlantsDatabase.update(DataBaseScheme.PlantsTable.NAME, values,
                DataBaseScheme.PlantsTable.Cols.PLANT_ID + " = ?",
                new String[] {plant.getId().toString()}
        );
    }
    public Plant getPlant(Context context, UUID id){
        DataBaseCursorWrapper cursor = queryDbPlants(context, DataBaseScheme.PlantsTable.Cols.PLANT_ID+" = ?",
                new String[]{id.toString()});
        try{
            if (cursor.getCount() == 0)
                return null;
            else {
                cursor.moveToFirst();
                return cursor.getPlant();
            }
        }
        finally {
            cursor.close();
        }
    }
    public static void logInUser(Context context, String account_id){ //добавляю в бд значение
        ContentValues values = getAccountContentValues(account_id);
        mPlantsDatabase.insert(DataBaseScheme.AccountTable.NAME, null, values);
    }
    public static void signOutUser(String accountId){ //удаляю из бд значение
        mPlantsDatabase.delete(DataBaseScheme.AccountTable.NAME, DataBaseScheme.AccountTable.Cols.ACCOUNT_UID + " = ?",
                new String[]{accountId}
        );
    }
    public static boolean isAnyUserLogInApplication(Context context){ //проверяю есть ли в бд хоть один элемент(работаю по одному элементу)
        DataBaseCursorWrapper cursor = queryDbAccount(context, null,
                null
        );
        if(cursor.getCount() == 0) {
            return false;
        }
        else{
            cursor.moveToFirst(); //обязательно нужно двигать курсор
            return cursor.isAnyUserLogInApplication();
        }
    }
    public static String getIdLogInUser(Context context){ //получаю конкретное значение из бд
        DataBaseCursorWrapper cursor = queryDbAccount(context, null,
                null
        );
        try {
            cursor.moveToFirst();
            return cursor.getIdLogInUser(); //возвращаю значение id из таблицы для данного курсора
        }
        finally {
            cursor.close();
        }
    }
    public static Species getSpecies(Context context, Species species){
        DataBaseCursorWrapper cursor = queryDbSpecies(context, DataBaseScheme.SpeciesTable.Cols.SPECIES + " = ?",
                new String[]{species.toString()});
        try {
            cursor.moveToFirst();
            return cursor.getSpecies(); //возвращаю значение id из таблицы для данного курсора
        }
        finally {
            cursor.close();
        }
    }
    public void addSpecies(Species species){
        ContentValues values = getSpeciesContentValues(species);
        mPlantsDatabase.insert(DataBaseScheme.SpeciesTable.NAME, null, values);
    }
    public static List<Species> getSpeciesList(){
        return mSpeciesList;
    }

    private static byte[] serialize(Object obj) { //Чтобы записать объект Java в БД как byte[];
        byte[] byteArray  = new byte[0];
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            byteArray = b.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
