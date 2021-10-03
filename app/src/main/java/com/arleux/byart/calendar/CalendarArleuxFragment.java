package com.arleux.byart.calendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.arleux.byart.Plant;
import com.arleux.byart.R;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;


public class CalendarArleuxFragment extends Fragment implements View.OnClickListener {
    private CalendarArleux mCalendarArleux;
    private ArrayList<TextView> days = new ArrayList<>(); //textView
    private ArrayList<ImageView> daysWatering_iv = new ArrayList<>(); //ImageView с капелькой
    private ArrayList<LocalDate> daysWatering = new ArrayList<>(); //Дни полива
    private ArrayList<ImageView> activeDay_line = new ArrayList<>(); //ImageView с линией означает, что данный день выбран
    private LocalDate currentDate; //текущая дата, обновляется в updateView();
    private static final LocalDate mCalendarDate = LocalDate.now(); //календарная дата
    private Plant mPlant;
    private CalendarArleuxFragment mFragment = this;
    private TextView month_tv;
    private TextView year_tv;
    private ImageView prevMonthArrow;
    private ImageView nextMonthArrow;
    public static TextView next;

    public static Fragment newInstance(Plant plant) {
    return new CalendarArleuxFragment(plant);
}

    public CalendarArleuxFragment(Plant plant){
        mPlant = plant;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCalendarArleux = CalendarArleux.getInstance();
        setCalendarDate(mCalendarDate.getYear(), mCalendarDate.getMonthValue()-1, mCalendarDate.getDayOfMonth());
        daysWatering = mPlant.getDaysWatering();
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        month_tv = (TextView) v.findViewById(R.id.month_tv);
        year_tv = (TextView) v.findViewById(R.id.year_tv);
        prevMonthArrow = (ImageView) v.findViewById(R.id.navigate_before_arrow_iv);
        nextMonthArrow = (ImageView) v.findViewById(R.id.navigate_next_arrow_iv);
        next = getNextButton(v);

        fillDays_tv(v); // Добавить в коллекцию все названия дней
        fillDaysWatering_iv(v); // Добавить в коллекцию все дни полива
        fillActiveDay_line(v); // Добавить в коллекцию все черточки под днями

        for (ImageView iv: daysWatering_iv) //чтобы все капельки не отображались
            iv.setVisibility(View.INVISIBLE);

        prevMonthArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCalendarArleux.getCurrentMonth() == 1 && mCalendarArleux.getCurrentYear()>2021) {
                    mCalendarArleux.setCurrentMonth(12);
                    mCalendarArleux.setCurrentYear(mCalendarArleux.getCurrentYear()-1);
                }
                else if(mCalendarArleux.getCurrentMonth() == 1 && mCalendarArleux.getCurrentYear() == 2021){

                }
                else
                    mCalendarArleux.setCurrentMonth(mCalendarArleux.getCurrentMonth()-1);
                monthTransitionUpdateDay(); //установить активный день в зав-ти от месяца(если календарный - дата, если другой - то первое число)
                for (ImageView iv:daysWatering_iv){ //чтобы для других месяцев не отображались эти дни полива
                    iv.setVisibility(View.INVISIBLE);
                }
                updateView();
            }
        });
        nextMonthArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCalendarArleux.getCurrentMonth()== 12) {
                    mCalendarArleux.setCurrentMonth(1);
                    mCalendarArleux.setCurrentYear(mCalendarArleux.getCurrentYear()+1);
                }
                else
                    mCalendarArleux.setCurrentMonth(mCalendarArleux.getCurrentMonth()+1);
                monthTransitionUpdateDay(); //установить активный день в зав-ти от месяца(если календарный - дата, если другой - то первое число)

                for (ImageView iv:daysWatering_iv){ //чтобы для других месяцев не отображались эти дни полива
                    iv.setVisibility(View.INVISIBLE);
                }

                updateView();
            }
        });
        for (final TextView textView: days){
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCalendarArleux.setCurrentDay(days.indexOf(textView)-mCalendarArleux.getDifferenceFromPrevMonth()+1);
                    updateView();
                }
            });
        }
        setActionForNextButton(v); //действие при клике на кнопку next;

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        updateView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) { //это когда я уже выбрал дату и нажал продолжить
        getFragmentManager().beginTransaction().remove(mFragment).commit(); //удаляю фрагмент с календарем
    }
    public void setActionForNextButton(View v){ //действие при клике на кнопку next;
        getCloseButton(v).setOnClickListener(this);
    }
    public static TextView getNextButton(View view){ //возвр-т кнопку next календаря
        return (TextView) view.findViewById(R.id.dialog_calendar_watering_calendar_view_next_tv);
    }
    public static TextView getCloseButton(View view){ //возвр-т кнопку close календаря
        TextView closeButton = (TextView) view.findViewById(R.id.dialog_calendar_watering_calendar_view_next_tv);
        closeButton.setText(R.string.close);
        return closeButton;
    }

    private void updateView(){
        currentDate = mCalendarArleux.getCurrentDate();
        mCalendarArleux.currentDate(mCalendarArleux.getCurrentMonth(), mCalendarArleux.getCurrentYear());
        month_tv.setText(monthName(mCalendarArleux.getCurrentMonth()));
        year_tv.setText(String.valueOf(mCalendarArleux.getCurrentYear()));
        highlightDay(); //черточка над нужным днём;
        calendarView(); //настроит правильно календарик
    }

    private void calendarView() {
        int day = 0; //день, которые нумеруется с первой клетки календаря, а не с 1
        int daysOfCurrentMonth;

        if (mCalendarArleux.getCurrentMonth() == 1)
            daysOfCurrentMonth = mCalendarArleux.getDaysInMonth(mCalendarArleux.getCurrentMonth(), mCalendarArleux.getCurrentYear());
        else
            daysOfCurrentMonth = mCalendarArleux.getDaysInMonth(mCalendarArleux.getCurrentMonth()-1, mCalendarArleux.getCurrentYear());
        int prevDays = (daysOfCurrentMonth - (mCalendarArleux.getDifferenceFromPrevMonth()-1)); // числа с прошлого месяца, которые будут полупрозрачными
        int nextDays = 0; // числа для будущего месяца, которые будут полупрозрачными

        for (TextView textView : days) {
            textView.setText(""); //а то на феврале проявится последняя строка из января, так как поля не обновятся
            if ((days.indexOf(textView) < mCalendarArleux.getDifferenceFromPrevMonth()) && (mCalendarArleux.getDifferenceFromPrevMonth() != 0)) { // для чисел с прошлого месяца, которые будут полупрозрачными
                if (prevDays <= daysOfCurrentMonth) {
                    textView.setText(String.valueOf(prevDays));
                    textView.setTextColor(Color.parseColor("#E0E0E0"));
                    prevDays++;
                    continue;
                }
            }
            if (day < mCalendarArleux.getDaysInMonth(mCalendarArleux.getCurrentMonth(), mCalendarArleux.getCurrentYear())) {
                day++;

                textView.setText(String.valueOf(day));
                textView.setTypeface(null, Typeface.BOLD);
                textView.setTextColor(Color.parseColor("#212121"));

                try { //чтобы подсвечивалась капелька для дней полива
                    if (daysWatering.contains(getLocalCurrentDate(day)) && getLocalCurrentDate(day).equals(daysWatering.get(daysWatering.indexOf(getLocalCurrentDate(day))))) { //Не нужно завязываться на day, надо сделать LocaleDate и по нему судить
                        daysWatering_iv.get(mCalendarArleux.getDifferenceFromPrevMonth() + day - 1).setVisibility(View.VISIBLE);
                        // надо как-то делать дни эти невидимыми
                    }
                } catch (DateTimeException e) {

                }

                try { //чтобы только календарная дата подсвечивалась серым, а не каждый этот же день другого месяца или года
                    if (getLocalCurrentDate(day).equals(mCalendarArleux.calendarDate())) {
                        ImageView currentDay = activeDay_line.get(mCalendarArleux.getDifferenceFromPrevMonth() + day - 1);
                        currentDay.setImageResource(R.drawable.line_default_day); //если день выбран, то внизу его будет линия
                        currentDay.setVisibility(View.VISIBLE);
                    }
                }
                catch (DateTimeException e){
                    e.printStackTrace();
                }
                if (day == mCalendarArleux.getCurrentDay()){ //если день выбран, то внизу его будет линия
                    ImageView currentDay = activeDay_line.get(mCalendarArleux.getDifferenceFromPrevMonth()+day-1);
                    currentDay.setImageResource(R.drawable.line);
                    currentDay.setVisibility(View.VISIBLE);
                }
            } else if (mCalendarArleux.getDifferenceForNextMonth() > 0 && nextDays < mCalendarArleux.getDifferenceForNextMonth()) {
                //если дальше идут дни не из данного месяца
                nextDays++;
                textView.setText(String.valueOf(nextDays));
                textView.setTextColor(Color.parseColor("#E0E0E0"));
            }
        }
    }

    private void highlightDay() {
        for (int i = 0; i< activeDay_line.size(); i++){
            //условие, чтобы черточка отображалась только для выбранного дня
            if (i != mCalendarArleux.getDifferenceFromPrevMonth()+mCalendarArleux.getCalendarDay()-1 ||
                    (i == mCalendarArleux.getDifferenceFromPrevMonth()+mCalendarArleux.getCalendarDay()-1) && !isCalendarMonth())
                activeDay_line.get(i).setVisibility(View.INVISIBLE);
        }

        if (isCalendarMonth()) { //чтобы не распространядось на такие же числа других месяцев
            ImageView calendarDay = activeDay_line.get(mCalendarArleux.getDifferenceFromPrevMonth() + mCalendarArleux.getCalendarDay() - 1);
            calendarDay.setImageResource(R.drawable.line_default_day); //если выбран дефолтный день, то внизу его будет серая линия
            calendarDay.setVisibility(View.INVISIBLE);
        }
    }

    private int monthName(int monthNumber) {
        switch (monthNumber) {
            case 1:
                return R.string.calendar_month_january;
            case 2:
                return R.string.calendar_month_february;
            case 3:
                return R.string.calendar_month_march;
            case 4:
                return R.string.calendar_month_april;
            case 5:
                return R.string.calendar_month_may;
            case 6:
                return R.string.calendar_month_june;
            case 7:
                return R.string.calendar_month_july;
            case 8:
                return R.string.calendar_month_august;
            case 9:
                return R.string.calendar_month_semptember;
            case 10:
                return R.string.calendar_month_october;
            case 11:
                return R.string.calendar_month_november;
            case 12:
                return R.string.calendar_month_december;
        }
        return 0;
    }
    public void setCalendarDate(int year, int month, int day){
        mCalendarArleux.setCalendarYear(year);
        mCalendarArleux.setCalendarMonth(month+1); //текущий месяц из Calendar
        mCalendarArleux.setCalendarDay(day);
        mCalendarArleux.setCurrentYear(year);
        mCalendarArleux.setCurrentMonth(month+1); //текущий месяц из Calendar
        mCalendarArleux.setCurrentDay(day);
    }
    private boolean isCalendarMonth(){
        return (mCalendarArleux.getCurrentMonth() == mCalendarArleux.getCalendarMonth());
    } //является ли текущий месяц календарным
    private LocalDate getLocalCurrentDate(int day){
        return LocalDate.of(mCalendarArleux.getCurrentYear(), mCalendarArleux.getCurrentMonth(), day);
    } //дата дня
    private void monthTransitionUpdateDay() { // Установить активный день в зав-ти от месяца
        if (mCalendarArleux.getCurrentMonth() == mCalendarArleux.getCalendarMonth()) {
            mCalendarArleux.setCurrentDay(mCalendarArleux.getCalendarDay()); //при изменении месяца активно отображается первый день
        }
        else
            mCalendarArleux.setCurrentDay(1);
    }
    private void fillDays_tv(View v) {
        days.add((TextView) v.findViewById(R.id.one));
        days.add((TextView) v.findViewById(R.id.two));
        days.add((TextView) v.findViewById(R.id.three));
        days.add((TextView) v.findViewById(R.id.four));
        days.add((TextView) v.findViewById(R.id.five));
        days.add((TextView) v.findViewById(R.id.six));
        days.add((TextView) v.findViewById(R.id.seven));
        days.add((TextView) v.findViewById(R.id.eight));
        days.add((TextView) v.findViewById(R.id.nine));
        days.add((TextView) v.findViewById(R.id.ten));
        days.add((TextView) v.findViewById(R.id.eleven));
        days.add((TextView) v.findViewById(R.id.twelve));
        days.add((TextView) v.findViewById(R.id.thirteen));
        days.add((TextView) v.findViewById(R.id.fourteen));
        days.add((TextView) v.findViewById(R.id.fiveteen));
        days.add((TextView) v.findViewById(R.id.sixteen));
        days.add((TextView) v.findViewById(R.id.seventeen));
        days.add((TextView) v.findViewById(R.id.eighteen));
        days.add((TextView) v.findViewById(R.id.nineteen));
        days.add((TextView) v.findViewById(R.id.twenty));
        days.add((TextView) v.findViewById(R.id.twenty_one));
        days.add((TextView) v.findViewById(R.id.twenty_two));
        days.add((TextView) v.findViewById(R.id.twenty_three));
        days.add((TextView) v.findViewById(R.id.twenty_four));
        days.add((TextView) v.findViewById(R.id.twenty_five));
        days.add((TextView) v.findViewById(R.id.twenty_six));
        days.add((TextView) v.findViewById(R.id.twenty_seven));
        days.add((TextView) v.findViewById(R.id.twenty_eight));
        days.add((TextView) v.findViewById(R.id.twenty_nine));
        days.add((TextView) v.findViewById(R.id.thirty));
        days.add((TextView) v.findViewById(R.id.thirty_one));
        days.add((TextView) v.findViewById(R.id.thirty_two));
        days.add((TextView) v.findViewById(R.id.thirty_three));
        days.add((TextView) v.findViewById(R.id.thirty_four));
        days.add((TextView) v.findViewById(R.id.thirty_five));
        days.add((TextView) v.findViewById(R.id.thirty_six));
        days.add((TextView) v.findViewById(R.id.thirty_seven));
        days.add((TextView) v.findViewById(R.id.thirty_eight));
        days.add((TextView) v.findViewById(R.id.thirty_nine));
        days.add((TextView) v.findViewById(R.id.fourty));
        days.add((TextView) v.findViewById(R.id.fourty_one));
        days.add((TextView) v.findViewById(R.id.fourty_two));
    }
    private void fillDaysWatering_iv(View v){
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv1));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv2));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv3));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv4));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv5));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv6));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv7));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv8));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv9));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv10));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv11));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv12));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv13));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv14));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv15));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv16));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv17));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv18));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv19));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv20));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv21));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv22));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv23));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv24));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv25));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv26));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv27));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv28));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv29));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv30));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv31));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv32));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv33));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv34));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv35));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv36));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv37));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv38));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv39));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv40));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv41));
        daysWatering_iv.add((ImageView) v.findViewById(R.id.days_watering_iv42));
    }
    private void fillActiveDay_line(View v){
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv1));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv2));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv3));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv4));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv5));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv6));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv7));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv8));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv9));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv10));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv11));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv12));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv13));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv14));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv15));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv16));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv17));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv18));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv19));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv20));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv21));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv22));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv23));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv24));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv25));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv26));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv27));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv28));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv29));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv30));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv31));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv32));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv33));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv34));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv35));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv36));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv37));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv38));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv39));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv40));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv41));
        activeDay_line.add((ImageView) v.findViewById(R.id.chose_day_line_iv42));
    }
    public CalendarArleux getCalendarArleux() {
        return mCalendarArleux;
    }
}