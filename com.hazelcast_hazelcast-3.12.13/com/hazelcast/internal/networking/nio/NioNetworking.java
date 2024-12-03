/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelCloseListener;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.internal.networking.ChannelInitializer;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import com.hazelcast.internal.networking.Networking;
import com.hazelcast.internal.networking.nio.NioChannel;
import com.hazelcast.internal.networking.nio.NioInboundPipeline;
import com.hazelcast.internal.networking.nio.NioOutboundPipeline;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.internal.networking.nio.SelectorMode;
import com.hazelcast.internal.networking.nio.iobalancer.IOBalancer;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.concurrent.BackoffIdleStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public final class NioNetworking
implements Networking {
    private final AtomicInteger nextInputThreadIndex = new AtomicInteger();
    private final AtomicInteger nextOutputThreadIndex = new AtomicInteger();
    private final ILogger logger;
    private final MetricsRegistry metricsRegistry;
    private final AtomicBoolean metricsRegistryScheduled = new AtomicBoolean(false);
    private final LoggingService loggingService;
    private final String threadNamePrefix;
    private final ChannelErrorHandler errorHandler;
    private final int balancerIntervalSeconds;
    private final int inputThreadCount;
    private final int outputThreadCount;
    private final Set<NioChannel> channels = Collections.newSetFromMap(new ConcurrentHashMap());
    private final ChannelCloseListener channelCloseListener = new ChannelCloseListenerImpl();
    private final SelectorMode selectorMode;
    private final BackoffIdleStrategy idleStrategy;
    private final boolean selectorWorkaroundTest;
    private volatile ExecutorService closeListenerExecutor;
    private volatile IOBalancer ioBalancer;
    private volatile NioThread[] inputThreads;
    private volatile NioThread[] outputThreads;
    @Probe
    private volatile long bytesSend;
    @Probe
    private volatile long bytesReceived;
    @Probe
    private volatile long packetsSend;
    @Probe
    private volatile long packetsReceived;

    public NioNetworking(Context ctx) {
        this.threadNamePrefix = ctx.threadNamePrefix;
        this.metricsRegistry = ctx.metricsRegistry;
        this.loggingService = ctx.loggingService;
        this.inputThreadCount = ctx.inputThreadCount;
        this.outputThreadCount = ctx.outputThreadCount;
        this.logger = this.loggingService.getLogger(NioNetworking.class);
        this.errorHandler = ctx.errorHandler;
        this.balancerIntervalSeconds = ctx.balancerIntervalSeconds;
        this.selectorMode = ctx.selectorMode;
        this.selectorWorkaroundTest = ctx.selectorWorkaroundTest;
        this.idleStrategy = ctx.idleStrategy;
        this.metricsRegistry.scanAndRegister(this, "tcp");
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="used only for testing")
    public NioThread[] getInputThreads() {
        return this.inputThreads;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="used only for testing")
    public NioThread[] getOutputThreads() {
        return this.outputThreads;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="used only for testing")
    public Set<NioChannel> getChannels() {
        return this.channels;
    }

    public IOBalancer getIOBalancer() {
        return this.ioBalancer;
    }

    @Override
    public void start() {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("TcpIpConnectionManager configured with Non Blocking IO-threading model: " + this.inputThreadCount + " input threads and " + this.outputThreadCount + " output threads");
        }
        this.logger.log(this.selectorMode != SelectorMode.SELECT ? Level.INFO : Level.FINE, "IO threads selector mode is " + (Object)((Object)this.selectorMode));
        if (this.metricsRegistryScheduled.compareAndSet(false, true) && this.metricsRegistry.minimumLevel().isEnabled(ProbeLevel.DEBUG)) {
            this.metricsRegistry.scheduleAtFixedRate(new PublishAllTask(), 1L, TimeUnit.SECONDS, ProbeLevel.INFO);
        }
        this.closeListenerExecutor = Executors.newSingleThreadExecutor(new ThreadFactory(){

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(NioNetworking.this.threadNamePrefix + "-NioNetworking-closeListenerExecutor");
                return t;
            }
        });
        NioThread[] inThreads = new NioThread[this.inputThreadCount];
        for (int i = 0; i < inThreads.length; ++i) {
            NioThread thread = new NioThread(ThreadUtil.createThreadPoolName(this.threadNamePrefix, "IO") + "in-" + i, this.loggingService.getLogger(NioThread.class), this.errorHandler, this.selectorMode, this.idleStrategy);
            thread.id = i;
            thread.setSelectorWorkaroundTest(this.selectorWorkaroundTest);
            inThreads[i] = thread;
            this.metricsRegistry.scanAndRegister(thread, "tcp.inputThread[" + thread.getName() + "]");
            thread.start();
        }
        this.inputThreads = inThreads;
        NioThread[] outThreads = new NioThread[this.outputThreadCount];
        for (int i = 0; i < outThreads.length; ++i) {
            NioThread thread = new NioThread(ThreadUtil.createThreadPoolName(this.threadNamePrefix, "IO") + "out-" + i, this.loggingService.getLogger(NioThread.class), this.errorHandler, this.selectorMode, this.idleStrategy);
            thread.id = i;
            thread.setSelectorWorkaroundTest(this.selectorWorkaroundTest);
            outThreads[i] = thread;
            this.metricsRegistry.scanAndRegister(thread, "tcp.outputThread[" + thread.getName() + "]");
            thread.start();
        }
        this.outputThreads = outThreads;
        this.startIOBalancer();
    }

    private void startIOBalancer() {
        this.ioBalancer = new IOBalancer(this.inputThreads, this.outputThreads, this.threadNamePrefix, this.balancerIntervalSeconds, this.loggingService);
        this.ioBalancer.start();
        this.metricsRegistry.scanAndRegister(this.ioBalancer, "tcp.balancer");
    }

    @Override
    public void shutdown() {
        this.ioBalancer.stop();
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Shutting down IO Threads... Total: " + (this.inputThreads.length + this.outputThreads.length));
        }
        this.shutdown(this.inputThreads);
        this.inputThreads = null;
        this.shutdown(this.outputThreads);
        this.outputThreads = null;
        this.closeListenerExecutor.shutdown();
        this.metricsRegistry.deregister(this.ioBalancer);
    }

    private void shutdown(NioThread[] threads) {
        if (threads == null) {
            return;
        }
        for (NioThread thread : threads) {
            thread.shutdown();
            this.metricsRegistry.deregister(thread);
        }
    }

    @Override
    public Channel register(EndpointQualifier endpointQualifier, ChannelInitializerProvider channelInitializerProvider, SocketChannel socketChannel, boolean clientMode) throws IOException {
        ChannelInitializer initializer = channelInitializerProvider.provide(endpointQualifier);
        assert (initializer != null) : "Found NULL channel initializer for endpoint-qualifier " + endpointQualifier;
        NioChannel channel = new NioChannel(socketChannel, clientMode, initializer, this.metricsRegistry, this.closeListenerExecutor);
        socketChannel.configureBlocking(false);
        NioInboundPipeline inboundPipeline = this.newInboundPipeline(channel);
        NioOutboundPipeline outboundPipeline = this.newOutboundPipeline(channel);
        channel.init(inboundPipeline, outboundPipeline);
        this.ioBalancer.channelAdded(inboundPipeline, outboundPipeline);
        channel.addCloseListener(this.channelCloseListener);
        this.channels.add(channel);
        return channel;
    }

    private NioOutboundPipeline newOutboundPipeline(NioChannel channel) {
        int index = HashUtil.hashToIndex(this.nextOutputThreadIndex.getAndIncrement(), this.outputThreadCount);
        NioThread[] threads = this.outputThreads;
        if (threads == null) {
            throw new IllegalStateException("NioNetworking is shutdown!");
        }
        return new NioOutboundPipeline(channel, threads[index], this.errorHandler, this.loggingService.getLogger(NioOutboundPipeline.class), this.ioBalancer);
    }

    private NioInboundPipeline newInboundPipeline(NioChannel channel) {
        int index = HashUtil.hashToIndex(this.nextInputThreadIndex.getAndIncrement(), this.inputThreadCount);
        NioThread[] threads = this.inputThreads;
        if (threads == null) {
            throw new IllegalStateException("NioNetworking is shutdown!");
        }
        return new NioInboundPipeline(channel, threads[index], this.errorHandler, this.loggingService.getLogger(NioInboundPipeline.class), this.ioBalancer);
    }

    public static class Context {
        private BackoffIdleStrategy idleStrategy;
        private LoggingService loggingService;
        private MetricsRegistry metricsRegistry;
        private String threadNamePrefix = "hz";
        private ChannelErrorHandler errorHandler;
        private int inputThreadCount = 1;
        private int outputThreadCount = 1;
        private int balancerIntervalSeconds;
        private SelectorMode selectorMode = SelectorMode.getConfiguredValue();
        private boolean selectorWorkaroundTest = Boolean.getBoolean("hazelcast.io.selector.workaround.test");

        public Context() {
            String selectorModeString = SelectorMode.getConfiguredString();
            if (selectorModeString.startsWith("selectnow,")) {
                this.idleStrategy = BackoffIdleStrategy.createBackoffIdleStrategy(selectorModeString);
            }
        }

        public Context selectorWorkaroundTest(boolean selectorWorkaroundTest) {
            this.selectorWorkaroundTest = selectorWorkaroundTest;
            return this;
        }

        public Context selectorMode(SelectorMode selectorMode) {
            this.selectorMode = selectorMode;
            return this;
        }

        public Context loggingService(LoggingService loggingService) {
            this.loggingService = loggingService;
            return this;
        }

        public Context metricsRegistry(MetricsRegistry metricsRegistry) {
            this.metricsRegistry = metricsRegistry;
            return this;
        }

        public Context threadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
            return this;
        }

        public Context errorHandler(ChannelErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Context inputThreadCount(int inputThreadCount) {
            this.inputThreadCount = inputThreadCount;
            return this;
        }

        public Context outputThreadCount(int outputThreadCount) {
            this.outputThreadCount = outputThreadCount;
            return this;
        }

        public Context balancerIntervalSeconds(int balancerIntervalSeconds) {
            this.balancerIntervalSeconds = balancerIntervalSeconds;
            return this;
        }
    }

    private class PublishAllTask
    implements Runnable {
        private PublishAllTask() {
        }

        @Override
        public void run() {
            for (NioChannel channel : NioNetworking.this.channels) {
                NioOutboundPipeline outboundPipeline;
                NioThread outputThread;
                final NioInboundPipeline inboundPipeline = channel.inboundPipeline;
                NioThread inputThread = inboundPipeline.owner();
                if (inputThread != null) {
                    inputThread.addTaskAndWakeup(new Runnable(){

                        @Override
                        public void run() {
                            inboundPipeline.publishMetrics();
                        }
                    });
                }
                if ((outputThread = (outboundPipeline = channel.outboundPipeline).owner()) == null) continue;
                outputThread.addTaskAndWakeup(new Runnable(){

                    @Override
                    public void run() {
                        outboundPipeline.publishMetrics();
                    }
                });
            }
            NioNetworking.this.bytesSend = this.bytesTransceived(NioNetworking.this.outputThreads);
            NioNetworking.this.bytesReceived = this.bytesTransceived(NioNetworking.this.inputThreads);
            NioNetworking.this.packetsSend = this.packetsTransceived(NioNetworking.this.outputThreads);
            NioNetworking.this.packetsReceived = this.packetsTransceived(NioNetworking.this.inputThreads);
        }

        private long bytesTransceived(NioThread[] threads) {
            if (threads == null) {
                return 0L;
            }
            long result = 0L;
            for (NioThread nioThread : threads) {
                result += nioThread.bytesTransceived;
            }
            return result;
        }

        private long packetsTransceived(NioThread[] threads) {
            if (threads == null) {
                return 0L;
            }
            long result = 0L;
            for (NioThread nioThread : threads) {
                result += nioThread.framesTransceived + nioThread.priorityFramesTransceived;
            }
            return result;
        }
    }

    private class ChannelCloseListenerImpl
    implements ChannelCloseListener {
        private ChannelCloseListenerImpl() {
        }

        @Override
        public void onClose(Channel channel) {
            NioChannel nioChannel = (NioChannel)channel;
            NioNetworking.this.channels.remove(channel);
            NioNetworking.this.ioBalancer.channelRemoved(nioChannel.inboundPipeline(), nioChannel.outboundPipeline());
            NioNetworking.this.metricsRegistry.deregister(nioChannel.inboundPipeline());
            NioNetworking.this.metricsRegistry.deregister(nioChannel.outboundPipeline());
        }
    }
}

