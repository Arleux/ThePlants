package com.arleux.byart.calendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.arleux.byart.R;

import java.util.ArrayList;

public class CalendarFragmentDefault extends Fragment{
    private ArrayList<Integer> years = new ArrayList();
    private ArrayList<Integer> months = new ArrayList();
    private ArrayList<TextView> days = new ArrayList<>();
    private TextView month_tv;
    private TextView year_tv;
    private ImageView prevMonthArrow;
    private ImageView nextMonthArrow;
    private int year;
    private int month_number;
    private int january = 31;
    private int february = 28;
    private int february_4 = 29;
    private int march = 31;
    private int april = 4;
    private int may = 5;
    private int june = 6;
    private int july = 7;
    private int august = 8;
    private int semptember = 9;
    private int october = 10;
    private int november = 11;
    private int december = 12;

    private int differenceFromPrevMonth = 4; //количество дней с прошлого месяца
    private int differenceForNextMonth = 3; //кол-во дней для следующего месяца

//    public static Fragment newInstance() {
//        return new CalendarArleuxFragment();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //    year = CalendarArleux.getInstance().getTime().getYear();
        //    month_number = CalendarArleux.getInstance().getTime().getMonth();
        year = 2021;
        month_number = 1; //[1-12]
        for (int i = 2021; i < 2030; i++)
            years.add(i);
        months.add(january);
        months.add(february);
        months.add(march);
        months.add(april);
        months.add(may);
        months.add(june);
        months.add(july);
        months.add(august);
        months.add(semptember);
        months.add(october);
        months.add(november);
        months.add(december);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        month_tv = (TextView) v.findViewById(R.id.month_tv);
        year_tv = (TextView) v.findViewById(R.id.year_tv);
        prevMonthArrow = (ImageView) v.findViewById(R.id.navigate_before_arrow_iv);
        nextMonthArrow = (ImageView) v.findViewById(R.id.navigate_next_arrow_iv);

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

        prevMonthArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (month_number == 1 && year>2021) {
                    month_number = 12;
                    year--;
                }
                else if(month_number == 1 && year == 2021){

                }
                else
                    month_number--;
                updateView();
            }
        });
        nextMonthArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (month_number == 12) {
                    month_number = 1;
                    year++;
                }
                else
                    month_number++;
                updateView();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        updateView();
    }

    private void updateView(){
        //это начальные значения для января 2021
        differenceFromPrevMonth = 4;
        differenceForNextMonth = 3;
        in:
        for (int year : years) { //пробегаю по всем годам календаря
            if (year % 4 == 0) { //високосный
                months.remove(1);
                months.add(1, february_4);
            } else {
                months.remove(1);
                months.add(1, february);
            }
            out:
            while (year <= this.year) {
                for (int i = 0; i < months.size(); i++) {
                    if (differenceForNextMonth == 0)
                        differenceFromPrevMonth = differenceForNextMonth;
                    else
                        differenceFromPrevMonth = (7 - differenceForNextMonth);
                    if (differenceFromPrevMonth == 0)
                        differenceForNextMonth = 42 - months.get(i) - differenceFromPrevMonth; //всего возможно 42 дня(с учетом первого числа месяца в вс)
                    else
                        differenceForNextMonth = 42 - months.get(i) - differenceFromPrevMonth;
                    while (differenceForNextMonth >= 7)
                        differenceForNextMonth = differenceForNextMonth - 7;
                    if (i == 11)
                        break out;
                    if (year == this.year && (month_number == (i + 1))) //как только посчитал разницу для предыдущего месяца этого же года, то выхожу
                        break in;
                }
            }
        }
        int day = 0;
        int prevDays = (months.get(month_number - 1) - (differenceFromPrevMonth - 1)); // числа с прошлого месяца, которые будут полупрозрачными
        int nextDays = 0; // числа для будущего месяца, которые будут полупрозрачными
        month_tv.setText(monthName(month_number));
        year_tv.setText(String.valueOf(year));
        for (TextView textView : days) {
            textView.setText(""); //а то на феврале проявится последняя строка из января, так как поля не обновятся
            if ((days.indexOf(textView) < differenceFromPrevMonth) && (differenceFromPrevMonth != 0)) { // для чисел с прошлого месяца, которые будут полупрозрачными
                if (prevDays <= months.get(month_number - 1)) {
                    textView.setText(String.valueOf(prevDays));
                    textView.setTextColor(Color.parseColor("#E0E0E0"));
                    prevDays++;
                    continue;
                }
            }
            if (day < months.get(month_number - 1)) {
                day++;
                textView.setText(String.valueOf(day));
                textView.setTypeface(null, Typeface.BOLD);
                textView.setTextColor(Color.parseColor("#212121"));
            } else if (differenceForNextMonth > 0 && nextDays < differenceForNextMonth) {
                nextDays++;
                textView.setText(String.valueOf(nextDays));
                textView.setTextColor(Color.parseColor("#E0E0E0"));
            }
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
}
