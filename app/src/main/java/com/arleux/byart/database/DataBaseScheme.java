package com.arleux.byart.database;

public class DataBaseScheme {
    public static final class PlantsTable{ // нужен только для объявления строковых констант, необходимых для создания таблицы
        public static final String NAME = "plants";
        public static final class Cols { //столбцы
            public static final String ACCOUNT_ID = "account_id";
            public static final String PLANT_ID = "id";
            public static final String NAME = "name";
            public static final String PHOTO = "photo";
            public static final String SPECIES = "species";
            public static final String DEFAULT_WATERING = "default_watering";
            public static final String DAY_FOR_WATERING = "day_to_watering";
            public static final String IS_DEFAULT = "default_plant"; //если 1, то дефолтный цветок(пустой эл-т для добавления)
            public static final String WATERING_DAYS = "watering_days"; // дни полива
        }
    }
    public static final class SpeciesTable{
        public static final String NAME = "plants_species";
        public static final class Cols { //столбцы
            public static final String SPECIES = "species";
            public static final String DEFAULT_WATERING = "default_watering";
        }
    }
    public static final class AccountTable{
        public static final String NAME = "account";
        public static final class Cols { //столбцы
            public static final String ACCOUNT_UID = "id";
        }
    }
}
