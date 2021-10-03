package com.arleux.byart;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class PlantActivity extends SingleFragmentActivity{
    final private static String TAG = "plantForPlantFragment";

    public static Intent newIntent(Context context, UUID id){
        Intent intent = new Intent(context, PlantActivity.class);
        intent.putExtra(TAG, id);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PlantFragment.newInstance();
    }

}
