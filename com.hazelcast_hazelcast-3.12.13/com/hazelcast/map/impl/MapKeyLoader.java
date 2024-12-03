/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IFunction;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.impl.MapKeyLoaderUtil;
import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.operation.KeyLoadStatusOperation;
import com.hazelcast.map.impl.operation.KeyLoadStatusOperationFactory;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.operation.TriggerLoadIfNeededOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.AbstractCompletableFuture;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.IterableUtil;
import com.hazelcast.util.StateMachine;
import com.hazelcast.util.scheduler.CoalescingDelayedTrigger;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapKeyLoader {
    private static final long LOADING_TRIGGER_DELAY = TimeUnit.SECONDS.toMillis(5L);
    private ILogger logger;
    private String mapName;
    private OperationService opService;
    private IPartitionService partitionService;
    private final ClusterService clusterService;
    private IFunction<Object, Data> toData;
    private ExecutionService execService;
    private CoalescingDelayedTrigger delayedTrigger;
    private int maxSizePerNode;
    private int maxBatch;
    private int mapNamePartition;
    private int partitionId;
    private boolean hasBackup;
    private LoadFinishedFuture keyLoadFinished = new LoadFinishedFuture(true);
    private MapOperationProvider operationProvider;
    private final StateMachine<Role> role = StateMachine.of(Role.NONE).withTransition(Role.NONE, Role.SENDER, new Role[]{Role.RECEIVER, Role.SENDER_BACKUP}).withTransition(Role.SENDER_BACKUP, Role.SENDER, new Role[0]);
    private final StateMachine<State> state = StateMachine.of(State.NOT_LOADED).withTransition(State.NOT_LOADED, State.LOADING, new State[0]).withTransition(State.LOADING, State.LOADED, new State[]{State.NOT_LOADED}).withTransition(State.LOADED, State.LOADING, new State[0]);

    public MapKeyLoader(String mapName, OperationService opService, IPartitionService ps, ClusterService clusterService, ExecutionService execService, IFunction<Object, Data> serialize) {
        this.mapName = mapName;
        this.opService = opService;
        this.partitionService = ps;
        this.clusterService = clusterService;
        this.toData = serialize;
        this.execService = execService;
        this.logger = Logger.getLogger(MapKeyLoader.class);
    }

    public Future startInitialLoad(MapStoreContext mapStoreContext, int partitionId) {
        this.partitionId = partitionId;
        this.mapNamePartition = this.partitionService.getPartitionId(this.toData.apply(this.mapName));
        Role newRole = this.calculateRole();
        this.role.nextOrStay(newRole);
        this.state.next(State.LOADING);
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("startInitialLoad invoked " + this.getStateMessage());
        }
        switch (newRole) {
            case SENDER: {
                return this.sendKeys(mapStoreContext, false);
            }
            case SENDER_BACKUP: 
            case RECEIVER: {
                return this.triggerLoading();
            }
        }
        return this.keyLoadFinished;
    }

    private Role calculateRole() {
        IPartition partition;
        Address firstReplicaAddress;
        MemberImpl member;
        boolean isPartitionOwner = this.partitionService.isPartitionOwner(this.partitionId);
        boolean isMapNamePartition = this.partitionId == this.mapNamePartition;
        boolean isMapNamePartitionFirstReplica = false;
        if (this.hasBackup && isMapNamePartition && (member = this.clusterService.getMember(firstReplicaAddress = (partition = this.partitionService.getPartition(this.partitionId)).getReplicaAddress(1))) != null) {
            isMapNamePartitionFirstReplica = member.localMember();
        }
        return MapKeyLoaderUtil.assignRole(isPartitionOwner, isMapNamePartition, isMapNamePartitionFirstReplica);
    }

    private Future<?> sendKeys(final MapStoreContext mapStoreContext, final boolean replaceExistingValues) {
        if (this.keyLoadFinished.isDone()) {
            this.keyLoadFinished = new LoadFinishedFuture();
            Future<Boolean> sent = this.execService.submit("hz:map-loadAllKeys", new Callable<Boolean>(){

                @Override
                public Boolean call() throws Exception {
                    MapKeyLoader.this.sendKeysInBatches(mapStoreContext, replaceExistingValues);
                    return false;
                }
            });
            this.execService.asCompletableFuture(sent).andThen(this.keyLoadFinished);
        }
        return this.keyLoadFinished;
    }

    private Future triggerLoading() {
        if (this.keyLoadFinished.isDone()) {
            this.keyLoadFinished = new LoadFinishedFuture();
            this.execService.execute("hz:map-loadAllKeys", new Runnable(){

                @Override
                public void run() {
                    TriggerLoadIfNeededOperation op = new TriggerLoadIfNeededOperation(MapKeyLoader.this.mapName);
                    MapKeyLoader.this.opService.invokeOnPartition("hz:impl:mapService", op, MapKeyLoader.this.mapNamePartition).andThen(MapKeyLoader.this.loadingFinishedCallback());
                }
            });
        }
        return this.keyLoadFinished;
    }

    private ExecutionCallback<Boolean> loadingFinishedCallback() {
        return new ExecutionCallback<Boolean>(){

            @Override
            public void onResponse(Boolean loadingFinished) {
                if (loadingFinished.booleanValue()) {
                    MapKeyLoader.this.updateLocalKeyLoadStatus(null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                MapKeyLoader.this.updateLocalKeyLoadStatus(t);
            }
        };
    }

    private void updateLocalKeyLoadStatus(Throwable t) {
        KeyLoadStatusOperation op = new KeyLoadStatusOperation(this.mapName, t);
        if (this.hasBackup && this.role.is(Role.SENDER_BACKUP, new Role[0])) {
            this.opService.createInvocationBuilder("hz:impl:mapService", (Operation)op, this.partitionId).setReplicaIndex(1).invoke();
        } else {
            this.opService.createInvocationBuilder("hz:impl:mapService", (Operation)op, this.partitionId).invoke();
        }
    }

    public Future<?> startLoading(MapStoreContext mapStoreContext, boolean replaceExistingValues) {
        this.role.nextOrStay(Role.SENDER);
        if (this.state.is(State.LOADING, new State[0])) {
            return this.keyLoadFinished;
        }
        this.state.next(State.LOADING);
        return this.sendKeys(mapStoreContext, replaceExistingValues);
    }

    public void trackLoading(boolean lastBatch, Throwable exception) {
        if (lastBatch) {
            this.state.nextOrStay(State.LOADED);
            if (exception != null) {
                this.keyLoadFinished.setResult(exception);
            } else {
                this.keyLoadFinished.setResult(true);
            }
        } else if (this.state.is(State.LOADED, new State[0])) {
            this.state.next(State.LOADING);
        }
    }

    public void triggerLoadingWithDelay() {
        if (this.delayedTrigger == null) {
            Runnable runnable = new Runnable(){

                @Override
                public void run() {
                    TriggerLoadIfNeededOperation op = new TriggerLoadIfNeededOperation(MapKeyLoader.this.mapName);
                    MapKeyLoader.this.opService.invokeOnPartition("hz:impl:mapService", op, MapKeyLoader.this.mapNamePartition);
                }
            };
            this.delayedTrigger = new CoalescingDelayedTrigger(this.execService, LOADING_TRIGGER_DELAY, LOADING_TRIGGER_DELAY, runnable);
        }
        this.delayedTrigger.executeWithDelay();
    }

    public boolean shouldDoInitialLoad() {
        if (this.role.is(Role.SENDER_BACKUP, new Role[0])) {
            this.role.next(Role.SENDER);
            if (this.state.is(State.LOADING, new State[0])) {
                this.state.next(State.NOT_LOADED);
                this.keyLoadFinished.setResult(false);
            }
        }
        return this.state.is(State.NOT_LOADED, new State[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendKeysInBatches(MapStoreContext mapStoreContext, boolean replaceExistingValues) throws Exception {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("sendKeysInBatches invoked " + this.getStateMessage());
        }
        int clusterSize = this.partitionService.getMemberPartitionsMap().size();
        Iterator<Object> keys = null;
        Exception loadError = null;
        try {
            Iterable<Object> allKeys = mapStoreContext.loadAllKeys();
            keys = allKeys.iterator();
            Iterator<Data> dataKeys = IterableUtil.map(keys, this.toData);
            int mapMaxSize = clusterSize * this.maxSizePerNode;
            if (mapMaxSize > 0) {
                dataKeys = IterableUtil.limit(dataKeys, mapMaxSize);
            }
            Iterator<Map.Entry<Integer, Data>> partitionsAndKeys = IterableUtil.map(dataKeys, MapKeyLoaderUtil.toPartition(this.partitionService));
            Iterator<Map<Integer, List<Data>>> batches = MapKeyLoaderUtil.toBatches(partitionsAndKeys, this.maxBatch);
            ArrayList<Future> futures = new ArrayList<Future>();
            while (batches.hasNext()) {
                Map<Integer, List<Data>> batch = batches.next();
                futures.addAll(this.sendBatch(batch, replaceExistingValues));
            }
            FutureUtil.waitForever(futures);
        }
        catch (Exception caught) {
            loadError = caught;
        }
        finally {
            this.sendKeyLoadCompleted(clusterSize, loadError);
            if (keys instanceof Closeable) {
                IOUtil.closeResource((Closeable)((Object)keys));
            }
        }
    }

    private List<Future> sendBatch(Map<Integer, List<Data>> batch, boolean replaceExistingValues) {
        Set<Map.Entry<Integer, List<Data>>> entries = batch.entrySet();
        ArrayList<Future> futures = new ArrayList<Future>(entries.size());
        for (Map.Entry<Integer, List<Data>> e : entries) {
            int partitionId = e.getKey();
            List<Data> keys = e.getValue();
            MapOperation op = this.operationProvider.createLoadAllOperation(this.mapName, keys, replaceExistingValues);
            InternalCompletableFuture future = this.opService.invokeOnPartition("hz:impl:mapService", op, partitionId);
            futures.add(future);
        }
        return futures;
    }

    private void sendKeyLoadCompleted(int clusterSize, Throwable exception) throws Exception {
        ArrayList futures = new ArrayList();
        KeyLoadStatusOperation senderStatus = new KeyLoadStatusOperation(this.mapName, exception);
        InternalCompletableFuture senderFuture = this.opService.createInvocationBuilder("hz:impl:mapService", (Operation)senderStatus, this.mapNamePartition).setReplicaIndex(0).invoke();
        futures.add(senderFuture);
        if (this.hasBackup && clusterSize > 1) {
            KeyLoadStatusOperation senderBackupStatus = new KeyLoadStatusOperation(this.mapName, exception);
            InternalCompletableFuture senderBackupFuture = this.opService.createInvocationBuilder("hz:impl:mapService", (Operation)senderBackupStatus, this.mapNamePartition).setReplicaIndex(1).invoke();
            futures.add(senderBackupFuture);
        }
        FutureUtil.waitForever(futures);
        this.opService.invokeOnAllPartitions("hz:impl:mapService", new KeyLoadStatusOperationFactory(this.mapName, exception));
    }

    public void setMaxBatch(int maxBatch) {
        this.maxBatch = maxBatch;
    }

    public void setMaxSize(int maxSize) {
        this.maxSizePerNode = maxSize;
    }

    public void setHasBackup(boolean hasBackup) {
        this.hasBackup = hasBackup;
    }

    public void setMapOperationProvider(MapOperationProvider operationProvider) {
        this.operationProvider = operationProvider;
    }

    public boolean isKeyLoadFinished() {
        return this.keyLoadFinished.isDone();
    }

    public void promoteToLoadedOnMigration() {
        this.state.next(State.LOADING);
        this.state.next(State.LOADED);
    }

    private String getStateMessage() {
        return "on partitionId=" + this.partitionId + " on " + this.clusterService.getThisAddress() + " role=" + this.role + " state=" + this.state;
    }

    private static final class LoadFinishedFuture
    extends AbstractCompletableFuture<Boolean>
    implements ExecutionCallback<Boolean> {
        private LoadFinishedFuture(Boolean result) {
            this();
            this.setResult(result);
        }

        private LoadFinishedFuture() {
            super((Executor)null, Logger.getLogger(LoadFinishedFuture.class));
        }

        @Override
        public Boolean get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.isDone()) {
                return (Boolean)this.getResult();
            }
            throw new UnsupportedOperationException("Future is not done yet");
        }

        @Override
        public void onResponse(Boolean loaded) {
            if (loaded.booleanValue()) {
                this.setResult(true);
            }
        }

        @Override
        public void onFailure(Throwable t) {
            this.setResult(t);
        }

        @Override
        protected boolean shouldCancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        protected boolean setResult(Object result) {
            return super.setResult(result);
        }

        public String toString() {
            return this.getClass().getSimpleName() + "{done=" + this.isDone() + "}";
        }
    }

    static enum State {
        NOT_LOADED,
        LOADING,
        LOADED;

    }

    static enum Role {
        NONE,
        SENDER,
        RECEIVER,
        SENDER_BACKUP;

    }
}

