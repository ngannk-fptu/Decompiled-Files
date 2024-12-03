/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ServerSocketRegistry;
import com.hazelcast.internal.networking.nio.SelectorMode;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;
import com.hazelcast.nio.tcp.TcpIpNetworkingService;
import com.hazelcast.util.ThreadUtil;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TcpIpAcceptor
implements MetricsProvider {
    private static final long SHUTDOWN_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10L);
    private static final long SELECT_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60L);
    private static final int SELECT_IDLE_COUNT_THRESHOLD = 10;
    private final ServerSocketRegistry registry;
    private final TcpIpNetworkingService networkingService;
    private final ILogger logger;
    private final IOService ioService;
    @Probe
    private final SwCounter eventCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter exceptionCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter selectorRecreateCount = SwCounter.newSwCounter();
    private final AcceptorIOThread acceptorThread;
    private volatile long lastSelectTimeMs;
    private final boolean selectorWorkaround = SelectorMode.getConfiguredValue() == SelectorMode.SELECT_WITH_FIX;
    private volatile boolean stop;
    private volatile Selector selector;
    private final Set<SelectionKey> selectionKeys = Collections.newSetFromMap(new ConcurrentHashMap());

    TcpIpAcceptor(ServerSocketRegistry registry, TcpIpNetworkingService networkingService, IOService ioService) {
        this.registry = registry;
        this.networkingService = networkingService;
        this.ioService = networkingService.getIoService();
        this.logger = ioService.getLoggingService().getLogger(this.getClass());
        this.acceptorThread = new AcceptorIOThread();
    }

    @Probe
    private long idleTimeMs() {
        return Math.max(System.currentTimeMillis() - this.lastSelectTimeMs, 0L);
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "tcp." + this.acceptorThread.getName());
    }

    public TcpIpAcceptor start() {
        this.acceptorThread.start();
        return this;
    }

    public synchronized void shutdown() {
        if (this.stop) {
            return;
        }
        this.logger.finest("Shutting down SocketAcceptor thread.");
        this.stop = true;
        Selector sel = this.selector;
        if (sel != null) {
            sel.wakeup();
        }
        try {
            this.acceptorThread.join(SHUTDOWN_TIMEOUT_MILLIS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.finest(e);
        }
    }

    private final class AcceptorIOThread
    extends Thread {
        private AcceptorIOThread() {
            super(ThreadUtil.createThreadPoolName(TcpIpAcceptor.this.ioService.getHazelcastName(), "IO") + "Acceptor");
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            if (TcpIpAcceptor.this.logger.isFinestEnabled()) {
                TcpIpAcceptor.this.logger.finest("Starting TcpIpAcceptor on " + TcpIpAcceptor.this.registry);
            }
            try {
                TcpIpAcceptor.this.selector = Selector.open();
                for (ServerSocketRegistry.Pair entry : TcpIpAcceptor.this.registry) {
                    ServerSocketChannel serverSocketChannel = entry.getChannel();
                    serverSocketChannel.configureBlocking(false);
                    SelectionKey selectionKey = serverSocketChannel.register(TcpIpAcceptor.this.selector, 16);
                    selectionKey.attach(entry);
                    TcpIpAcceptor.this.selectionKeys.add(selectionKey);
                }
                if (TcpIpAcceptor.this.selectorWorkaround) {
                    this.acceptLoopWithSelectorFix();
                } else {
                    this.acceptLoop();
                }
            }
            catch (OutOfMemoryError e) {
                OutOfMemoryErrorDispatcher.onOutOfMemory(e);
            }
            catch (Throwable e) {
                TcpIpAcceptor.this.logger.severe(e.getClass().getName() + ": " + e.getMessage(), e);
            }
            finally {
                this.closeSelector();
            }
        }

        private void acceptLoop() throws IOException {
            while (!TcpIpAcceptor.this.stop) {
                int keyCount = TcpIpAcceptor.this.selector.select();
                if (this.isInterrupted()) break;
                if (keyCount == 0) continue;
                Iterator<SelectionKey> it = TcpIpAcceptor.this.selector.selectedKeys().iterator();
                this.handleSelectionKeys(it);
            }
        }

        private void acceptLoopWithSelectorFix() throws IOException {
            int idleCount = 0;
            while (!TcpIpAcceptor.this.stop) {
                long before = System.currentTimeMillis();
                int keyCount = TcpIpAcceptor.this.selector.select(SELECT_TIMEOUT_MILLIS);
                if (this.isInterrupted()) break;
                if (keyCount == 0) {
                    long selectTimeTaken = System.currentTimeMillis() - before;
                    idleCount = selectTimeTaken < SELECT_TIMEOUT_MILLIS ? idleCount + 1 : 0;
                    if (idleCount <= 10) continue;
                    this.rebuildSelector();
                    idleCount = 0;
                    continue;
                }
                idleCount = 0;
                Iterator<SelectionKey> it = TcpIpAcceptor.this.selector.selectedKeys().iterator();
                this.handleSelectionKeys(it);
            }
        }

        private void rebuildSelector() throws IOException {
            TcpIpAcceptor.this.selectorRecreateCount.inc();
            for (SelectionKey key : TcpIpAcceptor.this.selectionKeys) {
                key.cancel();
            }
            TcpIpAcceptor.this.selectionKeys.clear();
            this.closeSelector();
            Selector newSelector = Selector.open();
            TcpIpAcceptor.this.selector = newSelector;
            for (ServerSocketRegistry.Pair entry : TcpIpAcceptor.this.registry) {
                ServerSocketChannel serverSocketChannel = entry.getChannel();
                SelectionKey selectionKey = serverSocketChannel.register(newSelector, 16);
                selectionKey.attach(entry);
                TcpIpAcceptor.this.selectionKeys.add(selectionKey);
            }
        }

        private void handleSelectionKeys(Iterator<SelectionKey> it) {
            TcpIpAcceptor.this.lastSelectTimeMs = System.currentTimeMillis();
            while (it.hasNext()) {
                SelectionKey sk = it.next();
                it.remove();
                if (!sk.isValid() || !sk.isAcceptable()) continue;
                TcpIpAcceptor.this.eventCount.inc();
                ServerSocketRegistry.Pair attachment = (ServerSocketRegistry.Pair)sk.attachment();
                ServerSocketChannel serverSocketChannel = attachment.getChannel();
                this.acceptSocket(attachment.getQualifier(), serverSocketChannel);
            }
        }

        private void closeSelector() {
            if (TcpIpAcceptor.this.selector == null) {
                return;
            }
            if (TcpIpAcceptor.this.logger.isFinestEnabled()) {
                TcpIpAcceptor.this.logger.finest("Closing selector " + Thread.currentThread().getName());
            }
            try {
                TcpIpAcceptor.this.selector.close();
            }
            catch (Exception e) {
                TcpIpAcceptor.this.logger.finest("Exception while closing selector", e);
            }
        }

        private void acceptSocket(EndpointQualifier qualifier, ServerSocketChannel serverSocketChannel) {
            Channel channel = null;
            TcpIpEndpointManager endpointManager = null;
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                endpointManager = (TcpIpEndpointManager)TcpIpAcceptor.this.networkingService.getUnifiedOrDedicatedEndpointManager(qualifier);
                if (socketChannel != null) {
                    channel = endpointManager.newChannel(socketChannel, false);
                }
            }
            catch (Exception e) {
                TcpIpAcceptor.this.exceptionCount.inc();
                if (e instanceof ClosedChannelException && !TcpIpAcceptor.this.networkingService.isLive()) {
                    TcpIpAcceptor.this.logger.finest("Terminating socket acceptor thread...", e);
                }
                TcpIpAcceptor.this.logger.severe("Unexpected error while accepting connection! " + e.getClass().getName() + ": " + e.getMessage());
                try {
                    serverSocketChannel.close();
                }
                catch (Exception ex) {
                    TcpIpAcceptor.this.logger.finest("Closing server socket failed", ex);
                }
                TcpIpAcceptor.this.ioService.onFatalError(e);
            }
            if (channel != null) {
                final Channel theChannel = channel;
                if (TcpIpAcceptor.this.logger.isFineEnabled()) {
                    TcpIpAcceptor.this.logger.fine("Accepting socket connection from " + theChannel.socket().getRemoteSocketAddress());
                }
                if (TcpIpAcceptor.this.ioService.isSocketInterceptorEnabled(qualifier)) {
                    final TcpIpEndpointManager finalEndpointManager = endpointManager;
                    TcpIpAcceptor.this.ioService.executeAsync(new Runnable(){

                        @Override
                        public void run() {
                            AcceptorIOThread.this.configureAndAssignSocket(finalEndpointManager, theChannel);
                        }
                    });
                } else {
                    this.configureAndAssignSocket(endpointManager, theChannel);
                }
            }
        }

        private void configureAndAssignSocket(TcpIpEndpointManager endpointManager, Channel channel) {
            try {
                TcpIpAcceptor.this.ioService.interceptSocket(endpointManager.getEndpointQualifier(), channel.socket(), true);
                endpointManager.newConnection(channel, null);
            }
            catch (Exception e) {
                TcpIpAcceptor.this.exceptionCount.inc();
                TcpIpAcceptor.this.logger.warning(e.getClass().getName() + ": " + e.getMessage(), e);
                IOUtil.closeResource(channel);
            }
        }
    }
}

