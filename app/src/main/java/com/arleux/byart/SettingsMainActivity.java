package com.arleux.byart;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class SettingsMainActivity extends SingleFragmentActivity {
    private static final String AMIHAVEACCOUNT = "amIhaveAccount from ThePlantsFragment";

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, SettingsMainActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new SettingsMainFragment();
    }
}
