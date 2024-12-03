/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.healthcheck.core.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckThreadFactory
implements ThreadFactory {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckThreadFactory.class);
    private static final Thread.UncaughtExceptionHandler UNCAUGHT_HANDLER = new HealthCheckUncaughtExceptionHandler();
    private final AtomicInteger threadCounter = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "HealthCheckThread-" + this.threadCounter.incrementAndGet());
        t.setUncaughtExceptionHandler(UNCAUGHT_HANDLER);
        t.setDaemon(true);
        return t;
    }

    private static class HealthCheckUncaughtExceptionHandler
    implements Thread.UncaughtExceptionHandler {
        private HealthCheckUncaughtExceptionHandler() {
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("The thread: " + t.getName() + " threw the exception: " + e.getMessage(), e);
        }
    }
}

