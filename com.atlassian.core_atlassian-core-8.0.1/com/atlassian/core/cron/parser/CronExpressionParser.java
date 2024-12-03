/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.cron.parser;

import com.atlassian.core.cron.CronEditorBean;
import com.atlassian.core.cron.parser.CronDayOfWeekEntry;
import com.atlassian.core.cron.parser.CronHoursEntry;
import com.atlassian.core.cron.parser.CronMinutesEntry;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public class CronExpressionParser {
    public static final String DEFAULT_CRONSTRING = "0 0 1 ? * *";
    private static final String VALID_DAY_OF_MONTH = "0123456789L";
    private static final String WILDCARD = "*";
    private static final String NOT_APPLICABLE = "?";
    private static final int MINUTES_IN_HOUR = 60;
    private static final int NUM_CRON_FIELDS = 7;
    private static final int NUM_CRON_FIELDS_NO_YEAR = 6;
    private CronMinutesEntry minutesEntry;
    private CronHoursEntry hoursEntry;
    private String dayOfMonth;
    private String month;
    private String daysOfWeek;
    private CronDayOfWeekEntry daysOfWeekEntry;
    private String year;
    private String cronString;
    private boolean isDaily;
    private boolean isDayPerWeek;
    private boolean isDaysPerMonth;
    private boolean isAdvanced;
    private boolean validForEditor;
    private String seconds;

    public CronExpressionParser() {
        this(DEFAULT_CRONSTRING);
    }

    public CronExpressionParser(String cronString) {
        this.cronString = cronString;
        this.parseAndValidateCronString(this.cronString);
    }

    public CronEditorBean getCronEditorBean() {
        CronEditorBean cronEditorBean = new CronEditorBean();
        cronEditorBean.setCronString(this.cronString);
        cronEditorBean.setSeconds(this.seconds);
        cronEditorBean.setDayOfMonth(this.dayOfMonth);
        cronEditorBean.setIncrementInMinutes(Integer.toString(this.getIncrementInMinutes()));
        if (this.getIncrementInMinutes() == 0) {
            cronEditorBean.setHoursRunOnce(Integer.toString(this.getHoursEntry().getRunOnce()));
            cronEditorBean.setHoursRunOnceMeridian(this.getHoursEntry().getRunOnceMeridian());
        } else {
            cronEditorBean.setHoursFrom(Integer.toString(this.getHoursEntry().getFrom()));
            cronEditorBean.setHoursFromMeridian(this.getHoursEntry().getFromMeridian());
            cronEditorBean.setHoursTo(Integer.toString(this.getHoursEntry().getTo()));
            cronEditorBean.setHoursToMeridian(this.getHoursEntry().getToMeridian());
        }
        cronEditorBean.setMinutes(Integer.toString(this.getMinutesEntry().getRunOnce()));
        cronEditorBean.setSpecifiedDaysOfWeek(this.getDaysOfWeekEntry().getDaysAsNumbers());
        cronEditorBean.setDayInMonthOrdinal(this.getDaysOfWeekEntry().getDayInMonthOrdinal());
        if (this.isDailyMode()) {
            cronEditorBean.setMode("daily");
        } else if (this.isDayPerWeekMode()) {
            cronEditorBean.setMode("daysOfWeek");
        } else if (this.isDaysPerMonthMode()) {
            cronEditorBean.setDayOfWeekOfMonth(this.isDayOfWeekOfMonth());
            cronEditorBean.setMode("daysOfMonth");
        } else {
            cronEditorBean.setMode("advanced");
        }
        return cronEditorBean;
    }

    public String getCronString() {
        return this.cronString;
    }

    public boolean isValidForEditor() {
        return this.validForEditor;
    }

    public boolean isAdvancedMode() {
        return this.isAdvanced;
    }

    public boolean isDailyMode() {
        return this.isDaily;
    }

    public boolean isDayPerWeekMode() {
        return this.isDayPerWeek;
    }

    public boolean isDaysPerMonthMode() {
        return this.isDaysPerMonth;
    }

    public boolean isDayOfWeekOfMonth() {
        return this.notApplicable(this.dayOfMonth) && !this.isWild(this.daysOfWeek) && !this.notApplicable(this.daysOfWeek);
    }

    public String getDayOfMonth() {
        return this.dayOfMonth;
    }

    public CronMinutesEntry getMinutesEntry() {
        return this.minutesEntry;
    }

    public CronHoursEntry getHoursEntry() {
        return this.hoursEntry;
    }

    public CronDayOfWeekEntry getDaysOfWeekEntry() {
        return this.daysOfWeekEntry;
    }

    public int getIncrementInMinutes() {
        return this.calculateIncrementInMinutes();
    }

    private int calculateIncrementInMinutes() {
        int incrementInMinutes = 0;
        boolean minutesHasIncrement = this.minutesEntry.hasIncrement();
        boolean hoursHasIncrement = this.hoursEntry.hasIncrement();
        int minutesIncrement = this.minutesEntry.getIncrement();
        int hoursIncrement = this.hoursEntry.getIncrement();
        if (minutesHasIncrement && hoursHasIncrement && hoursIncrement != 1) {
            incrementInMinutes = 0;
        } else if (minutesHasIncrement) {
            incrementInMinutes = minutesIncrement;
        } else if (hoursHasIncrement) {
            incrementInMinutes = hoursIncrement * 60;
        }
        return incrementInMinutes;
    }

    private void parseAndValidateCronString(String cronString) {
        this.parseCronString(cronString);
        this.updateEditorFlags();
        if (!this.validForEditor) {
            this.parseCronString(DEFAULT_CRONSTRING);
        }
    }

    private void parseCronString(String cronString) {
        StringTokenizer st = new StringTokenizer(cronString);
        if (st.countTokens() != 7 && st.countTokens() != 6) {
            throw new IllegalArgumentException("The provided cron string does not have 7 parts: " + cronString);
        }
        this.seconds = st.nextToken();
        String minutes = st.nextToken();
        String hours = st.nextToken();
        this.dayOfMonth = st.nextToken();
        this.month = st.nextToken();
        this.daysOfWeek = st.nextToken();
        this.hoursEntry = new CronHoursEntry(hours);
        this.minutesEntry = new CronMinutesEntry(minutes);
        this.daysOfWeekEntry = new CronDayOfWeekEntry(this.daysOfWeek);
        if (st.hasMoreTokens()) {
            this.year = st.nextToken();
        }
    }

    private void updateEditorFlags() {
        this.isDaily = !(!this.isWild(this.dayOfMonth) && !this.notApplicable(this.dayOfMonth) || !this.isWild(this.month) || !this.isWild(this.daysOfWeek) && !this.notApplicable(this.daysOfWeek));
        this.isDayPerWeek = (this.isWild(this.dayOfMonth) || this.notApplicable(this.dayOfMonth)) && this.isWild(this.month) && this.daysOfWeekEntry.getDayInMonthOrdinal() == null && !this.isWild(this.daysOfWeek);
        boolean numericDayOfMonth = !this.notApplicable(this.dayOfMonth) && !this.isWild(this.dayOfMonth) && this.isWild(this.month) && this.notApplicable(this.daysOfWeek) && StringUtils.containsOnly((CharSequence)this.dayOfMonth.toUpperCase(), (String)VALID_DAY_OF_MONTH);
        boolean dayOfWeekOfMonth = this.notApplicable(this.dayOfMonth) && this.isWild(this.month) && !this.isWild(this.daysOfWeek) && !this.notApplicable(this.daysOfWeek) && this.daysOfWeekEntry.getDayInMonthOrdinal() != null;
        this.isDaysPerMonth = dayOfWeekOfMonth || numericDayOfMonth;
        boolean isValidMode = this.isDaily || this.isDayPerWeek || this.isDaysPerMonth;
        boolean hoursAndMinutesAreValid = this.hoursEntry.isValid() && this.minutesEntry.isValid();
        boolean daysOfWeekAreValid = this.daysOfWeekEntry.isValid();
        boolean incrementsValid = !this.hoursEntry.hasIncrement() || !this.minutesEntry.hasIncrement() || this.hoursEntry.getIncrement() == 1;
        hoursAndMinutesAreValid = hoursAndMinutesAreValid && (!this.hoursEntry.isRunOnce() || !this.minutesEntry.hasIncrement());
        boolean bl = this.validForEditor = "0".equals(this.seconds) && this.isWild(this.month) && isValidMode && hoursAndMinutesAreValid && daysOfWeekAreValid && incrementsValid && StringUtils.isEmpty((CharSequence)this.year);
        if (!this.validForEditor) {
            this.isDaily = false;
            this.isDayPerWeek = false;
            this.isDaysPerMonth = false;
            this.isAdvanced = true;
        }
    }

    private boolean isWild(String expressionPart) {
        return WILDCARD.equals(expressionPart);
    }

    private boolean notApplicable(String expressionPart) {
        return NOT_APPLICABLE.equals(expressionPart);
    }
}

