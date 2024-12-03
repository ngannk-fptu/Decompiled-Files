/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.Timeout
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.util.misc;

import io.atlassian.util.concurrent.Timeout;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ConcurrentConversionUtil {
    public static com.atlassian.util.concurrent.Timeout toComTimeout(Timeout ioTimeout) {
        if (ioTimeout == null) {
            return null;
        }
        if (TimeUnit.MILLISECONDS == ioTimeout.getUnit()) {
            return com.atlassian.util.concurrent.Timeout.getMillisTimeout((long)ioTimeout.getTimeoutPeriod(), (TimeUnit)TimeUnit.MILLISECONDS);
        }
        if (TimeUnit.NANOSECONDS == ioTimeout.getUnit()) {
            return com.atlassian.util.concurrent.Timeout.getNanosTimeout((long)ioTimeout.getTimeoutPeriod(), (TimeUnit)TimeUnit.NANOSECONDS);
        }
        throw new IllegalArgumentException("Don't know how to convert the time unit of the timeout: " + ioTimeout.getUnit());
    }

    public static Timeout toIoTimeout(com.atlassian.util.concurrent.Timeout comTimeout) {
        if (comTimeout == null) {
            return null;
        }
        if (TimeUnit.MILLISECONDS == comTimeout.getUnit()) {
            return Timeout.getMillisTimeout((long)comTimeout.getTimeoutPeriod(), (TimeUnit)TimeUnit.MILLISECONDS);
        }
        if (TimeUnit.NANOSECONDS == comTimeout.getUnit()) {
            return Timeout.getNanosTimeout((long)comTimeout.getTimeoutPeriod(), (TimeUnit)TimeUnit.NANOSECONDS);
        }
        throw new IllegalArgumentException("Don't know how to convert the time unit of the timeout: " + comTimeout.getUnit());
    }
}

