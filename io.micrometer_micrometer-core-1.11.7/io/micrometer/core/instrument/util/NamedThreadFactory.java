/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory
implements ThreadFactory {
    private final AtomicInteger sequence = new AtomicInteger(1);
    private final String prefix;

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        int seq = this.sequence.getAndIncrement();
        thread.setName(this.prefix + (seq > 1 ? "-" + seq : ""));
        thread.setDaemon(true);
        return thread;
    }
}

