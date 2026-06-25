package com.mygroup.grp1.util;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds day lists that respect month length and leap years.
 */
public final class DateOfBirthHelper {

    private DateOfBirthHelper() {
    }

    public static List<Integer> years() {
        int currentYear = Year.now().getValue();
        List<Integer> years = new ArrayList<>();
        for (int year = currentYear - 75; year <= currentYear - 18; year++) {
            years.add(year);
        }
        return years;
    }

    public static List<String> months() {
        List<String> months = new ArrayList<>();
        for (Month month : Month.values()) {
            months.add(capitalize(month.name()));
        }
        return months;
    }

    public static List<Integer> days(Integer year, String monthName) {
        if (year == null || monthName == null || monthName.isBlank()) {
            return defaultDays();
        }
        Month month = Month.valueOf(monthName.trim().toUpperCase());
        int dayCount = YearMonth.of(year, month).lengthOfMonth();
        List<Integer> days = new ArrayList<>();
        for (int day = 1; day <= dayCount; day++) {
            days.add(day);
        }
        return days;
    }

    public static LocalDate toLocalDate(Integer year, String monthName, Integer day) {
        if (year == null || monthName == null || monthName.isBlank() || day == null) {
            return null;
        }
        Month month = Month.valueOf(monthName.trim().toUpperCase());
        return LocalDate.of(year, month, day);
    }

    private static List<Integer> defaultDays() {
        List<Integer> days = new ArrayList<>();
        for (int day = 1; day <= 31; day++) {
            days.add(day);
        }
        return days;
    }

    private static String capitalize(String value) {
        String lower = value.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
