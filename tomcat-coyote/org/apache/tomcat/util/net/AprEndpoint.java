/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.jni.Address
 *  org.apache.tomcat.jni.Error
 *  org.apache.tomcat.jni.File
 *  org.apache.tomcat.jni.Library
 *  org.apache.tomcat.jni.OS
 *  org.apache.tomcat.jni.Poll
 *  org.apache.tomcat.jni.Pool
 *  org.apache.tomcat.jni.SSL
 *  org.apache.tomcat.jni.SSLContext
 *  org.apache.tomcat.jni.SSLContext$SNICallBack
 *  org.apache.tomcat.jni.SSLSocket
 *  org.apache.tomcat.jni.Sockaddr
 *  org.apache.tomcat.jni.Socket
 *  org.apache.tomcat.jni.Status
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.ByteBufferUtils
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.compat.JrePlatform
 */
package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.net.ssl.KeyManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Address;
import org.apache.tomcat.jni.Error;
import org.apache.tomcat.jni.File;
import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.OS;
import org.apache.tomcat.jni.Poll;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.jni.SSLSocket;
import org.apache.tomcat.jni.Sockaddr;
import org.apache.tomcat.jni.Socket;
import org.apache.tomcat.jni.Status;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.Acceptor;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.AprSSLSupport;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SendfileDataBase;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketProcessorBase;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.net.openssl.OpenSSLContext;
import org.apache.tomcat.util.net.openssl.OpenSSLUtil;

@Deprecated
public class AprEndpoint
extends AbstractEndpoint<Long, Long>
implements SSLContext.SNICallBack {
    private static final Log log = LogFactory.getLog(AprEndpoint.class);
    private static final Log logCertificate = LogFactory.getLog((String)(AprEndpoint.class.getName() + ".certificate"));
    protected long rootPool = 0L;
    protected volatile long serverSock = 0L;
    protected long serverSockPool = 0L;
    protected volatile long sslContext = 0L;
    private int previousAcceptedPort = -1;
    private String previousAcceptedAddress = null;
    private long previousAcceptedSocketNanoTime = 0L;
    protected boolean deferAccept = true;
    private boolean ipv6v6only = false;
    protected int sendfileSize = 1024;
    protected int pollTime = 2000;
    private boolean useSendFileSet = false;
    protected Poller poller = null;
    protected Sendfile sendfile = null;
    private String unixDomainSocketPath = null;
    private String unixDomainSocketPathPermissions = null;

    public AprEndpoint() {
        this.setUseAsyncIO(false);
    }

    public void setDeferAccept(boolean deferAccept) {
        this.deferAccept = deferAccept;
    }

    @Override
    public boolean getDeferAccept() {
        return this.deferAccept;
    }

    public void setIpv6v6only(boolean ipv6v6only) {
        this.ipv6v6only = ipv6v6only;
    }

    public boolean getIpv6v6only() {
        return this.ipv6v6only;
    }

    public void setSendfileSize(int sendfileSize) {
        this.sendfileSize = sendfileSize;
    }

    public int getSendfileSize() {
        return this.sendfileSize;
    }

    public int getPollTime() {
        return this.pollTime;
    }

    public void setPollTime(int pollTime) {
        if (pollTime > 0) {
            this.pollTime = pollTime;
        }
    }

    @Override
    public void setUseSendfile(boolean useSendfile) {
        this.useSendFileSet = true;
        super.setUseSendfile(useSendfile);
    }

    private void setUseSendfileInternal(boolean useSendfile) {
        super.setUseSendfile(useSendfile);
    }

    public Poller getPoller() {
        return this.poller;
    }

    public Sendfile getSendfile() {
        return this.sendfile;
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        long sa;
        long s = this.serverSock;
        if (s == 0L) {
            return null;
        }
        try {
            sa = Address.get((int)0, (long)s);
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        Sockaddr addr = Address.getInfo((long)sa);
        if (addr.hostname == null) {
            if (addr.family == 2) {
                return new InetSocketAddress("::", addr.port);
            }
            return new InetSocketAddress("0.0.0.0", addr.port);
        }
        return new InetSocketAddress(addr.hostname, addr.port);
    }

    @Override
    public void setMaxConnections(int maxConnections) {
        if (maxConnections == -1) {
            log.warn((Object)sm.getString("endpoint.apr.maxConnections.unlimited", new Object[]{this.getMaxConnections()}));
            return;
        }
        if (this.running) {
            log.warn((Object)sm.getString("endpoint.apr.maxConnections.running", new Object[]{this.getMaxConnections()}));
            return;
        }
        super.setMaxConnections(maxConnections);
    }

    public String getUnixDomainSocketPath() {
        return this.unixDomainSocketPath;
    }

    public void setUnixDomainSocketPath(String unixDomainSocketPath) {
        this.unixDomainSocketPath = unixDomainSocketPath;
    }

    public String getUnixDomainSocketPathPermissions() {
        return this.unixDomainSocketPathPermissions;
    }

    public void setUnixDomainSocketPathPermissions(String unixDomainSocketPathPermissions) {
        this.unixDomainSocketPathPermissions = unixDomainSocketPathPermissions;
    }

    public int getKeepAliveCount() {
        if (this.poller == null) {
            return 0;
        }
        return this.poller.getConnectionCount();
    }

    public int getSendfileCount() {
        if (this.sendfile == null) {
            return 0;
        }
        return this.sendfile.getSendfileCount();
    }

    @Override
    public String getId() {
        if (this.getUnixDomainSocketPath() != null) {
            return this.getUnixDomainSocketPath();
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void bind() throws Exception {
        int family;
        String hostname = null;
        try {
            this.rootPool = Pool.create((long)0L);
        }
        catch (UnsatisfiedLinkError e) {
            throw new Exception(sm.getString("endpoint.init.notavail"));
        }
        this.serverSockPool = Pool.create((long)this.rootPool);
        if (this.getUnixDomainSocketPath() != null) {
            if (!Library.APR_HAVE_UNIX) throw new Exception(sm.getString("endpoint.init.unixnotavail"));
            hostname = this.getUnixDomainSocketPath();
            family = 3;
        } else {
            if (this.getAddress() != null) {
                hostname = this.getAddress().getHostAddress();
            }
            family = 0;
        }
        long sockAddress = Address.info((String)hostname, (int)family, (int)this.getPortWithOffset(), (int)0, (long)this.rootPool);
        if (family == 3) {
            this.serverSock = Socket.create((int)family, (int)0, (int)0, (long)this.rootPool);
        } else {
            int saFamily = Address.getInfo((long)sockAddress).family;
            this.serverSock = Socket.create((int)saFamily, (int)0, (int)6, (long)this.rootPool);
            if (OS.IS_UNIX) {
                Socket.optSet((long)this.serverSock, (int)16, (int)1);
            }
            if (Library.APR_HAVE_IPV6 && saFamily == 2) {
                if (this.getIpv6v6only()) {
                    Socket.optSet((long)this.serverSock, (int)16384, (int)1);
                } else {
                    Socket.optSet((long)this.serverSock, (int)16384, (int)0);
                }
            }
            Socket.optSet((long)this.serverSock, (int)2, (int)1);
        }
        int ret = Socket.bind((long)this.serverSock, (long)sockAddress);
        if (ret != 0) {
            throw new Exception(sm.getString("endpoint.init.bind", new Object[]{"" + ret, Error.strerror((int)ret)}));
        }
        ret = Socket.listen((long)this.serverSock, (int)this.getAcceptCount());
        if (ret != 0) {
            throw new Exception(sm.getString("endpoint.init.listen", new Object[]{"" + ret, Error.strerror((int)ret)}));
        }
        if (family == 3) {
            if (this.getUnixDomainSocketPathPermissions() != null) {
                FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(this.getUnixDomainSocketPathPermissions()));
                Path path = Paths.get(this.getUnixDomainSocketPath(), new String[0]);
                Files.setAttribute(path, attrs.name(), attrs.value(), new LinkOption[0]);
            }
        } else if (OS.IS_WIN32 || OS.IS_WIN64) {
            Socket.optSet((long)this.serverSock, (int)16, (int)1);
        }
        if (!this.useSendFileSet) {
            this.setUseSendfileInternal(Library.APR_HAS_SENDFILE);
        } else if (this.getUseSendfile() && !Library.APR_HAS_SENDFILE) {
            this.setUseSendfileInternal(false);
        }
        if (this.deferAccept && Socket.optSet((long)this.serverSock, (int)32768, (int)1) == 70023) {
            this.deferAccept = false;
        }
        if (!this.isSSLEnabled()) return;
        for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
            this.createSSLContext(sslHostConfig);
        }
        SSLHostConfig defaultSSLHostConfig = (SSLHostConfig)this.sslHostConfigs.get(this.getDefaultSSLHostConfigName());
        if (defaultSSLHostConfig == null) {
            throw new IllegalArgumentException(sm.getString("endpoint.noSslHostConfig", new Object[]{this.getDefaultSSLHostConfigName(), this.getName()}));
        }
        this.setDefaultSslHostConfig(defaultSSLHostConfig);
        if (!this.getUseSendfile()) return;
        this.setUseSendfileInternal(false);
        if (!this.useSendFileSet) return;
        log.warn((Object)sm.getString("endpoint.apr.noSendfileWithSSL"));
    }

    @Override
    protected void createSSLContext(SSLHostConfig sslHostConfig) throws Exception {
        OpenSSLContext sslContext = null;
        Set<SSLHostConfigCertificate> certificates = sslHostConfig.getCertificates(true);
        for (SSLHostConfigCertificate certificate : certificates) {
            KeyManager[] kms;
            OpenSSLUtil sslUtil;
            if (sslContext == null) {
                sslUtil = new OpenSSLUtil(certificate);
                sslHostConfig.setEnabledProtocols(sslUtil.getEnabledProtocols());
                sslHostConfig.setEnabledCiphers(sslUtil.getEnabledCiphers());
                try {
                    sslContext = (OpenSSLContext)sslUtil.createSSLContext(this.negotiableProtocols);
                }
                catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
                try {
                    kms = sslUtil.getKeyManagers();
                    certificate.setCertificateKeyManager(OpenSSLUtil.chooseKeyManager(kms));
                }
                catch (Exception e) {
                    log.debug((Object)sm.getString("endpoint.apr.keyManagerError"), (Throwable)e);
                }
            } else {
                sslUtil = new OpenSSLUtil(certificate);
                kms = sslUtil.getKeyManagers();
                certificate.setCertificateKeyManager(OpenSSLUtil.chooseKeyManager(kms));
                sslContext.addCertificate(certificate);
            }
            certificate.setSslContext(sslContext);
            this.logCertificate(certificate);
        }
        if (certificates.size() > 2) {
            throw new Exception(sm.getString("endpoint.apr.tooManyCertFiles"));
        }
    }

    public long getSslContext(String sniHostName) {
        SSLHostConfig sslHostConfig = this.getSSLHostConfig(sniHostName);
        Long ctx = sslHostConfig.getOpenSslContext();
        if (ctx != null) {
            return ctx;
        }
        return 0L;
    }

    @Override
    protected void setDefaultSslHostConfig(SSLHostConfig sslHostConfig) {
        Long ctx = sslHostConfig.getOpenSslContext();
        this.sslContext = ctx;
        SSLContext.registerDefault((Long)ctx, (SSLContext.SNICallBack)this);
    }

    @Override
    public boolean isAlpnSupported() {
        return this.isSSLEnabled();
    }

    @Override
    public void startInternal() throws Exception {
        if (!this.running) {
            this.running = true;
            this.paused = false;
            if (this.socketProperties.getProcessorCache() != 0) {
                this.processorCache = new SynchronizedStack(128, this.socketProperties.getProcessorCache());
            }
            if (this.getExecutor() == null) {
                this.createExecutor();
            }
            this.initializeConnectionLatch();
            this.poller = new Poller();
            this.poller.init();
            this.poller.start();
            if (this.getUseSendfile()) {
                this.sendfile = new Sendfile();
                this.sendfile.init();
                this.sendfile.start();
            }
            this.startAcceptorThread();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stopInternal() {
        if (!this.paused) {
            this.pause();
        }
        if (this.running) {
            int waitMillis;
            this.running = false;
            this.acceptor.stop(10);
            this.poller.stop();
            if (this.getUseSendfile()) {
                this.sendfile.stop();
            }
            if (this.acceptor.getState() != Acceptor.AcceptorState.ENDED && !this.getBindOnInit()) {
                log.warn((Object)sm.getString("endpoint.warn.unlockAcceptorFailed", new Object[]{this.acceptor.getThreadName()}));
                if (this.serverSock != 0L) {
                    Socket.shutdown((long)this.serverSock, (int)0);
                    this.serverSock = 0L;
                }
            }
            try {
                for (waitMillis = 0; this.poller.pollerThread.isAlive() && waitMillis < 10000; ++waitMillis) {
                    Thread.sleep(1L);
                }
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (this.getUseSendfile()) {
                try {
                    try {
                        for (waitMillis = 0; this.sendfile.sendfileThread.isAlive() && waitMillis < 10000; ++waitMillis) {
                            Thread.sleep(1L);
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    if (this.sendfile.sendfileThread.isAlive()) {
                        log.warn((Object)sm.getString("endpoint.sendfileThreadStop"));
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            for (SocketWrapperBase socketWrapper : this.connections.values()) {
                ReentrantReadWriteLock.WriteLock wl = ((AprSocketWrapper)socketWrapper).getBlockingStatusWriteLock();
                wl.lock();
                try {
                    socketWrapper.close();
                }
                finally {
                    wl.unlock();
                }
            }
            for (Long socket : this.connections.keySet()) {
                Socket.shutdown((long)socket, (int)2);
            }
            if (this.getUseSendfile()) {
                try {
                    this.sendfile.destroy();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.sendfile = null;
            }
            try {
                this.poller.destroy();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.poller = null;
            this.connections.clear();
            if (this.processorCache != null) {
                this.processorCache.clear();
                this.processorCache = null;
            }
        }
        this.shutdownExecutor();
    }

    @Override
    public void unbind() throws Exception {
        if (this.running) {
            this.stop();
        }
        if (this.serverSockPool != 0L) {
            Pool.destroy((long)this.serverSockPool);
            this.serverSockPool = 0L;
        }
        this.doCloseServerSocket();
        this.destroySsl();
        if (this.rootPool != 0L) {
            Pool.destroy((long)this.rootPool);
            this.rootPool = 0L;
        }
        this.getHandler().recycle();
    }

    @Override
    protected void doCloseServerSocket() {
        if (this.serverSock != 0L) {
            Socket.close((long)this.serverSock);
            this.serverSock = 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected boolean setSocketOptions(SocketWrapperBase<Long> socketWrapper) {
        long socket = socketWrapper.getSocket();
        int step = 1;
        try {
            if (this.socketProperties.getSoLingerOn() && this.socketProperties.getSoLingerTime() >= 0) {
                Socket.optSet((long)socket, (int)1, (int)this.socketProperties.getSoLingerTime());
            }
            if (this.socketProperties.getTcpNoDelay()) {
                Socket.optSet((long)socket, (int)512, (int)(this.socketProperties.getTcpNoDelay() ? 1 : 0));
            }
            Socket.timeoutSet((long)socket, (long)(this.socketProperties.getSoTimeout() * 1000));
            step = 2;
            if (this.sslContext == 0L) return true;
            int rv = SSLSocket.attach((long)this.sslContext, (long)socket);
            if (rv != 0) {
                log.warn((Object)sm.getString("endpoint.err.attach", new Object[]{rv}));
                return false;
            }
            ReentrantReadWriteLock.WriteLock wl = ((AprSocketWrapper)socketWrapper).getBlockingStatusWriteLock();
            wl.lock();
            try {
                if (SSLSocket.handshake((long)socket) != 0) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)(sm.getString("endpoint.err.handshake") + ": " + SSL.getLastError()));
                    }
                    boolean bl = false;
                    return bl;
                }
            }
            finally {
                wl.unlock();
            }
            if (this.negotiableProtocols.size() <= 0) return true;
            byte[] negotiated = new byte[256];
            int len = SSLSocket.getALPN((long)socket, (byte[])negotiated);
            String negotiatedProtocol = new String(negotiated, 0, len, StandardCharsets.UTF_8);
            if (negotiatedProtocol.length() <= 0) return true;
            socketWrapper.setNegotiatedProtocol(negotiatedProtocol);
            if (!log.isDebugEnabled()) return true;
            log.debug((Object)sm.getString("endpoint.alpn.negotiated", new Object[]{negotiatedProtocol}));
            return true;
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            if (!log.isDebugEnabled()) return false;
            if (step == 2) {
                log.debug((Object)sm.getString("endpoint.err.handshake"), t);
                return false;
            }
            log.debug((Object)sm.getString("endpoint.err.unexpected"), t);
            return false;
        }
    }

    protected long allocatePoller(int size, long pool, int timeout) {
        try {
            return Poll.create((int)size, (long)pool, (int)0, (long)(timeout * 1000));
        }
        catch (Error e) {
            if (Status.APR_STATUS_IS_EINVAL((int)e.getError())) {
                log.info((Object)sm.getString("endpoint.poll.limitedpollsize", new Object[]{"" + size}));
                throw new RuntimeException(e);
            }
            log.error((Object)sm.getString("endpoint.poll.initfail"), (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean setSocketOptions(Long socket) {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("endpoint.debug.socket", new Object[]{socket}));
            }
            AprSocketWrapper wrapper = new AprSocketWrapper(socket, this);
            if (!JrePlatform.IS_WINDOWS && this.getUnixDomainSocketPath() == null) {
                long currentNanoTime = System.nanoTime();
                if (wrapper.getRemotePort() == this.previousAcceptedPort && wrapper.getRemoteAddr().equals(this.previousAcceptedAddress) && currentNanoTime - this.previousAcceptedSocketNanoTime < 1000L) {
                    throw new IOException(sm.getString("endpoint.err.duplicateAccept"));
                }
                this.previousAcceptedPort = wrapper.getRemotePort();
                this.previousAcceptedAddress = wrapper.getRemoteAddr();
                this.previousAcceptedSocketNanoTime = currentNanoTime;
            }
            this.connections.put(socket, wrapper);
            wrapper.setKeepAliveLeft(this.getMaxKeepAliveRequests());
            wrapper.setReadTimeout(this.getConnectionTimeout());
            wrapper.setWriteTimeout(this.getConnectionTimeout());
            this.getExecutor().execute(new SocketWithOptionsProcessor(wrapper));
            return true;
        }
        catch (RejectedExecutionException x) {
            log.warn((Object)sm.getString("endpoint.rejectedExecution", new Object[]{socket}), (Throwable)x);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.error((Object)sm.getString("endpoint.process.fail"), t);
        }
        return false;
    }

    @Override
    protected Long serverSocketAccept() throws Exception {
        long socket = Socket.accept((long)this.serverSock);
        if (socket == 0L) {
            throw new IOException(sm.getString("endpoint.err.accept", new Object[]{this.getName()}));
        }
        if (log.isDebugEnabled()) {
            long sa = Address.get((int)1, (long)socket);
            Sockaddr addr = Address.getInfo((long)sa);
            log.debug((Object)sm.getString("endpoint.apr.remoteport", new Object[]{socket, (long)addr.port}));
        }
        return socket;
    }

    protected boolean processSocket(long socket, SocketEvent event) {
        SocketWrapperBase socketWrapper = (SocketWrapperBase)this.connections.get(socket);
        if (socketWrapper == null) {
            return false;
        }
        if (event == SocketEvent.OPEN_READ && socketWrapper.readOperation != null) {
            return socketWrapper.readOperation.process();
        }
        if (event == SocketEvent.OPEN_WRITE && socketWrapper.writeOperation != null) {
            return socketWrapper.writeOperation.process();
        }
        return this.processSocket(socketWrapper, event, true);
    }

    @Override
    protected SocketProcessorBase<Long> createSocketProcessor(SocketWrapperBase<Long> socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    private void closeSocketInternal(long socket) {
        this.closeSocket(socket);
    }

    @Override
    protected void destroySocket(Long socket) {
        this.countDownConnection();
        this.destroySocketInternal(socket);
    }

    private void destroySocketInternal(long socket) {
        if (log.isDebugEnabled()) {
            String msg = sm.getString("endpoint.debug.destroySocket", new Object[]{socket});
            if (log.isTraceEnabled()) {
                log.trace((Object)msg, (Throwable)new Exception());
            } else {
                log.debug((Object)msg);
            }
        }
        if (socket != 0L) {
            Socket.destroy((long)socket);
        }
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    protected Log getLogCertificate() {
        return logCertificate;
    }

    public class Poller
    implements Runnable {
        private long aprPoller;
        private int pollerSize = 0;
        private long pool = 0L;
        private long[] desc;
        private SocketList addList = null;
        private SocketList closeList = null;
        private SocketTimeouts timeouts = null;
        private long lastMaintain = System.currentTimeMillis();
        private AtomicInteger connectionCount = new AtomicInteger(0);
        private volatile Thread pollerThread;
        private volatile boolean pollerRunning = true;

        public int getConnectionCount() {
            return this.connectionCount.get();
        }

        protected synchronized void init() {
            this.pool = Pool.create((long)AprEndpoint.this.serverSockPool);
            this.pollerSize = AprEndpoint.this.getMaxConnections();
            this.timeouts = new SocketTimeouts(this.pollerSize);
            this.aprPoller = AprEndpoint.this.allocatePoller(this.pollerSize, this.pool, -1);
            this.desc = new long[this.pollerSize * 4];
            this.connectionCount.set(0);
            this.addList = new SocketList(this.pollerSize);
            this.closeList = new SocketList(this.pollerSize);
        }

        protected void start() {
            this.pollerThread = new Thread((Runnable)AprEndpoint.this.poller, AprEndpoint.this.getName() + "-Poller");
            this.pollerThread.setPriority(AprEndpoint.this.threadPriority);
            this.pollerThread.setDaemon(true);
            this.pollerThread.start();
        }

        protected synchronized void stop() {
            this.pollerRunning = false;
            this.notify();
        }

        protected synchronized void destroy() {
            for (int loops = 50; loops > 0 && this.pollerThread.isAlive(); --loops) {
                try {
                    this.wait(AprEndpoint.this.pollTime / 1000);
                    continue;
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            if (this.pollerThread.isAlive()) {
                log.warn((Object)AbstractEndpoint.sm.getString("endpoint.pollerThreadStop"));
            }
            SocketInfo info = this.closeList.get();
            while (info != null) {
                this.addList.remove(info.socket);
                this.removeFromPoller(info.socket);
                AprEndpoint.this.closeSocketInternal(info.socket);
                AprEndpoint.this.destroySocketInternal(info.socket);
                info = this.closeList.get();
            }
            this.closeList.clear();
            info = this.addList.get();
            while (info != null) {
                this.removeFromPoller(info.socket);
                AprEndpoint.this.closeSocketInternal(info.socket);
                AprEndpoint.this.destroySocketInternal(info.socket);
                info = this.addList.get();
            }
            this.addList.clear();
            int rv = Poll.pollset((long)this.aprPoller, (long[])this.desc);
            if (rv > 0) {
                for (int n = 0; n < rv; ++n) {
                    AprEndpoint.this.closeSocketInternal(this.desc[n * 2 + 1]);
                    AprEndpoint.this.destroySocketInternal(this.desc[n * 2 + 1]);
                }
            }
            Pool.destroy((long)this.pool);
            this.connectionCount.set(0);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void add(long socket, long timeout, int flags) {
            if (log.isDebugEnabled()) {
                String msg = AbstractEndpoint.sm.getString("endpoint.debug.pollerAdd", new Object[]{socket, timeout, flags});
                if (log.isTraceEnabled()) {
                    log.trace((Object)msg, (Throwable)new Exception());
                } else {
                    log.debug((Object)msg);
                }
            }
            if (timeout <= 0L) {
                timeout = Integer.MAX_VALUE;
            }
            Poller poller = this;
            synchronized (poller) {
                if (this.addList.add(socket, timeout, flags)) {
                    this.notify();
                }
            }
        }

        private boolean addToPoller(long socket, int events) {
            int rv = Poll.add((long)this.aprPoller, (long)socket, (int)events);
            if (rv == 0) {
                this.connectionCount.incrementAndGet();
                return true;
            }
            return false;
        }

        private synchronized void close(long socket) {
            this.closeList.add(socket, 0L, 0);
            this.notify();
        }

        private void removeFromPoller(long socket) {
            int rv;
            if (log.isDebugEnabled()) {
                log.debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.pollerRemove", new Object[]{socket}));
            }
            if ((rv = Poll.remove((long)this.aprPoller, (long)socket)) != 70015) {
                this.connectionCount.decrementAndGet();
                if (log.isDebugEnabled()) {
                    log.debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.pollerRemoved", new Object[]{socket}));
                }
            }
            this.timeouts.remove(socket);
        }

        private synchronized void maintain() {
            long date = System.currentTimeMillis();
            if (date - this.lastMaintain < 1000L) {
                return;
            }
            this.lastMaintain = date;
            long socket = this.timeouts.check(date);
            while (socket != 0L) {
                SocketWrapperBase socketWrapper;
                if (log.isDebugEnabled()) {
                    log.debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.socketTimeout", new Object[]{socket}));
                }
                if ((socketWrapper = (SocketWrapperBase)AprEndpoint.this.connections.get(socket)) != null) {
                    socketWrapper.setError(new SocketTimeoutException());
                    if (socketWrapper.readOperation != null || socketWrapper.writeOperation != null) {
                        if (socketWrapper.readOperation != null) {
                            socketWrapper.readOperation.process();
                        } else {
                            socketWrapper.writeOperation.process();
                        }
                    } else {
                        AprEndpoint.this.processSocket(socketWrapper, SocketEvent.ERROR, true);
                    }
                }
                socket = this.timeouts.check(date);
            }
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("Poller");
            long[] res = new long[this.pollerSize * 2];
            int count = Poll.pollset((long)this.aprPoller, (long[])res);
            buf.append(" [ ");
            for (int j = 0; j < count; ++j) {
                buf.append(this.desc[2 * j + 1]).append(' ');
            }
            buf.append(']');
            return buf.toString();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            SocketList localAddList = new SocketList(AprEndpoint.this.getMaxConnections());
            SocketList localCloseList = new SocketList(AprEndpoint.this.getMaxConnections());
            while (this.pollerRunning) {
                while (this.pollerRunning && this.connectionCount.get() < 1 && this.addList.size() < 1 && this.closeList.size() < 1) {
                    try {
                        if (AprEndpoint.this.getConnectionTimeout() > 0 && this.pollerRunning) {
                            this.maintain();
                        }
                        Poller poller = this;
                        synchronized (poller) {
                            if (this.pollerRunning && this.addList.size() < 1 && this.closeList.size() < 1) {
                                this.wait(10000L);
                            }
                        }
                    }
                    catch (InterruptedException interruptedException) {
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                        AprEndpoint.this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.timeout.err"));
                    }
                }
                if (!this.pollerRunning) break;
                try {
                    int errn;
                    SocketInfo info;
                    Poller t = this;
                    synchronized (t) {
                        if (this.closeList.size() > 0) {
                            this.closeList.duplicate(localCloseList);
                            this.closeList.clear();
                        } else {
                            localCloseList.clear();
                        }
                    }
                    t = this;
                    synchronized (t) {
                        if (this.addList.size() > 0) {
                            this.addList.duplicate(localAddList);
                            this.addList.clear();
                        } else {
                            localAddList.clear();
                        }
                    }
                    if (localCloseList.size() > 0) {
                        info = localCloseList.get();
                        while (info != null) {
                            localAddList.remove(info.socket);
                            this.removeFromPoller(info.socket);
                            AprEndpoint.this.closeSocketInternal(info.socket);
                            AprEndpoint.this.destroySocketInternal(info.socket);
                            info = localCloseList.get();
                        }
                    }
                    if (localAddList.size() > 0) {
                        info = localAddList.get();
                        while (info != null) {
                            if (log.isDebugEnabled()) {
                                log.debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.pollerAddDo", new Object[]{info.socket}));
                            }
                            this.timeouts.remove(info.socket);
                            AprSocketWrapper wrapper = (AprSocketWrapper)AprEndpoint.this.connections.get(info.socket);
                            if (wrapper != null) {
                                if (info.read() || info.write()) {
                                    wrapper.pollerFlags = wrapper.pollerFlags | (info.read() ? 1 : 0) | (info.write() ? 4 : 0);
                                    this.removeFromPoller(info.socket);
                                    if (!this.addToPoller(info.socket, wrapper.pollerFlags)) {
                                        wrapper.close();
                                    } else {
                                        this.timeouts.add(info.socket, System.currentTimeMillis() + info.timeout);
                                    }
                                } else {
                                    wrapper.close();
                                    AprEndpoint.this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.apr.pollAddInvalid", new Object[]{info}));
                                }
                            }
                            info = localAddList.get();
                        }
                    }
                    boolean reset = false;
                    int rv = Poll.poll((long)this.aprPoller, (long)AprEndpoint.this.pollTime, (long[])this.desc, (boolean)true);
                    if (rv > 0) {
                        rv = this.mergeDescriptors(this.desc, rv);
                        this.connectionCount.addAndGet(-rv);
                        for (int n = 0; n < rv; ++n) {
                            if (AprEndpoint.this.getLog().isDebugEnabled()) {
                                log.debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.pollerProcess", new Object[]{this.desc[n * 2 + 1], this.desc[n * 2]}));
                            }
                            long timeout = this.timeouts.remove(this.desc[n * 2 + 1]);
                            AprSocketWrapper wrapper = (AprSocketWrapper)AprEndpoint.this.connections.get(this.desc[n * 2 + 1]);
                            if (wrapper == null) continue;
                            wrapper.pollerFlags = wrapper.pollerFlags & ~((int)this.desc[n * 2]);
                            if ((this.desc[n * 2] & 0x20L) == 32L || (this.desc[n * 2] & 0x10L) == 16L || (this.desc[n * 2] & 0x40L) == 64L) {
                                if ((this.desc[n * 2] & 1L) == 1L) {
                                    if (AprEndpoint.this.processSocket(this.desc[n * 2 + 1], SocketEvent.OPEN_READ)) continue;
                                    wrapper.close();
                                    continue;
                                }
                                if ((this.desc[n * 2] & 4L) == 4L) {
                                    if (AprEndpoint.this.processSocket(this.desc[n * 2 + 1], SocketEvent.OPEN_WRITE)) continue;
                                    wrapper.close();
                                    continue;
                                }
                                if ((wrapper.pollerFlags & 1) == 1) {
                                    if (AprEndpoint.this.processSocket(this.desc[n * 2 + 1], SocketEvent.OPEN_READ)) continue;
                                    wrapper.close();
                                    continue;
                                }
                                if ((wrapper.pollerFlags & 4) == 4) {
                                    if (AprEndpoint.this.processSocket(this.desc[n * 2 + 1], SocketEvent.OPEN_WRITE)) continue;
                                    wrapper.close();
                                    continue;
                                }
                                wrapper.close();
                                continue;
                            }
                            if ((this.desc[n * 2] & 1L) == 1L || (this.desc[n * 2] & 4L) == 4L) {
                                boolean error = false;
                                if ((this.desc[n * 2] & 1L) == 1L && !AprEndpoint.this.processSocket(this.desc[n * 2 + 1], SocketEvent.OPEN_READ)) {
                                    error = true;
                                    wrapper.close();
                                }
                                if (!error && (this.desc[n * 2] & 4L) == 4L && !AprEndpoint.this.processSocket(this.desc[n * 2 + 1], SocketEvent.OPEN_WRITE)) {
                                    error = true;
                                    wrapper.close();
                                }
                                if (error || wrapper.pollerFlags == 0) continue;
                                if (timeout > 0L) {
                                    timeout -= System.currentTimeMillis();
                                }
                                if (timeout <= 0L) {
                                    timeout = 1L;
                                }
                                if (timeout > Integer.MAX_VALUE) {
                                    timeout = Integer.MAX_VALUE;
                                }
                                this.add(this.desc[n * 2 + 1], (int)timeout, wrapper.pollerFlags);
                                continue;
                            }
                            AprEndpoint.this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.apr.pollUnknownEvent", new Object[]{this.desc[n * 2]}));
                            wrapper.close();
                        }
                    } else if (rv < 0 && (errn = -rv) != 120001 && errn != 120003) {
                        if (errn > 120000) {
                            errn -= 120000;
                        }
                        AprEndpoint.this.getLog().error((Object)AbstractEndpoint.sm.getString("endpoint.apr.pollError", new Object[]{errn, Error.strerror((int)errn)}));
                        reset = true;
                    }
                    if (reset && this.pollerRunning) {
                        int count = Poll.pollset((long)this.aprPoller, (long[])this.desc);
                        long newPoller = AprEndpoint.this.allocatePoller(this.pollerSize, this.pool, -1);
                        this.connectionCount.addAndGet(-count);
                        Poll.destroy((long)this.aprPoller);
                        this.aprPoller = newPoller;
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    AprEndpoint.this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.poll.error"), t);
                }
                try {
                    if (AprEndpoint.this.getConnectionTimeout() <= 0 || !this.pollerRunning) continue;
                    this.maintain();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    AprEndpoint.this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.timeout.err"), t);
                }
            }
            Poller poller = this;
            synchronized (poller) {
                this.notifyAll();
            }
        }

        private int mergeDescriptors(long[] desc, int startCount) {
            HashMap<Long, Long> merged = new HashMap<Long, Long>(startCount);
            for (int n = 0; n < startCount; ++n) {
                Long newValue = merged.merge(desc[2 * n + 1], desc[2 * n], (v1, v2) -> v1 | v2);
                if (!log.isDebugEnabled() || newValue == desc[2 * n]) continue;
                log.debug((Object)AbstractEndpoint.sm.getString("endpoint.apr.pollMergeEvents", new Object[]{desc[2 * n + 1], desc[2 * n], newValue}));
            }
            int i = 0;
            for (Map.Entry entry : merged.entrySet()) {
                desc[i++] = (Long)entry.getValue();
                desc[i++] = (Long)entry.getKey();
            }
            return merged.size();
        }
    }

    public class Sendfile
    implements Runnable {
        protected long sendfilePollset = 0L;
        protected long pool = 0L;
        protected long[] desc;
        protected HashMap<Long, SendfileData> sendfileData;
        protected int sendfileCount;
        protected ArrayList<SendfileData> addS;
        private volatile Thread sendfileThread;
        private volatile boolean sendfileRunning = true;

        public int getSendfileCount() {
            return this.sendfileCount;
        }

        protected void init() {
            this.pool = Pool.create((long)AprEndpoint.this.serverSockPool);
            int size = AprEndpoint.this.sendfileSize;
            if (size <= 0) {
                size = 16384;
            }
            this.sendfilePollset = AprEndpoint.this.allocatePoller(size, this.pool, AprEndpoint.this.getConnectionTimeout());
            this.desc = new long[size * 2];
            this.sendfileData = new HashMap(size);
            this.addS = new ArrayList();
        }

        protected void start() {
            this.sendfileThread = new Thread((Runnable)AprEndpoint.this.sendfile, AprEndpoint.this.getName() + "-Sendfile");
            this.sendfileThread.setPriority(AprEndpoint.this.threadPriority);
            this.sendfileThread.setDaemon(true);
            this.sendfileThread.start();
        }

        protected synchronized void stop() {
            this.sendfileRunning = false;
            this.notify();
        }

        protected void destroy() {
            for (int i = this.addS.size() - 1; i >= 0; --i) {
                SendfileData data = this.addS.get(i);
                AprEndpoint.this.closeSocketInternal(data.socket);
            }
            int rv = Poll.pollset((long)this.sendfilePollset, (long[])this.desc);
            if (rv > 0) {
                for (int n = 0; n < rv; ++n) {
                    AprEndpoint.this.closeSocketInternal(this.desc[n * 2 + 1]);
                }
            }
            Pool.destroy((long)this.pool);
            this.sendfileData.clear();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public SendfileState add(SendfileData data) {
            SocketWrapperBase socketWrapper = (SocketWrapperBase)AprEndpoint.this.connections.get(data.socket);
            ReentrantReadWriteLock.WriteLock wl = ((AprSocketWrapper)socketWrapper).getBlockingStatusWriteLock();
            wl.lock();
            try {
                data.fdpool = Socket.pool((long)data.socket);
                data.fd = File.open((String)data.fileName, (int)4129, (int)0, (long)data.fdpool);
                Socket.timeoutSet((long)data.socket, (long)0L);
                while (this.sendfileRunning) {
                    long nw = Socket.sendfilen((long)data.socket, (long)data.fd, (long)data.pos, (long)data.length, (int)0);
                    if (nw < 0L) {
                        if (-nw != 120002L) {
                            Pool.destroy((long)data.fdpool);
                            data.socket = 0L;
                            SendfileState sendfileState = SendfileState.ERROR;
                            return sendfileState;
                        }
                        break;
                    }
                    data.pos += nw;
                    data.length -= nw;
                    if (data.length != 0L) continue;
                    Pool.destroy((long)data.fdpool);
                    Socket.timeoutSet((long)data.socket, (long)(AprEndpoint.this.getConnectionTimeout() * 1000));
                    SendfileState sendfileState = SendfileState.DONE;
                    return sendfileState;
                }
            }
            catch (Exception e) {
                log.warn((Object)AbstractEndpoint.sm.getString("endpoint.sendfile.error"), (Throwable)e);
                SendfileState sendfileState = SendfileState.ERROR;
                return sendfileState;
            }
            finally {
                wl.unlock();
            }
            Sendfile sendfile = this;
            synchronized (sendfile) {
                this.addS.add(data);
                this.notify();
            }
            return SendfileState.PENDING;
        }

        protected void remove(SendfileData data) {
            int rv = Poll.remove((long)this.sendfilePollset, (long)data.socket);
            if (rv == 0) {
                --this.sendfileCount;
            }
            this.sendfileData.remove(data.socket);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            long maintainTime = 0L;
            while (this.sendfileRunning) {
                while (this.sendfileRunning && this.sendfileCount < 1 && this.addS.size() < 1) {
                    maintainTime = 0L;
                    try {
                        Sendfile sendfile = this;
                        synchronized (sendfile) {
                            if (this.sendfileRunning && this.sendfileCount < 1 && this.addS.size() < 1) {
                                this.wait();
                            }
                        }
                    }
                    catch (InterruptedException interruptedException) {
                    }
                }
                if (!this.sendfileRunning) break;
                try {
                    int errn;
                    Object state;
                    int n;
                    if (this.addS.size() > 0) {
                        Sendfile sendfile = this;
                        synchronized (sendfile) {
                            for (int i = this.addS.size() - 1; i >= 0; --i) {
                                SendfileData data = this.addS.get(i);
                                int rv = Poll.add((long)this.sendfilePollset, (long)data.socket, (int)4);
                                if (rv == 0) {
                                    this.sendfileData.put(data.socket, data);
                                    ++this.sendfileCount;
                                    continue;
                                }
                                AprEndpoint.this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.sendfile.addfail", new Object[]{rv, Error.strerror((int)rv)}));
                                AprEndpoint.this.closeSocketInternal(data.socket);
                            }
                            this.addS.clear();
                        }
                    }
                    maintainTime += (long)AprEndpoint.this.pollTime;
                    int rv = Poll.poll((long)this.sendfilePollset, (long)AprEndpoint.this.pollTime, (long[])this.desc, (boolean)false);
                    if (rv > 0) {
                        block24: for (n = 0; n < rv; ++n) {
                            state = this.sendfileData.get(this.desc[n * 2 + 1]);
                            if ((this.desc[n * 2] & 0x20L) == 32L || (this.desc[n * 2] & 0x10L) == 16L) {
                                this.remove((SendfileData)state);
                                AprEndpoint.this.closeSocketInternal(((SendfileData)state).socket);
                                continue;
                            }
                            long nw = Socket.sendfilen((long)((SendfileData)state).socket, (long)((SendfileData)state).fd, (long)((SendfileData)state).pos, (long)((SendfileData)state).length, (int)0);
                            if (nw < 0L) {
                                this.remove((SendfileData)state);
                                AprEndpoint.this.closeSocketInternal(((SendfileData)state).socket);
                                continue;
                            }
                            ((SendfileData)state).pos += nw;
                            ((SendfileData)state).length -= nw;
                            if (((SendfileData)state).length != 0L) continue;
                            this.remove((SendfileData)state);
                            switch (((SendfileData)state).keepAliveState) {
                                case NONE: {
                                    AprEndpoint.this.closeSocketInternal(((SendfileData)state).socket);
                                    continue block24;
                                }
                                case PIPELINED: {
                                    Pool.destroy((long)((SendfileData)state).fdpool);
                                    Socket.timeoutSet((long)((SendfileData)state).socket, (long)(AprEndpoint.this.getConnectionTimeout() * 1000));
                                    if (AprEndpoint.this.processSocket(((SendfileData)state).socket, SocketEvent.OPEN_READ)) continue block24;
                                    AprEndpoint.this.closeSocketInternal(((SendfileData)state).socket);
                                    continue block24;
                                }
                                case OPEN: {
                                    Pool.destroy((long)((SendfileData)state).fdpool);
                                    Socket.timeoutSet((long)((SendfileData)state).socket, (long)(AprEndpoint.this.getConnectionTimeout() * 1000));
                                    AprEndpoint.this.getPoller().add(((SendfileData)state).socket, AprEndpoint.this.getKeepAliveTimeout(), 1);
                                }
                            }
                        }
                    } else if (rv < 0 && (errn = -rv) != 120001 && errn != 120003) {
                        if (errn > 120000) {
                            errn -= 120000;
                        }
                        AprEndpoint.this.getLog().error((Object)AbstractEndpoint.sm.getString("endpoint.apr.pollError", new Object[]{errn, Error.strerror((int)errn)}));
                        state = this;
                        synchronized (state) {
                            this.destroy();
                            this.init();
                            continue;
                        }
                    }
                    if (AprEndpoint.this.getConnectionTimeout() <= 0 || maintainTime <= 1000000L || !this.sendfileRunning) continue;
                    rv = Poll.maintain((long)this.sendfilePollset, (long[])this.desc, (boolean)false);
                    maintainTime = 0L;
                    if (rv <= 0) continue;
                    for (n = 0; n < rv; ++n) {
                        state = this.sendfileData.get(this.desc[n]);
                        this.remove((SendfileData)state);
                        AprEndpoint.this.closeSocketInternal(((SendfileData)state).socket);
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    AprEndpoint.this.getLog().error((Object)AbstractEndpoint.sm.getString("endpoint.poll.error"), t);
                }
            }
            Sendfile sendfile = this;
            synchronized (sendfile) {
                this.notifyAll();
            }
        }
    }

    public static class AprSocketWrapper
    extends SocketWrapperBase<Long> {
        private static final int SSL_OUTPUT_BUFFER_SIZE = 8192;
        private final ByteBuffer sslOutputBuffer;
        private int pollerFlags = 0;
        private volatile boolean blockingStatus = true;
        private final Lock blockingStatusReadLock;
        private final ReentrantReadWriteLock.WriteLock blockingStatusWriteLock;

        public AprSocketWrapper(Long socket, AprEndpoint endpoint) {
            super(socket, endpoint);
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            this.blockingStatusReadLock = lock.readLock();
            this.blockingStatusWriteLock = lock.writeLock();
            if (endpoint.isSSLEnabled()) {
                this.sslOutputBuffer = ByteBuffer.allocateDirect(8192);
                this.sslOutputBuffer.position(8192);
            } else {
                this.sslOutputBuffer = null;
            }
            this.socketBufferHandler = new SocketBufferHandler(9000, 9000, true);
        }

        public boolean getBlockingStatus() {
            return this.blockingStatus;
        }

        public void setBlockingStatus(boolean blockingStatus) {
            this.blockingStatus = blockingStatus;
        }

        public Lock getBlockingStatusReadLock() {
            return this.blockingStatusReadLock;
        }

        public ReentrantReadWriteLock.WriteLock getBlockingStatusWriteLock() {
            return this.blockingStatusWriteLock;
        }

        @Override
        public int read(boolean block, byte[] b, int off, int len) throws IOException {
            int nRead = this.populateReadBuffer(b, off, len);
            if (nRead > 0) {
                return nRead;
            }
            nRead = this.fillReadBuffer(block);
            if (nRead > 0) {
                this.socketBufferHandler.configureReadBufferForRead();
                nRead = Math.min(nRead, len);
                this.socketBufferHandler.getReadBuffer().get(b, off, nRead);
            }
            return nRead;
        }

        @Override
        public int read(boolean block, ByteBuffer to) throws IOException {
            int nRead = this.populateReadBuffer(to);
            if (nRead > 0) {
                return nRead;
            }
            int limit = this.socketBufferHandler.getReadBuffer().capacity();
            if (to.isDirect() && to.remaining() >= limit) {
                to.limit(to.position() + limit);
                nRead = this.fillReadBuffer(block, to);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Socket: [" + this + "], Read direct from socket: [" + nRead + "]"));
                }
            } else {
                nRead = this.fillReadBuffer(block);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Socket: [" + this + "], Read into buffer: [" + nRead + "]"));
                }
                if (nRead > 0) {
                    nRead = this.populateReadBuffer(to);
                }
            }
            return nRead;
        }

        private int fillReadBuffer(boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return this.fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private int fillReadBuffer(boolean block, ByteBuffer to) throws IOException {
            Lock readLock = this.getBlockingStatusReadLock();
            ReentrantReadWriteLock.WriteLock writeLock = this.getBlockingStatusWriteLock();
            boolean readDone = false;
            int result = 0;
            readLock.lock();
            try {
                this.checkClosed();
                if (this.getBlockingStatus() == block) {
                    if (block) {
                        Socket.timeoutSet((long)((Long)this.getSocket()), (long)(this.getReadTimeout() * 1000L));
                    }
                    result = Socket.recvb((long)((Long)this.getSocket()), (ByteBuffer)to, (int)to.position(), (int)to.remaining());
                    readDone = true;
                }
            }
            finally {
                readLock.unlock();
            }
            if (!readDone) {
                writeLock.lock();
                try {
                    this.checkClosed();
                    this.setBlockingStatus(block);
                    if (block) {
                        Socket.timeoutSet((long)((Long)this.getSocket()), (long)(this.getReadTimeout() * 1000L));
                    } else {
                        Socket.timeoutSet((long)((Long)this.getSocket()), (long)0L);
                    }
                    readLock.lock();
                    try {
                        writeLock.unlock();
                        result = Socket.recvb((long)((Long)this.getSocket()), (ByteBuffer)to, (int)to.position(), (int)to.remaining());
                    }
                    finally {
                        readLock.unlock();
                    }
                }
                finally {
                    if (writeLock.isHeldByCurrentThread()) {
                        writeLock.unlock();
                    }
                }
            }
            if (result > 0) {
                to.position(to.position() + result);
                return result;
            }
            if (result == 0 || -result == 120002) {
                return 0;
            }
            if (-result == 120005 || -result == 120001) {
                if (block) {
                    throw new SocketTimeoutException(sm.getString("iib.readtimeout"));
                }
                return 0;
            }
            if (-result == 70014) {
                return -1;
            }
            if ((OS.IS_WIN32 || OS.IS_WIN64) && -result == 730053) {
                throw new EOFException(sm.getString("socket.apr.clientAbort"));
            }
            throw new IOException(sm.getString("socket.apr.read.error", new Object[]{-result, this.getSocket(), this}));
        }

        @Override
        public boolean isReadyForRead() throws IOException {
            this.socketBufferHandler.configureReadBufferForRead();
            if (this.socketBufferHandler.getReadBuffer().remaining() > 0) {
                return true;
            }
            int read = this.fillReadBuffer(false);
            boolean isReady = this.socketBufferHandler.getReadBuffer().position() > 0 || read == -1;
            return isReady;
        }

        private void checkClosed() throws IOException {
            if (this.isClosed()) {
                throw new IOException(sm.getString("socket.apr.closed", new Object[]{this.getSocket()}));
            }
        }

        @Override
        protected void doClose() {
            Poller poller;
            if (log.isDebugEnabled()) {
                log.debug((Object)("Calling [" + this.getEndpoint() + "].closeSocket([" + this + "])"));
            }
            this.getEndpoint().connections.remove(this.getSocket());
            this.socketBufferHandler.free();
            this.socketBufferHandler = SocketBufferHandler.EMPTY;
            this.nonBlockingWriteBuffer.clear();
            if (this.sslOutputBuffer != null) {
                ByteBufferUtils.cleanDirectBuffer((ByteBuffer)this.sslOutputBuffer);
            }
            if ((poller = ((AprEndpoint)this.getEndpoint()).getPoller()) != null) {
                poller.close((Long)this.getSocket());
            }
        }

        @Override
        protected boolean flushNonBlocking() throws IOException {
            boolean dataLeft;
            boolean bl = dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
            if (dataLeft) {
                this.doWrite(false);
                boolean bl2 = dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
            }
            if (!(dataLeft || this.nonBlockingWriteBuffer.isEmpty() || (dataLeft = this.nonBlockingWriteBuffer.write(this, false)) || this.socketBufferHandler.isWriteBufferEmpty())) {
                this.doWrite(false);
                dataLeft = !this.socketBufferHandler.isWriteBufferEmpty();
            }
            return dataLeft;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void doWrite(boolean block, ByteBuffer from) throws IOException {
            Lock readLock = this.getBlockingStatusReadLock();
            ReentrantReadWriteLock.WriteLock writeLock = this.getBlockingStatusWriteLock();
            readLock.lock();
            try {
                this.checkClosed();
                if (this.getBlockingStatus() == block) {
                    if (block) {
                        Socket.timeoutSet((long)((Long)this.getSocket()), (long)(this.getWriteTimeout() * 1000L));
                    }
                    this.doWriteInternal(from);
                    return;
                }
            }
            finally {
                readLock.unlock();
            }
            writeLock.lock();
            try {
                this.checkClosed();
                this.setBlockingStatus(block);
                if (block) {
                    Socket.timeoutSet((long)((Long)this.getSocket()), (long)(this.getWriteTimeout() * 1000L));
                } else {
                    Socket.timeoutSet((long)((Long)this.getSocket()), (long)0L);
                }
                readLock.lock();
                try {
                    writeLock.unlock();
                    this.doWriteInternal(from);
                }
                finally {
                    readLock.unlock();
                }
            }
            finally {
                if (writeLock.isHeldByCurrentThread()) {
                    writeLock.unlock();
                }
            }
        }

        private void doWriteInternal(ByteBuffer from) throws IOException {
            int thisTime;
            if (this.previousIOException != null) {
                throw new IOException(this.previousIOException);
            }
            do {
                thisTime = 0;
                if (this.getEndpoint().isSSLEnabled()) {
                    if (this.sslOutputBuffer.remaining() == 0) {
                        this.sslOutputBuffer.clear();
                        AprSocketWrapper.transfer(from, this.sslOutputBuffer);
                        this.sslOutputBuffer.flip();
                    }
                    if ((thisTime = Socket.sendb((long)((Long)this.getSocket()), (ByteBuffer)this.sslOutputBuffer, (int)this.sslOutputBuffer.position(), (int)this.sslOutputBuffer.limit())) > 0) {
                        this.sslOutputBuffer.position(this.sslOutputBuffer.position() + thisTime);
                    }
                } else {
                    thisTime = Socket.sendb((long)((Long)this.getSocket()), (ByteBuffer)from, (int)from.position(), (int)from.remaining());
                    if (thisTime > 0) {
                        from.position(from.position() + thisTime);
                    }
                }
                if (Status.APR_STATUS_IS_EAGAIN((int)(-thisTime))) {
                    thisTime = 0;
                    continue;
                }
                if (-thisTime == 70014) {
                    throw new EOFException(sm.getString("socket.apr.clientAbort"));
                }
                if ((OS.IS_WIN32 || OS.IS_WIN64) && -thisTime == 730053) {
                    throw new EOFException(sm.getString("socket.apr.clientAbort"));
                }
                if (thisTime >= 0) continue;
                this.previousIOException = new IOException(sm.getString("socket.apr.write.error", new Object[]{-thisTime, this.getSocket(), this}));
                throw this.previousIOException;
            } while ((thisTime > 0 || this.getBlockingStatus()) && from.hasRemaining());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void registerReadInterest() {
            AtomicBoolean atomicBoolean = this.closed;
            synchronized (atomicBoolean) {
                Poller p;
                if (this.isClosed()) {
                    return;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("endpoint.debug.registerRead", new Object[]{this}));
                }
                if ((p = ((AprEndpoint)this.getEndpoint()).getPoller()) != null) {
                    p.add((Long)this.getSocket(), this.getReadTimeout(), 1);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void registerWriteInterest() {
            AtomicBoolean atomicBoolean = this.closed;
            synchronized (atomicBoolean) {
                if (this.isClosed()) {
                    return;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("endpoint.debug.registerWrite", new Object[]{this}));
                }
                ((AprEndpoint)this.getEndpoint()).getPoller().add((Long)this.getSocket(), this.getWriteTimeout(), 4);
            }
        }

        @Override
        public SendfileDataBase createSendfileData(String filename, long pos, long length) {
            return new SendfileData(filename, pos, length);
        }

        @Override
        public SendfileState processSendfile(SendfileDataBase sendfileData) {
            ((SendfileData)sendfileData).socket = (Long)this.getSocket();
            return ((AprEndpoint)this.getEndpoint()).getSendfile().add((SendfileData)sendfileData);
        }

        @Override
        protected void populateRemoteAddr() {
            if (this.isClosed()) {
                return;
            }
            try {
                long socket = (Long)this.getSocket();
                long sa = Address.get((int)1, (long)socket);
                this.remoteAddr = Address.getip((long)sa);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("endpoint.warn.noRemoteAddr", new Object[]{this.getSocket()}), (Throwable)e);
            }
        }

        @Override
        protected void populateRemoteHost() {
            if (this.isClosed()) {
                return;
            }
            try {
                long socket = (Long)this.getSocket();
                long sa = Address.get((int)1, (long)socket);
                this.remoteHost = Address.getnameinfo((long)sa, (int)0);
                if (this.remoteAddr == null) {
                    this.remoteAddr = Address.getip((long)sa);
                }
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("endpoint.warn.noRemoteHost", new Object[]{this.getSocket()}), (Throwable)e);
            }
        }

        @Override
        protected void populateRemotePort() {
            if (this.isClosed()) {
                return;
            }
            try {
                long socket = (Long)this.getSocket();
                long sa = Address.get((int)1, (long)socket);
                Sockaddr addr = Address.getInfo((long)sa);
                this.remotePort = addr.port;
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("endpoint.warn.noRemotePort", new Object[]{this.getSocket()}), (Throwable)e);
            }
        }

        @Override
        protected void populateLocalName() {
            if (this.isClosed()) {
                return;
            }
            try {
                long socket = (Long)this.getSocket();
                long sa = Address.get((int)0, (long)socket);
                this.localName = Address.getnameinfo((long)sa, (int)0);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("endpoint.warn.noLocalName"), (Throwable)e);
            }
        }

        @Override
        protected void populateLocalAddr() {
            if (this.isClosed()) {
                return;
            }
            try {
                long socket = (Long)this.getSocket();
                long sa = Address.get((int)0, (long)socket);
                this.localAddr = Address.getip((long)sa);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("endpoint.warn.noLocalAddr"), (Throwable)e);
            }
        }

        @Override
        protected void populateLocalPort() {
            if (this.isClosed()) {
                return;
            }
            try {
                long socket = (Long)this.getSocket();
                long sa = Address.get((int)0, (long)socket);
                Sockaddr addr = Address.getInfo((long)sa);
                this.localPort = addr.port;
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("endpoint.warn.noLocalPort"), (Throwable)e);
            }
        }

        @Override
        public SSLSupport getSslSupport(String clientCertProvider) {
            if (this.getEndpoint().isSSLEnabled()) {
                return new AprSSLSupport(this, clientCertProvider);
            }
            return null;
        }

        @Override
        public SSLSupport getSslSupport() {
            throw new UnsupportedOperationException();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void doClientAuth(SSLSupport sslSupport) throws IOException {
            block14: {
                long socket = (Long)this.getSocket();
                Lock readLock = this.getBlockingStatusReadLock();
                ReentrantReadWriteLock.WriteLock writeLock = this.getBlockingStatusWriteLock();
                boolean renegotiateDone = false;
                try {
                    readLock.lock();
                    try {
                        if (this.getBlockingStatus()) {
                            Socket.timeoutSet((long)((Long)this.getSocket()), (long)(this.getReadTimeout() * 1000L));
                            SSLSocket.setVerify((long)socket, (int)2, (int)-1);
                            SSLSocket.renegotiate((long)socket);
                            renegotiateDone = true;
                        }
                    }
                    finally {
                        readLock.unlock();
                    }
                    if (renegotiateDone) break block14;
                    writeLock.lock();
                    try {
                        this.setBlockingStatus(true);
                        Socket.timeoutSet((long)((Long)this.getSocket()), (long)(this.getReadTimeout() * 1000L));
                        readLock.lock();
                        try {
                            writeLock.unlock();
                            SSLSocket.setVerify((long)socket, (int)2, (int)-1);
                            SSLSocket.renegotiate((long)socket);
                        }
                        finally {
                            readLock.unlock();
                        }
                    }
                    finally {
                        if (writeLock.isHeldByCurrentThread()) {
                            writeLock.unlock();
                        }
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    throw new IOException(sm.getString("socket.sslreneg"), t);
                }
            }
        }

        @Override
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        }

        String getSSLInfoS(int id) {
            AtomicBoolean atomicBoolean = this.closed;
            synchronized (atomicBoolean) {
                if (this.isClosed()) {
                    return null;
                }
                try {
                    return SSLSocket.getInfoS((long)((Long)this.getSocket()), (int)id);
                }
                catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        int getSSLInfoI(int id) {
            AtomicBoolean atomicBoolean = this.closed;
            synchronized (atomicBoolean) {
                if (this.isClosed()) {
                    return 0;
                }
                try {
                    return SSLSocket.getInfoI((long)((Long)this.getSocket()), (int)id);
                }
                catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        byte[] getSSLInfoB(int id) {
            AtomicBoolean atomicBoolean = this.closed;
            synchronized (atomicBoolean) {
                if (this.isClosed()) {
                    return null;
                }
                try {
                    return SSLSocket.getInfoB((long)((Long)this.getSocket()), (int)id);
                }
                catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        @Override
        protected <A> SocketWrapperBase.OperationState<A> newOperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase.VectoredIOCompletionHandler<A> completion) {
            return new AprOperationState(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
        }

        private class AprOperationState<A>
        extends SocketWrapperBase.OperationState<A> {
            private volatile boolean inline;
            private volatile long flushBytes;

            private AprOperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase.VectoredIOCompletionHandler<A> completion) {
                super(AprSocketWrapper.this, read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
                this.inline = true;
                this.flushBytes = 0L;
            }

            @Override
            protected boolean isInline() {
                return this.inline;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                long nBytes = 0L;
                if (AprSocketWrapper.this.getError() == null) {
                    try {
                        AprOperationState aprOperationState = this;
                        synchronized (aprOperationState) {
                            if (!this.completionDone) {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)("Skip concurrent " + (this.read ? "read" : "write") + " notification"));
                                }
                                return;
                            }
                            ByteBuffer buffer = null;
                            for (int i = 0; i < this.length; ++i) {
                                if (!this.buffers[i + this.offset].hasRemaining()) continue;
                                buffer = this.buffers[i + this.offset];
                                break;
                            }
                            if (buffer == null && this.flushBytes == 0L) {
                                this.completion.completed(0L, this);
                                return;
                            }
                            if (this.read) {
                                nBytes = AprSocketWrapper.this.read(false, buffer);
                            } else if (!AprSocketWrapper.this.flush(this.block == SocketWrapperBase.BlockingMode.BLOCK)) {
                                if (this.flushBytes > 0L) {
                                    nBytes = this.flushBytes;
                                    this.flushBytes = 0L;
                                } else {
                                    int remaining = buffer.remaining();
                                    AprSocketWrapper.this.write(this.block == SocketWrapperBase.BlockingMode.BLOCK, buffer);
                                    nBytes = remaining - buffer.remaining();
                                    if (nBytes > 0L && AprSocketWrapper.this.flush(this.block == SocketWrapperBase.BlockingMode.BLOCK)) {
                                        this.inline = false;
                                        AprSocketWrapper.this.registerWriteInterest();
                                        this.flushBytes = nBytes;
                                        return;
                                    }
                                }
                            } else {
                                this.inline = false;
                                AprSocketWrapper.this.registerWriteInterest();
                                return;
                            }
                            if (nBytes != 0L) {
                                this.completionDone = false;
                            }
                        }
                    }
                    catch (IOException e) {
                        AprSocketWrapper.this.setError(e);
                    }
                }
                if (nBytes > 0L) {
                    this.completion.completed(nBytes, this);
                } else if (nBytes < 0L || AprSocketWrapper.this.getError() != null) {
                    IOException error = AprSocketWrapper.this.getError();
                    if (error == null) {
                        error = new EOFException();
                    }
                    this.completion.failed((Throwable)error, this);
                } else {
                    this.inline = false;
                    if (this.read) {
                        AprSocketWrapper.this.registerReadInterest();
                    } else {
                        AprSocketWrapper.this.registerWriteInterest();
                    }
                }
            }
        }
    }

    protected class SocketWithOptionsProcessor
    implements Runnable {
        protected SocketWrapperBase<Long> socket = null;

        public SocketWithOptionsProcessor(SocketWrapperBase<Long> socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            Lock lock = this.socket.getLock();
            lock.lock();
            try {
                if (!AprEndpoint.this.deferAccept) {
                    if (AprEndpoint.this.setSocketOptions(this.socket)) {
                        AprEndpoint.this.getPoller().add(this.socket.getSocket(), AprEndpoint.this.getConnectionTimeout(), 1);
                    } else {
                        AprEndpoint.this.getHandler().process(this.socket, SocketEvent.CONNECT_FAIL);
                        this.socket.close();
                        this.socket = null;
                    }
                } else {
                    if (!AprEndpoint.this.setSocketOptions(this.socket)) {
                        AprEndpoint.this.getHandler().process(this.socket, SocketEvent.CONNECT_FAIL);
                        this.socket.close();
                        this.socket = null;
                        return;
                    }
                    AbstractEndpoint.Handler.SocketState state = AprEndpoint.this.getHandler().process(this.socket, SocketEvent.OPEN_READ);
                    if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        this.socket.close();
                        this.socket = null;
                    }
                }
            }
            finally {
                lock.unlock();
            }
        }
    }

    protected class SocketProcessor
    extends SocketProcessorBase<Long> {
        public SocketProcessor(SocketWrapperBase<Long> socketWrapper, SocketEvent event) {
            super(socketWrapper, event);
        }

        @Override
        protected void doRun() {
            try {
                AbstractEndpoint.Handler.SocketState state = AprEndpoint.this.getHandler().process(this.socketWrapper, this.event);
                if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                    this.socketWrapper.close();
                }
            }
            finally {
                this.socketWrapper = null;
                this.event = null;
                if (AprEndpoint.this.running && AprEndpoint.this.processorCache != null) {
                    AprEndpoint.this.processorCache.push((Object)this);
                }
            }
        }
    }

    public static class SendfileData
    extends SendfileDataBase {
        protected long fd;
        protected long fdpool;
        protected long socket;

        public SendfileData(String filename, long pos, long length) {
            super(filename, pos, length);
        }
    }

    public static class SocketList {
        protected volatile int size = 0;
        protected int pos = 0;
        protected long[] sockets;
        protected long[] timeouts;
        protected int[] flags;
        protected SocketInfo info = new SocketInfo();

        public SocketList(int size) {
            this.sockets = new long[size];
            this.timeouts = new long[size];
            this.flags = new int[size];
        }

        public int size() {
            return this.size;
        }

        public SocketInfo get() {
            if (this.pos == this.size) {
                return null;
            }
            this.info.socket = this.sockets[this.pos];
            this.info.timeout = this.timeouts[this.pos];
            this.info.flags = this.flags[this.pos];
            ++this.pos;
            return this.info;
        }

        public void clear() {
            this.size = 0;
            this.pos = 0;
        }

        public boolean add(long socket, long timeout, int flag) {
            if (this.size == this.sockets.length) {
                return false;
            }
            for (int i = 0; i < this.size; ++i) {
                if (this.sockets[i] != socket) continue;
                this.flags[i] = SocketInfo.merge(this.flags[i], flag);
                return true;
            }
            this.sockets[this.size] = socket;
            this.timeouts[this.size] = timeout;
            this.flags[this.size] = flag;
            ++this.size;
            return true;
        }

        public boolean remove(long socket) {
            for (int i = 0; i < this.size; ++i) {
                if (this.sockets[i] != socket) continue;
                this.sockets[i] = this.sockets[this.size - 1];
                this.timeouts[i] = this.timeouts[this.size - 1];
                this.flags[this.size] = this.flags[this.size - 1];
                --this.size;
                return true;
            }
            return false;
        }

        public void duplicate(SocketList copy) {
            copy.size = this.size;
            copy.pos = this.pos;
            System.arraycopy(this.sockets, 0, copy.sockets, 0, this.size);
            System.arraycopy(this.timeouts, 0, copy.timeouts, 0, this.size);
            System.arraycopy(this.flags, 0, copy.flags, 0, this.size);
        }
    }

    public static class SocketTimeouts {
        protected int size = 0;
        protected long[] sockets;
        protected long[] timeouts;
        protected int pos = 0;

        public SocketTimeouts(int size) {
            this.sockets = new long[size];
            this.timeouts = new long[size];
        }

        public void add(long socket, long timeout) {
            this.sockets[this.size] = socket;
            this.timeouts[this.size] = timeout;
            ++this.size;
        }

        public long remove(long socket) {
            long result = 0L;
            for (int i = 0; i < this.size; ++i) {
                if (this.sockets[i] != socket) continue;
                result = this.timeouts[i];
                this.sockets[i] = this.sockets[this.size - 1];
                this.timeouts[i] = this.timeouts[this.size - 1];
                --this.size;
                break;
            }
            return result;
        }

        public long check(long date) {
            while (this.pos < this.size) {
                if (date >= this.timeouts[this.pos]) {
                    long result = this.sockets[this.pos];
                    this.sockets[this.pos] = this.sockets[this.size - 1];
                    this.timeouts[this.pos] = this.timeouts[this.size - 1];
                    --this.size;
                    return result;
                }
                ++this.pos;
            }
            this.pos = 0;
            return 0L;
        }
    }

    public static class SocketInfo {
        public long socket;
        public long timeout;
        public int flags;

        public boolean read() {
            return (this.flags & 1) == 1;
        }

        public boolean write() {
            return (this.flags & 4) == 4;
        }

        public static int merge(int flag1, int flag2) {
            return flag1 & 1 | flag2 & 1 | (flag1 & 4 | flag2 & 4);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Socket: [");
            sb.append(this.socket);
            sb.append("], timeout: [");
            sb.append(this.timeout);
            sb.append("], flags: [");
            sb.append(this.flags);
            return sb.toString();
        }
    }
}

