/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 *  javax.cache.integration.CompletionListener
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CacheProxyUtil;
import com.hazelcast.cache.impl.ICacheInternal;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import com.hazelcast.util.executor.CompletableFutureTask;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.CacheException;
import javax.cache.integration.CompletionListener;

abstract class AbstractCacheProxyBase<K, V>
extends AbstractDistributedObject<ICacheService>
implements ICacheInternal<K, V> {
    private static final int TIMEOUT = 10;
    private static final double SIZING_FUDGE_FACTOR = 1.3;
    protected final ILogger logger;
    protected final CacheConfig<K, V> cacheConfig;
    protected final String name;
    protected final String nameWithPrefix;
    protected final ICacheService cacheService;
    protected final SerializationService serializationService;
    protected final CacheOperationProvider operationProvider;
    protected final IPartitionService partitionService;
    private final NodeEngine nodeEngine;
    private final CopyOnWriteArrayList<Future> loadAllTasks = new CopyOnWriteArrayList();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);

    AbstractCacheProxyBase(CacheConfig<K, V> cacheConfig, NodeEngine nodeEngine, ICacheService cacheService) {
        super(nodeEngine, cacheService);
        this.name = cacheConfig.getName();
        this.nameWithPrefix = cacheConfig.getNameWithPrefix();
        this.cacheConfig = cacheConfig;
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
        this.partitionService = nodeEngine.getPartitionService();
        this.cacheService = cacheService;
        this.serializationService = nodeEngine.getSerializationService();
        this.operationProvider = cacheService.getCacheOperationProvider(this.nameWithPrefix, cacheConfig.getInMemoryFormat());
    }

    void injectDependencies(Object obj) {
        ManagedContext managedContext = this.serializationService.getManagedContext();
        managedContext.initialize(obj);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected String getDistributedObjectName() {
        return this.nameWithPrefix;
    }

    @Override
    public String getPrefixedName() {
        return this.nameWithPrefix;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public void open() {
        if (this.isDestroyed.get()) {
            throw new IllegalStateException("Cache is already destroyed! Cannot be reopened");
        }
        this.isClosed.compareAndSet(true, false);
    }

    public void close() {
        this.close0(false);
    }

    private void close0(boolean destroy) {
        if (!this.isClosed.compareAndSet(false, true)) {
            return;
        }
        Exception caughtException = null;
        for (Future f : this.loadAllTasks) {
            try {
                f.get(10L, TimeUnit.SECONDS);
            }
            catch (Exception e) {
                if (caughtException == null) {
                    caughtException = e;
                }
                this.getNodeEngine().getLogger(this.getClass()).warning("Problem while waiting for loadAll tasks to complete", e);
            }
        }
        this.loadAllTasks.clear();
        this.closeListeners();
        if (!destroy) {
            this.resetCacheManager();
        }
        if (caughtException != null) {
            throw new CacheException("Problem while waiting for loadAll tasks to complete", (Throwable)caughtException);
        }
    }

    @Override
    protected boolean preDestroy() {
        this.close0(true);
        if (!this.isDestroyed.compareAndSet(false, true)) {
            return false;
        }
        this.isClosed.set(true);
        return true;
    }

    public boolean isClosed() {
        return this.isClosed.get();
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed.get();
    }

    abstract void closeListeners();

    void ensureOpen() {
        if (this.isClosed()) {
            throw new IllegalStateException("Cache operations can not be performed. The cache closed");
        }
    }

    void submitLoadAllTask(LoadAllTask loadAllTask) {
        ExecutionService executionService = this.nodeEngine.getExecutionService();
        final CompletableFutureTask future = (CompletableFutureTask)executionService.submit("loadAll-" + this.nameWithPrefix, loadAllTask);
        this.loadAllTasks.add(future);
        future.andThen(new ExecutionCallback(){

            public void onResponse(Object response) {
                AbstractCacheProxyBase.this.loadAllTasks.remove(future);
            }

            @Override
            public void onFailure(Throwable t) {
                AbstractCacheProxyBase.this.loadAllTasks.remove(future);
                AbstractCacheProxyBase.this.getNodeEngine().getLogger(this.getClass()).warning("Problem in loadAll task", t);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractCacheProxyBase that = (AbstractCacheProxyBase)o;
        return !(this.nameWithPrefix != null ? !this.nameWithPrefix.equals(that.nameWithPrefix) : that.nameWithPrefix != null);
    }

    @Override
    public int hashCode() {
        return this.nameWithPrefix != null ? this.nameWithPrefix.hashCode() : 0;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + '{' + "name=" + this.name + ", nameWithPrefix=" + this.nameWithPrefix + '}';
    }

    final class LoadAllTask
    implements Runnable {
        private final CompletionListener completionListener;
        private final CacheOperationProvider operationProvider;
        private final Set<Data> keysData;
        private final boolean replaceExistingValues;

        LoadAllTask(CacheOperationProvider operationProvider, Set<Data> keysData, boolean replaceExistingValues, CompletionListener completionListener) {
            this.operationProvider = operationProvider;
            this.keysData = keysData;
            this.replaceExistingValues = replaceExistingValues;
            this.completionListener = completionListener;
        }

        @Override
        public void run() {
            block7: {
                try {
                    AbstractCacheProxyBase.this.injectDependencies(this.completionListener);
                    OperationService operationService = AbstractCacheProxyBase.this.getNodeEngine().getOperationService();
                    IPartitionService partitionService = AbstractCacheProxyBase.this.getNodeEngine().getPartitionService();
                    Map<Address, List<Integer>> memberPartitionsMap = partitionService.getMemberPartitionsMap();
                    Map<Integer, Object> results = MapUtil.createHashMap(partitionService.getPartitionCount());
                    for (Map.Entry<Address, List<Integer>> memberPartitions : memberPartitionsMap.entrySet()) {
                        HashSet<Integer> partitions = new HashSet<Integer>((Collection)memberPartitions.getValue());
                        Set<Data> ownerKeys = this.filterOwnerKeys(partitionService, partitions);
                        OperationFactory operationFactory = this.operationProvider.createLoadAllOperationFactory(ownerKeys, this.replaceExistingValues);
                        Map memberResults = operationService.invokeOnPartitions(AbstractCacheProxyBase.this.getServiceName(), operationFactory, partitions);
                        results.putAll(memberResults);
                    }
                    CacheProxyUtil.validateResults(results);
                    if (this.completionListener != null) {
                        this.completionListener.onCompletion();
                    }
                }
                catch (Exception e) {
                    if (this.completionListener != null) {
                        this.completionListener.onException(e);
                    }
                }
                catch (Throwable t) {
                    if (t instanceof OutOfMemoryError) {
                        throw ExceptionUtil.rethrow(t);
                    }
                    if (this.completionListener == null) break block7;
                    this.completionListener.onException((Exception)new CacheException(t));
                }
            }
        }

        private Set<Data> filterOwnerKeys(IPartitionService partitionService, Set<Integer> partitions) {
            int roughSize = (int)((double)(this.keysData.size() * partitions.size()) / (double)partitionService.getPartitionCount() * 1.3);
            Set<Data> ownerKeys = SetUtil.createHashSet(roughSize);
            for (Data key : this.keysData) {
                int keyPartitionId = partitionService.getPartitionId(key);
                if (!partitions.contains(keyPartitionId)) continue;
                ownerKeys.add(key);
            }
            return ownerKeys;
        }
    }
}

