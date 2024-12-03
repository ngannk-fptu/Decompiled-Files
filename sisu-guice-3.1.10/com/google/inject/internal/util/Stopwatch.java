/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import java.util.logging.Logger;

public final class Stopwatch {
    private static final Logger logger = Logger.getLogger(Stopwatch.class.getName());
    private long start = System.currentTimeMillis();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long reset() {
        long now = System.currentTimeMillis();
        try {
            long l = now - this.start;
            return l;
        }
        finally {
            this.start = now;
        }
    }

    public void resetAndLog(String label) {
        logger.fine(label + ": " + this.reset() + "ms");
    }
}

