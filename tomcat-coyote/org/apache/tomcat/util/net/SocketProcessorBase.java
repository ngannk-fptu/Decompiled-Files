/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

public abstract class SocketProcessorBase<S>
implements Runnable {
    protected SocketWrapperBase<S> socketWrapper;
    protected SocketEvent event;

    public SocketProcessorBase(SocketWrapperBase<S> socketWrapper, SocketEvent event) {
        this.reset(socketWrapper, event);
    }

    public void reset(SocketWrapperBase<S> socketWrapper, SocketEvent event) {
        Objects.requireNonNull(event);
        this.socketWrapper = socketWrapper;
        this.event = event;
    }

    @Override
    public final void run() {
        Lock lock = this.socketWrapper.getLock();
        lock.lock();
        try {
            if (this.socketWrapper.isClosed()) {
                return;
            }
            this.doRun();
        }
        finally {
            lock.unlock();
        }
    }

    protected abstract void doRun();
}

