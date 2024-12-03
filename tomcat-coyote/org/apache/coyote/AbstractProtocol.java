/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpUpgradeHandler
 *  javax.servlet.http.WebConnection
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.WebConnection;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.Request;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.juli.logging.Log;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractProtocol<S>
implements ProtocolHandler,
MBeanRegistration {
    private static final StringManager sm = StringManager.getManager(AbstractProtocol.class);
    private static final AtomicInteger nameCounter = new AtomicInteger(0);
    private int nameIndex = 0;
    private final AbstractEndpoint<S, ?> endpoint;
    private AbstractEndpoint.Handler<S> handler;
    private final Set<Processor> waitingProcessors = ConcurrentHashMap.newKeySet();
    private ScheduledFuture<?> timeoutFuture = null;
    private ScheduledFuture<?> monitorFuture;
    protected ObjectName rgOname = null;
    protected Adapter adapter;
    protected int processorCache = 200;
    private String clientCertProvider = null;
    private int maxHeaderCount = 100;
    protected String domain;
    protected ObjectName oname;
    protected MBeanServer mserver;

    public AbstractProtocol(AbstractEndpoint<S, ?> endpoint) {
        this.endpoint = endpoint;
        ConnectionHandler cHandler = new ConnectionHandler(this);
        this.getEndpoint().setHandler(cHandler);
        this.setHandler(cHandler);
        this.setConnectionLinger(-1);
        this.setTcpNoDelay(true);
    }

    public boolean setProperty(String name, String value) {
        return this.endpoint.setProperty(name, value);
    }

    public String getProperty(String name) {
        return this.endpoint.getProperty(name);
    }

    public ObjectName getGlobalRequestProcessorMBeanName() {
        return this.rgOname;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Adapter getAdapter() {
        return this.adapter;
    }

    public int getProcessorCache() {
        return this.processorCache;
    }

    public void setProcessorCache(int processorCache) {
        this.processorCache = processorCache;
    }

    public String getClientCertProvider() {
        return this.clientCertProvider;
    }

    public void setClientCertProvider(String s) {
        this.clientCertProvider = s;
    }

    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }

    public void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    @Override
    public boolean isAprRequired() {
        return false;
    }

    @Override
    public boolean isSendfileSupported() {
        return this.endpoint.getUseSendfile();
    }

    @Override
    public String getId() {
        return this.endpoint.getId();
    }

    @Override
    public Executor getExecutor() {
        return this.endpoint.getExecutor();
    }

    @Override
    public void setExecutor(Executor executor) {
        this.endpoint.setExecutor(executor);
    }

    @Override
    public ScheduledExecutorService getUtilityExecutor() {
        return this.endpoint.getUtilityExecutor();
    }

    @Override
    public void setUtilityExecutor(ScheduledExecutorService utilityExecutor) {
        this.endpoint.setUtilityExecutor(utilityExecutor);
    }

    public int getMaxThreads() {
        return this.endpoint.getMaxThreads();
    }

    public void setMaxThreads(int maxThreads) {
        this.endpoint.setMaxThreads(maxThreads);
    }

    public int getMaxConnections() {
        return this.endpoint.getMaxConnections();
    }

    public void setMaxConnections(int maxConnections) {
        this.endpoint.setMaxConnections(maxConnections);
    }

    public int getMinSpareThreads() {
        return this.endpoint.getMinSpareThreads();
    }

    public void setMinSpareThreads(int minSpareThreads) {
        this.endpoint.setMinSpareThreads(minSpareThreads);
    }

    public int getThreadPriority() {
        return this.endpoint.getThreadPriority();
    }

    public void setThreadPriority(int threadPriority) {
        this.endpoint.setThreadPriority(threadPriority);
    }

    public int getAcceptCount() {
        return this.endpoint.getAcceptCount();
    }

    public void setAcceptCount(int acceptCount) {
        this.endpoint.setAcceptCount(acceptCount);
    }

    public boolean getTcpNoDelay() {
        return this.endpoint.getTcpNoDelay();
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.endpoint.setTcpNoDelay(tcpNoDelay);
    }

    public int getConnectionLinger() {
        return this.endpoint.getConnectionLinger();
    }

    public void setConnectionLinger(int connectionLinger) {
        this.endpoint.setConnectionLinger(connectionLinger);
    }

    public int getKeepAliveTimeout() {
        return this.endpoint.getKeepAliveTimeout();
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.endpoint.setKeepAliveTimeout(keepAliveTimeout);
    }

    public InetAddress getAddress() {
        return this.endpoint.getAddress();
    }

    public void setAddress(InetAddress ia) {
        this.endpoint.setAddress(ia);
    }

    public int getPort() {
        return this.endpoint.getPort();
    }

    public void setPort(int port) {
        this.endpoint.setPort(port);
    }

    public int getPortOffset() {
        return this.endpoint.getPortOffset();
    }

    public void setPortOffset(int portOffset) {
        this.endpoint.setPortOffset(portOffset);
    }

    public int getPortWithOffset() {
        return this.endpoint.getPortWithOffset();
    }

    public int getLocalPort() {
        return this.endpoint.getLocalPort();
    }

    public int getConnectionTimeout() {
        return this.endpoint.getConnectionTimeout();
    }

    public void setConnectionTimeout(int timeout) {
        this.endpoint.setConnectionTimeout(timeout);
    }

    public long getConnectionCount() {
        return this.endpoint.getConnectionCount();
    }

    @Deprecated
    public void setAcceptorThreadCount(int threadCount) {
    }

    @Deprecated
    public int getAcceptorThreadCount() {
        return 1;
    }

    public void setAcceptorThreadPriority(int threadPriority) {
        this.endpoint.setAcceptorThreadPriority(threadPriority);
    }

    public int getAcceptorThreadPriority() {
        return this.endpoint.getAcceptorThreadPriority();
    }

    public synchronized int getNameIndex() {
        if (this.nameIndex == 0) {
            this.nameIndex = nameCounter.incrementAndGet();
        }
        return this.nameIndex;
    }

    public String getName() {
        return ObjectName.quote(this.getNameInternal());
    }

    private String getNameInternal() {
        StringBuilder name = new StringBuilder(this.getNamePrefix());
        name.append('-');
        String id = this.getId();
        if (id != null) {
            name.append(id);
        } else {
            int port;
            if (this.getAddress() != null) {
                name.append(this.getAddress().getHostAddress());
                name.append('-');
            }
            if ((port = this.getPortWithOffset()) == 0) {
                name.append("auto-");
                name.append(this.getNameIndex());
                port = this.getLocalPort();
                if (port != -1) {
                    name.append('-');
                    name.append(port);
                }
            } else {
                name.append(port);
            }
        }
        return name.toString();
    }

    public void addWaitingProcessor(Processor processor) {
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)sm.getString("abstractProtocol.waitingProcessor.add", new Object[]{processor}));
        }
        this.waitingProcessors.add(processor);
    }

    public void removeWaitingProcessor(Processor processor) {
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)sm.getString("abstractProtocol.waitingProcessor.remove", new Object[]{processor}));
        }
        this.waitingProcessors.remove(processor);
    }

    public int getWaitingProcessorCount() {
        return this.waitingProcessors.size();
    }

    protected AbstractEndpoint<S, ?> getEndpoint() {
        return this.endpoint;
    }

    protected AbstractEndpoint.Handler<S> getHandler() {
        return this.handler;
    }

    protected void setHandler(AbstractEndpoint.Handler<S> handler) {
        this.handler = handler;
    }

    protected abstract Log getLog();

    protected abstract String getNamePrefix();

    protected abstract String getProtocolName();

    protected abstract UpgradeProtocol getNegotiatedProtocol(String var1);

    protected abstract UpgradeProtocol getUpgradeProtocol(String var1);

    protected abstract Processor createProcessor();

    protected abstract Processor createUpgradeProcessor(SocketWrapperBase<?> var1, UpgradeToken var2);

    public ObjectName getObjectName() {
        return this.oname;
    }

    public String getDomain() {
        return this.domain;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        this.oname = name;
        this.mserver = server;
        this.domain = name.getDomain();
        return name;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public void postDeregister() {
    }

    private ObjectName createObjectName() throws MalformedObjectNameException {
        this.domain = this.getAdapter().getDomain();
        if (this.domain == null) {
            return null;
        }
        StringBuilder name = new StringBuilder(this.getDomain());
        name.append(":type=ProtocolHandler,port=");
        int port = this.getPortWithOffset();
        if (port > 0) {
            name.append(port);
        } else {
            name.append("auto-");
            name.append(this.getNameIndex());
        }
        InetAddress address = this.getAddress();
        if (address != null) {
            name.append(",address=");
            name.append(ObjectName.quote(address.getHostAddress()));
        }
        return new ObjectName(name.toString());
    }

    @Override
    public void init() throws Exception {
        if (this.getLog().isInfoEnabled()) {
            this.getLog().info((Object)sm.getString("abstractProtocolHandler.init", new Object[]{this.getName()}));
            this.logPortOffset();
        }
        if (this.oname == null) {
            this.oname = this.createObjectName();
            if (this.oname != null) {
                Registry.getRegistry(null, null).registerComponent((Object)this, this.oname, null);
            }
        }
        if (this.domain != null) {
            ObjectName rgOname;
            this.rgOname = rgOname = new ObjectName(this.domain + ":type=GlobalRequestProcessor,name=" + this.getName());
            Registry.getRegistry(null, null).registerComponent(this.getHandler().getGlobal(), rgOname, null);
        }
        String endpointName = this.getName();
        this.endpoint.setName(endpointName.substring(1, endpointName.length() - 1));
        this.endpoint.setDomain(this.domain);
        this.endpoint.init();
    }

    @Override
    public void start() throws Exception {
        if (this.getLog().isInfoEnabled()) {
            this.getLog().info((Object)sm.getString("abstractProtocolHandler.start", new Object[]{this.getName()}));
            this.logPortOffset();
        }
        this.endpoint.start();
        this.monitorFuture = this.getUtilityExecutor().scheduleWithFixedDelay(() -> this.startAsyncTimeout(), 0L, 60L, TimeUnit.SECONDS);
    }

    protected void startAsyncTimeout() {
        if (this.timeoutFuture == null || this.timeoutFuture.isDone()) {
            if (this.timeoutFuture != null && this.timeoutFuture.isDone()) {
                try {
                    this.timeoutFuture.get();
                }
                catch (InterruptedException | ExecutionException e) {
                    this.getLog().error((Object)sm.getString("abstractProtocolHandler.asyncTimeoutError"), (Throwable)e);
                }
            }
            this.timeoutFuture = this.getUtilityExecutor().scheduleAtFixedRate(() -> {
                long now = System.currentTimeMillis();
                for (Processor processor : this.waitingProcessors) {
                    processor.timeoutAsync(now);
                }
            }, 1L, 1L, TimeUnit.SECONDS);
        }
    }

    protected void stopAsyncTimeout() {
        if (this.timeoutFuture != null) {
            this.timeoutFuture.cancel(false);
            this.timeoutFuture = null;
        }
    }

    @Override
    public void pause() throws Exception {
        if (this.getLog().isInfoEnabled()) {
            this.getLog().info((Object)sm.getString("abstractProtocolHandler.pause", new Object[]{this.getName()}));
        }
        this.endpoint.pause();
    }

    public boolean isPaused() {
        return this.endpoint.isPaused();
    }

    @Override
    public void resume() throws Exception {
        if (this.getLog().isInfoEnabled()) {
            this.getLog().info((Object)sm.getString("abstractProtocolHandler.resume", new Object[]{this.getName()}));
        }
        this.endpoint.resume();
    }

    @Override
    public void stop() throws Exception {
        if (this.getLog().isInfoEnabled()) {
            this.getLog().info((Object)sm.getString("abstractProtocolHandler.stop", new Object[]{this.getName()}));
            this.logPortOffset();
        }
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        this.stopAsyncTimeout();
        for (Processor processor : this.waitingProcessors) {
            processor.timeoutAsync(-1L);
        }
        this.endpoint.stop();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void destroy() throws Exception {
        ObjectName rgOname;
        block10: {
            block11: {
                if (this.getLog().isInfoEnabled()) {
                    this.getLog().info((Object)sm.getString("abstractProtocolHandler.destroy", new Object[]{this.getName()}));
                    this.logPortOffset();
                }
                try {
                    this.endpoint.destroy();
                    if (this.oname == null) break block10;
                    if (this.mserver != null) break block11;
                    Registry.getRegistry(null, null).unregisterComponent(this.oname);
                    break block10;
                }
                catch (Throwable throwable) {
                    ObjectName rgOname2;
                    if (this.oname != null) {
                        if (this.mserver == null) {
                            Registry.getRegistry(null, null).unregisterComponent(this.oname);
                        } else {
                            try {
                                this.mserver.unregisterMBean(this.oname);
                            }
                            catch (InstanceNotFoundException | MBeanRegistrationException e) {
                                this.getLog().info((Object)sm.getString("abstractProtocol.mbeanDeregistrationFailed", new Object[]{this.oname, this.mserver}));
                            }
                        }
                    }
                    if ((rgOname2 = this.getGlobalRequestProcessorMBeanName()) == null) throw throwable;
                    Registry.getRegistry(null, null).unregisterComponent(rgOname2);
                    throw throwable;
                }
            }
            try {
                this.mserver.unregisterMBean(this.oname);
            }
            catch (InstanceNotFoundException | MBeanRegistrationException e) {
                this.getLog().info((Object)sm.getString("abstractProtocol.mbeanDeregistrationFailed", new Object[]{this.oname, this.mserver}));
            }
        }
        if ((rgOname = this.getGlobalRequestProcessorMBeanName()) == null) return;
        Registry.getRegistry(null, null).unregisterComponent(rgOname);
    }

    @Override
    public void closeServerSocketGraceful() {
        this.endpoint.closeServerSocketGraceful();
    }

    @Override
    public long awaitConnectionsClose(long waitMillis) {
        this.getLog().info((Object)sm.getString("abstractProtocol.closeConnectionsAwait", new Object[]{waitMillis, this.getName()}));
        return this.endpoint.awaitConnectionsClose(waitMillis);
    }

    private void logPortOffset() {
        if (this.getPort() != this.getPortWithOffset()) {
            this.getLog().info((Object)sm.getString("abstractProtocolHandler.portOffset", new Object[]{this.getName(), String.valueOf(this.getPort()), String.valueOf(this.getPortOffset())}));
        }
    }

    protected static class ConnectionHandler<S>
    implements AbstractEndpoint.Handler<S> {
        private final AbstractProtocol<S> proto;
        private final RequestGroupInfo global = new RequestGroupInfo();
        private final AtomicLong registerCount = new AtomicLong(0L);
        private final RecycledProcessors recycledProcessors = new RecycledProcessors(this);

        public ConnectionHandler(AbstractProtocol<S> proto) {
            this.proto = proto;
        }

        protected AbstractProtocol<S> getProtocol() {
            return this.proto;
        }

        protected Log getLog() {
            return this.getProtocol().getLog();
        }

        @Override
        public Object getGlobal() {
            return this.global;
        }

        @Override
        public void recycle() {
            this.recycledProcessors.clear();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public AbstractEndpoint.Handler.SocketState process(SocketWrapperBase<S> wrapper, SocketEvent status) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)sm.getString("abstractConnectionHandler.process", new Object[]{wrapper.getSocket(), status}));
            }
            if (wrapper == null) {
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            S socket = wrapper.getSocket();
            Processor processor = (Processor)wrapper.takeCurrentProcessor();
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)sm.getString("abstractConnectionHandler.connectionsGet", new Object[]{processor, socket}));
            }
            if (SocketEvent.TIMEOUT == status && (processor == null || !processor.isAsync() && !processor.isUpgrade() || processor.isAsync() && !processor.checkAsyncTimeoutGeneration())) {
                return AbstractEndpoint.Handler.SocketState.OPEN;
            }
            if (processor != null) {
                this.getProtocol().removeWaitingProcessor(processor);
            } else if (status == SocketEvent.DISCONNECT || status == SocketEvent.ERROR) {
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            try {
                ClassLoader oldCL;
                UpgradeToken upgradeToken;
                String negotiatedProtocol;
                if (processor == null && (negotiatedProtocol = wrapper.getNegotiatedProtocol()) != null && negotiatedProtocol.length() > 0) {
                    UpgradeProtocol upgradeProtocol = this.getProtocol().getNegotiatedProtocol(negotiatedProtocol);
                    if (upgradeProtocol != null) {
                        processor = upgradeProtocol.getProcessor(wrapper, this.getProtocol().getAdapter());
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)sm.getString("abstractConnectionHandler.processorCreate", new Object[]{processor}));
                        }
                    } else if (!negotiatedProtocol.equals("http/1.1")) {
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)sm.getString("abstractConnectionHandler.negotiatedProcessor.fail", new Object[]{negotiatedProtocol}));
                        }
                        return AbstractEndpoint.Handler.SocketState.CLOSED;
                    }
                }
                if (processor == null) {
                    processor = this.recycledProcessors.pop();
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)sm.getString("abstractConnectionHandler.processorPop", new Object[]{processor}));
                    }
                }
                if (processor == null) {
                    processor = this.getProtocol().createProcessor();
                    this.register(processor);
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)sm.getString("abstractConnectionHandler.processorCreate", new Object[]{processor}));
                    }
                }
                processor.setSslSupport(wrapper.getSslSupport(this.getProtocol().getClientCertProvider()));
                AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.CLOSED;
                do {
                    if ((state = processor.process(wrapper, status)) != AbstractEndpoint.Handler.SocketState.UPGRADING) continue;
                    upgradeToken = processor.getUpgradeToken();
                    ByteBuffer leftOverInput = processor.getLeftoverInput();
                    wrapper.unRead(leftOverInput);
                    if (upgradeToken == null) {
                        UpgradeProtocol upgradeProtocol = this.getProtocol().getUpgradeProtocol("h2c");
                        if (upgradeProtocol != null) {
                            this.release(processor);
                            processor = upgradeProtocol.getProcessor(wrapper, this.getProtocol().getAdapter());
                            continue;
                        }
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)sm.getString("abstractConnectionHandler.negotiatedProcessor.fail", new Object[]{"h2c"}));
                        }
                        state = AbstractEndpoint.Handler.SocketState.CLOSED;
                        continue;
                    }
                    HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
                    this.release(processor);
                    processor = this.getProtocol().createUpgradeProcessor(wrapper, upgradeToken);
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)sm.getString("abstractConnectionHandler.upgradeCreate", new Object[]{processor, wrapper}));
                    }
                    if (upgradeToken.getInstanceManager() == null) {
                        httpUpgradeHandler.init((WebConnection)processor);
                    } else {
                        oldCL = upgradeToken.getContextBind().bind(false, null);
                        try {
                            httpUpgradeHandler.init((WebConnection)processor);
                        }
                        finally {
                            upgradeToken.getContextBind().unbind(false, oldCL);
                        }
                    }
                    if (!(httpUpgradeHandler instanceof InternalHttpUpgradeHandler) || !((InternalHttpUpgradeHandler)httpUpgradeHandler).hasAsyncIO()) continue;
                    state = AbstractEndpoint.Handler.SocketState.ASYNC_IO;
                } while (state == AbstractEndpoint.Handler.SocketState.UPGRADING);
                if (state == AbstractEndpoint.Handler.SocketState.LONG) {
                    this.longPoll(wrapper, processor);
                    if (processor.isAsync()) {
                        this.getProtocol().addWaitingProcessor(processor);
                    }
                } else if (state == AbstractEndpoint.Handler.SocketState.OPEN) {
                    this.release(processor);
                    processor = null;
                    wrapper.registerReadInterest();
                } else if (state != AbstractEndpoint.Handler.SocketState.SENDFILE) {
                    if (state == AbstractEndpoint.Handler.SocketState.UPGRADED) {
                        if (status != SocketEvent.OPEN_WRITE) {
                            this.longPoll(wrapper, processor);
                            this.getProtocol().addWaitingProcessor(processor);
                        }
                    } else if (state == AbstractEndpoint.Handler.SocketState.ASYNC_IO) {
                        if (status != SocketEvent.OPEN_WRITE) {
                            this.getProtocol().addWaitingProcessor(processor);
                        }
                    } else if (state != AbstractEndpoint.Handler.SocketState.SUSPENDED) {
                        if (processor != null && processor.isUpgrade()) {
                            upgradeToken = processor.getUpgradeToken();
                            HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
                            InstanceManager instanceManager = upgradeToken.getInstanceManager();
                            if (instanceManager == null) {
                                httpUpgradeHandler.destroy();
                            } else {
                                oldCL = upgradeToken.getContextBind().bind(false, null);
                                try {
                                    httpUpgradeHandler.destroy();
                                }
                                finally {
                                    try {
                                        instanceManager.destroyInstance((Object)httpUpgradeHandler);
                                    }
                                    catch (Throwable e) {
                                        ExceptionUtils.handleThrowable((Throwable)e);
                                        this.getLog().error((Object)sm.getString("abstractConnectionHandler.error"), e);
                                    }
                                    upgradeToken.getContextBind().unbind(false, oldCL);
                                }
                            }
                        }
                        this.release(processor);
                        processor = null;
                    }
                }
                if (processor != null) {
                    wrapper.setCurrentProcessor(processor);
                }
                return state;
            }
            catch (SocketException e) {
                this.getLog().debug((Object)sm.getString("abstractConnectionHandler.socketexception.debug"), (Throwable)e);
            }
            catch (IOException e) {
                this.getLog().debug((Object)sm.getString("abstractConnectionHandler.ioexception.debug"), (Throwable)e);
            }
            catch (ProtocolException e) {
                this.getLog().debug((Object)sm.getString("abstractConnectionHandler.protocolexception.debug"), (Throwable)e);
            }
            catch (OutOfMemoryError oome) {
                this.getLog().error((Object)sm.getString("abstractConnectionHandler.oome"), (Throwable)oome);
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                this.getLog().error((Object)sm.getString("abstractConnectionHandler.error"), e);
            }
            this.release(processor);
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }

        protected void longPoll(SocketWrapperBase<?> socket, Processor processor) {
            if (!processor.isAsync()) {
                socket.registerReadInterest();
            }
        }

        @Override
        public Set<S> getOpenSockets() {
            Set<SocketWrapperBase<S>> set = this.proto.getEndpoint().getConnections();
            HashSet<S> result = new HashSet<S>();
            for (SocketWrapperBase<S> socketWrapper : set) {
                S socket = socketWrapper.getSocket();
                if (socket == null) continue;
                result.add(socket);
            }
            return result;
        }

        private void release(Processor processor) {
            if (processor != null) {
                processor.recycle();
                if (processor.isUpgrade()) {
                    this.getProtocol().removeWaitingProcessor(processor);
                } else {
                    this.recycledProcessors.push(processor);
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)("Pushed Processor [" + processor + "]"));
                    }
                }
            }
        }

        @Override
        public void release(SocketWrapperBase<S> socketWrapper) {
            Processor processor = (Processor)socketWrapper.takeCurrentProcessor();
            this.release(processor);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void register(Processor processor) {
            if (this.getProtocol().getDomain() != null) {
                ConnectionHandler connectionHandler = this;
                synchronized (connectionHandler) {
                    try {
                        long count = this.registerCount.incrementAndGet();
                        RequestInfo rp = processor.getRequest().getRequestProcessor();
                        rp.setGlobalProcessor(this.global);
                        ObjectName rpName = new ObjectName(this.getProtocol().getDomain() + ":type=RequestProcessor,worker=" + this.getProtocol().getName() + ",name=" + this.getProtocol().getProtocolName() + "Request" + count);
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)("Register [" + processor + "] as [" + rpName + "]"));
                        }
                        Registry.getRegistry(null, null).registerComponent((Object)rp, rpName, null);
                        rp.setRpName(rpName);
                    }
                    catch (Exception e) {
                        this.getLog().warn((Object)sm.getString("abstractProtocol.processorRegisterError"), (Throwable)e);
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void unregister(Processor processor) {
            if (this.getProtocol().getDomain() != null) {
                ConnectionHandler connectionHandler = this;
                synchronized (connectionHandler) {
                    try {
                        Request r = processor.getRequest();
                        if (r == null) {
                            return;
                        }
                        RequestInfo rp = r.getRequestProcessor();
                        rp.setGlobalProcessor(null);
                        ObjectName rpName = rp.getRpName();
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)("Unregister [" + rpName + "]"));
                        }
                        Registry.getRegistry(null, null).unregisterComponent(rpName);
                        rp.setRpName(null);
                    }
                    catch (Exception e) {
                        this.getLog().warn((Object)sm.getString("abstractProtocol.processorUnregisterError"), (Throwable)e);
                    }
                }
            }
        }

        @Override
        public final void pause() {
            for (SocketWrapperBase<S> wrapper : this.proto.getEndpoint().getConnections()) {
                Processor processor = (Processor)wrapper.getCurrentProcessor();
                if (processor == null) continue;
                processor.pause();
            }
        }
    }

    protected static class RecycledProcessors
    extends SynchronizedStack<Processor> {
        private final transient ConnectionHandler<?> handler;
        protected final AtomicInteger size = new AtomicInteger(0);

        public RecycledProcessors(ConnectionHandler<?> handler) {
            this.handler = handler;
        }

        public boolean push(Processor processor) {
            int cacheSize = this.handler.getProtocol().getProcessorCache();
            boolean offer = cacheSize == -1 ? true : this.size.get() < cacheSize;
            boolean result = false;
            if (offer && (result = super.push((Object)processor))) {
                this.size.incrementAndGet();
            }
            if (!result) {
                this.handler.unregister(processor);
            }
            return result;
        }

        public Processor pop() {
            Processor result = (Processor)super.pop();
            if (result != null) {
                this.size.decrementAndGet();
            }
            return result;
        }

        public synchronized void clear() {
            Processor next = this.pop();
            while (next != null) {
                this.handler.unregister(next);
                next = this.pop();
            }
            super.clear();
            this.size.set(0);
        }
    }
}

