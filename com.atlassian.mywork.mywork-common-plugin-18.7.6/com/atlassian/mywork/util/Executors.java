/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 */
package com.atlassian.mywork.util;

import com.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Executors {
    private static final Logger LOG = LoggerFactory.getLogger(Executors.class);

    public static ExecutorService newSingleThreadExecutor(String threadName) {
        return java.util.concurrent.Executors.newSingleThreadExecutor(new MyWorkThreadFactory(threadName));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String threadName) {
        return java.util.concurrent.Executors.newSingleThreadScheduledExecutor(new MyWorkThreadFactory(threadName));
    }

    private static class MyWorkThreadFactory
    implements ThreadFactory {
        private final ThreadFactory innerThreadFactory;

        public MyWorkThreadFactory(String threadName) {
            this.innerThreadFactory = ThreadFactories.namedThreadFactory((String)threadName, (ThreadFactories.Type)ThreadFactories.Type.DAEMON);
        }

        @Override
        public Thread newThread(Runnable r) {
            MDC.clear();
            Thread thread = this.innerThreadFactory.newThread(r);
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    LOG.error("Uncaught exception in thread \"" + t.getName() + "\": " + e.getMessage(), e);
                }
            });
            return thread;
        }
    }
}

