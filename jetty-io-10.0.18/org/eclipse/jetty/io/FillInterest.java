/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.Invocable
 *  org.eclipse.jetty.util.thread.Invocable$InvocationType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadPendingException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FillInterest {
    private static final Logger LOG = LoggerFactory.getLogger(FillInterest.class);
    private final AtomicReference<Callback> _interested = new AtomicReference<Object>(null);

    protected FillInterest() {
    }

    public void register(Callback callback) throws ReadPendingException {
        if (!this.tryRegister(callback)) {
            LOG.warn("Read pending for {} prevented {}", this._interested, (Object)callback);
            throw new ReadPendingException();
        }
    }

    public boolean tryRegister(Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException();
        }
        if (!this._interested.compareAndSet(null, callback)) {
            return false;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("interested {}", (Object)this);
        }
        try {
            this.needsFillInterest();
        }
        catch (Throwable e) {
            this.onFail(e);
        }
        return true;
    }

    public boolean fillable() {
        Callback callback;
        if (LOG.isDebugEnabled()) {
            LOG.debug("fillable {}", (Object)this);
        }
        if ((callback = this._interested.get()) != null && this._interested.compareAndSet(callback, null)) {
            callback.succeeded();
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} lost race {}", (Object)this, (Object)callback);
        }
        return false;
    }

    public boolean isInterested() {
        return this._interested.get() != null;
    }

    public Invocable.InvocationType getCallbackInvocationType() {
        Callback callback = this._interested.get();
        return Invocable.getInvocationType((Object)callback);
    }

    public boolean onFail(Throwable cause) {
        Callback callback;
        if (LOG.isDebugEnabled()) {
            LOG.debug("onFail {}", (Object)this, (Object)cause);
        }
        if ((callback = this._interested.get()) != null && this._interested.compareAndSet(callback, null)) {
            callback.failed(cause);
            return true;
        }
        return false;
    }

    public void onClose() {
        Callback callback;
        if (LOG.isDebugEnabled()) {
            LOG.debug("onClose {}", (Object)this);
        }
        if ((callback = this._interested.get()) != null && this._interested.compareAndSet(callback, null)) {
            callback.failed((Throwable)new ClosedChannelException());
        }
    }

    public String toString() {
        return String.format("FillInterest@%x{%s}", this.hashCode(), this._interested.get());
    }

    public String toStateString() {
        return this._interested.get() == null ? "-" : "FI";
    }

    protected abstract void needsFillInterest() throws IOException;
}

