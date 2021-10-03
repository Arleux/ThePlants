package com.arleux.byart;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ThePlantsActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, ThePlantsActivity.class);
        //флаги, чтобы нельзя было возвратиться из главного окна в окно с авторизацией по кнопке
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    protected Fragment createFragment(){
        return ThePlantsFragment.newInstance();
    }

}