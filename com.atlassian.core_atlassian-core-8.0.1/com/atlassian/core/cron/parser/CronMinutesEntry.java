/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.cron.parser;

import com.atlassian.core.util.collection.EasyList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronMinutesEntry {
    private static final Logger log = LoggerFactory.getLogger(CronMinutesEntry.class);
    static final String MINUTE_INCREMENT_SEPARATOR = "/";
    private static final String REGEX_VALID = "[\\d/]+";
    private static final int UNSET_FLAG = -1;
    private static final int MAX_MINUTES = 59;
    private static final int MINUTE_FACTOR = 5;
    private static final List VALID_INCREMENT_IN_MINUTES = EasyList.build(new Integer(15), new Integer(30));
    private int runOnce = -1;
    private boolean valid = true;
    private int increment = -1;

    public CronMinutesEntry(String cronEntry) {
        if (cronEntry == null) {
            throw new IllegalArgumentException("Can not create a cron entry from a null value.");
        }
        this.parseEntry(cronEntry);
    }

    public boolean isValid() {
        boolean validIncrement = this.increment == -1 || VALID_INCREMENT_IN_MINUTES.contains(new Integer(this.increment)) && this.runOnce == 0;
        return this.valid && this.runOnce <= 59 && this.runOnce >= 0 && this.runOnce % 5 == 0 && validIncrement;
    }

    public int getRunOnce() {
        return this.runOnce;
    }

    private void parseEntry(String cronEntry) {
        if (!cronEntry.matches(REGEX_VALID)) {
            this.valid = false;
        } else {
            int separator = cronEntry.indexOf(MINUTE_INCREMENT_SEPARATOR);
            if (separator >= 0) {
                String incrementStr = cronEntry.substring(separator + 1, cronEntry.length());
                try {
                    this.increment = Integer.parseInt(incrementStr);
                }
                catch (NumberFormatException nfe) {
                    log.debug("The increment portion of the hour cron entry must be an integer.");
                    this.valid = false;
                }
                cronEntry = cronEntry.substring(0, separator);
            }
            try {
                this.runOnce = Integer.parseInt(cronEntry);
            }
            catch (NumberFormatException nfe) {
                log.debug("The minute of the cron entry must be an integer, instead it is: " + cronEntry);
                this.valid = false;
            }
        }
    }

    public int getIncrement() {
        return this.increment;
    }

    public boolean hasIncrement() {
        return this.increment > 0;
    }
}

