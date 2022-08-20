package com.example.pinkhome;

public enum Day {

    DAY_1 ("Понедельник"),
    DAY_2 ("Вторник"),
    DAY_3 ("Среда"),
    DAY_4 ("Четверг"),
    DAY_5 ("Пятница"),
    DAY_6 ("Суббота"),
    DAY_7 ("Воскресенье");

    private String day;

    Day(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }
}
