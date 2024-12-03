/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

class ReducerTaskScheduler {
    private final AtomicReference<State> state = new AtomicReference<State>(State.INACTIVE);
    private final ExecutorService executorService;
    private final Runnable task;

    ReducerTaskScheduler(ExecutorService executorService, Runnable task) {
        this.executorService = executorService;
        this.task = task;
    }

    private void scheduleExecution() {
        this.executorService.submit(this.task);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void requestExecution() {
        block4: while (true) {
            State currentState = this.state.get();
            switch (currentState) {
                case INACTIVE: {
                    if (!this.state.compareAndSet(State.INACTIVE, State.RUNNING)) continue block4;
                    this.scheduleExecution();
                    return;
                }
                case RUNNING: {
                    if (this.state.compareAndSet(State.RUNNING, State.REQUESTED)) return;
                    continue block4;
                }
            }
            break;
        }
    }

    void afterExecution() {
        block4: while (true) {
            State currentState = this.state.get();
            switch (currentState) {
                case REQUESTED: {
                    this.state.set(State.RUNNING);
                    this.scheduleExecution();
                    return;
                }
                case RUNNING: {
                    if (!this.state.compareAndSet(State.RUNNING, State.INACTIVE)) continue block4;
                    return;
                }
            }
            break;
        }
        throw new IllegalStateException("Inactive state is illegal here.");
    }

    private static enum State {
        INACTIVE,
        RUNNING,
        REQUESTED;

    }
}

