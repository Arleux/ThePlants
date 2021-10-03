package com.arleux.byart.calendar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.arleux.byart.Plant;
import com.arleux.byart.PlantFragment;
import com.arleux.byart.R;
import com.arleux.byart.WateringService;
import com.arleux.byart.WateringWorker;

import java.util.concurrent.TimeUnit;

public class CalendarLastWateringArleuxFragment extends CalendarArleuxFragment { // для указания последнего полива цветка при его создании
    private static CalendarLastWateringArleuxFragment mCalendarLastWateringArleuxFragment;
    private static PlantFragment mPlantFragment;
    private CalendarLastWateringArleuxFragment mFragment = this;
    private CalendarArleux mCalendarArleux;
    private Plant mPlant;

    public static final CalendarLastWateringArleuxFragment getInstance(Plant plant, PlantFragment plantFragment){
        mPlantFragment = plantFragment;
        return mCalendarLastWateringArleuxFragment == null ? new CalendarLastWateringArleuxFragment(plant) : mCalendarLastWateringArleuxFragment;
    }
    private CalendarLastWateringArleuxFragment(Plant plant){
        super(plant);
        mPlant = plant;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCalendarArleux = super.getCalendarArleux();
        backPressed(); //переопределние нажатия кнопки "Back", работает именно в этом перепоределенном методе
    }
    @Override
    public void onDestroy() { //чтобы обновить PlantFragment, когда календарь закроется
        super.onDestroy();
    }

    @Override public void setActionForNextButton(View view){
        CalendarArleuxFragment.getNextButton(view).setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) { //это когда я уже выбрал дату и нажал продолжить
        if (mCalendarArleux.getCurrentDate().isBefore(mCalendarArleux.calendarDate())) { // чтобы не выбирали дату из будущего
            mPlant.getDaysWatering().add(mCalendarArleux.getCurrentDate()); //добавляю в список с днями полива выбранный день
            mPlantFragment.addPlant(mPlantFragment.getPlantPicture(), mPlantFragment.getPlantSpecies(), mCalendarArleux.getCurrentDate());
            mPlantFragment.updateView();

        /*    WateringService wateringService = new WateringService();
            Intent intent = WateringService.newIntent(getContext());
            wateringService.startService(intent);

         */
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(WateringWorker.class) //Для китайских тлф нужно копаться в настройках приложения
                    .setInitialDelay(mPlant.getDayForWatering(), TimeUnit.SECONDS)
                    .build();
            WorkManager.getInstance().enqueue(workRequest);

            getFragmentManager().beginTransaction().remove(mFragment).commit(); //удаляю фрагмент с календарем
        }
        else {
            Toast.makeText(getActivity(), R.string.dialog_calendar_choose_actual_date, Toast.LENGTH_SHORT).show();
        }
    }

    private void backPressed() { //переопределние нажатия кнопки "Back"
        getView().setFocusableInTouchMode(true); //без этих двух методов не будет работать
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) { //зачем-то обязательно нужно, иначе вызлвется два раза: и для DOWN и для UP
                    if (keyCode == keyEvent.KEYCODE_BACK) {
                        Toast.makeText(getActivity(), R.string.dialog_calendar_choose_date, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
