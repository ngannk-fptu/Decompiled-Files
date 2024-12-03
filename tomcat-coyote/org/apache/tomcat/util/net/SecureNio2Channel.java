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
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.Nio2Channel;
import org.apache.tomcat.util.net.Nio2Endpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.net.TLSClientHelloExtractor;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

public class SecureNio2Channel
extends Nio2Channel {
    private static final Log log = LogFactory.getLog(SecureNio2Channel.class);
    private static final StringManager sm = StringManager.getManager(SecureNio2Channel.class);
    private static final int DEFAULT_NET_BUFFER_SIZE = 16921;
    protected final Nio2Endpoint endpoint;
    protected ByteBuffer netInBuffer;
    protected ByteBuffer netOutBuffer;
    protected SSLEngine sslEngine;
    protected volatile boolean sniComplete = false;
    private volatile boolean handshakeComplete = false;
    private volatile SSLEngineResult.HandshakeStatus handshakeStatus;
    protected boolean closed;
    protected boolean closing;
    private final Map<String, List<String>> additionalTlsAttributes = new HashMap<String, List<String>>();
    private volatile boolean unwrapBeforeRead;
    private final CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> handshakeReadCompletionHandler;
    private final CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> handshakeWriteCompletionHandler;

    public SecureNio2Channel(SocketBufferHandler bufHandler, Nio2Endpoint endpoint) {
        super(bufHandler);
        this.endpoint = endpoint;
        if (endpoint.getSocketProperties().getDirectSslBuffer()) {
            this.netInBuffer = ByteBuffer.allocateDirect(16921);
            this.netOutBuffer = ByteBuffer.allocateDirect(16921);
        } else {
            this.netInBuffer = ByteBuffer.allocate(16921);
            this.netOutBuffer = ByteBuffer.allocate(16921);
        }
        this.handshakeReadCompletionHandler = new HandshakeReadCompletionHandler();
        this.handshakeWriteCompletionHandler = new HandshakeWriteCompletionHandler();
    }

    @Override
    public void reset(AsynchronousSocketChannel channel, SocketWrapperBase<Nio2Channel> socket) throws IOException {
        super.reset(channel, socket);
        this.sslEngine = null;
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.unwrapBeforeRead = true;
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

    @Override
    public Future<Boolean> flush() {
        return new FutureFlush();
    }

    @Override
    public int handshake() throws IOException {
        return this.handshakeInternal(true);
    }

    protected int handshakeInternal(boolean async) throws IOException {
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
        SSLEngineResult handshake = null;
        long timeout = this.endpoint.getConnectionTimeout();
        block15: while (!this.handshakeComplete) {
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
                    boolean bl = this.handshakeComplete = !this.netOutBuffer.hasRemaining();
                    if (this.handshakeComplete) {
                        return 0;
                    }
                    if (async) {
                        this.sc.write(this.netOutBuffer, AbstractEndpoint.toTimeout(timeout), TimeUnit.MILLISECONDS, this.socketWrapper, this.handshakeWriteCompletionHandler);
                    } else {
                        try {
                            if (timeout > 0L) {
                                this.sc.write(this.netOutBuffer).get(timeout, TimeUnit.MILLISECONDS);
                            } else {
                                this.sc.write(this.netOutBuffer).get();
                            }
                        }
                        catch (InterruptedException | ExecutionException | TimeoutException e) {
                            throw new IOException(sm.getString("channel.nio.ssl.handshakeError"));
                        }
                    }
                    return 1;
                }
                case NEED_WRAP: {
                    try {
                        handshake = this.handshakeWrap();
                    }
                    catch (SSLException e) {
                        handshake = this.handshakeWrap();
                        throw e;
                    }
                    if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                        if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            this.handshakeStatus = this.tasks();
                        }
                    } else {
                        if (handshake.getStatus() == SSLEngineResult.Status.CLOSED) {
                            return -1;
                        }
                        throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", new Object[]{handshake.getStatus()}));
                    }
                    if (this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP || this.netOutBuffer.remaining() > 0) {
                        if (async) {
                            this.sc.write(this.netOutBuffer, AbstractEndpoint.toTimeout(timeout), TimeUnit.MILLISECONDS, this.socketWrapper, this.handshakeWriteCompletionHandler);
                        } else {
                            try {
                                if (timeout > 0L) {
                                    this.sc.write(this.netOutBuffer).get(timeout, TimeUnit.MILLISECONDS);
                                } else {
                                    this.sc.write(this.netOutBuffer).get();
                                }
                            }
                            catch (InterruptedException | ExecutionException | TimeoutException e) {
                                throw new IOException(sm.getString("channel.nio.ssl.handshakeError"));
                            }
                        }
                        return 1;
                    }
                }
                case NEED_UNWRAP: {
                    handshake = this.handshakeUnwrap();
                    if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                        if (this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_TASK) continue block15;
                        this.handshakeStatus = this.tasks();
                        continue block15;
                    }
                    if (handshake.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                        if (async) {
                            this.sc.read(this.netInBuffer, AbstractEndpoint.toTimeout(timeout), TimeUnit.MILLISECONDS, this.socketWrapper, this.handshakeReadCompletionHandler);
                        } else {
                            try {
                                int read = timeout > 0L ? this.sc.read(this.netInBuffer).get(timeout, TimeUnit.MILLISECONDS).intValue() : this.sc.read(this.netInBuffer).get().intValue();
                                if (read == -1) {
                                    throw new EOFException();
                                }
                            }
                            catch (InterruptedException | ExecutionException | TimeoutException e) {
                                throw new IOException(sm.getString("channel.nio.ssl.handshakeError"));
                            }
                        }
                        return 1;
                    }
                    throw new IOException(sm.getString("channel.nio.ssl.unexpectedStatusDuringUnwrap", new Object[]{handshake.getStatus()}));
                }
                case NEED_TASK: {
                    this.handshakeStatus = this.tasks();
                    continue block15;
                }
            }
            throw new IllegalStateException(sm.getString("channel.nio.ssl.invalidStatus", new Object[]{this.handshakeStatus}));
        }
        return this.handshakeComplete ? 0 : this.handshakeInternal(async);
    }

    private int processSNI() throws IOException {
        if (this.netInBuffer.position() == 0) {
            this.sc.read(this.netInBuffer, AbstractEndpoint.toTimeout(this.endpoint.getConnectionTimeout()), TimeUnit.MILLISECONDS, this.socketWrapper, this.handshakeReadCompletionHandler);
            return 1;
        }
        TLSClientHelloExtractor extractor = new TLSClientHelloExtractor(this.netInBuffer);
        if (extractor.getResult() == TLSClientHelloExtractor.ExtractorResult.UNDERFLOW && this.netInBuffer.capacity() < this.endpoint.getSniParseLimit()) {
            int newLimit = Math.min(this.netInBuffer.capacity() * 2, this.endpoint.getSniParseLimit());
            log.info((Object)sm.getString("channel.nio.ssl.expandNetInBuffer", new Object[]{Integer.toString(newLimit)}));
            this.netInBuffer = ByteBufferUtils.expand((ByteBuffer)this.netInBuffer, (int)newLimit);
            this.sc.read(this.netInBuffer, AbstractEndpoint.toTimeout(this.endpoint.getConnectionTimeout()), TimeUnit.MILLISECONDS, this.socketWrapper, this.handshakeReadCompletionHandler);
            return 1;
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
                this.sc.read(this.netInBuffer, AbstractEndpoint.toTimeout(this.endpoint.getConnectionTimeout()), TimeUnit.MILLISECONDS, this.socketWrapper, this.handshakeReadCompletionHandler);
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
                this.flush();
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

    public void rehandshake() throws IOException {
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
        this.netOutBuffer.position(0);
        this.netOutBuffer.limit(0);
        this.netInBuffer.position(0);
        this.netInBuffer.limit(0);
        this.getBufHandler().reset();
        this.handshakeComplete = false;
        this.sslEngine.beginHandshake();
        this.handshakeStatus = this.sslEngine.getHandshakeStatus();
        boolean handshaking = true;
        try {
            while (handshaking) {
                int hsStatus = this.handshakeInternal(false);
                switch (hsStatus) {
                    case -1: {
                        throw new EOFException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
                    }
                    case 0: {
                        handshaking = false;
                        break;
                    }
                }
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
    }

    protected SSLEngineResult.HandshakeStatus tasks() {
        Runnable r = null;
        while ((r = this.sslEngine.getDelegatedTask()) != null) {
            r.run();
        }
        return this.sslEngine.getHandshakeStatus();
    }

    protected SSLEngineResult handshakeWrap() throws IOException {
        this.netOutBuffer.clear();
        this.getBufHandler().configureWriteBufferForRead();
        SSLEngineResult result = this.sslEngine.wrap(this.getBufHandler().getWriteBuffer(), this.netOutBuffer);
        this.netOutBuffer.flip();
        this.handshakeStatus = result.getHandshakeStatus();
        return result;
    }

    protected SSLEngineResult handshakeUnwrap() throws IOException {
        SSLEngineResult result;
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
        long timeout = this.endpoint.getConnectionTimeout();
        try {
            if (timeout > 0L) {
                if (!this.flush().get(timeout, TimeUnit.MILLISECONDS).booleanValue()) {
                    this.closeSilently();
                    throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
                }
            } else if (!this.flush().get().booleanValue()) {
                this.closeSilently();
                throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
            }
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            this.closeSilently();
            throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"), e);
        }
        catch (WritePendingException e) {
            this.closeSilently();
            throw new IOException(sm.getString("channel.nio.ssl.pendingWriteDuringClose"), e);
        }
        this.netOutBuffer.clear();
        SSLEngineResult handshake = this.sslEngine.wrap(this.getEmptyBuf(), this.netOutBuffer);
        if (handshake.getStatus() != SSLEngineResult.Status.CLOSED) {
            throw new IOException(sm.getString("channel.nio.ssl.invalidCloseState"));
        }
        this.netOutBuffer.flip();
        try {
            if (timeout > 0L) {
                if (!this.flush().get(timeout, TimeUnit.MILLISECONDS).booleanValue()) {
                    this.closeSilently();
                    throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
                }
            } else if (!this.flush().get().booleanValue()) {
                this.closeSilently();
                throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
            }
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            this.closeSilently();
            throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"), e);
        }
        catch (WritePendingException e) {
            this.closeSilently();
            throw new IOException(sm.getString("channel.nio.ssl.pendingWriteDuringClose"), e);
        }
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
    public Future<Integer> read(ByteBuffer dst) {
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        return new FutureRead(dst);
    }

    @Override
    public Future<Integer> write(ByteBuffer src) {
        return new FutureWrite(src);
    }

    @Override
    public <A> void read(final ByteBuffer dst, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Integer, ? super A> handler) {
        if (this.closing || this.closed) {
            handler.completed(-1, attachment);
            return;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        CompletionHandler readCompletionHandler = new CompletionHandler<Integer, A>(){

            @Override
            public void completed(Integer nBytes, A attach) {
                if (nBytes < 0) {
                    this.failed(new EOFException(), attach);
                } else {
                    try {
                        ByteBuffer dst2 = dst;
                        int read = 0;
                        do {
                            SecureNio2Channel.this.netInBuffer.flip();
                            SSLEngineResult unwrap = SecureNio2Channel.this.sslEngine.unwrap(SecureNio2Channel.this.netInBuffer, dst2);
                            SecureNio2Channel.this.netInBuffer.compact();
                            if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                                read += unwrap.bytesProduced();
                                if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                                    SecureNio2Channel.this.tasks();
                                }
                                if (unwrap.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) continue;
                                if (read != 0) break;
                                SecureNio2Channel.this.sc.read(SecureNio2Channel.this.netInBuffer, timeout, unit, attachment, this);
                                return;
                            }
                            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                                if (read > 0) break;
                                if (dst2 == SecureNio2Channel.this.getBufHandler().getReadBuffer()) {
                                    SecureNio2Channel.this.getBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                                    dst2 = SecureNio2Channel.this.getBufHandler().getReadBuffer();
                                    continue;
                                }
                                if (SecureNio2Channel.this.getAppReadBufHandler() != null && dst2 == SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer()) {
                                    SecureNio2Channel.this.getAppReadBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                                    dst2 = SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer();
                                    continue;
                                }
                                throw new IOException(sm.getString("channel.nio.ssl.unwrapFailResize", new Object[]{unwrap.getStatus()}));
                            }
                            throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", new Object[]{unwrap.getStatus()}));
                        } while (SecureNio2Channel.this.netInBuffer.position() != 0);
                        if (!dst2.hasRemaining()) {
                            SecureNio2Channel.this.unwrapBeforeRead = true;
                        } else {
                            SecureNio2Channel.this.unwrapBeforeRead = false;
                        }
                        handler.completed(read, attach);
                    }
                    catch (Exception e) {
                        this.failed(e, attach);
                    }
                }
            }

            @Override
            public void failed(Throwable exc, A attach) {
                handler.failed(exc, attach);
            }
        };
        if (this.unwrapBeforeRead || this.netInBuffer.position() > 0) {
            readCompletionHandler.completed(this.netInBuffer.position(), attachment);
        } else {
            this.sc.read(this.netInBuffer, timeout, unit, attachment, readCompletionHandler);
        }
    }

    @Override
    public <A> void read(final ByteBuffer[] dsts, final int offset, final int length, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Long, ? super A> handler) {
        if (offset < 0 || dsts == null || offset + length > dsts.length) {
            throw new IllegalArgumentException();
        }
        if (this.closing || this.closed) {
            handler.completed(-1L, attachment);
            return;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        CompletionHandler readCompletionHandler = new CompletionHandler<Integer, A>(){

            @Override
            public void completed(Integer nBytes, A attach) {
                if (nBytes < 0) {
                    this.failed(new EOFException(), attach);
                } else {
                    try {
                        long read = 0L;
                        ByteBuffer[] dsts2 = dsts;
                        int length2 = length;
                        OverflowState overflowState = OverflowState.NONE;
                        do {
                            if (overflowState == OverflowState.PROCESSING) {
                                overflowState = OverflowState.DONE;
                            }
                            SecureNio2Channel.this.netInBuffer.flip();
                            SSLEngineResult unwrap = SecureNio2Channel.this.sslEngine.unwrap(SecureNio2Channel.this.netInBuffer, dsts2, offset, length2);
                            SecureNio2Channel.this.netInBuffer.compact();
                            if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                                read += (long)unwrap.bytesProduced();
                                if (overflowState == OverflowState.DONE) {
                                    read -= (long)SecureNio2Channel.this.getBufHandler().getReadBuffer().position();
                                }
                                if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                                    SecureNio2Channel.this.tasks();
                                }
                                if (unwrap.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) continue;
                                if (read != 0L) break;
                                SecureNio2Channel.this.sc.read(SecureNio2Channel.this.netInBuffer, timeout, unit, attachment, this);
                                return;
                            }
                            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW && read > 0L) break;
                            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                                ByteBuffer readBuffer = SecureNio2Channel.this.getBufHandler().getReadBuffer();
                                boolean found = false;
                                boolean resized = true;
                                for (int i = 0; i < length2; ++i) {
                                    if (dsts[offset + i] == SecureNio2Channel.this.getBufHandler().getReadBuffer()) {
                                        SecureNio2Channel.this.getBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                                        if (dsts[offset + i] == SecureNio2Channel.this.getBufHandler().getReadBuffer()) {
                                            resized = false;
                                        }
                                        dsts[offset + i] = SecureNio2Channel.this.getBufHandler().getReadBuffer();
                                        found = true;
                                        continue;
                                    }
                                    if (SecureNio2Channel.this.getAppReadBufHandler() == null || dsts[offset + i] != SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer()) continue;
                                    SecureNio2Channel.this.getAppReadBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                                    if (dsts[offset + i] == SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer()) {
                                        resized = false;
                                    }
                                    dsts[offset + i] = SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer();
                                    found = true;
                                }
                                if (found) {
                                    if (resized) continue;
                                    throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", new Object[]{unwrap.getStatus()}));
                                }
                                dsts2 = new ByteBuffer[dsts.length + 1];
                                int dstOffset = 0;
                                for (int i = 0; i < dsts.length + 1; ++i) {
                                    if (i == offset + length) {
                                        dsts2[i] = readBuffer;
                                        dstOffset = -1;
                                        continue;
                                    }
                                    dsts2[i] = dsts[i + dstOffset];
                                }
                                length2 = length + 1;
                                SecureNio2Channel.this.getBufHandler().configureReadBufferForWrite();
                                overflowState = OverflowState.PROCESSING;
                                continue;
                            }
                            if (unwrap.getStatus() == SSLEngineResult.Status.CLOSED) break;
                            throw new IOException(sm.getString("channel.nio.ssl.unwrapFail", new Object[]{unwrap.getStatus()}));
                        } while ((SecureNio2Channel.this.netInBuffer.position() != 0 || overflowState == OverflowState.PROCESSING) && overflowState != OverflowState.DONE);
                        int capacity = 0;
                        int endOffset = offset + length;
                        for (int i = offset; i < endOffset; ++i) {
                            capacity += dsts[i].remaining();
                        }
                        if (capacity == 0) {
                            SecureNio2Channel.this.unwrapBeforeRead = true;
                        } else {
                            SecureNio2Channel.this.unwrapBeforeRead = false;
                        }
                        handler.completed(read, attach);
                    }
                    catch (Exception e) {
                        this.failed(e, attach);
                    }
                }
            }

            @Override
            public void failed(Throwable exc, A attach) {
                handler.failed(exc, attach);
            }
        };
        if (this.unwrapBeforeRead || this.netInBuffer.position() > 0) {
            readCompletionHandler.completed(this.netInBuffer.position(), attachment);
        } else {
            this.sc.read(this.netInBuffer, timeout, unit, attachment, readCompletionHandler);
        }
    }

    @Override
    public <A> void write(final ByteBuffer src, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Integer, ? super A> handler) {
        if (this.closing || this.closed) {
            handler.failed(new IOException(sm.getString("channel.nio.ssl.closing")), attachment);
            return;
        }
        try {
            this.netOutBuffer.clear();
            SSLEngineResult result = this.sslEngine.wrap(src, this.netOutBuffer);
            final int written = result.bytesConsumed();
            this.netOutBuffer.flip();
            if (result.getStatus() == SSLEngineResult.Status.OK) {
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.tasks();
                }
            } else {
                throw new IOException(sm.getString("channel.nio.ssl.wrapFail", new Object[]{result.getStatus()}));
            }
            this.sc.write(this.netOutBuffer, timeout, unit, attachment, new CompletionHandler<Integer, A>(){

                @Override
                public void completed(Integer nBytes, A attach) {
                    if (nBytes < 0) {
                        this.failed(new EOFException(), attach);
                    } else if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                        SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer, timeout, unit, attachment, this);
                    } else if (written == 0) {
                        SecureNio2Channel.this.write(src, timeout, unit, attachment, handler);
                    } else {
                        handler.completed(written, attach);
                    }
                }

                @Override
                public void failed(Throwable exc, A attach) {
                    handler.failed(exc, attach);
                }
            });
        }
        catch (Exception e) {
            handler.failed(e, attachment);
        }
    }

    @Override
    public <A> void write(final ByteBuffer[] srcs, final int offset, final int length, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Long, ? super A> handler) {
        if (offset < 0 || length < 0 || offset > srcs.length - length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.closing || this.closed) {
            handler.failed(new IOException(sm.getString("channel.nio.ssl.closing")), attachment);
            return;
        }
        try {
            this.netOutBuffer.clear();
            SSLEngineResult result = this.sslEngine.wrap(srcs, offset, length, this.netOutBuffer);
            final int written = result.bytesConsumed();
            this.netOutBuffer.flip();
            if (result.getStatus() == SSLEngineResult.Status.OK) {
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.tasks();
                }
            } else {
                throw new IOException(sm.getString("channel.nio.ssl.wrapFail", new Object[]{result.getStatus()}));
            }
            this.sc.write(this.netOutBuffer, timeout, unit, attachment, new CompletionHandler<Integer, A>(){

                @Override
                public void completed(Integer nBytes, A attach) {
                    if (nBytes < 0) {
                        this.failed(new EOFException(), attach);
                    } else if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                        SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer, timeout, unit, attachment, this);
                    } else if (written == 0) {
                        SecureNio2Channel.this.write(srcs, offset, length, timeout, unit, attachment, handler);
                    } else {
                        handler.completed(Long.valueOf(written), attach);
                    }
                }

                @Override
                public void failed(Throwable exc, A attach) {
                    handler.failed(exc, attach);
                }
            });
        }
        catch (Exception e) {
            handler.failed(e, attachment);
        }
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

    private class HandshakeReadCompletionHandler
    implements CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> {
        private HandshakeReadCompletionHandler() {
        }

        @Override
        public void completed(Integer result, SocketWrapperBase<Nio2Channel> attachment) {
            if (result < 0) {
                this.failed((Throwable)new EOFException(), attachment);
            } else {
                SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.OPEN_READ, false);
            }
        }

        @Override
        public void failed(Throwable exc, SocketWrapperBase<Nio2Channel> attachment) {
            SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.ERROR, false);
        }
    }

    private class HandshakeWriteCompletionHandler
    implements CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> {
        private HandshakeWriteCompletionHandler() {
        }

        @Override
        public void completed(Integer result, SocketWrapperBase<Nio2Channel> attachment) {
            if (result < 0) {
                this.failed((Throwable)new EOFException(), attachment);
            } else {
                SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.OPEN_WRITE, false);
            }
        }

        @Override
        public void failed(Throwable exc, SocketWrapperBase<Nio2Channel> attachment) {
            SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.ERROR, false);
        }
    }

    private class FutureFlush
    implements Future<Boolean> {
        private Future<Integer> integer;
        private Exception e = null;

        protected FutureFlush() {
            try {
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
            }
            catch (IllegalStateException e) {
                this.e = e;
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.e != null ? true : this.integer.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.e != null ? true : this.integer.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.e != null ? true : this.integer.isDone();
        }

        @Override
        public Boolean get() throws InterruptedException, ExecutionException {
            if (this.e != null) {
                throw new ExecutionException(this.e);
            }
            return this.integer.get() >= 0;
        }

        @Override
        public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.e != null) {
                throw new ExecutionException(this.e);
            }
            return this.integer.get(timeout, unit) >= 0;
        }
    }

    private class FutureRead
    implements Future<Integer> {
        private ByteBuffer dst;
        private Future<Integer> integer;

        private FutureRead(ByteBuffer dst) {
            this.dst = dst;
            this.integer = SecureNio2Channel.this.unwrapBeforeRead || SecureNio2Channel.this.netInBuffer.position() > 0 ? null : SecureNio2Channel.this.sc.read(SecureNio2Channel.this.netInBuffer);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.integer == null ? false : this.integer.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.integer == null ? false : this.integer.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.integer == null ? true : this.integer.isDone();
        }

        @Override
        public Integer get() throws InterruptedException, ExecutionException {
            try {
                return this.integer == null ? this.unwrap(SecureNio2Channel.this.netInBuffer.position(), -1L, TimeUnit.MILLISECONDS) : this.unwrap(this.integer.get(), -1L, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e) {
                throw new ExecutionException(e);
            }
        }

        @Override
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.integer == null ? this.unwrap(SecureNio2Channel.this.netInBuffer.position(), timeout, unit) : this.unwrap(this.integer.get(timeout, unit), timeout, unit);
        }

        private Integer unwrap(int nRead, long timeout, TimeUnit unit) throws ExecutionException, TimeoutException, InterruptedException {
            if (SecureNio2Channel.this.closing || SecureNio2Channel.this.closed) {
                return -1;
            }
            if (nRead < 0) {
                return -1;
            }
            int read = 0;
            do {
                SSLEngineResult unwrap;
                SecureNio2Channel.this.netInBuffer.flip();
                try {
                    unwrap = SecureNio2Channel.this.sslEngine.unwrap(SecureNio2Channel.this.netInBuffer, this.dst);
                }
                catch (SSLException e) {
                    throw new ExecutionException(e);
                }
                SecureNio2Channel.this.netInBuffer.compact();
                if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    read += unwrap.bytesProduced();
                    if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                        SecureNio2Channel.this.tasks();
                    }
                    if (unwrap.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) continue;
                    if (read != 0) break;
                    this.integer = SecureNio2Channel.this.sc.read(SecureNio2Channel.this.netInBuffer);
                    if (timeout > 0L) {
                        return this.unwrap(this.integer.get(timeout, unit), timeout, unit);
                    }
                    return this.unwrap(this.integer.get(), -1L, TimeUnit.MILLISECONDS);
                }
                if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                    if (read > 0) break;
                    if (this.dst == SecureNio2Channel.this.getBufHandler().getReadBuffer()) {
                        SecureNio2Channel.this.getBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                        this.dst = SecureNio2Channel.this.getBufHandler().getReadBuffer();
                        continue;
                    }
                    if (this.dst == SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer()) {
                        SecureNio2Channel.this.getAppReadBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                        this.dst = SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer();
                        continue;
                    }
                    throw new ExecutionException(new IOException(sm.getString("channel.nio.ssl.unwrapFailResize", new Object[]{unwrap.getStatus()})));
                }
                throw new ExecutionException(new IOException(sm.getString("channel.nio.ssl.unwrapFail", new Object[]{unwrap.getStatus()})));
            } while (SecureNio2Channel.this.netInBuffer.position() != 0);
            if (!this.dst.hasRemaining()) {
                SecureNio2Channel.this.unwrapBeforeRead = true;
            } else {
                SecureNio2Channel.this.unwrapBeforeRead = false;
            }
            return read;
        }
    }

    private class FutureWrite
    implements Future<Integer> {
        private final ByteBuffer src;
        private Future<Integer> integer = null;
        private int written = 0;
        private Throwable t = null;

        private FutureWrite(ByteBuffer src) {
            this.src = src;
            if (SecureNio2Channel.this.closing || SecureNio2Channel.this.closed) {
                this.t = new IOException(sm.getString("channel.nio.ssl.closing"));
            } else {
                this.wrap();
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.integer.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.integer.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.integer.isDone();
        }

        @Override
        public Integer get() throws InterruptedException, ExecutionException {
            if (this.t != null) {
                throw new ExecutionException(this.t);
            }
            if (this.integer.get() > 0 && this.written == 0) {
                this.wrap();
                return this.get();
            }
            if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
                return this.get();
            }
            return this.written;
        }

        @Override
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.t != null) {
                throw new ExecutionException(this.t);
            }
            if (this.integer.get(timeout, unit) > 0 && this.written == 0) {
                this.wrap();
                return this.get(timeout, unit);
            }
            if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
                return this.get(timeout, unit);
            }
            return this.written;
        }

        protected void wrap() {
            try {
                if (!SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                    SecureNio2Channel.this.netOutBuffer.clear();
                    SSLEngineResult result = SecureNio2Channel.this.sslEngine.wrap(this.src, SecureNio2Channel.this.netOutBuffer);
                    this.written = result.bytesConsumed();
                    SecureNio2Channel.this.netOutBuffer.flip();
                    if (result.getStatus() == SSLEngineResult.Status.OK) {
                        if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            SecureNio2Channel.this.tasks();
                        }
                    } else {
                        this.t = new IOException(sm.getString("channel.nio.ssl.wrapFail", new Object[]{result.getStatus()}));
                    }
                }
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
            }
            catch (SSLException e) {
                this.t = e;
            }
        }
    }

    private static enum OverflowState {
        NONE,
        PROCESSING,
        DONE;

    }
}

