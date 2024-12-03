/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util;

import io.atlassian.util.concurrent.Timeout;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitUntil {
    private static final Logger log = LoggerFactory.getLogger(WaitUntil.class);
    private static final int DEFAULT_STARTUP_WAIT = 60;
    private static final int STARTUP_WAIT = Integer.getInteger("com.atlassian.plugin.startup.wait", 60);

    private WaitUntil() {
    }

    public static boolean invoke(WaitCondition waitCondition) {
        return WaitUntil.invoke(waitCondition, STARTUP_WAIT);
    }

    public static boolean invoke(WaitCondition waitCondition, int tries) {
        int secondMillis = 1000;
        return WaitUntil.invoke(waitCondition, tries * 1000, TimeUnit.MILLISECONDS, 1000);
    }

    public static boolean invoke(WaitCondition waitCondition, int time, TimeUnit unit, int retryInterval) {
        Timeout timeout = Timeout.getMillisTimeout((long)time, (TimeUnit)unit);
        boolean successful = false;
        while (!timeout.isExpired()) {
            if (waitCondition.isFinished()) {
                successful = true;
                break;
            }
            if (log.isInfoEnabled()) {
                log.info("{}, {} seconds remaining", (Object)waitCondition.getWaitMessage(), (Object)TimeUnit.SECONDS.convert(timeout.getTime(), timeout.getUnit()));
            }
            try {
                Thread.sleep(unit.toMillis(retryInterval));
            }
            catch (InterruptedException e) {
                break;
            }
        }
        return successful;
    }

    public static interface WaitCondition {
        public boolean isFinished();

        public String getWaitMessage();
    }
}

