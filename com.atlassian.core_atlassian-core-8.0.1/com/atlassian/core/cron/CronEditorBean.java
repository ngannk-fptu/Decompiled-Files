/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.cron;

import com.atlassian.core.util.DateUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class CronEditorBean {
    public static final String DAILY_SPEC_MODE = "daily";
    public static final String DAYS_OF_WEEK_SPEC_MODE = "daysOfWeek";
    public static final String DAYS_OF_MONTH_SPEC_MODE = "daysOfMonth";
    public static final String ADVANCED_MODE = "advanced";
    public static final String DOT = ".";
    private static final String CRON_STRING = "cronString";
    private static final String DAILY_WEEKLY_MONTHLY = "dailyWeeklyMonthly";
    private static final String RUN_ONCE_MINS = "runOnceMins";
    private static final String RUN_ONCE_HOURS = "runOnceHours";
    private static final String RUN_ONCE_MERIDIAN = "runOnceMeridian";
    private static final String RUN_FROM_HOURS = "runFromHours";
    private static final String RUN_FROM_MERIDIAN = "runFromMeridian";
    private static final String RUN_TO_HOURS = "runToHours";
    private static final String RUN_TO_MERIDIAN = "runToMeridian";
    private static final String WEEKDAY = "weekday";
    private static final String DAY = "day";
    private static final String WEEK = "week";
    private static final String DAYS_OF_MONTH_OPT = "daysOfMonthOpt";
    private static final String MONTH_DAY = "monthDay";
    private static final String INTERVAL = "interval";
    private static final String DAY_OF_WEEK_OF_MONTH = "dayOfWeekOfMonth";
    private Map params;
    private String cronString;
    private String mode;
    private boolean dayOfWeekOfMonth;
    private String dayOfMonth;
    private String minutes;
    private String hoursRunOnce;
    private String hoursRunOnceMeridian;
    private String hoursFrom;
    private String hoursFromMeridian;
    private String hoursTo;
    private String hoursToMeridian;
    private String specifiedDaysOfWeek;
    private String dayInMonthOrdinal;
    private String incrementInMinutes;
    private String seconds;

    public CronEditorBean() {
        this.params = new HashMap();
    }

    public CronEditorBean(String paramPrefix, Map params) {
        this.params = params;
        this.cronString = this.getParam(paramPrefix, CRON_STRING);
        this.mode = this.getParam(paramPrefix, DAILY_WEEKLY_MONTHLY);
        this.minutes = this.getParam(paramPrefix, RUN_ONCE_MINS);
        this.hoursRunOnce = this.getParam(paramPrefix, RUN_ONCE_HOURS);
        this.hoursRunOnceMeridian = this.getParam(paramPrefix, RUN_ONCE_MERIDIAN);
        this.hoursFrom = this.getParam(paramPrefix, RUN_FROM_HOURS);
        this.hoursFromMeridian = this.getParam(paramPrefix, RUN_FROM_MERIDIAN);
        this.hoursTo = this.getParam(paramPrefix, RUN_TO_HOURS);
        this.hoursToMeridian = this.getParam(paramPrefix, RUN_TO_MERIDIAN);
        Object[] daysOfWeek = (String[])params.get(paramPrefix + DOT + WEEKDAY);
        if (DAYS_OF_MONTH_SPEC_MODE.equals(this.mode)) {
            this.specifiedDaysOfWeek = this.getParam(paramPrefix, DAY);
            this.dayInMonthOrdinal = this.getParam(paramPrefix, WEEK);
            String dayOfWeekOfMonthString = this.getParam(paramPrefix, DAYS_OF_MONTH_OPT);
            this.dayOfWeekOfMonth = DAY_OF_WEEK_OF_MONTH.equals(dayOfWeekOfMonthString);
        } else if (DAYS_OF_WEEK_SPEC_MODE.equals(this.mode)) {
            this.specifiedDaysOfWeek = StringUtils.join((Object[])daysOfWeek, (char)',');
        }
        this.dayOfMonth = this.getParam(paramPrefix, MONTH_DAY);
        this.incrementInMinutes = this.getParam(paramPrefix, INTERVAL);
    }

    public boolean isRangeHoursValid() {
        if (this.hoursFrom != null && this.hoursFromMeridian != null && this.hoursTo != null && this.hoursToMeridian != null && this.incrementInMinutes != null && !this.incrementInMinutes.equals("0")) {
            try {
                int hoursFromInt = Integer.parseInt(this.hoursFrom);
                int hoursToInt = Integer.parseInt(this.hoursTo);
                return DateUtils.get24HourTime(this.hoursFromMeridian, hoursFromInt) <= DateUtils.get24HourTime(this.hoursToMeridian, hoursToInt);
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public boolean isRange() {
        return !this.incrementInMinutes.equals("0") && (this.isDailyMode() || this.isDayPerWeekMode());
    }

    public boolean is24HourRange() {
        boolean result = false;
        if (this.isRange()) {
            int hoursFromInt = Integer.parseInt(this.hoursFrom);
            int hoursToInt = Integer.parseInt(this.hoursTo);
            result = DateUtils.get24HourTime(this.hoursFromMeridian, hoursFromInt) == DateUtils.get24HourTime(this.hoursToMeridian, hoursToInt);
        }
        return result;
    }

    public String getCronString() {
        return this.cronString;
    }

    public String getMode() {
        return this.mode;
    }

    public boolean isAdvancedMode() {
        return ADVANCED_MODE.equals(this.mode);
    }

    public boolean isDailyMode() {
        return DAILY_SPEC_MODE.equals(this.mode);
    }

    public boolean isDayPerWeekMode() {
        return DAYS_OF_WEEK_SPEC_MODE.equals(this.mode);
    }

    public boolean isDaysPerMonthMode() {
        return DAYS_OF_MONTH_SPEC_MODE.equals(this.mode);
    }

    public boolean isDayOfWeekOfMonth() {
        return this.dayOfWeekOfMonth;
    }

    public boolean getDayOfWeekOfMonth() {
        return this.dayOfWeekOfMonth;
    }

    public String getDayOfMonth() {
        return this.dayOfMonth;
    }

    public String getMinutes() {
        return this.minutes;
    }

    public String getHoursFrom() {
        return this.hoursFrom;
    }

    public String getHoursTo() {
        return this.hoursTo;
    }

    public String getHoursFromMeridian() {
        return this.hoursFromMeridian;
    }

    public String getHoursToMeridian() {
        return this.hoursToMeridian;
    }

    public String getHoursRunOnce() {
        return this.hoursRunOnce;
    }

    public String getHoursRunOnceMeridian() {
        return this.hoursRunOnceMeridian;
    }

    public boolean isDaySpecified(String dayStr) {
        return this.specifiedDaysOfWeek != null && StringUtils.contains((CharSequence)this.specifiedDaysOfWeek, (CharSequence)dayStr);
    }

    public String getDayInMonthOrdinal() {
        return this.dayInMonthOrdinal;
    }

    public String getSpecifiedDaysPerWeek() {
        return this.specifiedDaysOfWeek;
    }

    public String getIncrementInMinutes() {
        return this.incrementInMinutes;
    }

    public void setCronString(String cronString) {
        this.cronString = cronString;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setDayOfWeekOfMonth(boolean dayOfWeekOfMonth) {
        this.dayOfWeekOfMonth = dayOfWeekOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public void setHoursFrom(String hoursFrom) {
        this.hoursFrom = hoursFrom;
    }

    public void setHoursTo(String hoursTo) {
        this.hoursTo = hoursTo;
    }

    public void setHoursFromMeridian(String hoursFromMeridian) {
        this.hoursFromMeridian = hoursFromMeridian;
    }

    public void setHoursToMeridian(String hoursToMeridian) {
        this.hoursToMeridian = hoursToMeridian;
    }

    public void setHoursRunOnce(String hoursRunOnce) {
        this.hoursRunOnce = hoursRunOnce;
    }

    public void setHoursRunOnceMeridian(String hoursRunOnceMeridian) {
        this.hoursRunOnceMeridian = hoursRunOnceMeridian;
    }

    public void setSpecifiedDaysOfWeek(String specifiedDaysOfWeek) {
        this.specifiedDaysOfWeek = specifiedDaysOfWeek;
    }

    public void setDayInMonthOrdinal(String dayInMonthOrdinal) {
        this.dayInMonthOrdinal = dayInMonthOrdinal;
    }

    public void setIncrementInMinutes(String incrementInMinutes) {
        this.incrementInMinutes = incrementInMinutes;
    }

    private String getParam(String paramPrefix, String key) {
        String[] paramArr = (String[])this.params.get(paramPrefix + DOT + key);
        if (paramArr != null && paramArr.length == 1) {
            return paramArr[0];
        }
        return null;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public String getSeconds() {
        return this.seconds;
    }
}

