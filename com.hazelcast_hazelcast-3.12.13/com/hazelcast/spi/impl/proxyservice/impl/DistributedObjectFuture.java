/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.util.ExceptionUtil;

public class DistributedObjectFuture {
    private volatile DistributedObject proxy;
    private volatile Throwable error;
    private volatile DistributedObject rawProxy;

    boolean isSetAndInitialized() {
        return this.proxy != null || this.error != null;
    }

    public DistributedObject get() {
        if (this.proxy != null) {
            return this.proxy;
        }
        if (this.error != null) {
            throw ExceptionUtil.rethrow(this.error);
        }
        boolean interrupted = this.waitUntilSetAndInitialized();
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        if (this.proxy != null) {
            return this.proxy;
        }
        throw ExceptionUtil.rethrow(this.error);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean waitUntilSetAndInitialized() {
        boolean interrupted = false;
        DistributedObjectFuture distributedObjectFuture = this;
        synchronized (distributedObjectFuture) {
            while (this.proxy == null && this.error == null) {
                if (this.rawProxy != null) {
                    this.initialize();
                    break;
                }
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
        return interrupted;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initialize() {
        DistributedObjectFuture distributedObjectFuture = this;
        synchronized (distributedObjectFuture) {
            try {
                InitializingObject o = (InitializingObject)((Object)this.rawProxy);
                o.initialize();
                this.proxy = this.rawProxy;
            }
            catch (Throwable e) {
                this.error = e;
            }
            this.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void set(DistributedObject o, boolean initialized) {
        if (o == null) {
            throw new IllegalArgumentException("Proxy should not be null!");
        }
        DistributedObjectFuture distributedObjectFuture = this;
        synchronized (distributedObjectFuture) {
            if (!initialized && o instanceof InitializingObject) {
                this.rawProxy = o;
            } else {
                this.proxy = o;
            }
            this.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setError(Throwable t) {
        if (t == null) {
            throw new IllegalArgumentException("Error should not be null!");
        }
        if (this.proxy != null) {
            throw new IllegalStateException("Proxy is already set! Proxy: " + this.proxy + ", error: " + t);
        }
        DistributedObjectFuture distributedObjectFuture = this;
        synchronized (distributedObjectFuture) {
            this.error = t;
            this.notifyAll();
        }
    }
}

