/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;

public class ConfluenceDiagnosticsExecutor
implements Executor {
    private final ThreadPoolExecutor delegate = new ThreadPoolExecutor(1, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(250), ThreadFactories.namedThreadFactory((String)"alert-dispatch", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));

    @Override
    public void execute(Runnable command) {
        this.delegate.execute(command);
    }

    @PreDestroy
    public void destroy() {
        this.delegate.shutdown();
    }
}

