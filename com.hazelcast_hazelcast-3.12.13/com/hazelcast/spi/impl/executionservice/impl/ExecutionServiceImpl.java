/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.spi.impl.executionservice.impl.CompletableFutureEntry;
import com.hazelcast.spi.impl.executionservice.impl.CompletableFutureTask;
import com.hazelcast.spi.impl.executionservice.impl.DelegatingTaskScheduler;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.executor.CachedExecutorServiceDelegate;
import com.hazelcast.util.executor.ExecutorType;
import com.hazelcast.util.executor.LoggingScheduledExecutor;
import com.hazelcast.util.executor.ManagedExecutorService;
import com.hazelcast.util.executor.NamedThreadPoolExecutor;
import com.hazelcast.util.executor.PoolExecutorThreadFactory;
import com.hazelcast.util.executor.SingleExecutorThreadFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ExecutionServiceImpl
implements InternalExecutionService {
    private static final int CORE_POOL_SIZE = 3;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final long INITIAL_DELAY = 1000L;
    private static final long PERIOD = 100L;
    private static final int BEGIN_INDEX = 3;
    private static final long AWAIT_TIME = 3L;
    private static final int POOL_MULTIPLIER = 2;
    private static final int QUEUE_MULTIPLIER = 100000;
    private static final int ASYNC_QUEUE_CAPACITY = 100000;
    private static final int OFFLOADABLE_QUEUE_CAPACITY = 100000;
    private final NodeEngineImpl nodeEngine;
    private final ExecutorService cachedExecutorService;
    private final LoggingScheduledExecutor scheduledExecutorService;
    private final TaskScheduler globalTaskScheduler;
    private final ILogger logger;
    private final CompletableFutureTask completableFutureTask;
    private final ConcurrentMap<String, ManagedExecutorService> executors = new ConcurrentHashMap<String, ManagedExecutorService>();
    private final ConcurrentMap<String, ManagedExecutorService> durableExecutors = new ConcurrentHashMap<String, ManagedExecutorService>();
    private final ConcurrentMap<String, ManagedExecutorService> scheduleDurableExecutors = new ConcurrentHashMap<String, ManagedExecutorService>();
    private final ConstructorFunction<String, ManagedExecutorService> constructor = new ConstructorFunction<String, ManagedExecutorService>(){

        @Override
        public ManagedExecutorService createNew(String name) {
            ExecutorConfig config = ExecutionServiceImpl.this.nodeEngine.getConfig().findExecutorConfig(name);
            int queueCapacity = config.getQueueCapacity() <= 0 ? Integer.MAX_VALUE : config.getQueueCapacity();
            return ExecutionServiceImpl.this.createExecutor(name, config.getPoolSize(), queueCapacity, ExecutorType.CACHED, null);
        }
    };
    private final ConstructorFunction<String, ManagedExecutorService> durableConstructor = new ConstructorFunction<String, ManagedExecutorService>(){

        @Override
        public ManagedExecutorService createNew(String name) {
            DurableExecutorConfig cfg = ExecutionServiceImpl.this.nodeEngine.getConfig().findDurableExecutorConfig(name);
            return ExecutionServiceImpl.this.createExecutor(name, cfg.getPoolSize(), Integer.MAX_VALUE, ExecutorType.CACHED, null);
        }
    };
    private final ConstructorFunction<String, ManagedExecutorService> scheduledDurableConstructor = new ConstructorFunction<String, ManagedExecutorService>(){

        @Override
        public ManagedExecutorService createNew(String name) {
            ScheduledExecutorConfig cfg = ExecutionServiceImpl.this.nodeEngine.getConfig().findScheduledExecutorConfig(name);
            return ExecutionServiceImpl.this.createExecutor(name, cfg.getPoolSize(), Integer.MAX_VALUE, ExecutorType.CACHED, null);
        }
    };
    private final MetricsRegistry metricsRegistry;

    public ExecutionServiceImpl(NodeEngineImpl nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.metricsRegistry = nodeEngine.getMetricsRegistry();
        Node node = nodeEngine.getNode();
        this.logger = node.getLogger(ExecutionService.class.getName());
        String hzName = nodeEngine.getHazelcastInstance().getName();
        ClassLoader configClassLoader = node.getConfigClassLoader();
        PoolExecutorThreadFactory threadFactory = new PoolExecutorThreadFactory(ThreadUtil.createThreadPoolName(hzName, "cached"), configClassLoader);
        this.cachedExecutorService = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory, new RejectedExecutionHandler(){

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (ExecutionServiceImpl.this.logger.isFinestEnabled()) {
                    ExecutionServiceImpl.this.logger.finest("Node is shutting down; discarding the task: " + r);
                }
            }
        });
        SingleExecutorThreadFactory singleExecutorThreadFactory = new SingleExecutorThreadFactory(configClassLoader, ThreadUtil.createThreadPoolName(hzName, "scheduled"));
        this.scheduledExecutorService = new LoggingScheduledExecutor(this.logger, 1, (ThreadFactory)singleExecutorThreadFactory, nodeEngine.getProperties().getBoolean(GroupProperty.TASK_SCHEDULER_REMOVE_ON_CANCEL));
        int coreSize = Math.max(RuntimeAvailableProcessors.get(), 2);
        this.register("hz:system", coreSize, Integer.MAX_VALUE, ExecutorType.CACHED);
        this.register("hz:scheduled", coreSize * 2, coreSize * 100000, ExecutorType.CACHED);
        this.register("hz:async", coreSize, 100000, ExecutorType.CONCRETE);
        this.register("hz:offloadable", coreSize, 100000, ExecutorType.CACHED);
        this.globalTaskScheduler = this.getTaskScheduler("hz:scheduled");
        this.completableFutureTask = new CompletableFutureTask();
        this.scheduleWithRepetition(this.completableFutureTask, 1000L, 100L, TimeUnit.MILLISECONDS);
    }

    public LoggingScheduledExecutor getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }

    @Override
    public ManagedExecutorService register(String name, int defaultPoolSize, int defaultQueueCapacity, ExecutorType type) {
        return this.register(name, defaultPoolSize, defaultQueueCapacity, type, null);
    }

    @Override
    public ManagedExecutorService register(String name, int defaultPoolSize, int defaultQueueCapacity, ThreadFactory threadFactory) {
        return this.register(name, defaultPoolSize, defaultQueueCapacity, ExecutorType.CONCRETE, threadFactory);
    }

    private ManagedExecutorService register(String name, int defaultPoolSize, int defaultQueueCapacity, ExecutorType type, ThreadFactory threadFactory) {
        ManagedExecutorService executor;
        ExecutorConfig config = this.nodeEngine.getConfig().getExecutorConfigs().get(name);
        int poolSize = defaultPoolSize;
        int queueCapacity = defaultQueueCapacity;
        if (config != null) {
            poolSize = config.getPoolSize();
            queueCapacity = config.getQueueCapacity() <= 0 ? Integer.MAX_VALUE : config.getQueueCapacity();
        }
        if (this.executors.putIfAbsent(name, executor = this.createExecutor(name, poolSize, queueCapacity, type, threadFactory)) != null) {
            throw new IllegalArgumentException("ExecutorService['" + name + "'] already exists!");
        }
        this.metricsRegistry.scanAndRegister(executor, "internal-executor[" + name + "]");
        return executor;
    }

    private ManagedExecutorService createExecutor(String name, int poolSize, int queueCapacity, ExecutorType type, ThreadFactory threadFactory) {
        ManagedExecutorService executor;
        if (type == ExecutorType.CACHED) {
            if (threadFactory != null) {
                throw new IllegalArgumentException("Cached executor can not be used with external thread factory");
            }
            executor = new CachedExecutorServiceDelegate(this.nodeEngine, name, this.cachedExecutorService, poolSize, queueCapacity);
        } else if (type == ExecutorType.CONCRETE) {
            if (threadFactory == null) {
                ClassLoader classLoader = this.nodeEngine.getConfigClassLoader();
                String hzName = this.nodeEngine.getHazelcastInstance().getName();
                String internalName = name.startsWith("hz:") ? name.substring(3) : name;
                String threadNamePrefix = ThreadUtil.createThreadPoolName(hzName, internalName);
                threadFactory = new PoolExecutorThreadFactory(threadNamePrefix, classLoader);
            }
            NamedThreadPoolExecutor pool = new NamedThreadPoolExecutor(name, poolSize, poolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueCapacity), threadFactory);
            pool.allowCoreThreadTimeOut(true);
            executor = pool;
        } else {
            throw new IllegalArgumentException("Unknown executor type: " + (Object)((Object)type));
        }
        return executor;
    }

    @Override
    public ManagedExecutorService getExecutor(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.executors, name, this.constructor);
    }

    @Override
    public ManagedExecutorService getDurable(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.durableExecutors, name, this.durableConstructor);
    }

    @Override
    public ExecutorService getScheduledDurable(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.scheduleDurableExecutors, name, this.scheduledDurableConstructor);
    }

    @Override
    public <V> ICompletableFuture<V> asCompletableFuture(Future<V> future) {
        if (future == null) {
            throw new IllegalArgumentException("future must not be null");
        }
        if (future instanceof ICompletableFuture) {
            return (ICompletableFuture)future;
        }
        return this.registerCompletableFuture(future);
    }

    @Override
    public void execute(String name, Runnable command) {
        this.getExecutor(name).execute(command);
    }

    @Override
    public void executeDurable(String name, Runnable command) {
        this.getDurable(name).execute(command);
    }

    @Override
    public Future<?> submit(String name, Runnable task) {
        return this.getExecutor(name).submit(task);
    }

    @Override
    public <T> Future<T> submit(String name, Callable<T> task) {
        return this.getExecutor(name).submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.globalTaskScheduler.schedule(command, delay, unit);
    }

    @Override
    public ScheduledFuture<?> schedule(String name, Runnable command, long delay, TimeUnit unit) {
        return this.getTaskScheduler(name).schedule(command, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleDurable(String name, Runnable command, long delay, TimeUnit unit) {
        return this.getDurableTaskScheduler(name).schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<Future<V>> scheduleDurable(String name, Callable<V> command, long delay, TimeUnit unit) {
        return this.getDurableTaskScheduler(name).schedule(command, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithRepetition(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.globalTaskScheduler.scheduleWithRepetition(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithRepetition(String name, Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.getTaskScheduler(name).scheduleWithRepetition(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleDurableWithRepetition(String name, Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.getDurableTaskScheduler(name).scheduleWithRepetition(command, initialDelay, period, unit);
    }

    @Override
    public TaskScheduler getGlobalTaskScheduler() {
        return this.globalTaskScheduler;
    }

    @Override
    public TaskScheduler getTaskScheduler(String name) {
        return new DelegatingTaskScheduler(this.scheduledExecutorService, this.getExecutor(name));
    }

    public void shutdown() {
        this.logger.finest("Stopping executors...");
        this.scheduledExecutorService.notifyShutdownInitiated();
        for (ExecutorService executorService : this.executors.values()) {
            executorService.shutdown();
        }
        for (ExecutorService executorService : this.durableExecutors.values()) {
            executorService.shutdown();
        }
        for (ExecutorService executorService : this.scheduleDurableExecutors.values()) {
            executorService.shutdown();
        }
        this.scheduledExecutorService.shutdownNow();
        this.cachedExecutorService.shutdown();
        try {
            this.scheduledExecutorService.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.finest(e);
        }
        try {
            this.cachedExecutorService.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.finest(e);
        }
        this.executors.clear();
        this.durableExecutors.clear();
        this.scheduleDurableExecutors.clear();
    }

    @Override
    public void shutdownExecutor(String name) {
        ExecutorService executorService = (ExecutorService)this.executors.remove(name);
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void shutdownDurableExecutor(String name) {
        ExecutorService executorService = (ExecutorService)this.durableExecutors.remove(name);
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void shutdownScheduledDurableExecutor(String name) {
        ExecutorService executorService = (ExecutorService)this.scheduleDurableExecutors.remove(name);
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private <V> ICompletableFuture<V> registerCompletableFuture(Future<V> future) {
        CompletableFutureEntry<V> entry = new CompletableFutureEntry<V>(future, this.nodeEngine);
        this.completableFutureTask.registerCompletableFutureEntry(entry);
        return entry.completableFuture;
    }

    private TaskScheduler getDurableTaskScheduler(String name) {
        return new DelegatingTaskScheduler(this.scheduledExecutorService, this.getScheduledDurable(name));
    }
}

