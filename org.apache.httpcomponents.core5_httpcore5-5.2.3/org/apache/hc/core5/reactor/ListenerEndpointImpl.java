/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.Closer;
import org.apache.hc.core5.reactor.ListenerEndpoint;

class ListenerEndpointImpl
implements ListenerEndpoint {
    private final SelectionKey key;
    final SocketAddress address;
    final Object attachment;
    private final AtomicBoolean closed;

    public ListenerEndpointImpl(SelectionKey key, Object attachment, SocketAddress address) {
        this.key = key;
        this.address = address;
        this.attachment = attachment;
        this.closed = new AtomicBoolean(false);
    }

    @Override
    public SocketAddress getAddress() {
        return this.address;
    }

    public String toString() {
        return "endpoint: " + this.address;
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }

    @Override
    public void close() throws IOException {
        if (this.closed.compareAndSet(false, true)) {
            this.key.cancel();
            this.key.channel().close();
        }
    }

    @Override
    public void close(CloseMode closeMode) {
        Closer.closeQuietly(this);
    }
}

