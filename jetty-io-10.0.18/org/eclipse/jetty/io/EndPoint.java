/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.Callback
 */
package org.eclipse.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.util.Callback;

public interface EndPoint
extends Closeable {
    @Deprecated
    public InetSocketAddress getLocalAddress();

    default public SocketAddress getLocalSocketAddress() {
        return this.getLocalAddress();
    }

    @Deprecated
    public InetSocketAddress getRemoteAddress();

    default public SocketAddress getRemoteSocketAddress() {
        return this.getRemoteAddress();
    }

    public boolean isOpen();

    public long getCreatedTimeStamp();

    public void shutdownOutput();

    public boolean isOutputShutdown();

    public boolean isInputShutdown();

    @Override
    default public void close() {
        this.close(null);
    }

    public void close(Throwable var1);

    default public int fill(ByteBuffer buffer) throws IOException {
        throw new UnsupportedOperationException();
    }

    default public boolean flush(ByteBuffer ... buffer) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Object getTransport();

    public long getIdleTimeout();

    public void setIdleTimeout(long var1);

    public void fillInterested(Callback var1) throws ReadPendingException;

    public boolean tryFillInterested(Callback var1);

    public boolean isFillInterested();

    default public void write(Callback callback, ByteBuffer ... buffers) throws WritePendingException {
        throw new UnsupportedOperationException();
    }

    public Connection getConnection();

    public void setConnection(Connection var1);

    public void onOpen();

    public void onClose(Throwable var1);

    public void upgrade(Connection var1);

    public static interface Wrapper {
        public EndPoint unwrap();
    }
}

