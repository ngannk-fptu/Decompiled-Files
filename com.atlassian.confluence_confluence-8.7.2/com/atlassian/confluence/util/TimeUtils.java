/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.TimePeriod;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class TimeUtils {
    private static final Logger log = LoggerFactory.getLogger(TimeUtils.class);

    private static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            log.warn("Pause was interrupted");
        }
    }

    public static void pause(TimePeriod period) {
        TimeUtils.pause(period.getPeriod(), period.getTimeUnit());
    }

    public static void pause(Duration duration) {
        TimeUtils.pause(duration.getMillis());
    }

    public static void pause(long time, TimeUnit unit) {
        TimeUtils.pause(unit.toMillis(time));
    }

    public static void pauseUntil(Date targetDate) {
        Date now = new Date();
        if (now.before(targetDate)) {
            TimeUtils.pause(targetDate.getTime() - now.getTime());
        }
    }
}

