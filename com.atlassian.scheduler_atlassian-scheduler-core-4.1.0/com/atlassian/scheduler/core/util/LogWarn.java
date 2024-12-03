/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.scheduler.core.util;

import org.slf4j.Logger;

public class LogWarn {
    private LogWarn() {
    }

    public static void logWarn(Logger log, String message, Throwable cause) {
        if (log.isDebugEnabled()) {
            log.warn(message, cause);
        } else {
            log.warn(message + ": " + cause);
        }
    }
}

