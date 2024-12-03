/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxCallbackContext
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import java.time.Duration;

class SandboxRequest<T, R> {
    private final SandboxTask<T, R> task;
    private final T input;
    private final long requestStart;
    private final long requestTimeLimit;
    private final SandboxCallbackContext callbackContext;

    SandboxRequest(SandboxTask<T, R> task, T input, Duration timeLimit, SandboxCallbackContext callbackContext) {
        this.task = task;
        this.input = input;
        this.requestStart = System.nanoTime();
        this.requestTimeLimit = timeLimit.toNanos();
        this.callbackContext = callbackContext;
    }

    public SandboxTask<T, R> getTask() {
        return this.task;
    }

    public T getInput() {
        return this.input;
    }

    public SandboxCallbackContext getCallbackContext() {
        return this.callbackContext;
    }

    Duration currentDuration() {
        return Duration.ofNanos(System.nanoTime() - this.requestStart);
    }

    Duration getTimeLimit() {
        return Duration.ofNanos(this.requestTimeLimit);
    }

    public String toString() {
        return "SandboxRequest{task=" + this.task.getClass().getName() + ", input=" + this.input.toString() + ", duration=" + this.currentDuration() + ", timeLimit=" + this.getTimeLimit() + "}";
    }
}

