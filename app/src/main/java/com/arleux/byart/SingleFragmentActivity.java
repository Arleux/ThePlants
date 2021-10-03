package com.arleux.byart;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){
            return R.layout.activity_fragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId()); //это метод из этого класса, если что

        FragmentManager fm = getSupportFragmentManager(); //управляет фрагментами
        Fragment fragment = fm.findFragmentById(R.id.fragment_container); //ищу фрагмент по идентификатору представления. Если активность удалится(при повороте экрана например) и воссоздастся, то фрагмент найдется, так как при удалении активности он попадает в стек
        if(fragment==null){ //если не нашел, то создаю его
            fragment = createFragment(); //упрощение как раз здесь, выражено через абстрактный метод, который создает объект той того фрагмента, с которым идет работа, не нужно сто раз писать один код
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit(); // Создать новую транзакцию фрагмента, включить в нее одну операцию add, а затем закрепить
        }
    }
    @Override
    public void onStart(){
        super.onStart();
    }
}
