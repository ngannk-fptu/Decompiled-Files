/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.utils.process.ExternalProcess
 *  com.atlassian.utils.process.ProcessHandler
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ProcessHandler;

abstract class DelegatingExternalProcess
implements ExternalProcess {
    final ExternalProcess delegate;

    DelegatingExternalProcess(ExternalProcess delegate) {
        this.delegate = delegate;
    }

    public void execute() {
        this.delegate.execute();
    }

    public void executeWhile(Runnable runnable) {
        this.delegate.executeWhile(runnable);
    }

    public void finish() {
        this.delegate.finish();
    }

    public boolean finish(int maxWait) {
        return this.delegate.finish(maxWait);
    }

    public String getCommandLine() {
        return this.delegate.getCommandLine();
    }

    public ProcessHandler getHandler() {
        return this.delegate.getHandler();
    }

    public long getStartTime() {
        return this.delegate.getStartTime();
    }

    public boolean isAlive() {
        return this.delegate.isAlive();
    }

    public boolean isTimedOut() {
        return this.delegate.isTimedOut();
    }

    public void start() {
        this.delegate.start();
    }

    public void cancel() {
        this.delegate.cancel();
    }

    public boolean isCanceled() {
        return this.delegate.isCanceled();
    }

    public void resetWatchdog() {
        this.delegate.resetWatchdog();
    }
}

