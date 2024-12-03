/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.cron.parser;

import com.atlassian.core.cron.parser.MeridianHour;
import com.atlassian.core.util.collection.EasyList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronHoursEntry {
    private static final Logger log = LoggerFactory.getLogger(CronHoursEntry.class);
    private static final String REGEX_VALID = "[\\d*/-]+";
    private static final int NO_INCREMENT_PART = -1;
    private static final List ACCEPTED_HOUR_INCREMENTS = EasyList.build(new Integer(-1), new Integer(1), new Integer(2), new Integer(3));
    static final String INCREMENT_DELIMITER = "/";
    static final String RANGE_DELIMITER = "-";
    private static final MeridianHour NULL_MERIDIAN_HOUR = new MeridianHour(-1, null);
    private MeridianHour fromMeridianHour = NULL_MERIDIAN_HOUR;
    private MeridianHour toMeridianHour = NULL_MERIDIAN_HOUR;
    private MeridianHour runOnceMeridianHour = NULL_MERIDIAN_HOUR;
    private int increment = -1;
    private boolean valid = true;

    public CronHoursEntry(String cronEntry) {
        if (cronEntry == null) {
            throw new IllegalArgumentException("Can not create a cron entry from a null value.");
        }
        this.parseEntry(cronEntry);
    }

    public boolean isValid() {
        return this.valid && ACCEPTED_HOUR_INCREMENTS.contains(new Integer(this.increment));
    }

    public int getFrom() {
        return this.fromMeridianHour.getHour();
    }

    public int getTo() {
        return this.toMeridianHour.getHour();
    }

    public String getFromMeridian() {
        return this.fromMeridianHour.getMeridian();
    }

    public String getToMeridian() {
        return this.toMeridianHour.getMeridian();
    }

    public int getRunOnce() {
        return this.runOnceMeridianHour.getHour();
    }

    public String getRunOnceMeridian() {
        return this.runOnceMeridianHour.getMeridian();
    }

    public int getIncrement() {
        return this.increment;
    }

    public boolean hasIncrement() {
        return this.increment != -1;
    }

    public boolean isRunOnce() {
        return !NULL_MERIDIAN_HOUR.equals(this.runOnceMeridianHour);
    }

    private void parseEntry(String cronEntry) {
        if (!cronEntry.matches(REGEX_VALID)) {
            this.valid = false;
        } else {
            int dashIndex;
            if ("*".equals(cronEntry)) {
                this.increment = 1;
                this.toMeridianHour = this.fromMeridianHour = this.parseMeridianHour("0");
                return;
            }
            int slashIndex = cronEntry.indexOf(INCREMENT_DELIMITER);
            if (slashIndex >= 0) {
                String incrementStr = cronEntry.substring(slashIndex + 1, cronEntry.length());
                try {
                    this.increment = Integer.parseInt(incrementStr);
                }
                catch (NumberFormatException nfe) {
                    log.debug("The increment portion of the hour cron entry must be an integer.");
                    this.valid = false;
                }
                cronEntry = cronEntry.substring(0, slashIndex);
            }
            if ((dashIndex = cronEntry.indexOf(RANGE_DELIMITER)) >= 0) {
                String fromStr = cronEntry.substring(0, dashIndex);
                this.fromMeridianHour = this.parseMeridianHour(fromStr);
                String toStr = cronEntry.substring(dashIndex + 1, cronEntry.length());
                this.toMeridianHour = this.parseMeridianHour(this.incrementHourByOne(toStr));
            } else if (this.hasIncrement()) {
                this.fromMeridianHour = this.parseMeridianHour(cronEntry);
                this.toMeridianHour = this.parseMeridianHour(cronEntry);
            } else {
                this.runOnceMeridianHour = this.parseMeridianHour(cronEntry);
            }
        }
    }

    private MeridianHour parseMeridianHour(String twentyFourHour) {
        MeridianHour meridianHour;
        if ("*".equals(twentyFourHour)) {
            twentyFourHour = "0";
        }
        if ((meridianHour = MeridianHour.parseMeridianHour(twentyFourHour)) == null) {
            this.valid = false;
            meridianHour = NULL_MERIDIAN_HOUR;
        }
        return meridianHour;
    }

    private String incrementHourByOne(String hour) {
        int h = Integer.parseInt(hour);
        h = h == 23 ? 0 : h + 1;
        return "" + h;
    }
}

