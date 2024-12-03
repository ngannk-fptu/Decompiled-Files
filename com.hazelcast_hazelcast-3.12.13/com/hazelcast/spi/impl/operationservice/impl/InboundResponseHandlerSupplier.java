/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.util.concurrent.MPSCQueue;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.operationexecutor.OperationHostileThread;
import com.hazelcast.spi.impl.operationservice.impl.InboundResponseHandler;
import com.hazelcast.spi.impl.operationservice.impl.InvocationRegistry;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.MutableInteger;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.concurrent.BackoffIdleStrategy;
import com.hazelcast.util.concurrent.BusySpinIdleStrategy;
import com.hazelcast.util.concurrent.IdleStrategy;
import com.hazelcast.util.function.Consumer;
import com.hazelcast.util.function.Supplier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class InboundResponseHandlerSupplier
implements MetricsProvider,
Supplier<Consumer<Packet>> {
    public static final HazelcastProperty IDLE_STRATEGY = new HazelcastProperty("hazelcast.operation.responsequeue.idlestrategy", "block");
    private static final ThreadLocal<MutableInteger> INT_HOLDER = new ThreadLocal<MutableInteger>(){

        @Override
        protected MutableInteger initialValue() {
            return new MutableInteger();
        }
    };
    private static final long IDLE_MAX_SPINS = 20L;
    private static final long IDLE_MAX_YIELDS = 50L;
    private static final long IDLE_MIN_PARK_NS = TimeUnit.NANOSECONDS.toNanos(1L);
    private static final long IDLE_MAX_PARK_NS = TimeUnit.MICROSECONDS.toNanos(100L);
    private final ResponseThread[] responseThreads;
    private final ILogger logger;
    private final Consumer<Packet> responseHandler;
    private final InboundResponseHandler[] inboundResponseHandlers;
    private final NodeEngine nodeEngine;
    private final InvocationRegistry invocationRegistry;
    private final HazelcastProperties properties;

    InboundResponseHandlerSupplier(ClassLoader classLoader, InvocationRegistry invocationRegistry, String hzName, NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.invocationRegistry = invocationRegistry;
        this.logger = nodeEngine.getLogger(InboundResponseHandlerSupplier.class);
        this.properties = nodeEngine.getProperties();
        int responseThreadCount = this.properties.getInteger(GroupProperty.RESPONSE_THREAD_COUNT);
        if (responseThreadCount < 0) {
            throw new IllegalArgumentException(GroupProperty.RESPONSE_THREAD_COUNT.getName() + " can't be smaller than 0");
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Running with " + responseThreadCount + " response threads");
        }
        this.responseThreads = new ResponseThread[responseThreadCount];
        if (responseThreadCount == 0) {
            this.inboundResponseHandlers = new InboundResponseHandler[1];
            this.inboundResponseHandlers[0] = new InboundResponseHandler(invocationRegistry, nodeEngine);
            this.responseHandler = this.inboundResponseHandlers[0];
        } else {
            this.inboundResponseHandlers = new InboundResponseHandler[responseThreadCount];
            for (int k = 0; k < this.responseThreads.length; ++k) {
                ResponseThread responseThread = new ResponseThread(hzName, k);
                responseThread.setContextClassLoader(classLoader);
                this.responseThreads[k] = responseThread;
                this.inboundResponseHandlers[k] = responseThread.inboundResponseHandler;
            }
            this.responseHandler = responseThreadCount == 1 ? new AsyncSingleThreadedResponseHandler() : new AsyncMultithreadedResponseHandler();
        }
    }

    public InboundResponseHandler backupHandler() {
        return this.inboundResponseHandlers[0];
    }

    @Probe(level=ProbeLevel.MANDATORY)
    public int responseQueueSize() {
        int result = 0;
        for (ResponseThread responseThread : this.responseThreads) {
            result += responseThread.responseQueue.size();
        }
        return result;
    }

    @Probe(name="responses[normal]", level=ProbeLevel.MANDATORY)
    long responsesNormal() {
        long result = 0L;
        for (InboundResponseHandler handler : this.inboundResponseHandlers) {
            result += handler.responsesNormal.get();
        }
        return result;
    }

    @Probe(name="responses[timeout]", level=ProbeLevel.MANDATORY)
    long responsesTimeout() {
        long result = 0L;
        for (InboundResponseHandler handler : this.inboundResponseHandlers) {
            result += handler.responsesTimeout.get();
        }
        return result;
    }

    @Probe(name="responses[backup]", level=ProbeLevel.MANDATORY)
    long responsesBackup() {
        long result = 0L;
        for (InboundResponseHandler handler : this.inboundResponseHandlers) {
            result += handler.responsesBackup.get();
        }
        return result;
    }

    @Probe(name="responses[error]", level=ProbeLevel.MANDATORY)
    long responsesError() {
        long result = 0L;
        for (InboundResponseHandler handler : this.inboundResponseHandlers) {
            result += handler.responsesError.get();
        }
        return result;
    }

    @Probe(name="responses[missing]", level=ProbeLevel.MANDATORY)
    long responsesMissing() {
        long result = 0L;
        for (InboundResponseHandler handler : this.inboundResponseHandlers) {
            result += handler.responsesMissing.get();
        }
        return result;
    }

    @Override
    public void provideMetrics(MetricsRegistry registry) {
        registry.scanAndRegister(this, "operation");
    }

    @Override
    public Consumer<Packet> get() {
        return this.responseHandler;
    }

    public void start() {
        for (ResponseThread responseThread : this.responseThreads) {
            responseThread.start();
        }
    }

    public void shutdown() {
        for (ResponseThread responseThread : this.responseThreads) {
            responseThread.shutdown();
        }
    }

    public static IdleStrategy getIdleStrategy(HazelcastProperties properties, HazelcastProperty property) {
        String idleStrategyString = properties.getString(property);
        if ("block".equals(idleStrategyString)) {
            return null;
        }
        if ("busyspin".equals(idleStrategyString)) {
            return new BusySpinIdleStrategy();
        }
        if ("backoff".equals(idleStrategyString)) {
            return new BackoffIdleStrategy(20L, 50L, IDLE_MIN_PARK_NS, IDLE_MAX_PARK_NS);
        }
        if (idleStrategyString.startsWith("backoff,")) {
            return BackoffIdleStrategy.createBackoffIdleStrategy(idleStrategyString);
        }
        throw new IllegalStateException("Unrecognized " + property.getName() + " value=" + idleStrategyString);
    }

    private final class ResponseThread
    extends Thread
    implements OperationHostileThread {
        private final BlockingQueue<Packet> responseQueue;
        private final InboundResponseHandler inboundResponseHandler;
        private volatile boolean shutdown;

        private ResponseThread(String hzName, int threadIndex) {
            super(ThreadUtil.createThreadName(hzName, "response-" + threadIndex));
            this.inboundResponseHandler = new InboundResponseHandler(InboundResponseHandlerSupplier.this.invocationRegistry, InboundResponseHandlerSupplier.this.nodeEngine);
            this.responseQueue = new MPSCQueue<Packet>(this, InboundResponseHandlerSupplier.getIdleStrategy(InboundResponseHandlerSupplier.this.properties, IDLE_STRATEGY));
        }

        @Override
        public void run() {
            try {
                this.doRun();
            }
            catch (InterruptedException e) {
                EmptyStatement.ignore(e);
            }
            catch (Throwable t) {
                OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(t);
                InboundResponseHandlerSupplier.this.logger.severe(t);
            }
        }

        private void doRun() throws InterruptedException {
            while (!this.shutdown) {
                Packet response = this.responseQueue.take();
                try {
                    this.inboundResponseHandler.accept(response);
                }
                catch (Throwable e) {
                    OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(e);
                    InboundResponseHandlerSupplier.this.logger.severe("Failed to process response: " + response + " on:" + this.getName(), e);
                }
            }
        }

        private void shutdown() {
            this.shutdown = true;
            this.interrupt();
        }
    }

    final class AsyncMultithreadedResponseHandler
    implements Consumer<Packet> {
        AsyncMultithreadedResponseHandler() {
        }

        @Override
        public void accept(Packet packet) {
            int threadIndex = HashUtil.hashToIndex(((MutableInteger)INT_HOLDER.get()).getAndInc(), InboundResponseHandlerSupplier.this.responseThreads.length);
            InboundResponseHandlerSupplier.this.responseThreads[threadIndex].responseQueue.add(packet);
        }
    }

    final class AsyncSingleThreadedResponseHandler
    implements Consumer<Packet> {
        private final ResponseThread responseThread;

        private AsyncSingleThreadedResponseHandler() {
            this.responseThread = InboundResponseHandlerSupplier.this.responseThreads[0];
        }

        @Override
        public void accept(Packet packet) {
            this.responseThread.responseQueue.add(packet);
        }
    }
}

