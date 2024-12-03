/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.reactor.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionBufferStatus;
import org.apache.http.nio.reactor.SocketAccessor;
import org.apache.http.nio.reactor.ssl.PermanentSSLBufferManagementStrategy;
import org.apache.http.nio.reactor.ssl.SSLBuffer;
import org.apache.http.nio.reactor.ssl.SSLBufferManagementStrategy;
import org.apache.http.nio.reactor.ssl.SSLMode;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class SSLIOSession
implements IOSession,
SessionBufferStatus,
SocketAccessor {
    public static final String SESSION_KEY = "http.session.ssl";
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    private final IOSession session;
    private final SSLEngine sslEngine;
    private final SSLBuffer inEncrypted;
    private final SSLBuffer outEncrypted;
    private final SSLBuffer inPlain;
    private final InternalByteChannel channel;
    private final SSLSetupHandler handler;
    private final AtomicInteger outboundClosedCount;
    private int appEventMask;
    private SessionBufferStatus appBufferStatus;
    private boolean endOfStream;
    private volatile SSLMode sslMode;
    private volatile int status;
    private volatile boolean initialized;
    private volatile boolean terminated;

    public SSLIOSession(IOSession session, SSLMode sslMode, HttpHost host, SSLContext sslContext, SSLSetupHandler handler) {
        this(session, sslMode, host, sslContext, handler, new PermanentSSLBufferManagementStrategy());
    }

    public SSLIOSession(IOSession session, SSLMode sslMode, HttpHost host, SSLContext sslContext, SSLSetupHandler handler, SSLBufferManagementStrategy bufferManagementStrategy) {
        Args.notNull(session, "IO session");
        Args.notNull(sslContext, "SSL context");
        Args.notNull(bufferManagementStrategy, "Buffer management strategy");
        this.session = session;
        this.sslMode = sslMode;
        this.appEventMask = session.getEventMask();
        this.channel = new InternalByteChannel();
        this.handler = handler;
        this.session.setBufferStatus(this);
        this.sslEngine = this.sslMode == SSLMode.CLIENT && host != null ? sslContext.createSSLEngine(host.getHostName(), host.getPort()) : sslContext.createSSLEngine();
        int netBuffersize = this.sslEngine.getSession().getPacketBufferSize();
        this.inEncrypted = bufferManagementStrategy.constructBuffer(netBuffersize);
        this.outEncrypted = bufferManagementStrategy.constructBuffer(netBuffersize);
        int appBuffersize = this.sslEngine.getSession().getApplicationBufferSize();
        this.inPlain = bufferManagementStrategy.constructBuffer(appBuffersize);
        this.outboundClosedCount = new AtomicInteger(0);
    }

    public SSLIOSession(IOSession session, SSLMode sslMode, SSLContext sslContext, SSLSetupHandler handler) {
        this(session, sslMode, null, sslContext, handler);
    }

    protected SSLSetupHandler getSSLSetupHandler() {
        return this.handler;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    @Deprecated
    public synchronized void initialize(SSLMode sslMode) throws SSLException {
        this.sslMode = sslMode;
        this.initialize();
    }

    public synchronized void initialize() throws SSLException {
        Asserts.check(!this.initialized, "SSL I/O session already initialized");
        if (this.status >= 1) {
            return;
        }
        switch (this.sslMode) {
            case CLIENT: {
                this.sslEngine.setUseClientMode(true);
                break;
            }
            case SERVER: {
                this.sslEngine.setUseClientMode(false);
            }
        }
        if (this.handler != null) {
            try {
                this.handler.initalize(this.sslEngine);
            }
            catch (RuntimeException ex) {
                throw this.convert(ex);
            }
        }
        this.initialized = true;
        this.sslEngine.beginHandshake();
        this.inEncrypted.release();
        this.outEncrypted.release();
        this.inPlain.release();
        this.doHandshake();
    }

    public synchronized SSLSession getSSLSession() {
        return this.sslEngine.getSession();
    }

    private SSLException convert(RuntimeException ex) {
        Throwable cause = ex.getCause();
        if (cause == null) {
            cause = ex;
        }
        return new SSLException(cause);
    }

    private SSLEngineResult doWrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        try {
            return this.sslEngine.wrap(src, dst);
        }
        catch (RuntimeException ex) {
            throw this.convert(ex);
        }
    }

    private SSLEngineResult doUnwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        try {
            return this.sslEngine.unwrap(src, dst);
        }
        catch (RuntimeException ex) {
            throw this.convert(ex);
        }
    }

    private void doRunTask() throws SSLException {
        try {
            Runnable r = this.sslEngine.getDelegatedTask();
            if (r != null) {
                r.run();
            }
        }
        catch (RuntimeException ex) {
            throw this.convert(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doHandshake() throws SSLException {
        boolean handshaking = true;
        SSLEngineResult result = null;
        while (handshaking) {
            SSLEngineResult.HandshakeStatus handshakeStatus = this.sslEngine.getHandshakeStatus();
            if (handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && this.outboundClosedCount.get() > 0) {
                handshakeStatus = SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            switch (handshakeStatus) {
                case NEED_WRAP: {
                    ByteBuffer outEncryptedBuf = this.outEncrypted.acquire();
                    result = this.doWrap(ByteBuffer.allocate(0), outEncryptedBuf);
                    if (result.getStatus() == SSLEngineResult.Status.OK && result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_WRAP) break;
                    handshaking = false;
                    break;
                }
                case NEED_UNWRAP: {
                    ByteBuffer inEncryptedBuf = this.inEncrypted.acquire();
                    ByteBuffer inPlainBuf = this.inPlain.acquire();
                    inEncryptedBuf.flip();
                    try {
                        result = this.doUnwrap(inEncryptedBuf, inPlainBuf);
                    }
                    finally {
                        inEncryptedBuf.compact();
                    }
                    try {
                        if (!inEncryptedBuf.hasRemaining() && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                            throw new SSLException("Input buffer is full");
                        }
                    }
                    finally {
                        if (inEncryptedBuf.position() == 0) {
                            this.inEncrypted.release();
                        }
                    }
                    if (this.status >= 1) {
                        this.inPlain.release();
                    }
                    if (result.getStatus() == SSLEngineResult.Status.OK) break;
                    handshaking = false;
                    break;
                }
                case NEED_TASK: {
                    this.doRunTask();
                    break;
                }
                case NOT_HANDSHAKING: {
                    handshaking = false;
                    break;
                }
            }
        }
        if (result != null && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED && this.handler != null) {
            try {
                this.handler.verify(this.session, this.sslEngine.getSession());
            }
            catch (RuntimeException ex) {
                throw this.convert(ex);
            }
        }
    }

    private void updateEventMask() {
        int oldMask;
        if (this.status == 0 && (this.endOfStream || this.sslEngine.isInboundDone())) {
            this.status = 1;
        }
        if (this.status == 1 && !this.outEncrypted.hasData()) {
            this.sslEngine.closeOutbound();
            this.outboundClosedCount.incrementAndGet();
        }
        if (this.status == 1 && this.sslEngine.isOutboundDone() && (this.endOfStream || this.sslEngine.isInboundDone()) && (this.terminated || !this.inPlain.hasData() && (this.appBufferStatus == null || !this.appBufferStatus.hasBufferedInput()))) {
            this.status = Integer.MAX_VALUE;
        }
        if (this.status <= 1 && this.endOfStream && this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
            this.status = Integer.MAX_VALUE;
        }
        if (this.status == Integer.MAX_VALUE) {
            this.session.close();
            return;
        }
        int newMask = oldMask = this.session.getEventMask();
        switch (this.sslEngine.getHandshakeStatus()) {
            case NEED_WRAP: {
                newMask = 5;
                break;
            }
            case NEED_UNWRAP: {
                newMask = 1;
                break;
            }
            case NOT_HANDSHAKING: {
                newMask = this.appEventMask;
                break;
            }
            case NEED_TASK: {
                break;
            }
        }
        if (!(!this.endOfStream || this.inPlain.hasData() || this.appBufferStatus != null && this.appBufferStatus.hasBufferedInput())) {
            newMask &= 0xFFFFFFFE;
        }
        if (this.outEncrypted.hasData()) {
            newMask |= 4;
        } else if (this.sslEngine.isOutboundDone()) {
            newMask &= 0xFFFFFFFB;
        }
        if (oldMask != newMask) {
            this.session.setEventMask(newMask);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int sendEncryptedData() throws IOException {
        int bytesWritten;
        if (!this.outEncrypted.hasData()) {
            return this.session.channel().write(EMPTY_BUFFER);
        }
        ByteBuffer outEncryptedBuf = this.outEncrypted.acquire();
        outEncryptedBuf.flip();
        try {
            bytesWritten = this.session.channel().write(outEncryptedBuf);
        }
        finally {
            outEncryptedBuf.compact();
        }
        if (outEncryptedBuf.position() == 0) {
            this.outEncrypted.release();
        }
        return bytesWritten;
    }

    private int receiveEncryptedData() throws IOException {
        if (this.endOfStream) {
            return -1;
        }
        ByteBuffer inEncryptedBuf = this.inEncrypted.acquire();
        int bytesRead = this.session.channel().read(inEncryptedBuf);
        if (inEncryptedBuf.position() == 0) {
            this.inEncrypted.release();
        }
        if (bytesRead == -1) {
            this.endOfStream = true;
        }
        return bytesRead;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean decryptData() throws SSLException {
        boolean decrypted = false;
        while (this.inEncrypted.hasData()) {
            SSLEngineResult result;
            ByteBuffer inEncryptedBuf = this.inEncrypted.acquire();
            ByteBuffer inPlainBuf = this.inPlain.acquire();
            inEncryptedBuf.flip();
            try {
                result = this.doUnwrap(inEncryptedBuf, inPlainBuf);
            }
            finally {
                inEncryptedBuf.compact();
            }
            try {
                if (!inEncryptedBuf.hasRemaining() && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                    throw new SSLException("Unable to complete SSL handshake");
                }
                SSLEngineResult.Status status = result.getStatus();
                if (status == SSLEngineResult.Status.OK) {
                    decrypted = true;
                    continue;
                }
                if (status == SSLEngineResult.Status.BUFFER_UNDERFLOW && this.endOfStream) {
                    throw new SSLException("Unable to decrypt incoming data due to unexpected end of stream");
                }
                break;
            }
            finally {
                if (this.inEncrypted.acquire().position() != 0) continue;
                this.inEncrypted.release();
            }
        }
        if (this.sslEngine.isInboundDone()) {
            this.endOfStream = true;
        }
        return decrypted;
    }

    public synchronized boolean isAppInputReady() throws IOException {
        do {
            this.receiveEncryptedData();
            this.doHandshake();
            SSLEngineResult.HandshakeStatus status = this.sslEngine.getHandshakeStatus();
            if (status == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING || status == SSLEngineResult.HandshakeStatus.FINISHED) {
                this.decryptData();
            }
            if (!this.terminated) continue;
            ByteBuffer inPlainBuf = this.inPlain.acquire();
            inPlainBuf.clear();
            if (inPlainBuf.position() != 0) continue;
            this.inPlain.release();
        } while (this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK);
        return (this.appEventMask & 1) > 0 && (this.inPlain.hasData() || this.appBufferStatus != null && this.appBufferStatus.hasBufferedInput() || this.endOfStream && this.status == 0);
    }

    public synchronized boolean isAppOutputReady() throws IOException {
        return (this.appEventMask & 4) > 0 && this.status == 0 && this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }

    public synchronized void inboundTransport() throws IOException {
        this.updateEventMask();
    }

    public synchronized void outboundTransport() throws IOException {
        this.sendEncryptedData();
        this.doHandshake();
        this.updateEventMask();
    }

    public synchronized boolean isInboundDone() {
        return this.sslEngine.isInboundDone();
    }

    public synchronized boolean isOutboundDone() {
        return this.sslEngine.isOutboundDone();
    }

    private synchronized int writePlain(ByteBuffer src) throws IOException {
        Args.notNull(src, "Byte buffer");
        if (this.status != 0) {
            throw new ClosedChannelException();
        }
        ByteBuffer outEncryptedBuf = this.outEncrypted.acquire();
        SSLEngineResult result = this.doWrap(src, outEncryptedBuf);
        if (result.getStatus() == SSLEngineResult.Status.CLOSED) {
            this.status = Integer.MAX_VALUE;
        }
        return result.bytesConsumed();
    }

    private synchronized int readPlain(ByteBuffer dst) {
        Args.notNull(dst, "Byte buffer");
        if (this.inPlain.hasData()) {
            ByteBuffer inPlainBuf = this.inPlain.acquire();
            inPlainBuf.flip();
            int n = Math.min(inPlainBuf.remaining(), dst.remaining());
            for (int i = 0; i < n; ++i) {
                dst.put(inPlainBuf.get());
            }
            inPlainBuf.compact();
            if (inPlainBuf.position() == 0) {
                this.inPlain.release();
            }
            return n;
        }
        return this.endOfStream ? -1 : 0;
    }

    @Override
    public synchronized void close() {
        this.terminated = true;
        if (this.status >= 1) {
            return;
        }
        this.status = 1;
        if (this.session.getSocketTimeout() == 0) {
            this.session.setSocketTimeout(1000);
        }
        try {
            this.updateEventMask();
        }
        catch (CancelledKeyException ex) {
            this.shutdown();
        }
    }

    @Override
    public synchronized void shutdown() {
        if (this.status == Integer.MAX_VALUE) {
            return;
        }
        this.status = Integer.MAX_VALUE;
        this.session.shutdown();
        this.inEncrypted.release();
        this.outEncrypted.release();
        this.inPlain.release();
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public boolean isClosed() {
        return this.status >= 1 || this.session.isClosed();
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
    public synchronized int getEventMask() {
        return this.appEventMask;
    }

    @Override
    public synchronized void setEventMask(int ops) {
        this.appEventMask = ops;
        this.updateEventMask();
    }

    @Override
    public synchronized void setEvent(int op) {
        this.appEventMask |= op;
        this.updateEventMask();
    }

    @Override
    public synchronized void clearEvent(int op) {
        this.appEventMask &= ~op;
        this.updateEventMask();
    }

    @Override
    public int getSocketTimeout() {
        return this.session.getSocketTimeout();
    }

    @Override
    public void setSocketTimeout(int timeout) {
        this.session.setSocketTimeout(timeout);
    }

    @Override
    public synchronized boolean hasBufferedInput() {
        return this.appBufferStatus != null && this.appBufferStatus.hasBufferedInput() || this.inEncrypted.hasData() || this.inPlain.hasData();
    }

    @Override
    public synchronized boolean hasBufferedOutput() {
        return this.appBufferStatus != null && this.appBufferStatus.hasBufferedOutput() || this.outEncrypted.hasData();
    }

    @Override
    public synchronized void setBufferStatus(SessionBufferStatus status) {
        this.appBufferStatus = status;
    }

    @Override
    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }

    @Override
    public Object removeAttribute(String name) {
        return this.session.removeAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object obj) {
        this.session.setAttribute(name, obj);
    }

    private static void formatOps(StringBuilder buffer, int ops) {
        if ((ops & 1) > 0) {
            buffer.append('r');
        }
        if ((ops & 4) > 0) {
            buffer.append('w');
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.session);
        buffer.append("[");
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
        SSLIOSession.formatOps(buffer, this.appEventMask);
        buffer.append("][");
        buffer.append((Object)this.sslEngine.getHandshakeStatus());
        if (this.sslEngine.isInboundDone()) {
            buffer.append("][inbound done][");
        }
        if (this.sslEngine.isOutboundDone()) {
            buffer.append("][outbound done][");
        }
        if (this.endOfStream) {
            buffer.append("][EOF][");
        }
        buffer.append("][");
        buffer.append(!this.inEncrypted.hasData() ? 0 : this.inEncrypted.acquire().position());
        buffer.append("][");
        buffer.append(!this.inPlain.hasData() ? 0 : this.inPlain.acquire().position());
        buffer.append("][");
        buffer.append(!this.outEncrypted.hasData() ? 0 : this.outEncrypted.acquire().position());
        buffer.append("]");
        return buffer.toString();
    }

    @Override
    public Socket getSocket() {
        return this.session instanceof SocketAccessor ? ((SocketAccessor)((Object)this.session)).getSocket() : null;
    }

    private class InternalByteChannel
    implements ByteChannel {
        private InternalByteChannel() {
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            return SSLIOSession.this.writePlain(src);
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return SSLIOSession.this.readPlain(dst);
        }

        @Override
        public void close() throws IOException {
            SSLIOSession.this.close();
        }

        @Override
        public boolean isOpen() {
            return !SSLIOSession.this.isClosed();
        }
    }
}

