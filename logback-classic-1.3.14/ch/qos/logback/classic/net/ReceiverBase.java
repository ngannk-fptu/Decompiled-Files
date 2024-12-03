/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.spi.ContextAwareBase
 *  ch.qos.logback.core.spi.LifeCycle
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

public abstract class ReceiverBase
extends ContextAwareBase
implements LifeCycle {
    private boolean started;

    public final void start() {
        if (this.isStarted()) {
            return;
        }
        if (this.getContext() == null) {
            throw new IllegalStateException("context not set");
        }
        if (this.shouldStart()) {
            this.getContext().getExecutorService().execute(this.getRunnableTask());
            this.started = true;
        }
    }

    public final void stop() {
        if (!this.isStarted()) {
            return;
        }
        try {
            this.onStop();
        }
        catch (RuntimeException ex) {
            this.addError("on stop: " + ex, ex);
        }
        this.started = false;
    }

    public final boolean isStarted() {
        return this.started;
    }

    protected abstract boolean shouldStart();

    protected abstract void onStop();

    protected abstract Runnable getRunnableTask();
}

