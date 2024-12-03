/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.threads.LimitLatch
 *  org.apache.tomcat.util.threads.ResizableExecutor
 *  org.apache.tomcat.util.threads.TaskQueue
 *  org.apache.tomcat.util.threads.TaskThreadFactory
 *  org.apache.tomcat.util.threads.ThreadPoolExecutor
 *  org.apache.tomcat.util.threads.VirtualThreadExecutor
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.Acceptor;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketProcessorBase;
import org.apache.tomcat.util.net.SocketProperties;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.threads.LimitLatch;
import org.apache.tomcat.util.threads.ResizableExecutor;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;

public abstract class AbstractEndpoint<S, U> {
    protected static final StringManager sm = StringManager.getManager(AbstractEndpoint.class);
    protected volatile boolean running = false;
    protected volatile boolean paused = false;
    protected volatile boolean internalExecutor = true;
    private volatile LimitLatch connectionLimitLatch = null;
    protected final SocketProperties socketProperties = new SocketProperties();
    protected Acceptor<U> acceptor;
    protected SynchronizedStack<SocketProcessorBase<S>> processorCache;
    private ObjectName oname = null;
    protected Map<U, SocketWrapperBase<S>> connections = new ConcurrentHashMap<U, SocketWrapperBase<S>>();
    private String defaultSSLHostConfigName = "_default_";
    protected ConcurrentMap<String, SSLHostConfig> sslHostConfigs = new ConcurrentHashMap<String, SSLHostConfig>();
    private boolean useSendfile = true;
    private long executorTerminationTimeoutMillis = 5000L;
    @Deprecated
    protected int acceptorThreadCount = 1;
    protected int acceptorThreadPriority = 5;
    private int maxConnections = 8192;
    private Executor executor = null;
    private boolean useVirtualThreads = false;
    private ScheduledExecutorService utilityExecutor = null;
    private int port = -1;
    private int portOffset = 0;
    private InetAddress address;
    private int acceptCount = 100;
    private boolean bindOnInit = true;
    private volatile BindState bindState = BindState.UNBOUND;
    private Integer keepAliveTimeout = null;
    private boolean SSLEnabled = false;
    private int minSpareThreads = 10;
    private int maxThreads = 200;
    protected int threadPriority = 5;
    private int maxKeepAliveRequests = 100;
    private String name = "TP";
    private String domain;
    private boolean daemon = true;
    private boolean useAsyncIO = true;
    protected final List<String> negotiableProtocols = new ArrayList<String>();
    private Handler<S> handler = null;
    protected HashMap<String, Object> attributes = new HashMap();

    public static long toTimeout(long timeout) {
        return timeout > 0L ? timeout : Long.MAX_VALUE;
    }

    public SocketProperties getSocketProperties() {
        return this.socketProperties;
    }

    public Set<SocketWrapperBase<S>> getConnections() {
        return new HashSet<SocketWrapperBase<S>>(this.connections.values());
    }

    public String getDefaultSSLHostConfigName() {
        return this.defaultSSLHostConfigName;
    }

    public void setDefaultSSLHostConfigName(String defaultSSLHostConfigName) {
        this.defaultSSLHostConfigName = defaultSSLHostConfigName.toLowerCase(Locale.ENGLISH);
    }

    public void addSslHostConfig(SSLHostConfig sslHostConfig) throws IllegalArgumentException {
        this.addSslHostConfig(sslHostConfig, false);
    }

    public void addSslHostConfig(SSLHostConfig sslHostConfig, boolean replace) throws IllegalArgumentException {
        String key = sslHostConfig.getHostName();
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException(sm.getString("endpoint.noSslHostName"));
        }
        if (this.bindState != BindState.UNBOUND && this.bindState != BindState.SOCKET_CLOSED_ON_STOP && this.isSSLEnabled()) {
            try {
                this.createSSLContext(sslHostConfig);
            }
            catch (IllegalArgumentException e) {
                throw e;
            }
            catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (replace) {
            SSLHostConfig previous = this.sslHostConfigs.put(key, sslHostConfig);
            if (key.equals(this.getDefaultSSLHostConfigName())) {
                this.setDefaultSslHostConfig(sslHostConfig);
            }
            if (previous != null) {
                this.unregisterJmx(sslHostConfig);
            }
            this.registerJmx(sslHostConfig);
        } else {
            SSLHostConfig duplicate = this.sslHostConfigs.putIfAbsent(key, sslHostConfig);
            if (duplicate != null) {
                this.releaseSSLContext(sslHostConfig);
                throw new IllegalArgumentException(sm.getString("endpoint.duplicateSslHostName", new Object[]{key}));
            }
            this.registerJmx(sslHostConfig);
        }
    }

    public SSLHostConfig removeSslHostConfig(String hostName) {
        if (hostName == null) {
            return null;
        }
        String hostNameLower = hostName.toLowerCase(Locale.ENGLISH);
        if (hostNameLower.equals(this.getDefaultSSLHostConfigName())) {
            throw new IllegalArgumentException(sm.getString("endpoint.removeDefaultSslHostConfig", new Object[]{hostName}));
        }
        SSLHostConfig sslHostConfig = (SSLHostConfig)this.sslHostConfigs.remove(hostNameLower);
        this.unregisterJmx(sslHostConfig);
        return sslHostConfig;
    }

    public void reloadSslHostConfig(String hostName) {
        SSLHostConfig sslHostConfig = (SSLHostConfig)this.sslHostConfigs.get(hostName.toLowerCase(Locale.ENGLISH));
        if (sslHostConfig == null) {
            throw new IllegalArgumentException(sm.getString("endpoint.unknownSslHostName", new Object[]{hostName}));
        }
        this.addSslHostConfig(sslHostConfig, true);
    }

    public void reloadSslHostConfigs() {
        for (String hostName : this.sslHostConfigs.keySet()) {
            this.reloadSslHostConfig(hostName);
        }
    }

    public SSLHostConfig[] findSslHostConfigs() {
        return this.sslHostConfigs.values().toArray(new SSLHostConfig[0]);
    }

    protected abstract void createSSLContext(SSLHostConfig var1) throws Exception;

    protected abstract void setDefaultSslHostConfig(SSLHostConfig var1);

    protected void logCertificate(SSLHostConfigCertificate certificate) {
        String certificateInfo;
        SSLHostConfig sslHostConfig = certificate.getSSLHostConfig();
        if (certificate.getStoreType() == SSLHostConfigCertificate.StoreType.PEM) {
            certificateInfo = sm.getString("endpoint.tls.info.cert.pem", new Object[]{certificate.getCertificateKeyFile(), certificate.getCertificateFile(), certificate.getCertificateChainFile()});
        } else {
            String keyAlias = certificate.getCertificateKeyAlias();
            if (keyAlias == null) {
                keyAlias = "tomcat";
            }
            certificateInfo = sm.getString("endpoint.tls.info.cert.keystore", new Object[]{certificate.getCertificateKeystoreFile(), keyAlias});
        }
        String trustStoreSource = sslHostConfig.getTruststoreFile();
        if (trustStoreSource == null) {
            trustStoreSource = sslHostConfig.getCaCertificateFile();
        }
        if (trustStoreSource == null) {
            trustStoreSource = sslHostConfig.getCaCertificatePath();
        }
        this.getLogCertificate().info((Object)sm.getString("endpoint.tls.info", new Object[]{this.getName(), sslHostConfig.getHostName(), certificate.getType(), certificateInfo, trustStoreSource}));
        if (this.getLogCertificate().isDebugEnabled()) {
            X509Certificate[] x509Certificates;
            String alias = certificate.getCertificateKeyAlias();
            if (alias == null) {
                alias = "tomcat";
            }
            if ((x509Certificates = certificate.getSslContext().getCertificateChain(alias)) != null && x509Certificates.length > 0) {
                this.getLogCertificate().debug((Object)this.generateCertificateDebug(x509Certificates[0]));
            } else {
                this.getLogCertificate().debug((Object)sm.getString("endpoint.tls.cert.noCerts"));
            }
        }
    }

    protected String generateCertificateDebug(X509Certificate certificate) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[");
        try {
            byte[] certBytes = certificate.getEncoded();
            sb.append("\nSHA-256 fingerprint: ");
            MessageDigest sha512Digest = MessageDigest.getInstance("SHA-256");
            sha512Digest.update(certBytes);
            sb.append(HexUtils.toHexString((byte[])sha512Digest.digest()));
            sb.append("\nSHA-1 fingerprint: ");
            MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");
            sha1Digest.update(certBytes);
            sb.append(HexUtils.toHexString((byte[])sha1Digest.digest()));
        }
        catch (CertificateEncodingException e) {
            this.getLogCertificate().warn((Object)sm.getString("endpoint.tls.cert.encodingError"), (Throwable)e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        sb.append("\n");
        sb.append(certificate);
        sb.append("\n]");
        return sb.toString();
    }

    protected void destroySsl() throws Exception {
        if (this.isSSLEnabled()) {
            for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                this.releaseSSLContext(sslHostConfig);
            }
        }
    }

    protected void releaseSSLContext(SSLHostConfig sslHostConfig) {
        for (SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
            SSLContext sslContext;
            if (certificate.getSslContext() == null || (sslContext = certificate.getSslContext()) == null) continue;
            sslContext.destroy();
        }
    }

    protected SSLHostConfig getSSLHostConfig(String sniHostName) {
        SSLHostConfig result = null;
        if (sniHostName != null) {
            result = (SSLHostConfig)this.sslHostConfigs.get(sniHostName);
            if (result != null) {
                return result;
            }
            int indexOfDot = sniHostName.indexOf(46);
            if (indexOfDot > -1) {
                result = (SSLHostConfig)this.sslHostConfigs.get("*" + sniHostName.substring(indexOfDot));
            }
        }
        if (result == null) {
            result = (SSLHostConfig)this.sslHostConfigs.get(this.getDefaultSSLHostConfigName());
        }
        if (result == null) {
            throw new IllegalStateException();
        }
        return result;
    }

    public boolean getUseSendfile() {
        return this.useSendfile;
    }

    public void setUseSendfile(boolean useSendfile) {
        this.useSendfile = useSendfile;
    }

    public long getExecutorTerminationTimeoutMillis() {
        return this.executorTerminationTimeoutMillis;
    }

    public void setExecutorTerminationTimeoutMillis(long executorTerminationTimeoutMillis) {
        this.executorTerminationTimeoutMillis = executorTerminationTimeoutMillis;
    }

    @Deprecated
    public void setAcceptorThreadCount(int acceptorThreadCount) {
    }

    @Deprecated
    public int getAcceptorThreadCount() {
        return 1;
    }

    public void setAcceptorThreadPriority(int acceptorThreadPriority) {
        this.acceptorThreadPriority = acceptorThreadPriority;
    }

    public int getAcceptorThreadPriority() {
        return this.acceptorThreadPriority;
    }

    public void setMaxConnections(int maxCon) {
        this.maxConnections = maxCon;
        LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            if (maxCon == -1) {
                this.releaseConnectionLatch();
            } else {
                latch.setLimit((long)maxCon);
            }
        } else if (maxCon > 0) {
            this.initializeConnectionLatch();
        }
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public long getConnectionCount() {
        LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            return latch.getCount();
        }
        return -1L;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
        this.internalExecutor = executor == null;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public void setUseVirtualThreads(boolean useVirtualThreads) {
        this.useVirtualThreads = useVirtualThreads;
    }

    public boolean getUseVirtualThreads() {
        return this.useVirtualThreads;
    }

    public void setUtilityExecutor(ScheduledExecutorService utilityExecutor) {
        this.utilityExecutor = utilityExecutor;
    }

    public ScheduledExecutorService getUtilityExecutor() {
        if (this.utilityExecutor == null) {
            this.getLog().warn((Object)sm.getString("endpoint.warn.noUtilityExecutor"));
            this.utilityExecutor = new ScheduledThreadPoolExecutor(1);
        }
        return this.utilityExecutor;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPortOffset() {
        return this.portOffset;
    }

    public void setPortOffset(int portOffset) {
        if (portOffset < 0) {
            throw new IllegalArgumentException(sm.getString("endpoint.portOffset.invalid", new Object[]{portOffset}));
        }
        this.portOffset = portOffset;
    }

    public int getPortWithOffset() {
        int port = this.getPort();
        if (port > 0) {
            return port + this.getPortOffset();
        }
        return port;
    }

    public final int getLocalPort() {
        try {
            InetSocketAddress localAddress = this.getLocalAddress();
            if (localAddress == null) {
                return -1;
            }
            return localAddress.getPort();
        }
        catch (IOException ioe) {
            return -1;
        }
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    protected abstract InetSocketAddress getLocalAddress() throws IOException;

    public void setAcceptCount(int acceptCount) {
        if (acceptCount > 0) {
            this.acceptCount = acceptCount;
        }
    }

    public int getAcceptCount() {
        return this.acceptCount;
    }

    public boolean getBindOnInit() {
        return this.bindOnInit;
    }

    public void setBindOnInit(boolean b) {
        this.bindOnInit = b;
    }

    protected BindState getBindState() {
        return this.bindState;
    }

    public int getKeepAliveTimeout() {
        if (this.keepAliveTimeout == null) {
            return this.getConnectionTimeout();
        }
        return this.keepAliveTimeout;
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public boolean getTcpNoDelay() {
        return this.socketProperties.getTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.socketProperties.setTcpNoDelay(tcpNoDelay);
    }

    public int getConnectionLinger() {
        return this.socketProperties.getSoLingerTime();
    }

    public void setConnectionLinger(int connectionLinger) {
        this.socketProperties.setSoLingerTime(connectionLinger);
        this.socketProperties.setSoLingerOn(connectionLinger >= 0);
    }

    public int getConnectionTimeout() {
        return this.socketProperties.getSoTimeout();
    }

    public void setConnectionTimeout(int soTimeout) {
        this.socketProperties.setSoTimeout(soTimeout);
    }

    public boolean isSSLEnabled() {
        return this.SSLEnabled;
    }

    public void setSSLEnabled(boolean SSLEnabled) {
        this.SSLEnabled = SSLEnabled;
    }

    public abstract boolean isAlpnSupported();

    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        Executor executor = this.executor;
        if (this.internalExecutor && executor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor)executor).setCorePoolSize(minSpareThreads);
        }
    }

    public int getMinSpareThreads() {
        return Math.min(this.getMinSpareThreadsInternal(), this.getMaxThreads());
    }

    private int getMinSpareThreadsInternal() {
        if (this.internalExecutor) {
            return this.minSpareThreads;
        }
        return -1;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        Executor executor = this.executor;
        if (this.internalExecutor && executor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor)executor).setMaximumPoolSize(maxThreads);
        }
    }

    public int getMaxThreads() {
        if (this.internalExecutor) {
            return this.maxThreads;
        }
        return -1;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public int getThreadPriority() {
        if (this.internalExecutor) {
            return this.threadPriority;
        }
        return -1;
    }

    public int getMaxKeepAliveRequests() {
        if (this.bindState.isBound()) {
            return this.maxKeepAliveRequests;
        }
        return 1;
    }

    public void setMaxKeepAliveRequests(int maxKeepAliveRequests) {
        this.maxKeepAliveRequests = maxKeepAliveRequests;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDaemon(boolean b) {
        this.daemon = b;
    }

    public boolean getDaemon() {
        return this.daemon;
    }

    public void setUseAsyncIO(boolean useAsyncIO) {
        this.useAsyncIO = useAsyncIO;
    }

    public boolean getUseAsyncIO() {
        return this.useAsyncIO;
    }

    protected abstract boolean getDeferAccept();

    public String getId() {
        return null;
    }

    public void addNegotiatedProtocol(String negotiableProtocol) {
        this.negotiableProtocols.add(negotiableProtocol);
    }

    public boolean hasNegotiableProtocols() {
        return this.negotiableProtocols.size() > 0;
    }

    public void setHandler(Handler<S> handler) {
        this.handler = handler;
    }

    public Handler<S> getHandler() {
        return this.handler;
    }

    public void setAttribute(String name, Object value) {
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)sm.getString("endpoint.setAttribute", new Object[]{name, value}));
        }
        this.attributes.put(name, value);
    }

    public Object getAttribute(String key) {
        Object value = this.attributes.get(key);
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)sm.getString("endpoint.getAttribute", new Object[]{key, value}));
        }
        return value;
    }

    public boolean setProperty(String name, String value) {
        this.setAttribute(name, value);
        String socketName = "socket.";
        try {
            if (name.startsWith("socket.")) {
                return IntrospectionUtils.setProperty((Object)this.socketProperties, (String)name.substring("socket.".length()), (String)value);
            }
            return IntrospectionUtils.setProperty((Object)this, (String)name, (String)value, (boolean)false);
        }
        catch (Exception x) {
            this.getLog().error((Object)sm.getString("endpoint.setAttributeError", new Object[]{name, value}), (Throwable)x);
            return false;
        }
    }

    public String getProperty(String name) {
        Object result;
        String value = (String)this.getAttribute(name);
        String socketName = "socket.";
        if (value == null && name.startsWith("socket.") && (result = IntrospectionUtils.getProperty((Object)this.socketProperties, (String)name.substring("socket.".length()))) != null) {
            value = result.toString();
        }
        return value;
    }

    public int getCurrentThreadCount() {
        Executor executor = this.executor;
        if (executor != null) {
            if (executor instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor)executor).getPoolSize();
            }
            if (executor instanceof java.util.concurrent.ThreadPoolExecutor) {
                return ((java.util.concurrent.ThreadPoolExecutor)executor).getPoolSize();
            }
            if (executor instanceof ResizableExecutor) {
                return ((ResizableExecutor)executor).getPoolSize();
            }
            return -1;
        }
        return -2;
    }

    public int getCurrentThreadsBusy() {
        Executor executor = this.executor;
        if (executor != null) {
            if (executor instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor)executor).getActiveCount();
            }
            if (executor instanceof java.util.concurrent.ThreadPoolExecutor) {
                return ((java.util.concurrent.ThreadPoolExecutor)executor).getActiveCount();
            }
            if (executor instanceof ResizableExecutor) {
                return ((ResizableExecutor)executor).getActiveCount();
            }
            return -1;
        }
        return -2;
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void createExecutor() {
        this.internalExecutor = true;
        if (this.getUseVirtualThreads()) {
            this.executor = new VirtualThreadExecutor(this.getName() + "-virt-");
        } else {
            TaskQueue taskqueue = new TaskQueue();
            TaskThreadFactory tf = new TaskThreadFactory(this.getName() + "-exec-", this.daemon, this.getThreadPriority());
            this.executor = new ThreadPoolExecutor(this.getMinSpareThreads(), this.getMaxThreads(), 60L, TimeUnit.SECONDS, (BlockingQueue)taskqueue, (ThreadFactory)tf);
            taskqueue.setParent((ThreadPoolExecutor)this.executor);
        }
    }

    public void shutdownExecutor() {
        Executor executor = this.executor;
        if (executor != null && this.internalExecutor) {
            this.executor = null;
            if (executor instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor tpe = (ThreadPoolExecutor)executor;
                tpe.shutdownNow();
                long timeout = this.getExecutorTerminationTimeoutMillis();
                if (timeout > 0L) {
                    try {
                        tpe.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    if (tpe.isTerminating()) {
                        this.getLog().warn((Object)sm.getString("endpoint.warn.executorShutdown", new Object[]{this.getName()}));
                    }
                }
                TaskQueue queue = (TaskQueue)tpe.getQueue();
                queue.setParent(null);
            }
        }
    }

    protected void unlockAccept() {
        block18: {
            if (this.acceptor == null || this.acceptor.getState() != Acceptor.AcceptorState.RUNNING) {
                return;
            }
            InetSocketAddress unlockAddress = null;
            InetSocketAddress localAddress = null;
            try {
                localAddress = this.getLocalAddress();
            }
            catch (IOException ioe) {
                this.getLog().debug((Object)sm.getString("endpoint.debug.unlock.localFail", new Object[]{this.getName()}), (Throwable)ioe);
            }
            if (localAddress == null) {
                this.getLog().warn((Object)sm.getString("endpoint.debug.unlock.localNone", new Object[]{this.getName()}));
                return;
            }
            try {
                unlockAddress = AbstractEndpoint.getUnlockAddress(localAddress);
                try (Socket s = new Socket();){
                    int stmo = 2000;
                    int utmo = 2000;
                    if (this.getSocketProperties().getSoTimeout() > stmo) {
                        stmo = this.getSocketProperties().getSoTimeout();
                    }
                    if (this.getSocketProperties().getUnlockTimeout() > utmo) {
                        utmo = this.getSocketProperties().getUnlockTimeout();
                    }
                    s.setSoTimeout(stmo);
                    s.setSoLinger(true, 0);
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)("About to unlock socket for:" + unlockAddress));
                    }
                    s.connect(unlockAddress, utmo);
                    if (this.getDeferAccept()) {
                        OutputStreamWriter sw = new OutputStreamWriter(s.getOutputStream(), "ISO-8859-1");
                        sw.write("OPTIONS * HTTP/1.0\r\nUser-Agent: Tomcat wakeup connection\r\n\r\n");
                        sw.flush();
                    }
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)("Socket unlock completed for:" + unlockAddress));
                    }
                }
                long startTime = System.nanoTime();
                while (startTime + 1000000000L > System.nanoTime() && this.acceptor.getState() == Acceptor.AcceptorState.RUNNING) {
                    if (startTime + 1000000L >= System.nanoTime()) continue;
                    Thread.sleep(1L);
                }
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                if (!this.getLog().isDebugEnabled()) break block18;
                this.getLog().debug((Object)sm.getString("endpoint.debug.unlock.fail", new Object[]{String.valueOf(this.getPortWithOffset())}), t);
            }
        }
    }

    private static InetSocketAddress getUnlockAddress(InetSocketAddress localAddress) throws SocketException {
        if (localAddress.getAddress().isAnyLocalAddress()) {
            InetAddress loopbackUnlockAddress = null;
            InetAddress linkLocalUnlockAddress = null;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!localAddress.getAddress().getClass().isAssignableFrom(inetAddress.getClass())) continue;
                    if (inetAddress.isLoopbackAddress()) {
                        if (loopbackUnlockAddress != null) continue;
                        loopbackUnlockAddress = inetAddress;
                        continue;
                    }
                    if (inetAddress.isLinkLocalAddress()) {
                        if (linkLocalUnlockAddress != null) continue;
                        linkLocalUnlockAddress = inetAddress;
                        continue;
                    }
                    return new InetSocketAddress(inetAddress, localAddress.getPort());
                }
            }
            if (loopbackUnlockAddress != null) {
                return new InetSocketAddress(loopbackUnlockAddress, localAddress.getPort());
            }
            if (linkLocalUnlockAddress != null) {
                return new InetSocketAddress(linkLocalUnlockAddress, localAddress.getPort());
            }
            return new InetSocketAddress("localhost", localAddress.getPort());
        }
        return localAddress;
    }

    public boolean processSocket(SocketWrapperBase<S> socketWrapper, SocketEvent event, boolean dispatch) {
        try {
            if (socketWrapper == null) {
                return false;
            }
            SocketProcessorBase<S> sc = null;
            if (this.processorCache != null) {
                sc = (SocketProcessorBase<S>)this.processorCache.pop();
            }
            if (sc == null) {
                sc = this.createSocketProcessor(socketWrapper, event);
            } else {
                sc.reset(socketWrapper, event);
            }
            Executor executor = this.getExecutor();
            if (dispatch && executor != null) {
                executor.execute(sc);
            } else {
                sc.run();
            }
        }
        catch (RejectedExecutionException ree) {
            this.getLog().warn((Object)sm.getString("endpoint.executor.fail", new Object[]{socketWrapper}), (Throwable)ree);
            return false;
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.getLog().error((Object)sm.getString("endpoint.process.fail"), t);
            return false;
        }
        return true;
    }

    protected abstract SocketProcessorBase<S> createSocketProcessor(SocketWrapperBase<S> var1, SocketEvent var2);

    public abstract void bind() throws Exception;

    public abstract void unbind() throws Exception;

    public abstract void startInternal() throws Exception;

    public abstract void stopInternal() throws Exception;

    private void bindWithCleanup() throws Exception {
        try {
            this.bind();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.unbind();
            throw t;
        }
    }

    public final void init() throws Exception {
        if (this.bindOnInit) {
            this.bindWithCleanup();
            this.bindState = BindState.BOUND_ON_INIT;
        }
        if (this.domain != null) {
            this.oname = new ObjectName(this.domain + ":type=ThreadPool,name=\"" + this.getName() + "\"");
            Registry.getRegistry(null, null).registerComponent((Object)this, this.oname, null);
            ObjectName socketPropertiesOname = new ObjectName(this.domain + ":type=SocketProperties,name=\"" + this.getName() + "\"");
            this.socketProperties.setObjectName(socketPropertiesOname);
            Registry.getRegistry(null, null).registerComponent((Object)this.socketProperties, socketPropertiesOname, null);
            for (SSLHostConfig sslHostConfig : this.findSslHostConfigs()) {
                this.registerJmx(sslHostConfig);
            }
        }
    }

    private void registerJmx(SSLHostConfig sslHostConfig) {
        if (this.domain == null) {
            return;
        }
        ObjectName sslOname = null;
        try {
            sslOname = new ObjectName(this.domain + ":type=SSLHostConfig,ThreadPool=\"" + this.getName() + "\",name=" + ObjectName.quote(sslHostConfig.getHostName()));
            sslHostConfig.setObjectName(sslOname);
            try {
                Registry.getRegistry(null, null).registerComponent((Object)sslHostConfig, sslOname, null);
            }
            catch (Exception e) {
                this.getLog().warn((Object)sm.getString("endpoint.jmxRegistrationFailed", new Object[]{sslOname}), (Throwable)e);
            }
        }
        catch (MalformedObjectNameException e) {
            this.getLog().warn((Object)sm.getString("endpoint.invalidJmxNameSslHost", new Object[]{sslHostConfig.getHostName()}), (Throwable)e);
        }
        for (SSLHostConfigCertificate sslHostConfigCert : sslHostConfig.getCertificates()) {
            ObjectName sslCertOname = null;
            try {
                sslCertOname = new ObjectName(this.domain + ":type=SSLHostConfigCertificate,ThreadPool=\"" + this.getName() + "\",Host=" + ObjectName.quote(sslHostConfig.getHostName()) + ",name=" + (Object)((Object)sslHostConfigCert.getType()));
                sslHostConfigCert.setObjectName(sslCertOname);
                try {
                    Registry.getRegistry(null, null).registerComponent((Object)sslHostConfigCert, sslCertOname, null);
                }
                catch (Exception e) {
                    this.getLog().warn((Object)sm.getString("endpoint.jmxRegistrationFailed", new Object[]{sslCertOname}), (Throwable)e);
                }
            }
            catch (MalformedObjectNameException e) {
                this.getLog().warn((Object)sm.getString("endpoint.invalidJmxNameSslHostCert", new Object[]{sslHostConfig.getHostName(), sslHostConfigCert.getType()}), (Throwable)e);
            }
        }
    }

    private void unregisterJmx(SSLHostConfig sslHostConfig) {
        Registry registry = Registry.getRegistry(null, null);
        registry.unregisterComponent(sslHostConfig.getObjectName());
        for (SSLHostConfigCertificate sslHostConfigCert : sslHostConfig.getCertificates()) {
            registry.unregisterComponent(sslHostConfigCert.getObjectName());
        }
    }

    public final void start() throws Exception {
        if (this.bindState == BindState.UNBOUND) {
            this.bindWithCleanup();
            this.bindState = BindState.BOUND_ON_START;
        }
        this.startInternal();
    }

    protected void startAcceptorThread() {
        this.acceptor = new Acceptor(this);
        String threadName = this.getName() + "-Acceptor";
        this.acceptor.setThreadName(threadName);
        Thread t = new Thread(this.acceptor, threadName);
        t.setPriority(this.getAcceptorThreadPriority());
        t.setDaemon(this.getDaemon());
        t.start();
    }

    public void pause() {
        if (this.running && !this.paused) {
            this.paused = true;
            this.releaseConnectionLatch();
            this.unlockAccept();
            this.getHandler().pause();
        }
    }

    public void resume() {
        if (this.running) {
            this.paused = false;
        }
    }

    public final void stop() throws Exception {
        this.stopInternal();
        if (this.bindState == BindState.BOUND_ON_START || this.bindState == BindState.SOCKET_CLOSED_ON_STOP) {
            this.unbind();
            this.bindState = BindState.UNBOUND;
        }
    }

    public final void destroy() throws Exception {
        if (this.bindState == BindState.BOUND_ON_INIT) {
            this.unbind();
            this.bindState = BindState.UNBOUND;
        }
        Registry registry = Registry.getRegistry(null, null);
        registry.unregisterComponent(this.oname);
        registry.unregisterComponent(this.socketProperties.getObjectName());
        for (SSLHostConfig sslHostConfig : this.findSslHostConfigs()) {
            this.unregisterJmx(sslHostConfig);
        }
    }

    protected abstract Log getLog();

    protected Log getLogCertificate() {
        return this.getLog();
    }

    protected LimitLatch initializeConnectionLatch() {
        if (this.maxConnections == -1) {
            return null;
        }
        if (this.connectionLimitLatch == null) {
            this.connectionLimitLatch = new LimitLatch((long)this.getMaxConnections());
        }
        return this.connectionLimitLatch;
    }

    private void releaseConnectionLatch() {
        LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            latch.releaseAll();
        }
        this.connectionLimitLatch = null;
    }

    protected void countUpOrAwaitConnection() throws InterruptedException {
        if (this.maxConnections == -1) {
            return;
        }
        LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            latch.countUpOrAwait();
        }
    }

    protected long countDownConnection() {
        if (this.maxConnections == -1) {
            return -1L;
        }
        LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            long result = latch.countDown();
            if (result < 0L) {
                this.getLog().warn((Object)sm.getString("endpoint.warn.incorrectConnectionCount"));
            }
            return result;
        }
        return -1L;
    }

    public final void closeServerSocketGraceful() {
        if (this.bindState == BindState.BOUND_ON_START) {
            this.acceptor.stop(-1);
            this.releaseConnectionLatch();
            this.unlockAccept();
            this.getHandler().pause();
            this.bindState = BindState.SOCKET_CLOSED_ON_STOP;
            try {
                this.doCloseServerSocket();
            }
            catch (IOException ioe) {
                this.getLog().warn((Object)sm.getString("endpoint.serverSocket.closeFailed", new Object[]{this.getName()}), (Throwable)ioe);
            }
        }
    }

    public final long awaitConnectionsClose(long waitMillis) {
        while (waitMillis > 0L && !this.connections.isEmpty()) {
            try {
                Thread.sleep(50L);
                waitMillis -= 50L;
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                waitMillis = 0L;
            }
        }
        return waitMillis;
    }

    protected abstract void doCloseServerSocket() throws IOException;

    protected abstract U serverSocketAccept() throws Exception;

    protected abstract boolean setSocketOptions(U var1);

    protected void closeSocket(U socket) {
        SocketWrapperBase<S> socketWrapper = this.connections.get(socket);
        if (socketWrapper != null) {
            socketWrapper.close();
        }
    }

    protected abstract void destroySocket(U var1);

    protected static enum BindState {
        UNBOUND(false, false),
        BOUND_ON_INIT(true, true),
        BOUND_ON_START(true, true),
        SOCKET_CLOSED_ON_STOP(false, true);

        private final boolean bound;
        private final boolean wasBound;

        private BindState(boolean bound, boolean wasBound) {
            this.bound = bound;
            this.wasBound = wasBound;
        }

        public boolean isBound() {
            return this.bound;
        }

        public boolean wasBound() {
            return this.wasBound;
        }
    }

    public static interface Handler<S> {
        public SocketState process(SocketWrapperBase<S> var1, SocketEvent var2);

        public Object getGlobal();

        @Deprecated
        public Set<S> getOpenSockets();

        public void release(SocketWrapperBase<S> var1);

        public void pause();

        public void recycle();

        public static enum SocketState {
            OPEN,
            CLOSED,
            LONG,
            ASYNC_END,
            SENDFILE,
            UPGRADING,
            UPGRADED,
            ASYNC_IO,
            SUSPENDED;

        }
    }
}

