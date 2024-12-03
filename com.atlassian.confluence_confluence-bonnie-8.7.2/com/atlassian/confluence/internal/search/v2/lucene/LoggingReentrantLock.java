/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingReentrantLock
extends ReentrantLock {
    private static final Logger log = LoggerFactory.getLogger(LoggingReentrantLock.class);
    private final String name;

    public LoggingReentrantLock(String name) {
        this.name = name;
    }

    @Override
    public void lock() {
        log.debug("Acquiring lock: {}", (Object)this.name);
        try (Ticker ignored = Timers.start((String)(this.getClass().getName() + ".lock(): " + this.name));){
            super.lock();
        }
    }

    @Override
    public void unlock() {
        log.debug("Releasing lock: {}", (Object)this.name);
        super.unlock();
    }
}

