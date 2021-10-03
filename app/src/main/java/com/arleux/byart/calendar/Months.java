package com.arleux.byart.calendar;

public enum Months { // кол-во дней в месяце

        JANUARY{int daysInMonth(){return 31;};
                int getMonthNumber(){return 1;}},
        FEBRUARY{int daysInMonth(){
                return 28;};
                int getMonthNumber(){return 2;}},
        MARCH{int daysInMonth(){return 31;};
                int getMonthNumber(){return 3;}},
        APRIL{int daysInMonth(){return 30;};
                int getMonthNumber(){return 4;}},
        MAY{int daysInMonth(){return 31;};
                int getMonthNumber(){return 5;}},
        JUNE{int daysInMonth(){return 30;};
                int getMonthNumber(){return 6;}},
        JULY{int daysInMonth(){return 31;};
                int getMonthNumber(){return 7;}},
        AUGUST{int daysInMonth(){return 31;};
                int getMonthNumber(){return 8;}},
        SEMPTEMBER{int daysInMonth(){return 30;};
                int getMonthNumber(){return 9;}},
        OCTOBER{int daysInMonth(){return 31;};
                int getMonthNumber(){return 10;}},
        NOVEMBER{int daysInMonth(){return 30;};
                int getMonthNumber(){return 11;}},
        DECEMBER{int daysInMonth(){return 31;};
                int getMonthNumber(){return 12;}};

        abstract int daysInMonth();
        abstract int getMonthNumber();
        }

