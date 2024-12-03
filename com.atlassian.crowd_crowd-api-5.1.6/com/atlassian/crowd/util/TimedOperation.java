/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util;

public class TimedOperation {
    protected final long start = System.currentTimeMillis();

    public String complete(String message) {
        long duration = System.currentTimeMillis() - this.start;
        String formattedTime = String.valueOf(duration) + "ms";
        return message + " in [ " + formattedTime + " ]";
    }
}

