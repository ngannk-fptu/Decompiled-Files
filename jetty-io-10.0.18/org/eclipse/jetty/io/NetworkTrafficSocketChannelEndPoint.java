/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.NetworkTrafficListener;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkTrafficSocketChannelEndPoint
extends SocketChannelEndPoint {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkTrafficSocketChannelEndPoint.class);
    private final NetworkTrafficListener listener;

    public NetworkTrafficSocketChannelEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key, Scheduler scheduler, long idleTimeout, NetworkTrafficListener listener) {
        super(channel, selectSet, key, scheduler);
        this.setIdleTimeout(idleTimeout);
        this.listener = listener;
    }

    @Override
    public int fill(ByteBuffer buffer) throws IOException {
        int read = super.fill(buffer);
        this.notifyIncoming(buffer, read);
        return read;
    }

    @Override
    public boolean flush(ByteBuffer ... buffers) throws IOException {
        boolean flushed = true;
        for (ByteBuffer b : buffers) {
            if (!b.hasRemaining()) continue;
            int position = b.position();
            ByteBuffer view = b.slice();
            flushed = super.flush(b);
            int l = b.position() - position;
            view.limit(view.position() + l);
            this.notifyOutgoing(view);
            if (!flushed) break;
        }
        return flushed;
    }

    @Override
    public void onOpen() {
        super.onOpen();
        if (this.listener != null) {
            try {
                this.listener.opened(this.getChannel().socket());
            }
            catch (Throwable x) {
                LOG.info("Exception while invoking listener {}", (Object)this.listener, (Object)x);
            }
        }
    }

    @Override
    public void onClose(Throwable failure) {
        super.onClose(failure);
        if (this.listener != null) {
            try {
                this.listener.closed(this.getChannel().socket());
            }
            catch (Throwable x) {
                LOG.info("Exception while invoking listener {}", (Object)this.listener, (Object)x);
            }
        }
    }

    public void notifyIncoming(ByteBuffer buffer, int read) {
        if (this.listener != null && read > 0) {
            try {
                ByteBuffer view = buffer.asReadOnlyBuffer();
                this.listener.incoming(this.getChannel().socket(), view);
            }
            catch (Throwable x) {
                LOG.info("Exception while invoking listener {}", (Object)this.listener, (Object)x);
            }
        }
    }

    public void notifyOutgoing(ByteBuffer view) {
        if (this.listener != null && view.hasRemaining()) {
            try {
                this.listener.outgoing(this.getChannel().socket(), view);
            }
            catch (Throwable x) {
                LOG.info("Exception while invoking listener {}", (Object)this.listener, (Object)x);
            }
        }
    }
}

