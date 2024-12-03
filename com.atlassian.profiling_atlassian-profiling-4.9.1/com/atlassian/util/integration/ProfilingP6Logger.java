/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.p6spy.engine.logging.appender.P6Logger
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.integration;

import com.atlassian.util.profiling.Timers;
import com.p6spy.engine.logging.appender.P6Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilingP6Logger
implements P6Logger {
    private static final Logger log = LoggerFactory.getLogger(ProfilingP6Logger.class);

    public void logSQL(int now, String elapsed, long connectionId, String category, String prepared, String sql) {
        if ("statement".equals(category)) {
            String logEntry = now + "|" + elapsed + "|" + (connectionId == -1L ? "" : String.valueOf(connectionId)) + "|" + category + "|" + sql;
            Timers.start(logEntry).close();
        }
    }

    public void logException(Exception e) {
        log.debug("", (Throwable)e);
    }

    public void logText(String s) {
        log.debug(s);
    }

    public String getLastEntry() {
        return null;
    }
}

