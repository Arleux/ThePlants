package com.arleux.byart;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface.OnClickListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class MainActivity extends SingleFragmentActivity implements MainFragment.Callbacks{
    private static final String AMIHAVEACCOUNT = "amIhaveAccount from ThePlantsFragment";
    private FirebaseAuth mAuthFromAuthorization;
    public static FirebaseAuth mAuth;
    private String mUserIdSQLite;
    private String DEFAULT_USER_ID = "default";

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }

    @Override
    public void onPlantSelected(Plant plant){
        startActivity(PlantActivity.newIntent(this, plant.getId()));
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    public static Intent newIntentForStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = ThePlantsFragment.mAuth;
        mUserIdSQLite = PlantsLab.getIdLogInUser(this); //получаю то значение из бд
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //при нажатии на кнопку назад из главного окна перейду к авторизации
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
            quitDialog.setPositiveButton("Да", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //             FirebaseUser user = (FirebaseUser) getIntent().getParcelableExtra(TAG); //получает данные аккаунта из бд, переданные из ThePlantsFragment в эту активность, чтобы выйти из этого акканута
                    PlantsLab.signOutUser(mUserIdSQLite); //при входе добавляю в бд этого пользователя, при выходе удалю: по такому критерию я проверяю нужно ли выполнять пользователю авторизацию при входе в приложение
                    if (mAuth != null){
                        mAuth.signOut(); //выход из аккаунта: такой способ через public static плохой, но по-другому как хз
                    }
                    MainActivity.super.onBackPressed();
                    startActivity(ThePlantsActivity.newIntent(MainActivity.this)); //переходит к авторизации
                }
            });
            quitDialog.setNegativeButton("Нет", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            if (getIntent().getBooleanExtra(AMIHAVEACCOUNT, false))
                quitDialog.setTitle(R.string.sign_out_have_account);
            else
                quitDialog.setTitle(R.string.sign_out_not_have_account);
            quitDialog.show();
        }
        return true;
    }
}
