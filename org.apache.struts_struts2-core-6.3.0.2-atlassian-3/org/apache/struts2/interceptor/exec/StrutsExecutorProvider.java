/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.exec;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.exec.ExecutorProvider;

public class StrutsExecutorProvider
implements ExecutorProvider {
    private static final Logger LOG = LogManager.getLogger(StrutsExecutorProvider.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    public void execute(Runnable task) {
        LOG.debug("Executing task: {}", (Object)task);
        this.executor.execute(task);
    }

    @Override
    public boolean isShutdown() {
        return this.executor.isShutdown();
    }

    @Override
    public void shutdown() {
        LOG.debug("Shutting down executor");
        this.executor.shutdown();
    }
}

