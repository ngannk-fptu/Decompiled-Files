/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionRequest;
import org.apache.http.nio.reactor.SessionRequestCallback;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class SessionRequestImpl
implements SessionRequest {
    private final SocketAddress remoteAddress;
    private final SocketAddress localAddress;
    private final Object attachment;
    private final SessionRequestCallback callback;
    private final AtomicReference<SessionRequestState> state;
    private volatile SelectionKey key;
    private volatile int connectTimeout;
    private volatile IOSession session = null;
    private volatile IOException exception = null;

    public SessionRequestImpl(SocketAddress remoteAddress, SocketAddress localAddress, Object attachment, SessionRequestCallback callback) {
        Args.notNull(remoteAddress, "Remote address");
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        this.attachment = attachment;
        this.callback = callback;
        this.state = new AtomicReference<SessionRequestState>(SessionRequestState.ACTIVE);
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    public Object getAttachment() {
        return this.attachment;
    }

    @Override
    public boolean isCompleted() {
        return this.state.get().compareTo(SessionRequestState.ACTIVE) != 0;
    }

    boolean isTerminated() {
        return this.state.get().compareTo(SessionRequestState.SUCCESSFUL) > 0;
    }

    protected void setKey(SelectionKey key) {
        this.key = key;
        if (this.isCompleted()) {
            key.cancel();
            SelectableChannel channel = key.channel();
            if (channel.isOpen()) {
                try {
                    channel.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void waitFor() throws InterruptedException {
        if (this.isCompleted()) {
            return;
        }
        SessionRequestImpl sessionRequestImpl = this;
        synchronized (sessionRequestImpl) {
            while (!this.isCompleted()) {
                this.wait();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IOSession getSession() {
        SessionRequestImpl sessionRequestImpl = this;
        synchronized (sessionRequestImpl) {
            return this.session;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IOException getException() {
        SessionRequestImpl sessionRequestImpl = this;
        synchronized (sessionRequestImpl) {
            return this.exception;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void completed(IOSession session) {
        Args.notNull(session, "Session");
        if (this.state.compareAndSet(SessionRequestState.ACTIVE, SessionRequestState.SUCCESSFUL)) {
            SessionRequestImpl sessionRequestImpl = this;
            synchronized (sessionRequestImpl) {
                this.session = session;
                if (this.callback != null) {
                    this.callback.completed(this);
                }
                this.notifyAll();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void failed(IOException exception) {
        if (exception == null) {
            return;
        }
        if (this.state.compareAndSet(SessionRequestState.ACTIVE, SessionRequestState.FAILED)) {
            SelectionKey key = this.key;
            if (key != null) {
                key.cancel();
                SelectableChannel channel = key.channel();
                try {
                    channel.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            SessionRequestImpl sessionRequestImpl = this;
            synchronized (sessionRequestImpl) {
                this.exception = exception;
                if (this.callback != null) {
                    this.callback.failed(this);
                }
                this.notifyAll();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void timeout() {
        if (this.state.compareAndSet(SessionRequestState.ACTIVE, SessionRequestState.TIMEDOUT)) {
            SelectionKey key = this.key;
            if (key != null) {
                key.cancel();
                SelectableChannel channel = key.channel();
                if (channel.isOpen()) {
                    try {
                        channel.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
            SessionRequestImpl sessionRequestImpl = this;
            synchronized (sessionRequestImpl) {
                if (this.callback != null) {
                    this.callback.timeout(this);
                }
            }
        }
    }

    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    @Override
    public void setConnectTimeout(int timeout) {
        if (this.connectTimeout != timeout) {
            this.connectTimeout = timeout;
            SelectionKey key = this.key;
            if (key != null) {
                key.selector().wakeup();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cancel() {
        if (this.state.compareAndSet(SessionRequestState.ACTIVE, SessionRequestState.CANCELLED)) {
            SelectionKey key = this.key;
            if (key != null) {
                key.cancel();
                SelectableChannel channel = key.channel();
                if (channel.isOpen()) {
                    try {
                        channel.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
            SessionRequestImpl sessionRequestImpl = this;
            synchronized (sessionRequestImpl) {
                if (this.callback != null) {
                    this.callback.cancelled(this);
                }
                this.notifyAll();
            }
        }
    }

    static enum SessionRequestState {
        ACTIVE,
        SUCCESSFUL,
        TIMEDOUT,
        CANCELLED,
        FAILED;

    }
}

