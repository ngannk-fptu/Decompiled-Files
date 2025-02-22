/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.hc.core5.annotation.Internal;

@Internal
public final class ExecSupport {
    private static final AtomicLong COUNT = new AtomicLong(0L);

    public static long getNextExecNumber() {
        return COUNT.incrementAndGet();
    }

    public static String getNextExchangeId() {
        return String.format("ex-%010d", COUNT.incrementAndGet());
    }
}

