/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.util.JodaTimeUtils
 *  org.joda.time.LocalDateTime
 *  org.joda.time.LocalTime
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.content.render.xhtml.model.time;

import com.atlassian.confluence.api.util.JodaTimeUtils;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Time {
    public static final String STORAGE_DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern((String)"yyyy-MM-dd");
    private final String datetimeString;
    private String cssClasses;
    private final LocalDate localDate;

    public Time(String datetimeString) throws IllegalArgumentException {
        this.datetimeString = datetimeString;
        this.localDate = this.parseDate(datetimeString);
    }

    public String getDatetimeString() {
        return this.datetimeString;
    }

    @Deprecated(forRemoval=true)
    public LocalDateTime getDateTime() {
        return JodaTimeUtils.convert((LocalDate)this.localDate).toLocalDateTime(LocalTime.MIDNIGHT);
    }

    public LocalDate getLocalDate() {
        return this.localDate;
    }

    public String getCssClasses() {
        return this.cssClasses;
    }

    public void setCssClasses(String cssClasses) {
        this.cssClasses = cssClasses;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Time time = (Time)o;
        if (this.cssClasses != null ? !this.cssClasses.equals(time.cssClasses) : time.cssClasses != null) {
            return false;
        }
        return this.datetimeString.equals(time.datetimeString);
    }

    public int hashCode() {
        int result = this.datetimeString.hashCode();
        result = 31 * result + (this.cssClasses != null ? this.cssClasses.hashCode() : 0);
        return result;
    }

    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        }
        catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}

