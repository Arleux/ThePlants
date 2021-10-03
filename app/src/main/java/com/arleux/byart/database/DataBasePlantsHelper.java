package com.arleux.byart.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.arleux.byart.database.DataBaseScheme.PlantsTable;
import com.arleux.byart.database.DataBaseScheme.AccountTable;
import com.arleux.byart.database.DataBaseScheme.SpeciesTable;


public class DataBasePlantsHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "plantsBase.db";

    public DataBasePlantsHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PlantsTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                PlantsTable.Cols.ACCOUNT_ID +","+
                PlantsTable.Cols.PLANT_ID + ","+
                PlantsTable.Cols.NAME +","+
                PlantsTable.Cols.PHOTO +","+
                PlantsTable.Cols.SPECIES +","+
                PlantsTable.Cols.DEFAULT_WATERING +","+
                PlantsTable.Cols.DAY_FOR_WATERING +","+
                PlantsTable.Cols.IS_DEFAULT +","+
                PlantsTable.Cols.WATERING_DAYS+")"
        );
        db.execSQL("create table " + AccountTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                AccountTable.Cols.ACCOUNT_UID +")"
        );
        db.execSQL("create table " + SpeciesTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                SpeciesTable.Cols.SPECIES +","+
                SpeciesTable.Cols.DEFAULT_WATERING +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
