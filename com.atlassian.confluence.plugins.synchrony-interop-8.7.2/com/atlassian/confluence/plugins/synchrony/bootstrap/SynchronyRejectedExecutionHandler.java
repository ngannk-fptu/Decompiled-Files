/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronyRejectedExecutionHandler
implements RejectedExecutionHandler {
    private static final Logger log = LoggerFactory.getLogger(SynchronyRejectedExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String message = String.format("Synchrony thread pool executor %s has rejected task %s because all threads were busy. Current maximum pool size is %s", e, r, e.getMaximumPoolSize());
        log.error(message);
        throw new RejectedExecutionException(message);
    }
}

