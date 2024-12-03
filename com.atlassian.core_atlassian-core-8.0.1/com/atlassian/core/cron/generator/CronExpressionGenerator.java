/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.cron.generator;

import com.atlassian.core.cron.CronEditorBean;
import com.atlassian.core.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

public class CronExpressionGenerator {
    private static final String DAY_IN_MONTH_SEPARATOR = "#";
    private static final String LAST_DAY_IN_MONTH_FLAG = "L";

    public String getCronExpressionFromInput(CronEditorBean cronEditorBean) {
        String cronSpec = null;
        if (cronEditorBean.isDailyMode()) {
            cronSpec = this.generateDailySpec(cronEditorBean) + " ? * *";
        } else if (cronEditorBean.isDayPerWeekMode()) {
            cronSpec = this.generateDailySpec(cronEditorBean) + " ? * " + this.generateDaysOfWeekSpec(cronEditorBean);
        } else if (cronEditorBean.isDaysPerMonthMode()) {
            cronSpec = this.generateDailySpec(cronEditorBean) + " " + this.generateDaysOfMonthOptSpec(cronEditorBean);
        } else if (cronEditorBean.isAdvancedMode()) {
            cronSpec = cronEditorBean.getCronString();
        }
        return cronSpec;
    }

    String generateDaysOfMonthOptSpec(CronEditorBean cronEditorBean) {
        if (cronEditorBean.isDayOfWeekOfMonth()) {
            return this.generateDayOfWeekOfMonthSpec(cronEditorBean);
        }
        return this.generateDayOfMonthSpec(cronEditorBean);
    }

    String generateDayOfWeekOfMonthSpec(CronEditorBean cronEditorBean) {
        String specifiedDaysPerWeek;
        String dayInMonthOrdinal = cronEditorBean.getDayInMonthOrdinal();
        if (dayInMonthOrdinal == null) {
            throw new IllegalStateException("You must have an ordinal set when generating the day of week of month cron portion: " + cronEditorBean.getCronString());
        }
        if (!LAST_DAY_IN_MONTH_FLAG.equalsIgnoreCase(dayInMonthOrdinal)) {
            dayInMonthOrdinal = DAY_IN_MONTH_SEPARATOR + dayInMonthOrdinal;
        }
        if ((specifiedDaysPerWeek = cronEditorBean.getSpecifiedDaysPerWeek()) == null) {
            throw new IllegalStateException("The days per week must be specified when creating a days per week cron portion: " + cronEditorBean.getCronString());
        }
        String specSegment = specifiedDaysPerWeek + dayInMonthOrdinal;
        return "? * " + specSegment;
    }

    String generateDayOfMonthSpec(CronEditorBean cronEditorBean) {
        String monthDay = cronEditorBean.getDayOfMonth();
        if (monthDay == null) {
            throw new IllegalStateException("The day of month must not be null when creating a day of month cron portion: " + cronEditorBean.getCronString());
        }
        return monthDay + " * ?";
    }

    String generateDaysOfWeekSpec(CronEditorBean cronEditorBean) {
        if (StringUtils.isBlank((CharSequence)cronEditorBean.getSpecifiedDaysPerWeek())) {
            throw new IllegalStateException("The days per week must be specified when creating a days per week cron portion: " + cronEditorBean.getCronString());
        }
        return cronEditorBean.getSpecifiedDaysPerWeek();
    }

    String generateDailySpec(CronEditorBean cronEditorBean) {
        StringBuilder dailyString = new StringBuilder("0 ");
        int increment = this.getIntFromString(cronEditorBean.getIncrementInMinutes());
        if (increment == 0 || cronEditorBean.isDaysPerMonthMode()) {
            if (cronEditorBean.getHoursRunOnceMeridian() == null) {
                throw new IllegalStateException("You must specify a run once hour meridian when generating a daily spec with no interval: " + cronEditorBean.getCronString());
            }
            if (cronEditorBean.getHoursRunOnce() == null) {
                throw new IllegalStateException("You must specify a run once hour when generating a daily spec with no interval: " + cronEditorBean.getCronString());
            }
            if (cronEditorBean.getMinutes() == null) {
                throw new IllegalStateException("You must specify a minutes when generating a daily spec with no interval: " + cronEditorBean.getCronString());
            }
            int atHours = this.getIntFromString(cronEditorBean.getHoursRunOnce());
            int atMins = this.getIntFromString(cronEditorBean.getMinutes());
            atHours = DateUtils.get24HourTime(cronEditorBean.getHoursRunOnceMeridian(), atHours);
            dailyString.append(atMins);
            dailyString.append(" ");
            dailyString.append(atHours);
        } else {
            dailyString.append("0");
            if (increment < 60) {
                dailyString.append("/");
                dailyString.append(increment);
            }
            dailyString.append(" ");
            if (cronEditorBean.getHoursFrom() == null) {
                throw new IllegalStateException("You must specify a from hour when generating a daily spec with an interval: " + cronEditorBean.getCronString());
            }
            if (cronEditorBean.getHoursFromMeridian() == null) {
                throw new IllegalStateException("You must specify a from hour meridian when generating a daily spec with an interval: " + cronEditorBean.getCronString());
            }
            if (cronEditorBean.getHoursTo() == null) {
                throw new IllegalStateException("You must specify a to hour when generating a daily spec with an interval: " + cronEditorBean.getCronString());
            }
            if (cronEditorBean.getHoursToMeridian() == null) {
                throw new IllegalStateException("You must specify a to hour meridian when generating a daily spec with an interval: " + cronEditorBean.getCronString());
            }
            int fromHours = DateUtils.get24HourTime(cronEditorBean.getHoursFromMeridian(), this.getIntFromString(cronEditorBean.getHoursFrom()));
            int toHours = DateUtils.get24HourTime(cronEditorBean.getHoursToMeridian(), this.getIntFromString(cronEditorBean.getHoursTo()));
            int hourIncrement = increment / 60;
            if (cronEditorBean.is24HourRange()) {
                dailyString.append("*");
            } else {
                dailyString.append(fromHours);
                dailyString.append("-");
                dailyString.append(this.decrementHourByOne(toHours));
            }
            if (hourIncrement >= 1) {
                dailyString.append("/");
                dailyString.append(hourIncrement);
            }
        }
        return dailyString.toString();
    }

    int getIntFromString(String string) {
        if (string != null && !StringUtils.isEmpty((CharSequence)string)) {
            return Integer.parseInt(string);
        }
        return 0;
    }

    private int decrementHourByOne(int hour) {
        return hour == 0 ? 23 : hour - 1;
    }
}

