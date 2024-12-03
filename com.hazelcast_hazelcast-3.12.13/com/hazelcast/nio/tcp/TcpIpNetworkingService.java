/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.config.Config;
import com.hazelcast.config.EndpointConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import com.hazelcast.internal.networking.Networking;
import com.hazelcast.internal.networking.ServerSocketRegistry;
import com.hazelcast.internal.util.concurrent.ThreadFactoryImpl;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.nio.DefaultAggregateEndpointManager;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.nio.UnifiedAggregateEndpointManager;
import com.hazelcast.nio.tcp.ClientViewUnifiedEndpointManager;
import com.hazelcast.nio.tcp.MemberViewUnifiedEndpointManager;
import com.hazelcast.nio.tcp.TcpIpAcceptor;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;
import com.hazelcast.nio.tcp.TcpIpUnifiedEndpointManager;
import com.hazelcast.nio.tcp.TextViewUnifiedEndpointManager;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.ThreadUtil;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpIpNetworkingService
implements NetworkingService<TcpIpConnection> {
    private static final int SCHEDULER_POOL_SIZE = 4;
    private final IOService ioService;
    private final ILogger logger;
    private final Networking networking;
    private final MetricsRegistry metricsRegistry;
    private final AtomicBoolean metricsRegistryScheduled = new AtomicBoolean(false);
    private final ServerSocketRegistry registry;
    private final ConcurrentMap<EndpointQualifier, EndpointManager<TcpIpConnection>> endpointManagers = new ConcurrentHashMap<EndpointQualifier, EndpointManager<TcpIpConnection>>();
    private final TcpIpUnifiedEndpointManager unifiedEndpointManager;
    private final AggregateEndpointManager aggregateEndpointManager;
    private final ScheduledExecutorService scheduler;
    private volatile TcpIpAcceptor acceptor;
    private volatile boolean live;

    public TcpIpNetworkingService(Config config, IOService ioService, ServerSocketRegistry registry, LoggingService loggingService, MetricsRegistry metricsRegistry, Networking networking, ChannelInitializerProvider channelInitializerProvider) {
        this(config, ioService, registry, loggingService, metricsRegistry, networking, channelInitializerProvider, null);
    }

    public TcpIpNetworkingService(Config config, IOService ioService, ServerSocketRegistry registry, LoggingService loggingService, MetricsRegistry metricsRegistry, Networking networking, ChannelInitializerProvider channelInitializerProvider, HazelcastProperties properties) {
        this.ioService = ioService;
        this.networking = networking;
        this.metricsRegistry = metricsRegistry;
        this.registry = registry;
        this.logger = loggingService.getLogger(TcpIpNetworkingService.class);
        this.scheduler = new ScheduledThreadPoolExecutor(4, new ThreadFactoryImpl(ThreadUtil.createThreadPoolName(ioService.getHazelcastName(), "TcpIpNetworkingService")));
        this.unifiedEndpointManager = registry.holdsUnifiedSocket() ? new TcpIpUnifiedEndpointManager(this, null, channelInitializerProvider, ioService, loggingService, metricsRegistry, properties) : null;
        this.initEndpointManager(config, ioService, loggingService, metricsRegistry, channelInitializerProvider, properties);
        this.aggregateEndpointManager = this.unifiedEndpointManager != null ? new UnifiedAggregateEndpointManager(this.unifiedEndpointManager, this.endpointManagers) : new DefaultAggregateEndpointManager(this.endpointManagers);
        metricsRegistry.scanAndRegister(this, "tcp.connection");
    }

    private void initEndpointManager(Config config, IOService ioService, LoggingService loggingService, MetricsRegistry metricsRegistry, ChannelInitializerProvider channelInitializerProvider, HazelcastProperties properties) {
        if (this.unifiedEndpointManager != null) {
            this.endpointManagers.put(EndpointQualifier.MEMBER, new MemberViewUnifiedEndpointManager(this.unifiedEndpointManager));
            this.endpointManagers.put(EndpointQualifier.CLIENT, new ClientViewUnifiedEndpointManager(this.unifiedEndpointManager));
            this.endpointManagers.put(EndpointQualifier.REST, new TextViewUnifiedEndpointManager(this.unifiedEndpointManager, true));
            this.endpointManagers.put(EndpointQualifier.MEMCACHE, new TextViewUnifiedEndpointManager(this.unifiedEndpointManager, false));
        } else {
            for (EndpointConfig endpointConfig : config.getAdvancedNetworkConfig().getEndpointConfigs().values()) {
                EndpointQualifier qualifier = endpointConfig.getQualifier();
                EndpointManager<TcpIpConnection> em = this.newEndpointManager(ioService, endpointConfig, channelInitializerProvider, loggingService, metricsRegistry, properties, Collections.singleton(endpointConfig.getProtocolType()));
                this.endpointManagers.put(qualifier, em);
            }
        }
    }

    private EndpointManager<TcpIpConnection> newEndpointManager(IOService ioService, EndpointConfig endpointConfig, ChannelInitializerProvider channelInitializerProvider, LoggingService loggingService, MetricsRegistry metricsRegistry, HazelcastProperties properties, Set<ProtocolType> supportedProtocolTypes) {
        return new TcpIpEndpointManager(this, endpointConfig, channelInitializerProvider, ioService, loggingService, metricsRegistry, properties, supportedProtocolTypes);
    }

    @Override
    public IOService getIoService() {
        return this.ioService;
    }

    @Override
    public Networking getNetworking() {
        return this.networking;
    }

    @Override
    public boolean isLive() {
        return this.live;
    }

    @Override
    public synchronized void start() {
        if (this.live) {
            return;
        }
        if (!this.registry.isOpen()) {
            throw new IllegalStateException("Networking Service is already shutdown. Cannot start!");
        }
        this.live = true;
        this.logger.finest("Starting Networking Service and IO selectors.");
        this.networking.start();
        this.startAcceptor();
        if (this.unifiedEndpointManager == null) {
            if (this.metricsRegistryScheduled.compareAndSet(false, true)) {
                this.metricsRegistry.scheduleAtFixedRate(new RefreshNetworkStatsTask(), 1L, TimeUnit.SECONDS, ProbeLevel.INFO);
            }
            this.aggregateEndpointManager.getInboundNetworkStats().registerMetrics(this.metricsRegistry, "tcp.bytesReceived");
            this.aggregateEndpointManager.getOutboundNetworkStats().registerMetrics(this.metricsRegistry, "tcp.bytesSend");
        }
    }

    @Override
    public synchronized void stop() {
        if (!this.live) {
            return;
        }
        this.live = false;
        this.logger.finest("Stopping Networking Service");
        if (this.unifiedEndpointManager == null) {
            this.metricsRegistry.deregister(this.aggregateEndpointManager.getInboundNetworkStats());
            this.metricsRegistry.deregister(this.aggregateEndpointManager.getOutboundNetworkStats());
        }
        this.shutdownAcceptor();
        if (this.unifiedEndpointManager != null) {
            this.unifiedEndpointManager.reset(false);
        } else {
            for (EndpointManager endpointManager : this.endpointManagers.values()) {
                ((TcpIpEndpointManager)endpointManager).reset(false);
            }
        }
        this.networking.shutdown();
    }

    @Override
    public synchronized void shutdown() {
        this.shutdownAcceptor();
        this.closeServerSockets();
        this.stop();
        this.scheduler.shutdownNow();
        if (this.unifiedEndpointManager != null) {
            this.unifiedEndpointManager.reset(true);
        } else {
            for (EndpointManager endpointManager : this.endpointManagers.values()) {
                ((TcpIpEndpointManager)endpointManager).reset(true);
            }
        }
    }

    @Override
    public AggregateEndpointManager getAggregateEndpointManager() {
        return this.aggregateEndpointManager;
    }

    @Override
    public EndpointManager<TcpIpConnection> getEndpointManager(EndpointQualifier qualifier) {
        EndpointManager mgr = (EndpointManager)this.endpointManagers.get(qualifier);
        if (mgr == null) {
            this.logger.finest("An endpoint manager for qualifier " + qualifier + " was never registered.");
        }
        return mgr;
    }

    EndpointManager<TcpIpConnection> getUnifiedOrDedicatedEndpointManager(EndpointQualifier qualifier) {
        return this.unifiedEndpointManager != null ? this.unifiedEndpointManager : (EndpointManager)this.endpointManagers.get(qualifier);
    }

    @Override
    public void scheduleDeferred(Runnable task, long delay, TimeUnit unit) {
        this.scheduler.schedule(task, delay, unit);
    }

    private void startAcceptor() {
        if (this.acceptor != null) {
            this.logger.warning("TcpIpAcceptor is already running! Shutting down old acceptor...");
            this.shutdownAcceptor();
        }
        this.acceptor = new TcpIpAcceptor(this.registry, this, this.ioService).start();
        this.metricsRegistry.collectMetrics(this.acceptor);
    }

    private void shutdownAcceptor() {
        if (this.acceptor != null) {
            this.acceptor.shutdown();
            this.metricsRegistry.deregister(this.acceptor);
            this.acceptor = null;
        }
    }

    private void closeServerSockets() {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Closing server socket channel: " + this.registry);
        }
        this.registry.destroy();
    }

    private class RefreshNetworkStatsTask
    implements Runnable {
        private RefreshNetworkStatsTask() {
        }

        @Override
        public void run() {
            for (ProtocolType type : ProtocolType.valuesAsSet()) {
                long bytesReceived = 0L;
                long bytesSent = 0L;
                for (EndpointManager endpointManager : TcpIpNetworkingService.this.endpointManagers.values()) {
                    TcpIpEndpointManager tcpIpEndpointManager = (TcpIpEndpointManager)endpointManager;
                    if (type != tcpIpEndpointManager.getEndpointQualifier().getType()) continue;
                    bytesReceived += tcpIpEndpointManager.calculateBytesReceived();
                    bytesSent += tcpIpEndpointManager.calculateBytesSent();
                }
                TcpIpNetworkingService.this.aggregateEndpointManager.getInboundNetworkStats().setBytesTransceivedForProtocol(type, bytesReceived);
                TcpIpNetworkingService.this.aggregateEndpointManager.getOutboundNetworkStats().setBytesTransceivedForProtocol(type, bytesSent);
            }
        }
    }
}

