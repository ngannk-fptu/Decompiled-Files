/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.eclipse.jetty.util.thread.Scheduler$Task
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IdleTimeout {
    private static final Logger LOG = LoggerFactory.getLogger(IdleTimeout.class);
    private final Scheduler _scheduler;
    private final AtomicReference<Scheduler.Task> _timeout = new AtomicReference();
    private volatile long _idleTimeout;
    private volatile long _idleNanoTime = NanoTime.now();

    public IdleTimeout(Scheduler scheduler) {
        this._scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return this._scheduler;
    }

    public long getIdleFor() {
        return NanoTime.millisSince((long)this._idleNanoTime);
    }

    public long getIdleTimeout() {
        return this._idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        long old = this._idleTimeout;
        this._idleTimeout = idleTimeout;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting idle timeout {} -> {} on {}", new Object[]{old, idleTimeout, this});
        }
        if (old > 0L) {
            if (old <= idleTimeout) {
                return;
            }
            this.deactivate();
        }
        if (this.isOpen()) {
            this.activate();
        }
    }

    public void notIdle() {
        this._idleNanoTime = NanoTime.now();
    }

    private void idleCheck() {
        long idleLeft = this.checkIdleTimeout();
        if (idleLeft >= 0L) {
            this.scheduleIdleTimeout(idleLeft > 0L ? idleLeft : this.getIdleTimeout());
        }
    }

    private void scheduleIdleTimeout(long delay) {
        Scheduler.Task oldTimeout;
        Scheduler.Task newTimeout = null;
        if (this.isOpen() && delay > 0L && this._scheduler != null) {
            newTimeout = this._scheduler.schedule(this::idleCheck, delay, TimeUnit.MILLISECONDS);
        }
        if ((oldTimeout = (Scheduler.Task)this._timeout.getAndSet(newTimeout)) != null) {
            oldTimeout.cancel();
        }
    }

    public void onOpen() {
        this.activate();
    }

    private void activate() {
        if (this._idleTimeout > 0L) {
            this.idleCheck();
        }
    }

    public void onClose() {
        this.deactivate();
    }

    private void deactivate() {
        Scheduler.Task oldTimeout = this._timeout.getAndSet(null);
        if (oldTimeout != null) {
            oldTimeout.cancel();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long checkIdleTimeout() {
        if (this.isOpen()) {
            long idleNanoTime = this._idleNanoTime;
            long idleElapsed = NanoTime.millisSince((long)idleNanoTime);
            long idleTimeout = this.getIdleTimeout();
            long idleLeft = idleTimeout - idleElapsed;
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} idle timeout check, elapsed: {} ms, remaining: {} ms", new Object[]{this, idleElapsed, idleLeft});
            }
            if (idleTimeout > 0L && idleLeft <= 0L) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} idle timeout expired", (Object)this);
                }
                try {
                    this.onIdleExpired(new TimeoutException("Idle timeout expired: " + idleElapsed + "/" + idleTimeout + " ms"));
                }
                finally {
                    this.notIdle();
                }
            }
            return idleLeft >= 0L ? idleLeft : 0L;
        }
        return -1L;
    }

    protected abstract void onIdleExpired(TimeoutException var1);

    public abstract boolean isOpen();
}

