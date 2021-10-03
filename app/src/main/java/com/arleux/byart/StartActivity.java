package com.arleux.byart;

import android.app.Activity;
import android.os.Bundle;

public class StartActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(PlantsLab.isAnyUserLogInApplication(this) == false){ //проверяет вошел ли пользователь уже в аккаунт, если да, то нужно загрузить стартовое окно, а не с авторизацией
            startActivity(ThePlantsActivity.newIntent(this));
        }
        else{
            startActivity(MainActivity.newIntentForStart(this));
        }
        finish();
    }
}
