/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl;

import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.executor.impl.ExecutorServiceProxy;
import com.hazelcast.logging.ILogger;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.monitor.impl.LocalExecutorStatsImpl;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.MapUtil;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DistributedExecutorService
implements ManagedService,
RemoteService,
StatisticsAwareService<LocalExecutorStats>,
QuorumAwareService {
    public static final String SERVICE_NAME = "hz:impl:executorService";
    private static final Object NULL_OBJECT = new Object();
    private static final AtomicReferenceFieldUpdater<CallableProcessor, Boolean> RESPONSE_FLAG = AtomicReferenceFieldUpdater.newUpdater(CallableProcessor.class, Boolean.class, "responseFlag");
    final ConcurrentMap<String, ExecutorConfig> executorConfigCache = new ConcurrentHashMap<String, ExecutorConfig>();
    private NodeEngine nodeEngine;
    private ExecutionService executionService;
    private final ConcurrentMap<String, CallableProcessor> submittedTasks = new ConcurrentHashMap<String, CallableProcessor>(100);
    private final Set<String> shutdownExecutors = Collections.newSetFromMap(new ConcurrentHashMap());
    private final ConcurrentHashMap<String, LocalExecutorStatsImpl> statsMap = new ConcurrentHashMap();
    private final ConstructorFunction<String, LocalExecutorStatsImpl> localExecutorStatsConstructorFunction = new ConstructorFunction<String, LocalExecutorStatsImpl>(){

        @Override
        public LocalExecutorStatsImpl createNew(String key) {
            return new LocalExecutorStatsImpl();
        }
    };
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            ExecutorConfig executorConfig = DistributedExecutorService.this.nodeEngine.getConfig().findExecutorConfig(name);
            String quorumName = executorConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };
    private ILogger logger;

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
        this.executionService = nodeEngine.getExecutionService();
        this.logger = nodeEngine.getLogger(DistributedExecutorService.class);
    }

    @Override
    public void reset() {
        this.shutdownExecutors.clear();
        this.submittedTasks.clear();
        this.statsMap.clear();
        this.executorConfigCache.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    public void execute(String name, String uuid, Callable callable, Operation op) {
        ExecutorConfig cfg = this.getOrFindExecutorConfig(name);
        if (cfg.isStatisticsEnabled()) {
            this.startPending(name);
        }
        CallableProcessor processor = new CallableProcessor(name, uuid, callable, op, cfg.isStatisticsEnabled());
        if (uuid != null) {
            this.submittedTasks.put(uuid, processor);
        }
        try {
            this.executionService.execute(name, processor);
        }
        catch (RejectedExecutionException e) {
            if (cfg.isStatisticsEnabled()) {
                this.rejectExecution(name);
            }
            this.logger.warning("While executing " + callable + " on Executor[" + name + "]", e);
            if (uuid != null) {
                this.submittedTasks.remove(uuid);
            }
            processor.sendResponse(e);
        }
    }

    public boolean cancel(String uuid, boolean interrupt) {
        CallableProcessor processor = (CallableProcessor)this.submittedTasks.remove(uuid);
        if (processor != null && processor.cancel(interrupt) && processor.sendResponse(new CancellationException())) {
            if (processor.isStatisticsEnabled()) {
                this.getLocalExecutorStats(processor.name).cancelExecution();
            }
            return true;
        }
        return false;
    }

    public String getName(String uuid) {
        CallableProcessor proc = (CallableProcessor)this.submittedTasks.get(uuid);
        if (proc != null) {
            return proc.name;
        }
        return null;
    }

    public void shutdownExecutor(String name) {
        this.executionService.shutdownExecutor(name);
        this.shutdownExecutors.add(name);
        this.executorConfigCache.remove(name);
    }

    public boolean isShutdown(String name) {
        return this.shutdownExecutors.contains(name);
    }

    @Override
    public ExecutorServiceProxy createDistributedObject(String name) {
        return new ExecutorServiceProxy(name, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String name) {
        this.shutdownExecutors.remove(name);
        this.executionService.shutdownExecutor(name);
        this.statsMap.remove(name);
        this.executorConfigCache.remove(name);
        this.quorumConfigCache.remove(name);
    }

    LocalExecutorStatsImpl getLocalExecutorStats(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.statsMap, name, this.localExecutorStatsConstructorFunction);
    }

    private void startExecution(String name, long elapsed) {
        this.getLocalExecutorStats(name).startExecution(elapsed);
    }

    private void finishExecution(String name, long elapsed) {
        this.getLocalExecutorStats(name).finishExecution(elapsed);
    }

    private void startPending(String name) {
        this.getLocalExecutorStats(name).startPending();
    }

    private void rejectExecution(String name) {
        this.getLocalExecutorStats(name).rejectExecution();
    }

    @Override
    public Map<String, LocalExecutorStats> getStats() {
        Map<String, LocalExecutorStats> executorStats = MapUtil.createHashMap(this.statsMap.size());
        for (Map.Entry<String, LocalExecutorStatsImpl> queueStat : this.statsMap.entrySet()) {
            executorStats.put(queueStat.getKey(), queueStat.getValue());
        }
        return executorStats;
    }

    private ExecutorConfig getOrFindExecutorConfig(String name) {
        ExecutorConfig cfg = (ExecutorConfig)this.executorConfigCache.get(name);
        if (cfg != null) {
            return cfg;
        }
        cfg = this.nodeEngine.getConfig().findExecutorConfig(name);
        ExecutorConfig executorConfig = this.executorConfigCache.putIfAbsent(name, cfg);
        return executorConfig == null ? cfg : executorConfig;
    }

    @Override
    public String getQuorumName(String name) {
        if (name == null) {
            return null;
        }
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }

    private final class CallableProcessor
    extends FutureTask
    implements Runnable {
        volatile Boolean responseFlag;
        private final String name;
        private final String uuid;
        private final Operation op;
        private final String callableToString;
        private final long creationTime;
        private final boolean statisticsEnabled;

        private CallableProcessor(String name, String uuid, Callable callable, Operation op, boolean statisticsEnabled) {
            super(callable);
            this.responseFlag = Boolean.FALSE;
            this.creationTime = Clock.currentTimeMillis();
            this.name = name;
            this.uuid = uuid;
            this.callableToString = String.valueOf(callable);
            this.op = op;
            this.statisticsEnabled = statisticsEnabled;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            long start = Clock.currentTimeMillis();
            if (this.statisticsEnabled) {
                DistributedExecutorService.this.startExecution(this.name, start - this.creationTime);
            }
            Exception result = null;
            try {
                super.run();
                if (!this.isCancelled()) {
                    result = (Exception)this.get();
                }
            }
            catch (Exception e) {
                this.logException(e);
                result = e;
            }
            finally {
                if (this.uuid != null) {
                    DistributedExecutorService.this.submittedTasks.remove(this.uuid);
                }
                if (!this.isCancelled()) {
                    this.sendResponse(result);
                    if (this.statisticsEnabled) {
                        DistributedExecutorService.this.finishExecution(this.name, Clock.currentTimeMillis() - start);
                    }
                }
            }
        }

        private void logException(Exception e) {
            if (DistributedExecutorService.this.logger.isFinestEnabled()) {
                DistributedExecutorService.this.logger.finest("While executing callable: " + this.callableToString, e);
            }
        }

        private boolean sendResponse(Object result) {
            if (RESPONSE_FLAG.compareAndSet(this, Boolean.FALSE, Boolean.TRUE)) {
                try {
                    this.op.sendResponse(result);
                }
                catch (HazelcastSerializationException e) {
                    this.op.sendResponse(e);
                }
                return true;
            }
            return false;
        }

        boolean isStatisticsEnabled() {
            return this.statisticsEnabled;
        }
    }
}

