/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.cron.parser;

import com.atlassian.core.util.collection.EasyList;
import com.atlassian.core.util.map.EasyMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronDayOfWeekEntry {
    private static final Logger log = LoggerFactory.getLogger(CronDayOfWeekEntry.class);
    private static final String ORDINAL_SEPARATOR = "#";
    private static final String LIST_SEPARATOR = ",";
    private static final String LAST = "L";
    private static final Map VALID_DAYS_MAP = EasyMap.build("MON", "2", "TUE", "3", "WED", "4", "THU", "5", "FRI", "6", "SAT", "7", "SUN", "1");
    private static final List VALID_NUMERIC_ORDINAL_VALUES = EasyList.build("1", "2", "3", "4");
    private static final String VALID_CHARACTERS = "MONTUEWEDTHUFRISATSUN1234567L#,?*";
    private boolean valid = true;
    private String ordinal = null;
    private final List<String> specifiedDays = new ArrayList<String>();

    public CronDayOfWeekEntry(String dayOfWeekEntry) {
        this.parseEntry(dayOfWeekEntry);
    }

    public boolean isDaySpecified(String dayStr) {
        String day = this.getDayForValue(dayStr);
        return day != null && this.specifiedDays.contains(day);
    }

    public String getDayInMonthOrdinal() {
        return this.ordinal;
    }

    public String getDaysAsNumbers() {
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (String day : this.specifiedDays) {
            result.append(day);
            if (i + 1 < this.specifiedDays.size()) {
                result.append(LIST_SEPARATOR);
            }
            ++i;
        }
        return result.toString();
    }

    public boolean isValid() {
        return this.valid;
    }

    private void parseEntry(String dayOfWeekEntry) {
        if (StringUtils.isBlank((CharSequence)dayOfWeekEntry)) {
            log.debug("Tried to create a CronDayOfWeek with empty or null string.");
            this.valid = false;
        } else if (!StringUtils.containsOnly((CharSequence)dayOfWeekEntry.toUpperCase(), (String)VALID_CHARACTERS)) {
            log.debug("Tried to create a CronDayOfWeek with invalid characters: " + dayOfWeekEntry);
            this.valid = false;
        } else if (StringUtils.contains((CharSequence)(dayOfWeekEntry = dayOfWeekEntry.toUpperCase()), (CharSequence)ORDINAL_SEPARATOR)) {
            this.parseOrdinalValue(dayOfWeekEntry);
        } else if (StringUtils.contains((CharSequence)dayOfWeekEntry, (CharSequence)LIST_SEPARATOR)) {
            this.parseDaysOfWeek(dayOfWeekEntry);
        } else if (StringUtils.contains((CharSequence)dayOfWeekEntry, (CharSequence)LAST)) {
            this.parseLastDayOfWeek(dayOfWeekEntry);
        } else {
            this.specifiedDays.add(dayOfWeekEntry);
        }
    }

    private void parseLastDayOfWeek(String dayOfWeekEntry) {
        if (!dayOfWeekEntry.endsWith(LAST)) {
            log.debug("The L character which specifies last is not at the end of the day of week string.");
            this.valid = false;
        } else {
            this.ordinal = LAST;
            String dayOfWeekStr = dayOfWeekEntry.substring(0, dayOfWeekEntry.length() - 1);
            String dayOfWeek = this.getDayForValue(dayOfWeekStr);
            if (dayOfWeek != null) {
                this.specifiedDays.add(dayOfWeek);
            } else {
                log.debug("The value specfied as a day of week was invalid: " + dayOfWeekStr);
                this.valid = false;
            }
        }
    }

    private void parseDaysOfWeek(String dayOfWeekEntry) {
        String[] days = StringUtils.split((String)dayOfWeekEntry, (String)LIST_SEPARATOR);
        if (days == null || days.length > 7) {
            log.debug("The days of week has specified more than 7, this is not valid: " + dayOfWeekEntry);
            this.valid = false;
        } else {
            for (String dayStr : days) {
                String day = this.getDayForValue(dayStr);
                if (day == null) {
                    log.debug("A day of week was specified that can not be mapped: " + dayStr);
                    this.valid = false;
                    break;
                }
                this.specifiedDays.add(day);
            }
        }
    }

    private void parseOrdinalValue(String dayOfWeekEntry) {
        String[] strings = StringUtils.split((String)dayOfWeekEntry, (String)ORDINAL_SEPARATOR);
        if (strings == null || strings.length != 2) {
            log.debug("The ordinal value specifed was not of the correct form: " + dayOfWeekEntry);
            this.valid = false;
        } else {
            String dayString = this.getDayForValue(strings[0]);
            if (dayString != null) {
                this.specifiedDays.add(dayString);
                String secondString = strings[1].toUpperCase();
                if (VALID_NUMERIC_ORDINAL_VALUES.contains(secondString)) {
                    this.ordinal = secondString;
                } else {
                    log.debug("invalid ordinal value " + this.ordinal);
                    this.valid = false;
                }
            }
        }
    }

    private String getDayForValue(String dayString) {
        if (VALID_DAYS_MAP.values().contains(dayString.toUpperCase())) {
            return dayString;
        }
        if (VALID_DAYS_MAP.containsKey(dayString)) {
            return (String)VALID_DAYS_MAP.get(dayString);
        }
        log.debug("Unable to resolve a day of week for the string: " + dayString);
        this.valid = false;
        return null;
    }
}

