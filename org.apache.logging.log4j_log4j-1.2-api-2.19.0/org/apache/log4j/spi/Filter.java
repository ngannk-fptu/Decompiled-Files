/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.spi;

import org.apache.log4j.spi.LoggingEvent;

public abstract class Filter {
    public static final int DENY = -1;
    public static final int NEUTRAL = 0;
    public static final int ACCEPT = 1;
    @Deprecated
    public Filter next;
    private static final boolean isCorePresent;

    public void activateOptions() {
    }

    public abstract int decide(LoggingEvent var1);

    public void setNext(Filter next) {
        this.next = next;
    }

    public Filter getNext() {
        return this.next;
    }

    static {
        boolean temp;
        try {
            temp = Class.forName("org.apache.logging.log4j.core.Filter") != null;
        }
        catch (Exception | LinkageError e) {
            temp = false;
        }
        isCorePresent = temp;
    }
}

