/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteBufferUtils
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.NioChannel;
import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.net.TLSClientHelloExtractor;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

public class SecureNioChannel
extends NioChannel {
    private static final Log log = LogFactory.getLog(SecureNioChannel.class);
    private static final StringManager sm = StringManager.getManager(SecureNioChannel.class);
    private static final int DEFAULT_NET_BUFFER_SIZE = 16921;
    private final NioEndpoint endpoint;
    protected ByteBuffer netInBuffer;
    protected ByteBuffer netOutBuffer;
    protected SSLEngine sslEngine;
    protected boolean sniComplete = false;
    protected boolean handshakeComplete = false;
    protected SSLEngineResult.HandshakeStatus handshakeStatus;
    protected boolean closed = false;
    protected boolean closing = false;
    private final Map<String, List<String>> additionalTlsAttributes = new HashMap<String, List<String>>();

    public SecureNioChannel(SocketBufferHandler bufHandler, NioEndpoint endpoint) {
        super(bufHandler);
        if (endpoint.getSocketProperties().getDirectSslBuffer()) {
            this.netInBuffer = ByteBuffer.allocateDirect(16921);
            this.netOutBuffer = ByteBuffer.allocateDirect(16921);
        } else {
            this.netInBuffer = ByteBuffer.allocate(16921);
            this.netOutBuffer = ByteBuffer.allocate(16921);
        }
        this.endpoint = endpoint;
    }

    @Override
    public void reset(SocketChannel channel, NioEndpoint.NioSocketWrapper socketWrapper) throws IOException {
        super.reset(channel, socketWrapper);
        this.sslEngine = null;
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.closed = false;
        this.closing = false;
        this.netInBuffer.clear();
    }

    @Override
    public void free() {
        super.free();
        if (this.endpoint.getSocketProperties().getDirectSslBuffer()) {
            ByteBufferUtils.cleanDirectBuffer((ByteBuffer)this.netInBuffer);
            ByteBufferUtils.cleanDirectBuffer((ByteBuffer)this.netOutBuffer);
        }
    }

    protected boolean flush(ByteBuffer buf) throws IOException {
        int remaining = buf.remaining();
        if (remaining > 0) {
            return this.sc.write(buf) >= remaining;
        }
        return true;
    }

    @Override
    public int handshake(boolean read, boolean write) throws IOException {
        if (this.handshakeComplete) {
            return 0;
        }
        if (!this.sniComplete) {
            int sniResult = this.processSNI();
            if (sniResult == 0) {
                this.sniComplete = true;
            } else {
                return sniResult;
            }
        }
        if (!this.flush(this.netOutBuffer)) {
            return 4;
        }
        SSLEngineResult handshake = null;
        block9: while (!this.handshakeComplete) {
            switch (this.handshakeStatus) {
                case NOT_HANDSHAKING: {
                    throw new IOException(sm.getString("channel.nio.ssl.notHandshaking"));
                }
                case FINISHED: {
                    if (this.endpoint.hasNegotiableProtocols()) {
                        if (this.sslEngine instanceof SSLUtil.ProtocolInfo) {
                            this.socketWrapper.setNegotiatedProtocol(((SSLUtil.ProtocolInfo)((Object)this.sslEngine)).getNegotiatedProtocol());
                        } else if (JreCompat.isAlpnSupported()) {
                            this.socketWrapper.setNegotiatedProtocol(JreCompat.getInstance().getApplicationProtocol(this.sslEngine));
                        }
                    }
                    this.handshakeComplete = !this.netOutBuffer.hasRemaining();
                    return this.handshakeComplete ? 0 : 4;
                }
                case NEED_WRAP: {
                    try {
                        handshake = this.handshakeWrap(write);
                    }
                    catch (SSLException e) {
                        handshake = this.handshakeWrap(write);
                        throw e;
                    }
                    if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                        if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            this.handshakeStatus = this.tasks();
                        }
                    } else {
                        if (handshake.getStatus() == SSLEngineResult.Status.CLOSED) {
                            this.flush(this.netOutBuffer);
                            return -1;
                        }
                        throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", new Object[]{handshake.getStatus()}));
                    }
                    if (this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP || !this.flush(this.netOutBuffer)) {
                        return 4;
                    }
                }
                case NEED_UNWRAP: {
                    handshake = this.handshakeUnwrap(read);
                    if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                        if (this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_TASK) continue block9;
                        this.handshakeStatus = this.tasks();
                        continue block9;
                    }
                    if (handshake.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                        return 1;
                    }
                    throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", new Object[]{handshake.getStatus()}));
                }
                case NEED_TASK: {
                    this.handshakeStatus = this.tasks();
                    continue block9;
                }
            }
            throw new IllegalStateException(sm.getString("channel.nio.ssl.invalidStatus", new Object[]{this.handshakeStatus}));
        }
        return 0;
    }

    private int processSNI() throws IOException {
        int bytesRead = this.sc.read(this.netInBuffer);
        if (bytesRead == -1) {
            return -1;
        }
        TLSClientHelloExtractor extractor = new TLSClientHelloExtractor(this.netInBuffer);
        while (extractor.getResult() == TLSClientHelloExtractor.ExtractorResult.UNDERFLOW && this.netInBuffer.capacity() < this.endpoint.getSniParseLimit()) {
            int newLimit = Math.min(this.netInBuffer.capacity() * 2, this.endpoint.getSniParseLimit());
            log.info((Object)sm.getString("channel.nio.ssl.expandNetInBuffer", new Object[]{Integer.toString(newLimit)}));
            this.netInBuffer = ByteBufferUtils.expand((ByteBuffer)this.netInBuffer, (int)newLimit);
            if (this.sc.read(this.netInBuffer) < 0) {
                return -1;
            }
            extractor = new TLSClientHelloExtractor(this.netInBuffer);
        }
        String hostName = null;
        List<Cipher> clientRequestedCiphers = null;
        List<String> clientRequestedApplicationProtocols = null;
        switch (extractor.getResult()) {
            case COMPLETE: {
                hostName = extractor.getSNIValue();
                clientRequestedApplicationProtocols = extractor.getClientRequestedApplicationProtocols();
            }
            case NOT_PRESENT: {
                clientRequestedCiphers = extractor.getClientRequestedCiphers();
                break;
            }
            case NEED_READ: {
                return 1;
            }
            case UNDERFLOW: {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("channel.nio.ssl.sniDefault"));
                }
                hostName = this.endpoint.getDefaultSSLHostConfigName();
                clientRequestedCiphers = Collections.emptyList();
                break;
            }
            case NON_SECURE: {
                this.netOutBuffer.clear();
                this.netOutBuffer.put(TLSClientHelloExtractor.USE_TLS_RESPONSE);
                this.netOutBuffer.flip();
                this.flushOutbound();
                throw new IOException(sm.getString("channel.nio.ssl.foundHttp"));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("channel.nio.ssl.sniHostName", new Object[]{this.sc, hostName}));
        }
        this.sslEngine = this.endpoint.createSSLEngine(hostName, clientRequestedCiphers, clientRequestedApplicationProtocols);
        this.additionalTlsAttributes.put("org.apache.tomcat.util.net.secure_requested_protocol_versions", extractor.getClientRequestedProtocols());
        this.additionalTlsAttributes.put("org.apache.tomcat.util.net.secure_requested_ciphers", extractor.getClientRequestedCipherNames());
        this.getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
        if (this.netOutBuffer.capacity() < this.sslEngine.getSession().getApplicationBufferSize()) {
            log.info((Object)sm.getString("channel.nio.ssl.expandNetOutBuffer", new Object[]{Integer.toString(this.sslEngine.getSession().getApplicationBufferSize())}));
        }
        this.netInBuffer = ByteBufferUtils.expand((ByteBuffer)this.netInBuffer, (int)this.sslEngine.getSession().getPacketBufferSize());
        this.netOutBuffer = ByteBufferUtils.expand((ByteBuffer)this.netOutBuffer, (int)this.sslEngine.getSession().getPacketBufferSize());
        this.netOutBuffer.position(0);
        this.netOutBuffer.limit(0);
        this.sslEngine.beginHandshake();
        this.handshakeStatus = this.sslEngine.getHandshakeStatus();
        return 0;
    }

    public void rehandshake(long timeout) throws IOException {
        if (this.netInBuffer.position() > 0 && this.netInBuffer.position() < this.netInBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netInputNotEmpty"));
        }
        if (this.netOutBuffer.position() > 0 && this.netOutBuffer.position() < this.netOutBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netOutputNotEmpty"));
        }
        if (!this.getBufHandler().isReadBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appInputNotEmpty"));
        }
        if (!this.getBufHandler().isWriteBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appOutputNotEmpty"));
        }
        this.handshakeComplete = false;
        boolean isReadable = false;
        boolean isWritable = false;
        boolean handshaking = true;
        Selector selector = null;
        SelectionKey key = null;
        try {
            this.sslEngine.beginHandshake();
            this.handshakeStatus = this.sslEngine.getHandshakeStatus();
            block18: while (handshaking) {
                int hsStatus = this.handshake(isReadable, isWritable);
                switch (hsStatus) {
                    case -1: {
                        throw new EOFException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
                    }
                    case 0: {
                        handshaking = false;
                        continue block18;
                    }
                }
                long now = System.currentTimeMillis();
                if (selector == null) {
                    selector = Selector.open();
                    key = this.getIOChannel().register(selector, hsStatus);
                } else {
                    key.interestOps(hsStatus);
                }
                int keyCount = selector.select(timeout);
                if (keyCount == 0 && System.currentTimeMillis() - now >= timeout) {
                    throw new SocketTimeoutException(sm.getString("channel.nio.ssl.timeoutDuringHandshake"));
                }
                isReadable = key.isReadable();
                isWritable = key.isWritable();
            }
        }
        catch (IOException x) {
            this.closeSilently();
            throw x;
        }
        catch (Exception cx) {
            this.closeSilently();
            IOException x = new IOException(cx);
            throw x;
        }
        finally {
            if (key != null) {
                try {
                    key.cancel();
                }
                catch (Exception exception) {}
            }
            if (selector != null) {
                try {
                    selector.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    protected SSLEngineResult.HandshakeStatus tasks() {
        Runnable r = null;
        while ((r = this.sslEngine.getDelegatedTask()) != null) {
            r.run();
        }
        return this.sslEngine.getHandshakeStatus();
    }

    protected SSLEngineResult handshakeWrap(boolean doWrite) throws IOException {
        this.netOutBuffer.clear();
        this.getBufHandler().configureWriteBufferForRead();
        SSLEngineResult result = this.sslEngine.wrap(this.getBufHandler().getWriteBuffer(), this.netOutBuffer);
        this.netOutBuffer.flip();
        this.handshakeStatus = result.getHandshakeStatus();
        if (doWrite) {
            this.flush(this.netOutBuffer);
        }
        return result;
    }

    protected SSLEngineResult handshakeUnwrap(boolean doread) throws IOException {
        SSLEngineResult result;
        int read;
        if (doread && (read = this.sc.read(this.netInBuffer)) == -1) {
            throw new IOException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
        }
        boolean cont = false;
        do {
            this.netInBuffer.flip();
            this.getBufHandler().configureReadBufferForWrite();
            result = this.sslEngine.unwrap(this.netInBuffer, this.getBufHandler().getReadBuffer());
            this.netInBuffer.compact();
            this.handshakeStatus = result.getHandshakeStatus();
            if (result.getStatus() != SSLEngineResult.Status.OK || result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_TASK) continue;
            this.handshakeStatus = this.tasks();
        } while (cont = result.getStatus() == SSLEngineResult.Status.OK && this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP);
        return result;
    }

    public SSLSupport getSSLSupport() {
        if (this.sslEngine != null) {
            SSLSession session = this.sslEngine.getSession();
            return this.endpoint.getSslImplementation().getSSLSupport(session, this.additionalTlsAttributes);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        if (this.closing) {
            return;
        }
        this.closing = true;
        if (this.sslEngine == null) {
            this.netOutBuffer.clear();
            this.closed = true;
            return;
        }
        this.sslEngine.closeOutbound();
        if (!this.flush(this.netOutBuffer)) {
            throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
        }
        this.netOutBuffer.clear();
        SSLEngineResult handshake = this.sslEngine.wrap(this.getEmptyBuf(), this.netOutBuffer);
        if (handshake.getStatus() != SSLEngineResult.Status.CLOSED) {
            throw new IOException(sm.getString("channel.nio.ssl.invalidCloseState"));
        }
        this.netOutBuffer.flip();
        this.flush(this.netOutBuffer);
        this.closed = !this.netOutBuffer.hasRemaining() && handshake.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_WRAP;
    }

    @Override
    public void close(boolean force) throws IOException {
        try {
            this.close();
        }
        finally {
            if (force || this.closed) {
                this.closed = true;
                this.sc.close();
            }
        }
    }

    private void closeSilently() {
        try {
            this.close(true);
        }
        catch (IOException ioe) {
            log.debug((Object)sm.getString("channel.nio.ssl.closeSilentError"), (Throwable)ioe);
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (this.closing || this.closed) {
            return -1;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        int netread = this.sc.read(this.netInBuffer);
        if (netread == -1) {
            return -1;
        }
        int read = 0;
        do {
            this.netInBuffer.flip();
            SSLEngineResult unwrap = this.sslEngine.unwrap(this.netInBuffer, dst);
            this.netInBuffer.compact();
            if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                read += unwrap.bytesProduced();
                if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.tasks();
                }
                if (unwrap.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) continue;
                break;
            }
            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                if (read > 0) break;
                if (dst == this.getBufHandler().getReadBuffer()) {
                    this.getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                    dst = this.getBufHandler().getReadBuffer();
                    continue;
                }
                if (this.getAppReadBufHandler() != null && dst == this.getAppReadBufHandler().getByteBuffer()) {
                    this.getAppReadBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                    dst = this.getAppReadBufHandler().getByteBuffer();
                    continue;
                }
                throw new IOException(sm.getString("channel.nio.ssl.unwrapFailResize", new Object[]{unwrap.getStatus()}));
            }
            if (unwrap.getStatus() == SSLEngineResult.Status.CLOSED && this.netInBuffer.position() == 0 && read > 0) continue;
            throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", new Object[]{unwrap.getStatus()}));
        } while (this.netInBuffer.position() != 0);
        return read;
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        if (this.closing || this.closed) {
            return -1L;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        int netread = this.sc.read(this.netInBuffer);
        if (netread == -1) {
            return -1L;
        }
        int read = 0;
        OverflowState overflowState = OverflowState.NONE;
        do {
            if (overflowState == OverflowState.PROCESSING) {
                overflowState = OverflowState.DONE;
            }
            this.netInBuffer.flip();
            SSLEngineResult unwrap = this.sslEngine.unwrap(this.netInBuffer, dsts, offset, length);
            this.netInBuffer.compact();
            if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                read += unwrap.bytesProduced();
                if (overflowState == OverflowState.DONE) {
                    read -= this.getBufHandler().getReadBuffer().position();
                }
                if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.tasks();
                }
                if (unwrap.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) continue;
                break;
            }
            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                if (read > 0) break;
                ByteBuffer readBuffer = this.getBufHandler().getReadBuffer();
                boolean found = false;
                boolean resized = true;
                for (int i = 0; i < length; ++i) {
                    if (dsts[offset + i] == this.getBufHandler().getReadBuffer()) {
                        this.getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                        if (dsts[offset + i] == this.getBufHandler().getReadBuffer()) {
                            resized = false;
                        }
                        dsts[offset + i] = this.getBufHandler().getReadBuffer();
                        found = true;
                        continue;
                    }
                    if (this.getAppReadBufHandler() == null || dsts[offset + i] != this.getAppReadBufHandler().getByteBuffer()) continue;
                    this.getAppReadBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                    if (dsts[offset + i] == this.getAppReadBufHandler().getByteBuffer()) {
                        resized = false;
                    }
                    dsts[offset + i] = this.getAppReadBufHandler().getByteBuffer();
                    found = true;
                }
                if (found) {
                    if (resized) continue;
                    throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", new Object[]{unwrap.getStatus()}));
                }
                ByteBuffer[] dsts2 = new ByteBuffer[dsts.length + 1];
                int dstOffset = 0;
                for (int i = 0; i < dsts.length + 1; ++i) {
                    if (i == offset + length) {
                        dsts2[i] = readBuffer;
                        dstOffset = -1;
                        continue;
                    }
                    dsts2[i] = dsts[i + dstOffset];
                }
                dsts = dsts2;
                ++length;
                this.getBufHandler().configureReadBufferForWrite();
                overflowState = OverflowState.PROCESSING;
                continue;
            }
            throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", new Object[]{unwrap.getStatus()}));
        } while ((this.netInBuffer.position() != 0 || overflowState == OverflowState.PROCESSING) && overflowState != OverflowState.DONE);
        return read;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        this.checkInterruptStatus();
        if (src == this.netOutBuffer) {
            int written = this.sc.write(src);
            return written;
        }
        if (this.closing || this.closed) {
            throw new IOException(sm.getString("channel.nio.ssl.closing"));
        }
        if (!this.flush(this.netOutBuffer)) {
            return 0;
        }
        if (!src.hasRemaining()) {
            return 0;
        }
        this.netOutBuffer.clear();
        SSLEngineResult result = this.sslEngine.wrap(src, this.netOutBuffer);
        int written = result.bytesConsumed();
        this.netOutBuffer.flip();
        if (result.getStatus() == SSLEngineResult.Status.OK) {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.tasks();
            }
        } else {
            throw new IOException(sm.getString("channel.nio.ssl.wrapFail", new Object[]{result.getStatus()}));
        }
        this.flush(this.netOutBuffer);
        return written;
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        this.checkInterruptStatus();
        if (this.closing || this.closed) {
            throw new IOException(sm.getString("channel.nio.ssl.closing"));
        }
        if (!this.flush(this.netOutBuffer)) {
            return 0L;
        }
        this.netOutBuffer.clear();
        SSLEngineResult result = this.sslEngine.wrap(srcs, offset, length, this.netOutBuffer);
        int written = result.bytesConsumed();
        this.netOutBuffer.flip();
        if (result.getStatus() == SSLEngineResult.Status.OK) {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.tasks();
            }
        } else {
            throw new IOException(sm.getString("channel.nio.ssl.wrapFail", new Object[]{result.getStatus()}));
        }
        this.flush(this.netOutBuffer);
        return written;
    }

    @Override
    public int getOutboundRemaining() {
        return this.netOutBuffer.remaining();
    }

    @Override
    public boolean flushOutbound() throws IOException {
        int remaining = this.netOutBuffer.remaining();
        this.flush(this.netOutBuffer);
        int remaining2 = this.netOutBuffer.remaining();
        return remaining2 < remaining;
    }

    @Override
    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    @Override
    public boolean isClosing() {
        return this.closing;
    }

    public SSLEngine getSslEngine() {
        return this.sslEngine;
    }

    public ByteBuffer getEmptyBuf() {
        return emptyBuf;
    }

    private static enum OverflowState {
        NONE,
        PROCESSING,
        DONE;

    }
}

