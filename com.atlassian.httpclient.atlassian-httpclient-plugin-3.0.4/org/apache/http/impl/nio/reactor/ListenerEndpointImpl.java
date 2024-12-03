/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.impl.nio.reactor.ListenerEndpointClosedCallback;
import org.apache.http.nio.reactor.ListenerEndpoint;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class ListenerEndpointImpl
implements ListenerEndpoint {
    private volatile boolean completed;
    private volatile boolean closed;
    private volatile SelectionKey key;
    private volatile SocketAddress address;
    private volatile IOException exception;
    private final ListenerEndpointClosedCallback callback;

    public ListenerEndpointImpl(SocketAddress address, ListenerEndpointClosedCallback callback) {
        Args.notNull(address, "Address");
        this.address = address;
        this.callback = callback;
    }

    @Override
    public SocketAddress getAddress() {
        return this.address;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public IOException getException() {
        return this.exception;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void waitFor() throws InterruptedException {
        if (this.completed) {
            return;
        }
        ListenerEndpointImpl listenerEndpointImpl = this;
        synchronized (listenerEndpointImpl) {
            while (!this.completed) {
                this.wait();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void completed(SocketAddress address) {
        Args.notNull(address, "Address");
        if (this.completed) {
            return;
        }
        this.completed = true;
        ListenerEndpointImpl listenerEndpointImpl = this;
        synchronized (listenerEndpointImpl) {
            this.address = address;
            this.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void failed(IOException exception) {
        if (exception == null) {
            return;
        }
        if (this.completed) {
            return;
        }
        this.completed = true;
        ListenerEndpointImpl listenerEndpointImpl = this;
        synchronized (listenerEndpointImpl) {
            this.exception = exception;
            this.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancel() {
        if (this.completed) {
            return;
        }
        this.completed = true;
        this.closed = true;
        ListenerEndpointImpl listenerEndpointImpl = this;
        synchronized (listenerEndpointImpl) {
            this.notifyAll();
        }
    }

    protected void setKey(SelectionKey key) {
        this.key = key;
    }

    @Override
    public boolean isClosed() {
        return this.closed || this.key != null && !this.key.isValid();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.completed = true;
        this.closed = true;
        if (this.key != null) {
            this.key.cancel();
            SelectableChannel channel = this.key.channel();
            try {
                channel.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (this.callback != null) {
            this.callback.endpointClosed(this);
        }
        ListenerEndpointImpl listenerEndpointImpl = this;
        synchronized (listenerEndpointImpl) {
            this.notifyAll();
        }
    }

    public String toString() {
        return "[address=" + this.address + ", key=" + this.key + ", closed=" + this.closed + ", completed=" + this.completed + ", exception=" + this.exception + ", callback=" + this.callback + "]";
    }
}

