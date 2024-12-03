/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import org.apache.commons.logging.Log;
import org.apache.http.impl.nio.conn.Wire;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionBufferStatus;

class LoggingIOSession
implements IOSession {
    private final IOSession session;
    private final ByteChannel channel;
    private final String id;
    private final Log log;
    private final Wire wireLog;

    public LoggingIOSession(IOSession session, String id, Log log, Log wireLog) {
        this.session = session;
        this.channel = new LoggingByteChannel();
        this.id = id;
        this.log = log;
        this.wireLog = new Wire(wireLog, this.id);
    }

    @Override
    public ByteChannel channel() {
        return this.channel;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.session.getLocalAddress();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.session.getRemoteAddress();
    }

    @Override
    public int getEventMask() {
        return this.session.getEventMask();
    }

    private static String formatOps(int ops) {
        StringBuilder buffer = new StringBuilder(6);
        buffer.append('[');
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
        buffer.append(']');
        return buffer.toString();
    }

    @Override
    public void setEventMask(int ops) {
        this.session.setEventMask(ops);
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Event mask set " + LoggingIOSession.formatOps(ops));
        }
    }

    @Override
    public void setEvent(int op) {
        this.session.setEvent(op);
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Event set " + LoggingIOSession.formatOps(op));
        }
    }

    @Override
    public void clearEvent(int op) {
        this.session.clearEvent(op);
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Event cleared " + LoggingIOSession.formatOps(op));
        }
    }

    @Override
    public void close() {
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Close");
        }
        this.session.close();
    }

    @Override
    public int getStatus() {
        return this.session.getStatus();
    }

    @Override
    public boolean isClosed() {
        return this.session.isClosed();
    }

    @Override
    public void shutdown() {
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Shutdown");
        }
        this.session.shutdown();
    }

    @Override
    public int getSocketTimeout() {
        return this.session.getSocketTimeout();
    }

    @Override
    public void setSocketTimeout(int timeout) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Set timeout " + timeout);
        }
        this.session.setSocketTimeout(timeout);
    }

    @Override
    public void setBufferStatus(SessionBufferStatus status) {
        this.session.setBufferStatus(status);
    }

    @Override
    public boolean hasBufferedInput() {
        return this.session.hasBufferedInput();
    }

    @Override
    public boolean hasBufferedOutput() {
        return this.session.hasBufferedOutput();
    }

    @Override
    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object obj) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Set attribute " + name);
        }
        this.session.setAttribute(name, obj);
    }

    @Override
    public Object removeAttribute(String name) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(this.id + " " + this.session + ": Remove attribute " + name);
        }
        return this.session.removeAttribute(name);
    }

    public String toString() {
        return this.id + " " + this.session.toString();
    }

    class LoggingByteChannel
    implements ByteChannel {
        LoggingByteChannel() {
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            int bytesRead = LoggingIOSession.this.session.channel().read(dst);
            if (LoggingIOSession.this.log.isDebugEnabled()) {
                LoggingIOSession.this.log.debug(LoggingIOSession.this.id + " " + LoggingIOSession.this.session + ": " + bytesRead + " bytes read");
            }
            if (bytesRead > 0 && LoggingIOSession.this.wireLog.isEnabled()) {
                ByteBuffer b = dst.duplicate();
                int p = b.position();
                b.limit(p);
                b.position(p - bytesRead);
                LoggingIOSession.this.wireLog.input(b);
            }
            return bytesRead;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            int byteWritten = LoggingIOSession.this.session.channel().write(src);
            if (LoggingIOSession.this.log.isDebugEnabled()) {
                LoggingIOSession.this.log.debug(LoggingIOSession.this.id + " " + LoggingIOSession.this.session + ": " + byteWritten + " bytes written");
            }
            if (byteWritten > 0 && LoggingIOSession.this.wireLog.isEnabled()) {
                ByteBuffer b = src.duplicate();
                int p = b.position();
                b.limit(p);
                b.position(p - byteWritten);
                LoggingIOSession.this.wireLog.output(b);
            }
            return byteWritten;
        }

        @Override
        public void close() throws IOException {
            if (LoggingIOSession.this.log.isDebugEnabled()) {
                LoggingIOSession.this.log.debug(LoggingIOSession.this.id + " " + LoggingIOSession.this.session + ": Channel close");
            }
            LoggingIOSession.this.session.channel().close();
        }

        @Override
        public boolean isOpen() {
            return LoggingIOSession.this.session.channel().isOpen();
        }
    }
}

