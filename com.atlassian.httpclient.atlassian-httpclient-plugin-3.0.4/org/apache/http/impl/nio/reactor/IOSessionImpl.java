/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.impl.nio.reactor.InterestOpEntry;
import org.apache.http.impl.nio.reactor.InterestOpsCallback;
import org.apache.http.impl.nio.reactor.SessionClosedCallback;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionBufferStatus;
import org.apache.http.nio.reactor.SocketAccessor;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public class IOSessionImpl
implements IOSession,
SocketAccessor {
    private final SelectionKey key;
    private final ByteChannel channel;
    private final Map<String, Object> attributes;
    private final InterestOpsCallback interestOpsCallback;
    private final SessionClosedCallback sessionClosedCallback;
    private volatile int status;
    private volatile int currentEventMask;
    private volatile SessionBufferStatus bufferStatus;
    private volatile int socketTimeout;
    private final long startedTime;
    private volatile long lastReadTime;
    private volatile long lastWriteTime;
    private volatile long lastAccessTime;

    public IOSessionImpl(SelectionKey key, InterestOpsCallback interestOpsCallback, SessionClosedCallback sessionClosedCallback) {
        long now;
        Args.notNull(key, "Selection key");
        this.key = key;
        this.channel = (ByteChannel)((Object)this.key.channel());
        this.interestOpsCallback = interestOpsCallback;
        this.sessionClosedCallback = sessionClosedCallback;
        this.attributes = Collections.synchronizedMap(new HashMap());
        this.currentEventMask = key.interestOps();
        this.socketTimeout = 0;
        this.status = 0;
        this.startedTime = now = System.currentTimeMillis();
        this.lastReadTime = now;
        this.lastWriteTime = now;
        this.lastAccessTime = now;
    }

    public IOSessionImpl(SelectionKey key, SessionClosedCallback sessionClosedCallback) {
        this(key, null, sessionClosedCallback);
    }

    @Override
    public ByteChannel channel() {
        return this.channel;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.channel instanceof SocketChannel ? ((SocketChannel)this.channel).socket().getLocalSocketAddress() : null;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.channel instanceof SocketChannel ? ((SocketChannel)this.channel).socket().getRemoteSocketAddress() : null;
    }

    @Override
    public int getEventMask() {
        return this.interestOpsCallback != null ? this.currentEventMask : this.key.interestOps();
    }

    @Override
    public synchronized void setEventMask(int ops) {
        if (this.status == Integer.MAX_VALUE) {
            return;
        }
        if (this.interestOpsCallback != null) {
            this.currentEventMask = ops;
            InterestOpEntry entry = new InterestOpEntry(this.key, this.currentEventMask);
            this.interestOpsCallback.addInterestOps(entry);
        } else {
            this.key.interestOps(ops);
        }
        this.key.selector().wakeup();
    }

    @Override
    public synchronized void setEvent(int op) {
        if (this.status == Integer.MAX_VALUE) {
            return;
        }
        if (this.interestOpsCallback != null) {
            this.currentEventMask |= op;
            InterestOpEntry entry = new InterestOpEntry(this.key, this.currentEventMask);
            this.interestOpsCallback.addInterestOps(entry);
        } else {
            int ops = this.key.interestOps();
            this.key.interestOps(ops | op);
        }
        this.key.selector().wakeup();
    }

    @Override
    public synchronized void clearEvent(int op) {
        if (this.status == Integer.MAX_VALUE) {
            return;
        }
        if (this.interestOpsCallback != null) {
            this.currentEventMask &= ~op;
            InterestOpEntry entry = new InterestOpEntry(this.key, this.currentEventMask);
            this.interestOpsCallback.addInterestOps(entry);
        } else {
            int ops = this.key.interestOps();
            this.key.interestOps(ops & ~op);
        }
        this.key.selector().wakeup();
    }

    @Override
    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    @Override
    public void setSocketTimeout(int timeout) {
        this.socketTimeout = timeout;
        this.lastAccessTime = System.currentTimeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        IOSessionImpl iOSessionImpl = this;
        synchronized (iOSessionImpl) {
            if (this.status == Integer.MAX_VALUE) {
                return;
            }
            this.status = Integer.MAX_VALUE;
            this.key.cancel();
            try {
                this.key.channel().close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (this.sessionClosedCallback != null) {
                this.sessionClosedCallback.sessionClosed(this);
            }
            if (this.key.selector().isOpen()) {
                this.key.selector().wakeup();
            }
        }
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public boolean isClosed() {
        return this.status == Integer.MAX_VALUE;
    }

    @Override
    public void shutdown() {
        this.close();
    }

    @Override
    public boolean hasBufferedInput() {
        SessionBufferStatus buffStatus = this.bufferStatus;
        return buffStatus != null && buffStatus.hasBufferedInput();
    }

    @Override
    public boolean hasBufferedOutput() {
        SessionBufferStatus buffStatus = this.bufferStatus;
        return buffStatus != null && buffStatus.hasBufferedOutput();
    }

    @Override
    public void setBufferStatus(SessionBufferStatus bufferStatus) {
        this.bufferStatus = bufferStatus;
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public Object removeAttribute(String name) {
        return this.attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object obj) {
        this.attributes.put(name, obj);
    }

    public long getStartedTime() {
        return this.startedTime;
    }

    public long getLastReadTime() {
        return this.lastReadTime;
    }

    public long getLastWriteTime() {
        return this.lastWriteTime;
    }

    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    void resetLastRead() {
        long now;
        this.lastReadTime = now = System.currentTimeMillis();
        this.lastAccessTime = now;
    }

    void resetLastWrite() {
        long now;
        this.lastWriteTime = now = System.currentTimeMillis();
        this.lastAccessTime = now;
    }

    private static void formatOps(StringBuilder buffer, int ops) {
        if ((ops & 1) > 0) {
            buffer.append('r');
        }
        if ((ops & 4) > 0) {
            buffer.append('w');
        }
        if ((ops & 0x10) > 0) {
            buffer.append('a');
        }
        if ((ops & 8) > 0) {
            buffer.append('c');
        }
    }

    private static void formatAddress(StringBuilder buffer, SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress)socketAddress;
            buffer.append(addr.getAddress() != null ? addr.getAddress().getHostAddress() : addr.getAddress()).append(':').append(addr.getPort());
        } else {
            buffer.append(socketAddress);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        SelectionKey selectionKey = this.key;
        synchronized (selectionKey) {
            SocketAddress remoteAddress = this.getRemoteAddress();
            SocketAddress localAddress = this.getLocalAddress();
            if (remoteAddress != null && localAddress != null) {
                IOSessionImpl.formatAddress(buffer, localAddress);
                buffer.append("<->");
                IOSessionImpl.formatAddress(buffer, remoteAddress);
            }
            buffer.append('[');
            switch (this.status) {
                case 0: {
                    buffer.append("ACTIVE");
                    break;
                }
                case 1: {
                    buffer.append("CLOSING");
                    break;
                }
                case 0x7FFFFFFF: {
                    buffer.append("CLOSED");
                }
            }
            buffer.append("][");
            if (this.key.isValid()) {
                IOSessionImpl.formatOps(buffer, this.interestOpsCallback != null ? this.currentEventMask : this.key.interestOps());
                buffer.append(':');
                IOSessionImpl.formatOps(buffer, this.key.readyOps());
            }
        }
        buffer.append(']');
        return new String(buffer);
    }

    @Override
    public Socket getSocket() {
        return this.channel instanceof SocketChannel ? ((SocketChannel)this.channel).socket() : null;
    }
}

