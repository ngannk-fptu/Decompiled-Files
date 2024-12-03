/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    INSTANCE;

    private static final Logger log;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught exception: " + e.getMessage(), e);
    }

    static {
        log = LoggerFactory.getLogger(LoggingUncaughtExceptionHandler.class);
    }
}

