/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.core.cron.generator;

import com.atlassian.core.cron.CronEditorBean;
import com.atlassian.core.i18n.I18nTextProvider;
import com.atlassian.core.util.map.EasyMap;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class CronExpressionDescriptor {
    private static final Map<String, String> MINUTE_INCREMENT_TO_MESG_KEY = EasyMap.build("15", "cron.editor.every.15.minutes", "30", "cron.editor.every.30.minutes", "60", "cron.editor.every.hour", "120", "cron.editor.every.2.hours", "180", "cron.editor.every.3.hours");
    private static final Map<String, String> DAY_NUMBERS = EasyMap.build("1", "sunday", "2", "monday", "3", "tuesday", "4", "wednesday", "5", "thursday", "6", "friday", "7", "saturday");
    private static final String CRON_EDITOR_KEY_PREFIX = "cron.editor.";
    private static final int MINUTES_DIGITS = 2;
    private static final int DAYS_IN_WEEK = 7;
    private static final String LAST_COMMA_REGEX = ",([^,]*)$";
    private final I18nTextProvider i18n;

    public CronExpressionDescriptor(I18nTextProvider i18n) {
        Validate.notNull((Object)i18n, (String)"I18nTextProvider must nnot be null", (Object[])new Object[0]);
        this.i18n = i18n;
    }

    private String getDay(String number) {
        String keypart = DAY_NUMBERS.get(number);
        Validate.notNull((Object)keypart, (String)("Unable to get day for number '" + number + "'. CronEditorBean likely to be inconsistent"), (Object[])new Object[0]);
        return this.i18n.getText(CRON_EDITOR_KEY_PREFIX + keypart);
    }

    public String getPrettySchedule(CronEditorBean bean) {
        if (bean.isAdvancedMode()) {
            return bean.getCronString();
        }
        StringBuilder desc = new StringBuilder();
        if (bean.isDailyMode()) {
            desc.append(this.i18n.getText("cron.editor.daily")).append(" ");
            desc.append(this.getTimePart(bean));
        } else if (bean.isDayPerWeekMode()) {
            desc.append(this.getDayPerWeekDescriptor(bean));
        } else if (bean.isDaysPerMonthMode()) {
            desc.append(this.getDayPerMonthDescriptor(bean));
        }
        return desc.toString();
    }

    private String getDayPerWeekDescriptor(CronEditorBean bean) {
        StringBuilder desc = new StringBuilder();
        desc.append(this.i18n.getText("cron.editor.each"));
        desc.append(" ");
        Object[] daysArray = bean.getSpecifiedDaysPerWeek().split(",");
        Arrays.sort(daysArray);
        String daysString = StringUtils.join((Object[])daysArray, (String)",");
        daysString = daysString.replaceAll(LAST_COMMA_REGEX, " and $1");
        for (int i = 1; i <= 7; ++i) {
            String dayNum = Integer.toString(i);
            daysString = daysString.replaceAll(dayNum, this.getDay(dayNum));
        }
        daysString = daysString.replaceAll(",", ", ");
        desc.append(daysString).append(" ");
        desc.append(this.getTimePart(bean));
        return desc.toString();
    }

    private String getDayPerMonthDescriptor(CronEditorBean bean) {
        StringBuilder desc = new StringBuilder();
        if (bean.isDayOfWeekOfMonth()) {
            String ordinal = this.i18n.getText("cron.editor.ordinal." + bean.getDayInMonthOrdinal());
            String ordinalWeekday = ordinal + " " + this.getDay(bean.getSpecifiedDaysPerWeek());
            desc.append(this.i18n.getText("cron.editor.the.of.every.month", new String[]{ordinalWeekday}));
            desc.append(" ");
            desc.append(this.getTimePart(bean));
        } else {
            desc.append(this.i18n.getText("cron.editor.the.day.of.every.month", new String[]{this.i18n.getText("cron.editor.nth." + bean.getDayOfMonth())}));
            desc.append(" ");
            desc.append(this.getTimePart(bean));
        }
        return desc.toString();
    }

    private String getTimePart(CronEditorBean bean) {
        StringBuilder desc = new StringBuilder();
        if (!bean.isRange()) {
            desc.append(this.getRunOnce(bean));
        } else {
            desc.append(this.getRepeatInRange(bean));
        }
        return desc.toString();
    }

    private String getRunOnce(CronEditorBean bean) {
        StringBuilder desc = new StringBuilder();
        desc.append(this.i18n.getText("cron.editor.at"));
        desc.append(" ");
        desc.append(bean.getHoursRunOnce()).append(":").append(this.getPaddedMinutes(bean.getMinutes())).append(" ").append(bean.getHoursRunOnceMeridian());
        return desc.toString();
    }

    private String getRepeatInRange(CronEditorBean bean) {
        StringBuilder desc = new StringBuilder();
        String increment = bean.getIncrementInMinutes();
        if (!increment.equals("0")) {
            String key = MINUTE_INCREMENT_TO_MESG_KEY.get(increment);
            desc.append(this.i18n.getText(key));
        }
        if (!bean.is24HourRange()) {
            desc.append(" ");
            desc.append(this.i18n.getText("cron.editor.from"));
            desc.append(" ");
            desc.append(bean.getHoursFrom()).append(":00 ").append(bean.getHoursFromMeridian());
            desc.append(" ");
            desc.append(this.i18n.getText("cron.editor.to"));
            desc.append(" ");
            desc.append(bean.getHoursTo()).append(":00 ").append(bean.getHoursToMeridian());
        }
        return desc.toString();
    }

    private String getPaddedMinutes(String minutes) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumIntegerDigits(2);
        return format.format(Integer.parseInt(minutes));
    }
}

