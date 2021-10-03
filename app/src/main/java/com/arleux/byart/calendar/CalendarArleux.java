package com.arleux.byart.calendar;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarArleux {
    private ArrayList<Integer> years = new ArrayList();
    private static final ArrayList<Integer> months = new ArrayList();
    private LocalDate mLocalDate;
    private static CalendarArleux mCalendarArleux;

    private static final int firstYear = 2021;
    private static final int lastYear = 2030;
    private int calendarYear = firstYear;
    private int calendarMonth = Months.JANUARY.getMonthNumber();
    private int calendarDay = 1;
    private int currentMonth;
    private int currentYear;
    private int currentDay;
    private int differenceFromPrevMonth = 4;
    private int differenceForNextMonth = 3;

    private CalendarArleux(){
        for (Months month: Months.values()){
            months.add(month.getMonthNumber());
        }

        for (int i = getFirstYear(); i < getLastYear(); i++)
            years.add(i);
    }
    public static CalendarArleux getInstance(){
        return mCalendarArleux == null ? mCalendarArleux = new CalendarArleux() : mCalendarArleux;
    }
    public void currentDate(int currentMonth, int currentYear){
        differenceFromPrevMonth = 4;
        differenceForNextMonth = 3;
        in:
        for (int year : years) { //пробегаю по всем годам календаря
            out:
            while (year <= currentYear) {

                for (int i = 0; i < 12; i++) { //для всех месяцев
                    if (differenceForNextMonth == 0)
                        differenceFromPrevMonth = differenceForNextMonth;
                    else
                        differenceFromPrevMonth = (7 - differenceForNextMonth);
                    if (differenceFromPrevMonth == 0)
                        differenceForNextMonth = 42 - getDaysInMonth(months.get(i), year) - differenceFromPrevMonth; //всего возможно 42 дня(с учетом первого числа месяца в вс)
                    else
                        differenceForNextMonth = 42 - getDaysInMonth(months.get(i), year) - differenceFromPrevMonth;
                    while (differenceForNextMonth >= 7)
                        differenceForNextMonth = differenceForNextMonth - 7;
                    if (i == 11)
                        break out;
                    if (year == currentYear && (currentMonth == (i + 1))) //как только посчитал разницу для предыдущего месяца этого же года, то выхожу
                        break in;
                }
            }
        }
    }
    public int getDaysInMonth(int month, int year){
            switch (month) {
                case 1:
                    return 31;
                case 2: if(year % 4 == 0) {
                                return 29;
                }
                else {
                    return 28;
                }
                case 3:
                    return 31;
                case 4:
                    return 30;
                case 5:
                    return 31;
                case 6:
                    return 30;
                case 7:
                    return 31;
                case 8:
                    return 31;
                case 9:
                    return 30;
                case 10:
                    return 31;
                case 11:
                    return 30;
                case 12:
                    return 31;
            }
            return 0;
        }

    public int getDifferenceFromPrevMonth() {
        return differenceFromPrevMonth;
    }

    public int getDifferenceForNextMonth() {
        return differenceForNextMonth;
    }

    public int getCalendarYear() {
        return calendarYear;
    }

    public void setCalendarYear(int calendarYear) {
        this.calendarYear = calendarYear;
    }

    public int getCalendarMonth() {
        return calendarMonth;
    }

    public void setCalendarMonth(int calendarMonth) {
        this.calendarMonth = calendarMonth;
    }

    public int getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(int calendarDay) {
        this.calendarDay = calendarDay;
    }

    public LocalDate getCurrentDate(){
        mLocalDate = LocalDate.of(this.getCurrentYear(), this.getCurrentMonth(), this.getCurrentDay());
        return mLocalDate;
    }

    public LocalDate calendarDate(){
        return LocalDate.now();
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getCurrentMonth(){
        return currentMonth;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public int getFirstYear(){
        return firstYear;
    }

    public int getLastYear() {
        return lastYear;
    }
}
