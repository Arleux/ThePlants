package com.arleux.byart;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class RegisterActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment(){
        return RegisterFragment.newInstance();
    }
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        return intent;
    }
}
