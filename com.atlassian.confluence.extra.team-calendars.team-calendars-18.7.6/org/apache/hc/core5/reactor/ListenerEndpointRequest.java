/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import java.io.Closeable;
import java.net.SocketAddress;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.reactor.ListenerEndpoint;

final class ListenerEndpointRequest
implements Closeable {
    final SocketAddress address;
    final Object attachment;
    final BasicFuture<ListenerEndpoint> future;

    ListenerEndpointRequest(SocketAddress address, Object attachment, BasicFuture<ListenerEndpoint> future) {
        this.address = address;
        this.attachment = attachment;
        this.future = future;
    }

    public void completed(ListenerEndpoint endpoint) {
        if (this.future != null) {
            this.future.completed(endpoint);
        }
    }

    public void failed(Exception cause) {
        if (this.future != null) {
            this.future.failed(cause);
        }
    }

    public void cancel() {
        if (this.future != null) {
            this.future.cancel();
        }
    }

    public boolean isCancelled() {
        return this.future != null && this.future.isCancelled();
    }

    @Override
    public void close() {
        this.cancel();
    }
}

