/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.IOEventHandler
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.IOSession$Status
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
 *  org.slf4j.Logger
 */
package org.apache.hc.client5.http.impl.async;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.concurrent.locks.Lock;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;

class LoggingIOSession
implements IOSession {
    private final Logger log;
    private final Logger wireLog;
    private final IOSession session;

    public LoggingIOSession(IOSession session, Logger log, Logger wireLog) {
        this.session = session;
        this.log = log;
        this.wireLog = wireLog;
    }

    public String getId() {
        return this.session.getId();
    }

    public Lock getLock() {
        return this.session.getLock();
    }

    public boolean hasCommands() {
        return this.session.hasCommands();
    }

    public Command poll() {
        return this.session.poll();
    }

    public void enqueue(Command command, Command.Priority priority) {
        this.session.enqueue(command, priority);
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Enqueued {} with priority {}", new Object[]{this.session, command.getClass().getSimpleName(), priority});
        }
    }

    public ByteChannel channel() {
        return this.session.channel();
    }

    public SocketAddress getLocalAddress() {
        return this.session.getLocalAddress();
    }

    public SocketAddress getRemoteAddress() {
        return this.session.getRemoteAddress();
    }

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

    public void setEventMask(int ops) {
        this.session.setEventMask(ops);
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Event mask set {}", (Object)this.session, (Object)LoggingIOSession.formatOps(ops));
        }
    }

    public void setEvent(int op) {
        this.session.setEvent(op);
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Event set {}", (Object)this.session, (Object)LoggingIOSession.formatOps(op));
        }
    }

    public void clearEvent(int op) {
        this.session.clearEvent(op);
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Event cleared {}", (Object)this.session, (Object)LoggingIOSession.formatOps(op));
        }
    }

    public boolean isOpen() {
        return this.session.isOpen();
    }

    public void close() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Close", (Object)this.session);
        }
        this.session.close();
    }

    public IOSession.Status getStatus() {
        return this.session.getStatus();
    }

    public void close(CloseMode closeMode) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Close {}", (Object)this.session, (Object)closeMode);
        }
        this.session.close(closeMode);
    }

    public Timeout getSocketTimeout() {
        return this.session.getSocketTimeout();
    }

    public void setSocketTimeout(Timeout timeout) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Set timeout {}", (Object)this.session, (Object)timeout);
        }
        this.session.setSocketTimeout(timeout);
    }

    public long getLastReadTime() {
        return this.session.getLastReadTime();
    }

    public long getLastWriteTime() {
        return this.session.getLastWriteTime();
    }

    public void updateReadTime() {
        this.session.updateReadTime();
    }

    public void updateWriteTime() {
        this.session.updateWriteTime();
    }

    public long getLastEventTime() {
        return this.session.getLastEventTime();
    }

    public IOEventHandler getHandler() {
        return this.session.getHandler();
    }

    public void upgrade(final IOEventHandler handler) {
        Args.notNull((Object)handler, (String)"Protocol handler");
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} protocol upgrade {}", (Object)this.session, handler.getClass());
        }
        this.session.upgrade(new IOEventHandler(){

            public void connected(IOSession protocolSession) throws IOException {
                handler.connected(protocolSession);
            }

            public void inputReady(IOSession protocolSession, ByteBuffer src) throws IOException {
                if (src != null && LoggingIOSession.this.wireLog.isDebugEnabled()) {
                    ByteBuffer b = src.duplicate();
                    LoggingIOSession.this.logData(b, "<< ");
                }
                handler.inputReady(protocolSession, src);
            }

            public void outputReady(IOSession protocolSession) throws IOException {
                handler.outputReady(protocolSession);
            }

            public void timeout(IOSession protocolSession, Timeout timeout) throws IOException {
                handler.timeout(protocolSession, timeout);
            }

            public void exception(IOSession protocolSession, Exception cause) {
                handler.exception(protocolSession, cause);
            }

            public void disconnected(IOSession protocolSession) {
                handler.disconnected(protocolSession);
            }
        });
    }

    private void logData(ByteBuffer data, String prefix) throws IOException {
        byte[] line = new byte[16];
        StringBuilder buf = new StringBuilder();
        while (data.hasRemaining()) {
            int i;
            buf.setLength(0);
            buf.append(this.session).append(" ").append(prefix);
            int chunk = Math.min(data.remaining(), line.length);
            data.get(line, 0, chunk);
            for (i = 0; i < chunk; ++i) {
                char ch = (char)line[i];
                if (ch > ' ' && ch <= '\u007f') {
                    buf.append(ch);
                    continue;
                }
                if (Character.isWhitespace(ch)) {
                    buf.append(' ');
                    continue;
                }
                buf.append('.');
            }
            for (i = chunk; i < 17; ++i) {
                buf.append(' ');
            }
            for (i = 0; i < chunk; ++i) {
                buf.append(' ');
                int b = line[i] & 0xFF;
                String s = Integer.toHexString(b);
                if (s.length() == 1) {
                    buf.append("0");
                }
                buf.append(s);
            }
            this.wireLog.debug(buf.toString());
        }
    }

    public int read(ByteBuffer dst) throws IOException {
        int bytesRead = this.session.read(dst);
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} {} bytes read", (Object)this.session, (Object)bytesRead);
        }
        if (bytesRead > 0 && this.wireLog.isDebugEnabled()) {
            ByteBuffer b = dst.duplicate();
            int p = b.position();
            b.limit(p);
            b.position(p - bytesRead);
            this.logData(b, "<< ");
        }
        return bytesRead;
    }

    public int write(ByteBuffer src) throws IOException {
        int byteWritten = this.session.write(src);
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} {} bytes written", (Object)this.session, (Object)byteWritten);
        }
        if (byteWritten > 0 && this.wireLog.isDebugEnabled()) {
            ByteBuffer b = src.duplicate();
            int p = b.position();
            b.limit(p);
            b.position(p - byteWritten);
            this.logData(b, ">> ");
        }
        return byteWritten;
    }

    public String toString() {
        return this.session.toString();
    }
}

