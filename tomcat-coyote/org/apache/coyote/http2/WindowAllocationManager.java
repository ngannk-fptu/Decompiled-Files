/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.util.concurrent.TimeUnit;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Response;
import org.apache.coyote.http2.Stream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

class WindowAllocationManager {
    private static final Log log = LogFactory.getLog(WindowAllocationManager.class);
    private static final StringManager sm = StringManager.getManager(WindowAllocationManager.class);
    private static final int NONE = 0;
    private static final int STREAM = 1;
    private static final int CONNECTION = 2;
    private final Stream stream;
    private int waitingFor = 0;

    WindowAllocationManager(Stream stream) {
        this.stream = stream;
    }

    void waitForStream(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("windowAllocationManager.waitFor.stream", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString(), Long.toString(timeout)}));
        }
        this.waitFor(1, timeout);
    }

    void waitForConnection(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("windowAllocationManager.waitFor.connection", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.stream.getConnectionAllocationRequested()), Long.toString(timeout)}));
        }
        this.waitFor(2, timeout);
    }

    void waitForStreamNonBlocking() {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("windowAllocationManager.waitForNonBlocking.stream", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}));
        }
        this.waitForNonBlocking(1);
    }

    void waitForConnectionNonBlocking() {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("windowAllocationManager.waitForNonBlocking.connection", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}));
        }
        this.waitForNonBlocking(2);
    }

    void notifyStream() {
        this.notify(1);
    }

    void notifyConnection() {
        this.notify(2);
    }

    void notifyAny() {
        this.notify(3);
    }

    boolean isWaitingForStream() {
        return this.isWaitingFor(1);
    }

    boolean isWaitingForConnection() {
        return this.isWaitingFor(2);
    }

    private boolean isWaitingFor(int waitTarget) {
        this.stream.windowAllocationLock.lock();
        try {
            boolean bl = (this.waitingFor & waitTarget) > 0;
            return bl;
        }
        finally {
            this.stream.windowAllocationLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void waitFor(int waitTarget, long timeout) throws InterruptedException {
        this.stream.windowAllocationLock.lock();
        try {
            if (this.waitingFor != 0) {
                throw new IllegalStateException(sm.getString("windowAllocationManager.waitFor.ise", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}));
            }
            this.waitingFor = waitTarget;
            long startNanos = -1L;
            do {
                long timeoutRemaining;
                if (timeout < 0L) {
                    this.stream.windowAllocationAvailable.await();
                    continue;
                }
                if (startNanos == -1L) {
                    startNanos = System.nanoTime();
                    timeoutRemaining = timeout;
                } else {
                    long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
                    if (elapsedMillis == 0L) {
                        elapsedMillis = 1L;
                    }
                    if ((timeoutRemaining = timeout - elapsedMillis) <= 0L) {
                        return;
                    }
                }
                this.stream.windowAllocationAvailable.await(timeoutRemaining, TimeUnit.MILLISECONDS);
            } while (this.waitingFor != 0);
        }
        finally {
            this.stream.windowAllocationLock.unlock();
        }
    }

    private void waitForNonBlocking(int waitTarget) {
        block5: {
            this.stream.windowAllocationLock.lock();
            try {
                if (this.waitingFor == 0) {
                    this.waitingFor = waitTarget;
                    break block5;
                }
                if (this.waitingFor == waitTarget) {
                    break block5;
                }
                throw new IllegalStateException(sm.getString("windowAllocationManager.waitFor.ise", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}));
            }
            finally {
                this.stream.windowAllocationLock.unlock();
            }
        }
    }

    private void notify(int notifyTarget) {
        this.stream.windowAllocationLock.lock();
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("windowAllocationManager.notify", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString(), Integer.toString(this.waitingFor), Integer.toString(notifyTarget)}));
            }
            if ((notifyTarget & this.waitingFor) > 0) {
                this.waitingFor = 0;
                Response response = this.stream.getCoyoteResponse();
                if (response != null) {
                    if (response.getWriteListener() == null) {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)sm.getString("windowAllocationManager.notified", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}));
                        }
                        this.stream.windowAllocationAvailable.signal();
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug((Object)sm.getString("windowAllocationManager.dispatched", new Object[]{this.stream.getConnectionId(), this.stream.getIdAsString()}));
                        }
                        response.action(ActionCode.DISPATCH_WRITE, null);
                        response.action(ActionCode.DISPATCH_EXECUTE, null);
                    }
                }
            }
        }
        finally {
            this.stream.windowAllocationLock.unlock();
        }
    }
}

