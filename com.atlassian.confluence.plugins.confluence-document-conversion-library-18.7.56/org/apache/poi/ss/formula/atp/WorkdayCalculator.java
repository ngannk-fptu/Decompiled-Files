/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.LocaleUtil;

public class WorkdayCalculator {
    public static final WorkdayCalculator instance = new WorkdayCalculator();
    private static final Set<Integer> standardWeekend = new HashSet<Integer>(Arrays.asList(7, 1));
    private static final Set<Integer> sunMonWeekend = new HashSet<Integer>(Arrays.asList(1, 2));
    private static final Set<Integer> monTuesWeekend = new HashSet<Integer>(Arrays.asList(2, 3));
    private static final Set<Integer> tuesWedsWeekend = new HashSet<Integer>(Arrays.asList(3, 4));
    private static final Set<Integer> wedsThursWeekend = new HashSet<Integer>(Arrays.asList(4, 5));
    private static final Set<Integer> thursFriWeekend = new HashSet<Integer>(Arrays.asList(5, 6));
    private static final Set<Integer> friSatWeekend = new HashSet<Integer>(Arrays.asList(6, 7));
    private static final Set<Integer> monWeekend = Collections.singleton(2);
    private static final Set<Integer> tuesWeekend = Collections.singleton(3);
    private static final Set<Integer> wedsWeekend = Collections.singleton(4);
    private static final Set<Integer> thursWeekend = Collections.singleton(5);
    private static final Set<Integer> friWeekend = Collections.singleton(6);
    private static final Set<Integer> satWeekend = Collections.singleton(7);
    private static final Set<Integer> sunWeekend = Collections.singleton(1);
    private static final Map<Integer, Set<Integer>> weekendTypeMap = new HashMap<Integer, Set<Integer>>();

    private WorkdayCalculator() {
    }

    public Set<Integer> getValidWeekendTypes() {
        return weekendTypeMap.keySet();
    }

    public int calculateWorkdays(double start, double end, double[] holidays) {
        Integer[] weekendDays = new Integer[standardWeekend.size()];
        int weekendDay1Past = (weekendDays = standardWeekend.toArray(weekendDays)).length == 0 ? 0 : this.pastDaysOfWeek(start, end, weekendDays[0]);
        int weekendDay2Past = weekendDays.length <= 1 ? 0 : this.pastDaysOfWeek(start, end, weekendDays[1]);
        int nonWeekendHolidays = this.calculateNonWeekendHolidays(start, end, holidays);
        return (int)(end - start + 1.0) - weekendDay1Past - weekendDay2Past - nonWeekendHolidays;
    }

    public Date calculateWorkdays(double start, int workdays, double[] holidays) {
        return this.calculateWorkdays(start, workdays, 1, holidays);
    }

    public Date calculateWorkdays(double start, int workdays, int weekendType, double[] holidays) {
        Set<Integer> weekendDays = weekendTypeMap.getOrDefault(weekendType, standardWeekend);
        Date startDate = DateUtil.getJavaDate(start);
        int direction = workdays < 0 ? -1 : 1;
        Calendar endDate = LocaleUtil.getLocaleCalendar();
        endDate.setTime(startDate);
        double excelEndDate = DateUtil.getExcelDate(endDate.getTime());
        while (workdays != 0) {
            endDate.add(6, direction);
            if (this.isWeekend(endDate, weekendDays) || this.isHoliday(excelEndDate += (double)direction, holidays)) continue;
            workdays -= direction;
        }
        return endDate.getTime();
    }

    protected int pastDaysOfWeek(double start, double end, int dayOfWeek) {
        int pastDaysOfWeek = 0;
        int endDay = (int)Math.floor(Math.max(end, start));
        for (int startDay = (int)Math.floor(Math.min(start, end)); startDay <= endDay; ++startDay) {
            Calendar today = LocaleUtil.getLocaleCalendar();
            today.setTime(DateUtil.getJavaDate(startDay));
            if (today.get(7) != dayOfWeek) continue;
            ++pastDaysOfWeek;
        }
        return start <= end ? pastDaysOfWeek : -pastDaysOfWeek;
    }

    protected int calculateNonWeekendHolidays(double start, double end, double[] holidays) {
        int nonWeekendHolidays = 0;
        double startDay = Math.min(start, end);
        double endDay = Math.max(end, start);
        for (double holiday : holidays) {
            if (!this.isInARange(startDay, endDay, holiday) || this.isWeekend(holiday)) continue;
            ++nonWeekendHolidays;
        }
        return start <= end ? nonWeekendHolidays : -nonWeekendHolidays;
    }

    protected boolean isWeekend(double aDate) {
        Calendar date = LocaleUtil.getLocaleCalendar();
        date.setTime(DateUtil.getJavaDate(aDate));
        return this.isWeekend(date);
    }

    private boolean isWeekend(Calendar date) {
        return this.isWeekend(date, standardWeekend);
    }

    private boolean isWeekend(Calendar date, Set<Integer> weekendDays) {
        return weekendDays.contains(date.get(7));
    }

    protected boolean isHoliday(double aDate, double[] holidays) {
        for (double holiday : holidays) {
            if (Math.round(holiday) != Math.round(aDate)) continue;
            return true;
        }
        return false;
    }

    protected boolean isInARange(double start, double end, double aDate) {
        return aDate >= start && aDate <= end;
    }

    static {
        weekendTypeMap.put(1, standardWeekend);
        weekendTypeMap.put(2, sunMonWeekend);
        weekendTypeMap.put(3, monTuesWeekend);
        weekendTypeMap.put(4, tuesWedsWeekend);
        weekendTypeMap.put(5, wedsThursWeekend);
        weekendTypeMap.put(6, thursFriWeekend);
        weekendTypeMap.put(7, friSatWeekend);
        weekendTypeMap.put(11, sunWeekend);
        weekendTypeMap.put(12, monWeekend);
        weekendTypeMap.put(13, tuesWeekend);
        weekendTypeMap.put(14, wedsWeekend);
        weekendTypeMap.put(15, thursWeekend);
        weekendTypeMap.put(16, friWeekend);
        weekendTypeMap.put(17, satWeekend);
    }
}

