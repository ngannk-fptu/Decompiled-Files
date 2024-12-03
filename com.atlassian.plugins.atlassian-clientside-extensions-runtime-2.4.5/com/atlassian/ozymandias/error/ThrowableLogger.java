/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.ozymandias.error;

import org.slf4j.Logger;

public final class ThrowableLogger {
    public static void logThrowable(String message, Throwable t, Logger log) {
        log.warn(message);
        if (log.isDebugEnabled()) {
            log.debug(message, t);
        }
    }

    public static String getClassName(Object o) {
        if (o == null) {
            return "NULL";
        }
        if (o instanceof Class) {
            return ((Class)o).getName();
        }
        return o.getClass().getName();
    }

    private ThrowableLogger() {
    }
}

