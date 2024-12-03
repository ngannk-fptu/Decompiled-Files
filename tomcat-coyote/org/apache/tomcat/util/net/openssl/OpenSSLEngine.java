/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.jni.Buffer
 *  org.apache.tomcat.jni.Pool
 *  org.apache.tomcat.jni.SSL
 *  org.apache.tomcat.jni.SSLContext
 *  org.apache.tomcat.util.buf.ByteBufferUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.openssl;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Buffer;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.openssl.OpenSSLSessionContext;
import org.apache.tomcat.util.net.openssl.OpenSSLX509Certificate;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import org.apache.tomcat.util.res.StringManager;

public final class OpenSSLEngine
extends SSLEngine
implements SSLUtil.ProtocolInfo {
    private static final Log logger = LogFactory.getLog(OpenSSLEngine.class);
    private static final StringManager sm = StringManager.getManager(OpenSSLEngine.class);
    private static final Certificate[] EMPTY_CERTIFICATES = new Certificate[0];
    public static final Set<String> AVAILABLE_CIPHER_SUITES;
    public static final Set<String> IMPLEMENTED_PROTOCOLS_SET;
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private static final int MAX_COMPRESSED_LENGTH = 17408;
    private static final int MAX_CIPHERTEXT_LENGTH = 18432;
    static final int VERIFY_DEPTH = 10;
    static final int MAX_ENCRYPTED_PACKET_LENGTH = 18713;
    static final int MAX_ENCRYPTION_OVERHEAD_LENGTH = 2329;
    private static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
    private static final long EMPTY_ADDR;
    private final long ssl;
    private final long networkBIO;
    private Accepted accepted = Accepted.NOT;
    private boolean handshakeFinished;
    private int currentHandshake;
    private boolean receivedShutdown;
    private volatile boolean destroyed;
    private volatile String version;
    private volatile String cipher;
    private volatile String applicationProtocol;
    private volatile Certificate[] peerCerts;
    @Deprecated
    private volatile X509Certificate[] x509PeerCerts;
    private volatile ClientAuthMode clientAuth = ClientAuthMode.NONE;
    private boolean isInboundDone;
    private boolean isOutboundDone;
    private boolean engineClosed;
    private boolean sendHandshakeError = false;
    private final boolean clientMode;
    private final String fallbackApplicationProtocol;
    private final OpenSSLSessionContext sessionContext;
    private final boolean alpn;
    private final boolean initialized;
    private final int certificateVerificationDepth;
    private final boolean certificateVerificationOptionalNoCA;
    private String selectedProtocol = null;
    private final OpenSSLSession session;

    OpenSSLEngine(long sslCtx, String fallbackApplicationProtocol, boolean clientMode, OpenSSLSessionContext sessionContext, boolean alpn, boolean initialized, int certificateVerificationDepth, boolean certificateVerificationOptionalNoCA) {
        if (sslCtx == 0L) {
            throw new IllegalArgumentException(sm.getString("engine.noSSLContext"));
        }
        this.session = new OpenSSLSession();
        this.ssl = SSL.newSSL((long)sslCtx, (!clientMode ? 1 : 0) != 0);
        this.networkBIO = SSL.makeNetworkBIO((long)this.ssl);
        this.fallbackApplicationProtocol = fallbackApplicationProtocol;
        this.clientMode = clientMode;
        this.sessionContext = sessionContext;
        this.alpn = alpn;
        this.initialized = initialized;
        this.certificateVerificationDepth = certificateVerificationDepth;
        this.certificateVerificationOptionalNoCA = certificateVerificationOptionalNoCA;
    }

    @Override
    public String getNegotiatedProtocol() {
        return this.selectedProtocol;
    }

    public synchronized void shutdown() {
        if (!this.destroyed) {
            this.destroyed = true;
            if (this.networkBIO != 0L) {
                SSL.freeBIO((long)this.networkBIO);
            }
            if (this.ssl != 0L) {
                SSL.freeSSL((long)this.ssl);
            }
            this.engineClosed = true;
            this.isOutboundDone = true;
            this.isInboundDone = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int writePlaintextData(long ssl, ByteBuffer src) throws SSLException {
        int sslWrote;
        OpenSSLEngine.clearLastError();
        int pos = src.position();
        int limit = src.limit();
        int len = Math.min(limit - pos, 16384);
        if (src.isDirect()) {
            long addr = Buffer.address((ByteBuffer)src) + (long)pos;
            sslWrote = SSL.writeToSSL((long)ssl, (long)addr, (int)len);
            if (sslWrote <= 0) {
                this.checkLastError();
            }
            if (sslWrote >= 0) {
                src.position(pos + sslWrote);
                return sslWrote;
            }
        } else {
            ByteBuffer buf = ByteBuffer.allocateDirect(len);
            try {
                long addr = Buffer.address((ByteBuffer)buf);
                src.limit(pos + len);
                buf.put(src);
                src.limit(limit);
                sslWrote = SSL.writeToSSL((long)ssl, (long)addr, (int)len);
                if (sslWrote <= 0) {
                    this.checkLastError();
                }
                if (sslWrote >= 0) {
                    src.position(pos + sslWrote);
                    int n = sslWrote;
                    return n;
                }
                src.position(pos);
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer((ByteBuffer)buf);
            }
        }
        throw new IllegalStateException(sm.getString("engine.writeToSSLFailed", new Object[]{Integer.toString(sslWrote)}));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int writeEncryptedData(long networkBIO, ByteBuffer src) throws SSLException {
        OpenSSLEngine.clearLastError();
        int pos = src.position();
        int len = src.remaining();
        if (src.isDirect()) {
            long addr = Buffer.address((ByteBuffer)src) + (long)pos;
            int netWrote = SSL.writeToBIO((long)networkBIO, (long)addr, (int)len);
            if (netWrote <= 0) {
                this.checkLastError();
            }
            if (netWrote >= 0) {
                src.position(pos + netWrote);
                return netWrote;
            }
        } else {
            ByteBuffer buf = ByteBuffer.allocateDirect(len);
            try {
                long addr = Buffer.address((ByteBuffer)buf);
                buf.put(src);
                int netWrote = SSL.writeToBIO((long)networkBIO, (long)addr, (int)len);
                if (netWrote <= 0) {
                    this.checkLastError();
                }
                if (netWrote >= 0) {
                    src.position(pos + netWrote);
                    int n = netWrote;
                    return n;
                }
                src.position(pos);
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer((ByteBuffer)buf);
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int readPlaintextData(long ssl, ByteBuffer dst) throws SSLException {
        OpenSSLEngine.clearLastError();
        if (dst.isDirect()) {
            int len;
            int pos = dst.position();
            long addr = Buffer.address((ByteBuffer)dst) + (long)pos;
            int sslRead = SSL.readFromSSL((long)ssl, (long)addr, (int)(len = dst.limit() - pos));
            if (sslRead > 0) {
                dst.position(pos + sslRead);
                return sslRead;
            }
            this.checkLastError();
        } else {
            int pos = dst.position();
            int limit = dst.limit();
            int len = Math.min(18713, limit - pos);
            ByteBuffer buf = ByteBuffer.allocateDirect(len);
            try {
                long addr = Buffer.address((ByteBuffer)buf);
                int sslRead = SSL.readFromSSL((long)ssl, (long)addr, (int)len);
                if (sslRead > 0) {
                    buf.limit(sslRead);
                    dst.limit(pos + sslRead);
                    dst.put(buf);
                    dst.limit(limit);
                    int n = sslRead;
                    return n;
                }
                this.checkLastError();
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer((ByteBuffer)buf);
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int readEncryptedData(long networkBIO, ByteBuffer dst, int pending) throws SSLException {
        OpenSSLEngine.clearLastError();
        if (dst.isDirect() && dst.remaining() >= pending) {
            int pos = dst.position();
            long addr = Buffer.address((ByteBuffer)dst) + (long)pos;
            int bioRead = SSL.readFromBIO((long)networkBIO, (long)addr, (int)pending);
            if (bioRead > 0) {
                dst.position(pos + bioRead);
                return bioRead;
            }
            this.checkLastError();
        } else {
            ByteBuffer buf = ByteBuffer.allocateDirect(pending);
            try {
                long addr = Buffer.address((ByteBuffer)buf);
                int bioRead = SSL.readFromBIO((long)networkBIO, (long)addr, (int)pending);
                if (bioRead > 0) {
                    buf.limit(bioRead);
                    int oldLimit = dst.limit();
                    dst.limit(dst.position() + bioRead);
                    dst.put(buf);
                    dst.limit(oldLimit);
                    int n = bioRead;
                    return n;
                }
                this.checkLastError();
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer((ByteBuffer)buf);
            }
        }
        return 0;
    }

    @Override
    public synchronized SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
        if (this.destroyed) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
        }
        if (srcs == null || dst == null) {
            throw new IllegalArgumentException(sm.getString("engine.nullBuffer"));
        }
        if (offset >= srcs.length || offset + length > srcs.length) {
            throw new IndexOutOfBoundsException(sm.getString("engine.invalidBufferArray", new Object[]{Integer.toString(offset), Integer.toString(length), Integer.toString(srcs.length)}));
        }
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        if (this.accepted == Accepted.NOT) {
            this.beginHandshakeImplicitly();
        }
        SSLEngineResult.HandshakeStatus handshakeStatus = this.getHandshakeStatus();
        if ((!this.handshakeFinished || this.engineClosed) && handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
            return new SSLEngineResult(this.getEngineStatus(), SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
        }
        int bytesProduced = 0;
        int pendingNet = SSL.pendingWrittenBytesInBIO((long)this.networkBIO);
        if (pendingNet > 0) {
            int capacity = dst.remaining();
            if (capacity < pendingNet) {
                return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, handshakeStatus, 0, 0);
            }
            try {
                bytesProduced = this.readEncryptedData(this.networkBIO, dst, pendingNet);
            }
            catch (Exception e) {
                throw new SSLException(e);
            }
            if (this.isOutboundDone) {
                this.shutdown();
            }
            return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), 0, bytesProduced);
        }
        int bytesConsumed = 0;
        int endOffset = offset + length;
        for (int i = offset; i < endOffset; ++i) {
            ByteBuffer src = srcs[i];
            if (src == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullBufferInArray"));
            }
            while (src.hasRemaining()) {
                try {
                    bytesConsumed += this.writePlaintextData(this.ssl, src);
                }
                catch (Exception e) {
                    throw new SSLException(e);
                }
                pendingNet = SSL.pendingWrittenBytesInBIO((long)this.networkBIO);
                if (pendingNet <= 0) continue;
                int capacity = dst.remaining();
                if (capacity < pendingNet) {
                    return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), bytesConsumed, bytesProduced);
                }
                try {
                }
                catch (Exception e) {
                    throw new SSLException(e);
                }
                return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), bytesConsumed, bytesProduced += this.readEncryptedData(this.networkBIO, dst, pendingNet));
            }
        }
        return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), bytesConsumed, bytesProduced);
    }

    @Override
    public synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
        if (this.destroyed) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
        }
        if (src == null || dsts == null) {
            throw new IllegalArgumentException(sm.getString("engine.nullBuffer"));
        }
        if (offset >= dsts.length || offset + length > dsts.length) {
            throw new IndexOutOfBoundsException(sm.getString("engine.invalidBufferArray", new Object[]{Integer.toString(offset), Integer.toString(length), Integer.toString(dsts.length)}));
        }
        int capacity = 0;
        int endOffset = offset + length;
        for (int i = offset; i < endOffset; ++i) {
            ByteBuffer dst = dsts[i];
            if (dst == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullBufferInArray"));
            }
            if (dst.isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
            capacity += dst.remaining();
        }
        if (this.accepted == Accepted.NOT) {
            this.beginHandshakeImplicitly();
        }
        SSLEngineResult.HandshakeStatus handshakeStatus = this.getHandshakeStatus();
        if ((!this.handshakeFinished || this.engineClosed) && handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
            return new SSLEngineResult(this.getEngineStatus(), SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
        }
        int len = src.remaining();
        if (len > 18713) {
            this.isInboundDone = true;
            this.isOutboundDone = true;
            this.engineClosed = true;
            this.shutdown();
            throw new SSLException(sm.getString("engine.oversizedPacket"));
        }
        int written = 0;
        try {
            written = this.writeEncryptedData(this.networkBIO, src);
        }
        catch (Exception e) {
            throw new SSLException(e);
        }
        int pendingApp = this.pendingReadableBytesInSSL();
        if (!this.handshakeFinished) {
            pendingApp = 0;
        }
        int bytesProduced = 0;
        int idx = offset;
        if (capacity == 0) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), written, 0);
        }
        while (pendingApp > 0) {
            if (idx == endOffset) {
                throw new IllegalStateException(sm.getString("engine.invalidDestinationBuffersState"));
            }
            while (idx < endOffset) {
                int bytesRead;
                ByteBuffer dst = dsts[idx];
                if (!dst.hasRemaining()) {
                    ++idx;
                    continue;
                }
                if (pendingApp <= 0) break;
                try {
                    bytesRead = this.readPlaintextData(this.ssl, dst);
                }
                catch (Exception e) {
                    throw new SSLException(e);
                }
                if (bytesRead == 0) {
                    throw new IllegalStateException(sm.getString("engine.failedToReadAvailableBytes"));
                }
                bytesProduced += bytesRead;
                pendingApp -= bytesRead;
                capacity -= bytesRead;
                if (dst.hasRemaining()) continue;
                ++idx;
            }
            if (capacity == 0) break;
            if (pendingApp != 0) continue;
            pendingApp = this.pendingReadableBytesInSSL();
        }
        if (!this.receivedShutdown && (SSL.getShutdown((long)this.ssl) & 2) == 2) {
            this.receivedShutdown = true;
            this.closeOutbound();
            this.closeInbound();
        }
        if (bytesProduced == 0 && (written == 0 || written > 0 && !src.hasRemaining() && this.handshakeFinished)) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, this.getHandshakeStatus(), written, 0);
        }
        return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), written, bytesProduced);
    }

    private int pendingReadableBytesInSSL() throws SSLException {
        OpenSSLEngine.clearLastError();
        int lastPrimingReadResult = SSL.readFromSSL((long)this.ssl, (long)EMPTY_ADDR, (int)0);
        if (lastPrimingReadResult <= 0) {
            this.checkLastError();
        }
        int pendingReadableBytesInSSL = SSL.pendingReadableBytesInSSL((long)this.ssl);
        if ("TLSv1".equals(this.version) && lastPrimingReadResult == 0 && pendingReadableBytesInSSL == 0) {
            lastPrimingReadResult = SSL.readFromSSL((long)this.ssl, (long)EMPTY_ADDR, (int)0);
            if (lastPrimingReadResult <= 0) {
                this.checkLastError();
            }
            pendingReadableBytesInSSL = SSL.pendingReadableBytesInSSL((long)this.ssl);
        }
        return pendingReadableBytesInSSL;
    }

    @Override
    public Runnable getDelegatedTask() {
        return null;
    }

    @Override
    public synchronized void closeInbound() throws SSLException {
        if (this.isInboundDone) {
            return;
        }
        this.isInboundDone = true;
        this.engineClosed = true;
        this.shutdown();
        if (this.accepted != Accepted.NOT && !this.receivedShutdown) {
            throw new SSLException(sm.getString("engine.inboundClose"));
        }
    }

    @Override
    public synchronized boolean isInboundDone() {
        return this.isInboundDone || this.engineClosed;
    }

    @Override
    public synchronized void closeOutbound() {
        if (this.isOutboundDone) {
            return;
        }
        this.isOutboundDone = true;
        this.engineClosed = true;
        if (this.accepted != Accepted.NOT && !this.destroyed) {
            int mode = SSL.getShutdown((long)this.ssl);
            if ((mode & 1) != 1) {
                SSL.shutdownSSL((long)this.ssl);
            }
        } else {
            this.shutdown();
        }
    }

    @Override
    public synchronized boolean isOutboundDone() {
        return this.isOutboundDone;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        Set<String> availableCipherSuites = AVAILABLE_CIPHER_SUITES;
        return availableCipherSuites.toArray(new String[0]);
    }

    @Override
    public synchronized String[] getEnabledCipherSuites() {
        if (this.destroyed) {
            return new String[0];
        }
        String[] enabled = SSL.getCiphers((long)this.ssl);
        if (enabled == null) {
            return new String[0];
        }
        for (int i = 0; i < enabled.length; ++i) {
            String mapped = OpenSSLCipherConfigurationParser.openSSLToJsse(enabled[i]);
            if (mapped == null) continue;
            enabled[i] = mapped;
        }
        return enabled;
    }

    @Override
    public synchronized void setEnabledCipherSuites(String[] cipherSuites) {
        if (this.initialized) {
            return;
        }
        if (cipherSuites == null) {
            throw new IllegalArgumentException(sm.getString("engine.nullCipherSuite"));
        }
        if (this.destroyed) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        for (String cipherSuite : cipherSuites) {
            if (cipherSuite == null) break;
            String converted = OpenSSLCipherConfigurationParser.jsseToOpenSSL(cipherSuite);
            if (!AVAILABLE_CIPHER_SUITES.contains(cipherSuite)) {
                logger.debug((Object)sm.getString("engine.unsupportedCipher", new Object[]{cipherSuite, converted}));
            }
            if (converted != null) {
                cipherSuite = converted;
            }
            buf.append(cipherSuite);
            buf.append(':');
        }
        if (buf.length() == 0) {
            throw new IllegalArgumentException(sm.getString("engine.emptyCipherSuite"));
        }
        buf.setLength(buf.length() - 1);
        String cipherSuiteSpec = buf.toString();
        try {
            SSL.setCipherSuites((long)this.ssl, (String)cipherSuiteSpec);
        }
        catch (Exception e) {
            throw new IllegalStateException(sm.getString("engine.failedCipherSuite", new Object[]{cipherSuiteSpec}), e);
        }
    }

    @Override
    public String[] getSupportedProtocols() {
        return IMPLEMENTED_PROTOCOLS_SET.toArray(new String[0]);
    }

    @Override
    public synchronized String[] getEnabledProtocols() {
        if (this.destroyed) {
            return new String[0];
        }
        ArrayList<String> enabled = new ArrayList<String>();
        enabled.add("SSLv2Hello");
        int opts = SSL.getOptions((long)this.ssl);
        if ((opts & 0x4000000) == 0) {
            enabled.add("TLSv1");
        }
        if ((opts & 0x10000000) == 0) {
            enabled.add("TLSv1.1");
        }
        if ((opts & 0x8000000) == 0) {
            enabled.add("TLSv1.2");
        }
        if ((opts & 0x1000000) == 0) {
            enabled.add("SSLv2");
        }
        if ((opts & 0x2000000) == 0) {
            enabled.add("SSLv3");
        }
        return enabled.toArray(new String[0]);
    }

    @Override
    public synchronized void setEnabledProtocols(String[] protocols) {
        if (this.initialized) {
            return;
        }
        if (protocols == null) {
            throw new IllegalArgumentException();
        }
        if (this.destroyed) {
            return;
        }
        boolean sslv2 = false;
        boolean sslv3 = false;
        boolean tlsv1 = false;
        boolean tlsv1_1 = false;
        boolean tlsv1_2 = false;
        for (String p : protocols) {
            if (!IMPLEMENTED_PROTOCOLS_SET.contains(p)) {
                throw new IllegalArgumentException(sm.getString("engine.unsupportedProtocol", new Object[]{p}));
            }
            if (p.equals("SSLv2")) {
                sslv2 = true;
                continue;
            }
            if (p.equals("SSLv3")) {
                sslv3 = true;
                continue;
            }
            if (p.equals("TLSv1")) {
                tlsv1 = true;
                continue;
            }
            if (p.equals("TLSv1.1")) {
                tlsv1_1 = true;
                continue;
            }
            if (!p.equals("TLSv1.2")) continue;
            tlsv1_2 = true;
        }
        SSL.setOptions((long)this.ssl, (int)4095);
        if (!sslv2) {
            SSL.setOptions((long)this.ssl, (int)0x1000000);
        }
        if (!sslv3) {
            SSL.setOptions((long)this.ssl, (int)0x2000000);
        }
        if (!tlsv1) {
            SSL.setOptions((long)this.ssl, (int)0x4000000);
        }
        if (!tlsv1_1) {
            SSL.setOptions((long)this.ssl, (int)0x10000000);
        }
        if (!tlsv1_2) {
            SSL.setOptions((long)this.ssl, (int)0x8000000);
        }
    }

    @Override
    public SSLSession getSession() {
        return this.session;
    }

    @Override
    public synchronized void beginHandshake() throws SSLException {
        if (this.engineClosed || this.destroyed) {
            throw new SSLException(sm.getString("engine.engineClosed"));
        }
        switch (this.accepted) {
            case NOT: {
                this.handshake();
                this.accepted = Accepted.EXPLICIT;
                break;
            }
            case IMPLICIT: {
                this.accepted = Accepted.EXPLICIT;
                break;
            }
            case EXPLICIT: {
                this.renegotiate();
            }
        }
    }

    private void beginHandshakeImplicitly() throws SSLException {
        this.handshake();
        this.accepted = Accepted.IMPLICIT;
    }

    private void handshake() throws SSLException {
        this.currentHandshake = SSL.getHandshakeCount((long)this.ssl);
        OpenSSLEngine.clearLastError();
        int code = SSL.doHandshake((long)this.ssl);
        if (code <= 0) {
            this.checkLastError();
        } else {
            if (this.alpn) {
                this.selectedProtocol = SSL.getAlpnSelected((long)this.ssl);
            }
            this.session.lastAccessedTime = System.currentTimeMillis();
            this.handshakeFinished = true;
        }
    }

    private synchronized void renegotiate() throws SSLException {
        OpenSSLEngine.clearLastError();
        int code = SSL.getVersion((long)this.ssl).equals("TLSv1.3") ? SSL.verifyClientPostHandshake((long)this.ssl) : SSL.renegotiate((long)this.ssl);
        if (code <= 0) {
            this.checkLastError();
        }
        this.handshakeFinished = false;
        this.peerCerts = null;
        this.x509PeerCerts = null;
        this.currentHandshake = SSL.getHandshakeCount((long)this.ssl);
        int code2 = SSL.doHandshake((long)this.ssl);
        if (code2 <= 0) {
            this.checkLastError();
        }
    }

    private void checkLastError() throws SSLException {
        String sslError = OpenSSLEngine.getLastError();
        if (sslError != null) {
            if (!this.handshakeFinished) {
                this.sendHandshakeError = true;
            } else {
                throw new SSLException(sslError);
            }
        }
    }

    private static void clearLastError() {
        OpenSSLEngine.getLastError();
    }

    private static String getLastError() {
        long error;
        String sslError = null;
        while ((error = (long)SSL.getLastErrorNumber()) != 0L) {
            String err = SSL.getErrorString((long)error);
            if (sslError == null) {
                sslError = err;
            }
            if (!logger.isDebugEnabled()) continue;
            logger.debug((Object)sm.getString("engine.openSSLError", new Object[]{Long.toString(error), err}));
        }
        return sslError;
    }

    private SSLEngineResult.Status getEngineStatus() {
        return this.engineClosed ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK;
    }

    @Override
    public synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        if (this.accepted == Accepted.NOT || this.destroyed) {
            return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        }
        if (!this.handshakeFinished) {
            if (this.sendHandshakeError || SSL.pendingWrittenBytesInBIO((long)this.networkBIO) != 0) {
                if (this.sendHandshakeError) {
                    this.sendHandshakeError = false;
                    ++this.currentHandshake;
                }
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            int handshakeCount = SSL.getHandshakeCount((long)this.ssl);
            if (handshakeCount != this.currentHandshake && SSL.renegotiatePending((long)this.ssl) == 0 && SSL.getPostHandshakeAuthInProgress((long)this.ssl) == 0) {
                if (this.alpn) {
                    this.selectedProtocol = SSL.getAlpnSelected((long)this.ssl);
                }
                this.session.lastAccessedTime = System.currentTimeMillis();
                this.version = SSL.getVersion((long)this.ssl);
                this.handshakeFinished = true;
                return SSLEngineResult.HandshakeStatus.FINISHED;
            }
            return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        }
        if (this.engineClosed) {
            if (SSL.pendingWrittenBytesInBIO((long)this.networkBIO) != 0) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        }
        return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }

    @Override
    public void setUseClientMode(boolean clientMode) {
        if (clientMode != this.clientMode) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean getUseClientMode() {
        return this.clientMode;
    }

    @Override
    public void setNeedClientAuth(boolean b) {
        this.setClientAuth(b ? ClientAuthMode.REQUIRE : ClientAuthMode.NONE);
    }

    @Override
    public boolean getNeedClientAuth() {
        return this.clientAuth == ClientAuthMode.REQUIRE;
    }

    @Override
    public void setWantClientAuth(boolean b) {
        this.setClientAuth(b ? ClientAuthMode.OPTIONAL : ClientAuthMode.NONE);
    }

    @Override
    public boolean getWantClientAuth() {
        return this.clientAuth == ClientAuthMode.OPTIONAL;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setClientAuth(ClientAuthMode mode) {
        if (this.clientMode) {
            return;
        }
        OpenSSLEngine openSSLEngine = this;
        synchronized (openSSLEngine) {
            if (this.clientAuth == mode) {
                return;
            }
            switch (mode) {
                case NONE: {
                    SSL.setVerify((long)this.ssl, (int)0, (int)this.certificateVerificationDepth);
                    break;
                }
                case REQUIRE: {
                    SSL.setVerify((long)this.ssl, (int)2, (int)this.certificateVerificationDepth);
                    break;
                }
                case OPTIONAL: {
                    SSL.setVerify((long)this.ssl, (int)(this.certificateVerificationOptionalNoCA ? 3 : 1), (int)this.certificateVerificationDepth);
                }
            }
            this.clientAuth = mode;
        }
    }

    @Override
    public void setEnableSessionCreation(boolean b) {
        if (!b) {
            String msg = sm.getString("engine.noRestrictSessionCreation");
            throw new UnsupportedOperationException(msg);
        }
    }

    @Override
    public boolean getEnableSessionCreation() {
        return true;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.shutdown();
    }

    static /* synthetic */ Certificate[] access$602(OpenSSLEngine x0, Certificate[] x1) {
        x0.peerCerts = x1;
        return x1;
    }

    static /* synthetic */ X509Certificate[] access$902(OpenSSLEngine x0, X509Certificate[] x1) {
        x0.x509PeerCerts = x1;
        return x1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        LinkedHashSet<String> availableCipherSuites = new LinkedHashSet<String>(128);
        long aprPool = Pool.create((long)0L);
        try {
            long sslCtx = SSLContext.make((long)aprPool, (int)SSL.SSL_PROTOCOL_ALL, (int)1);
            try {
                SSLContext.setOptions((long)sslCtx, (int)4095);
                SSLContext.setCipherSuite((long)sslCtx, (String)"ALL");
                long ssl = SSL.newSSL((long)sslCtx, (boolean)true);
                try {
                    for (String c : SSL.getCiphers((long)ssl)) {
                        if (c == null || c.length() == 0 || availableCipherSuites.contains(c)) continue;
                        availableCipherSuites.add(OpenSSLCipherConfigurationParser.openSSLToJsse(c));
                    }
                }
                finally {
                    SSL.freeSSL((long)ssl);
                }
            }
            finally {
                SSLContext.free((long)sslCtx);
            }
        }
        catch (Exception e) {
            logger.warn((Object)sm.getString("engine.ciphersFailure"), (Throwable)e);
        }
        finally {
            Pool.destroy((long)aprPool);
        }
        AVAILABLE_CIPHER_SUITES = Collections.unmodifiableSet(availableCipherSuites);
        HashSet<String> protocols = new HashSet<String>();
        protocols.add("SSLv2Hello");
        protocols.add("SSLv2");
        protocols.add("SSLv3");
        protocols.add("TLSv1");
        protocols.add("TLSv1.1");
        protocols.add("TLSv1.2");
        if (SSL.version() >= 0x1010100F) {
            protocols.add("TLSv1.3");
        }
        IMPLEMENTED_PROTOCOLS_SET = Collections.unmodifiableSet(protocols);
        EMPTY_ADDR = Buffer.address((ByteBuffer)ByteBuffer.allocate(0));
    }

    private static enum Accepted {
        NOT,
        IMPLICIT,
        EXPLICIT;

    }

    static enum ClientAuthMode {
        NONE,
        OPTIONAL,
        REQUIRE;

    }

    private class OpenSSLSession
    implements SSLSession {
        private Map<String, Object> values;
        private long lastAccessedTime = -1L;

        private OpenSSLSession() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte[] getId() {
            byte[] id = null;
            OpenSSLEngine openSSLEngine = OpenSSLEngine.this;
            synchronized (openSSLEngine) {
                if (!OpenSSLEngine.this.destroyed) {
                    id = SSL.getSessionId((long)OpenSSLEngine.this.ssl);
                }
            }
            return id;
        }

        @Override
        public SSLSessionContext getSessionContext() {
            return OpenSSLEngine.this.sessionContext;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long getCreationTime() {
            long creationTime = 0L;
            OpenSSLEngine openSSLEngine = OpenSSLEngine.this;
            synchronized (openSSLEngine) {
                if (!OpenSSLEngine.this.destroyed) {
                    creationTime = SSL.getTime((long)OpenSSLEngine.this.ssl);
                }
            }
            return creationTime * 1000L;
        }

        @Override
        public long getLastAccessedTime() {
            return this.lastAccessedTime > 0L ? this.lastAccessedTime : this.getCreationTime();
        }

        @Override
        public void invalidate() {
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void putValue(String name, Object value) {
            if (name == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullName"));
            }
            if (value == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullValue"));
            }
            Map<String, Object> values = this.values;
            if (values == null) {
                values = this.values = new HashMap<String, Object>(2);
            }
            Object old = values.put(name, value);
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueBound(new SSLSessionBindingEvent(this, name));
            }
            this.notifyUnbound(old, name);
        }

        @Override
        public Object getValue(String name) {
            if (name == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullName"));
            }
            if (this.values == null) {
                return null;
            }
            return this.values.get(name);
        }

        @Override
        public void removeValue(String name) {
            if (name == null) {
                throw new IllegalArgumentException(sm.getString("engine.nullName"));
            }
            Map<String, Object> values = this.values;
            if (values == null) {
                return;
            }
            Object old = values.remove(name);
            this.notifyUnbound(old, name);
        }

        @Override
        public String[] getValueNames() {
            Map<String, Object> values = this.values;
            if (values == null || values.isEmpty()) {
                return new String[0];
            }
            return values.keySet().toArray(new String[0]);
        }

        private void notifyUnbound(Object value, String name) {
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueUnbound(new SSLSessionBindingEvent(this, name));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
            Certificate[] c = OpenSSLEngine.this.peerCerts;
            if (c == null) {
                Certificate[] certificates;
                byte[] clientCert;
                byte[][] chain;
                OpenSSLEngine openSSLEngine = OpenSSLEngine.this;
                synchronized (openSSLEngine) {
                    if (OpenSSLEngine.this.destroyed || SSL.isInInit((long)OpenSSLEngine.this.ssl) != 0) {
                        throw new SSLPeerUnverifiedException(sm.getString("engine.unverifiedPeer"));
                    }
                    chain = SSL.getPeerCertChain((long)OpenSSLEngine.this.ssl);
                    clientCert = !OpenSSLEngine.this.clientMode ? SSL.getPeerCertificate((long)OpenSSLEngine.this.ssl) : null;
                }
                if (chain == null && clientCert == null) {
                    return null;
                }
                int len = 0;
                if (chain != null) {
                    len += chain.length;
                }
                int i = 0;
                if (clientCert != null) {
                    certificates = new Certificate[++len];
                    certificates[i++] = new OpenSSLX509Certificate(clientCert);
                } else {
                    certificates = new Certificate[len];
                }
                if (chain != null) {
                    int a = 0;
                    while (i < certificates.length) {
                        certificates[i] = new OpenSSLX509Certificate(chain[a++]);
                        ++i;
                    }
                }
                c = OpenSSLEngine.access$602(OpenSSLEngine.this, certificates);
            }
            return c;
        }

        @Override
        public Certificate[] getLocalCertificates() {
            return EMPTY_CERTIFICATES;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
            X509Certificate[] c = OpenSSLEngine.this.x509PeerCerts;
            if (c == null) {
                byte[][] chain;
                OpenSSLEngine openSSLEngine = OpenSSLEngine.this;
                synchronized (openSSLEngine) {
                    if (OpenSSLEngine.this.destroyed || SSL.isInInit((long)OpenSSLEngine.this.ssl) != 0) {
                        throw new SSLPeerUnverifiedException(sm.getString("engine.unverifiedPeer"));
                    }
                    chain = SSL.getPeerCertChain((long)OpenSSLEngine.this.ssl);
                }
                if (chain == null) {
                    throw new SSLPeerUnverifiedException(sm.getString("engine.unverifiedPeer"));
                }
                X509Certificate[] peerCerts = new X509Certificate[chain.length];
                for (int i = 0; i < peerCerts.length; ++i) {
                    try {
                        peerCerts[i] = X509Certificate.getInstance(chain[i]);
                        continue;
                    }
                    catch (CertificateException e) {
                        throw new IllegalStateException(e);
                    }
                }
                c = OpenSSLEngine.access$902(OpenSSLEngine.this, peerCerts);
            }
            return c;
        }

        @Override
        public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
            Certificate[] peer = this.getPeerCertificates();
            if (peer == null || peer.length == 0) {
                return null;
            }
            return this.principal(peer);
        }

        @Override
        public Principal getLocalPrincipal() {
            Certificate[] local = this.getLocalCertificates();
            if (local == null || local.length == 0) {
                return null;
            }
            return this.principal(local);
        }

        private Principal principal(Certificate[] certs) {
            return ((java.security.cert.X509Certificate)certs[0]).getIssuerX500Principal();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getCipherSuite() {
            if (OpenSSLEngine.this.cipher == null) {
                String ciphers;
                OpenSSLEngine openSSLEngine = OpenSSLEngine.this;
                synchronized (openSSLEngine) {
                    if (!OpenSSLEngine.this.handshakeFinished) {
                        return OpenSSLEngine.INVALID_CIPHER;
                    }
                    if (OpenSSLEngine.this.destroyed) {
                        return OpenSSLEngine.INVALID_CIPHER;
                    }
                    ciphers = SSL.getCipherForSSL((long)OpenSSLEngine.this.ssl);
                }
                String c = OpenSSLCipherConfigurationParser.openSSLToJsse(ciphers);
                if (c != null) {
                    OpenSSLEngine.this.cipher = c;
                }
            }
            return OpenSSLEngine.this.cipher;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getProtocol() {
            String applicationProtocol = OpenSSLEngine.this.applicationProtocol;
            if (applicationProtocol == null) {
                applicationProtocol = OpenSSLEngine.this.fallbackApplicationProtocol;
                if (applicationProtocol != null) {
                    OpenSSLEngine.this.applicationProtocol = applicationProtocol.replace(':', '_');
                } else {
                    applicationProtocol = "";
                    OpenSSLEngine.this.applicationProtocol = "";
                }
            }
            String version = null;
            OpenSSLEngine openSSLEngine = OpenSSLEngine.this;
            synchronized (openSSLEngine) {
                if (!OpenSSLEngine.this.destroyed) {
                    version = SSL.getVersion((long)OpenSSLEngine.this.ssl);
                }
            }
            if (applicationProtocol.isEmpty()) {
                return version;
            }
            return version + ':' + applicationProtocol;
        }

        @Override
        public String getPeerHost() {
            return null;
        }

        @Override
        public int getPeerPort() {
            return 0;
        }

        @Override
        public int getPacketBufferSize() {
            return 18713;
        }

        @Override
        public int getApplicationBufferSize() {
            return 16384;
        }
    }
}

