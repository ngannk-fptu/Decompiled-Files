/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.cron.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MeridianHour {
    private static final Logger log = LoggerFactory.getLogger(MeridianHour.class);
    private static final int MERIDIAN_HOURS = 12;
    private final int hour;
    private final String meridian;

    public MeridianHour(int hour, String meridian) {
        this.hour = hour;
        this.meridian = meridian;
    }

    public int getHour() {
        return this.hour;
    }

    public String getMeridian() {
        return this.meridian;
    }

    public static MeridianHour parseMeridianHour(String twentyFourHour) {
        String meridian = "am";
        try {
            int hour = Integer.parseInt(twentyFourHour);
            if (hour < 0 || hour > 23) {
                log.debug("The hour of the cron entry is out of range (0-23): " + twentyFourHour);
                return null;
            }
            if (hour == 0) {
                hour = 12;
            } else if (hour == 12) {
                meridian = "pm";
            } else if (hour >= 12) {
                meridian = "pm";
                hour -= 12;
            }
            return new MeridianHour(hour, meridian);
        }
        catch (NumberFormatException nfe) {
            log.debug("The hour of the cron entry must be an integer, instead it is: " + twentyFourHour);
            return null;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MeridianHour that = (MeridianHour)o;
        if (this.hour != that.hour) {
            return false;
        }
        return !(this.meridian != null ? !this.meridian.equals(that.meridian) : that.meridian != null);
    }

    public int hashCode() {
        int result = this.hour;
        result = 31 * result + (this.meridian != null ? this.meridian.hashCode() : 0);
        return result;
    }
}

