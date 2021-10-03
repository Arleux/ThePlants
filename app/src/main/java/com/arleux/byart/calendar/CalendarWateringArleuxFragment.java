package com.arleux.byart.calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.arleux.byart.Plant;
import com.arleux.byart.PlantFragment;
import com.arleux.byart.R;

public class CalendarWateringArleuxFragment extends CalendarArleuxFragment { // чтобы посмотреть историю полива
    private static CalendarWateringArleuxFragment mCalendarWateringArleuxFragment;
    private static PlantFragment mPlantFragment;
    private CalendarArleux mCalendarArleux;
    private Plant mPlant;
    private CalendarWateringArleuxFragment mFragment = this;

    public static final CalendarWateringArleuxFragment getInstance(Plant plant, PlantFragment plantFragment){
        mPlantFragment = plantFragment;
        return mCalendarWateringArleuxFragment == null ? new CalendarWateringArleuxFragment(plant) : mCalendarWateringArleuxFragment;
    }
    private CalendarWateringArleuxFragment(Plant plant){
        super(plant);
        mPlant = plant;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCalendarArleux = super.getCalendarArleux();
    }
    @Override
    public void setActionForNextButton(View view){
        CalendarArleuxFragment.getCloseButton(view).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) { //это когда я уже выбрал дату и нажал продолжить
            getFragmentManager().beginTransaction().remove(mFragment).commit(); //удаляю фрагмент с календарем
    }
}
