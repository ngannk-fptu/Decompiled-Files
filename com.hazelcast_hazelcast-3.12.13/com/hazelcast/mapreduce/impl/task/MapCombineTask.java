/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.LifecycleMapper;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.PartitionIdAware;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.notification.IntermediateChunkNotification;
import com.hazelcast.mapreduce.impl.notification.LastChunkNotification;
import com.hazelcast.mapreduce.impl.operation.KeysAssignmentOperation;
import com.hazelcast.mapreduce.impl.operation.KeysAssignmentResult;
import com.hazelcast.mapreduce.impl.operation.PostPonePartitionProcessing;
import com.hazelcast.mapreduce.impl.operation.RequestMemberIdAssignment;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionMapping;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionProcessed;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionReducing;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.task.DefaultContext;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.JobTaskConfiguration;
import com.hazelcast.mapreduce.impl.task.KeyValueSourceFacade;
import com.hazelcast.mapreduce.impl.task.MappingPhase;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MapCombineTask<KeyIn, ValueIn, KeyOut, ValueOut, Chunk> {
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final Mapper<KeyIn, ValueIn, KeyOut, ValueOut> mapper;
    private final MappingPhase<KeyIn, ValueIn, KeyOut, ValueOut> mappingPhase;
    private final KeyValueSource<KeyIn, ValueIn> keyValueSource;
    private final MapReduceService mapReduceService;
    private final IPartitionService partitionService;
    private final SerializationService serializationService;
    private final JobSupervisor supervisor;
    private final NodeEngine nodeEngine;
    private final String name;
    private final String jobId;
    private final int chunkSize;

    public MapCombineTask(JobTaskConfiguration configuration, JobSupervisor supervisor, MappingPhase<KeyIn, ValueIn, KeyOut, ValueOut> mappingPhase) {
        this.mappingPhase = mappingPhase;
        this.supervisor = supervisor;
        this.mapper = configuration.getMapper();
        this.name = configuration.getName();
        this.jobId = configuration.getJobId();
        this.chunkSize = configuration.getChunkSize();
        this.nodeEngine = configuration.getNodeEngine();
        this.partitionService = this.nodeEngine.getPartitionService();
        this.serializationService = this.nodeEngine.getSerializationService();
        this.mapReduceService = supervisor.getMapReduceService();
        this.keyValueSource = configuration.getKeyValueSource();
    }

    public String getName() {
        return this.name;
    }

    public String getJobId() {
        return this.jobId;
    }

    public int getChunkSize() {
        return this.chunkSize;
    }

    public void cancel() {
        this.cancelled.set(true);
        this.mappingPhase.cancel();
    }

    public void process() {
        ExecutorService es = this.mapReduceService.getExecutorService(this.name);
        if (this.keyValueSource instanceof PartitionIdAware) {
            es.submit(new PartitionBasedProcessor());
        } else {
            es.submit(new NonPartitionBasedProcessor());
        }
    }

    public final void processMapping(int partitionId, DefaultContext<KeyOut, ValueOut> context, KeyValueSource<KeyIn, ValueIn> keyValueSource, boolean partitionProcessor) throws Exception {
        int keyPreSelectorId;
        context.setPartitionId(partitionId);
        context.setSerializationService((InternalSerializationService)this.serializationService);
        if (this.mapper instanceof LifecycleMapper) {
            ((LifecycleMapper)this.mapper).initialize(context);
        }
        int n = keyPreSelectorId = partitionProcessor ? partitionId : -1;
        if (this.mappingPhase.processingPartitionNecessary(keyPreSelectorId, this.partitionService)) {
            this.mappingPhase.executeMappingPhase(keyValueSource, this.mapper, context);
        }
        if (this.mapper instanceof LifecycleMapper) {
            ((LifecycleMapper)this.mapper).finalized(context);
        }
    }

    void onEmit(DefaultContext<KeyOut, ValueOut> context, int partitionId) {
        if (this.supervisor.getConfiguration().getReducerFactory() != null && context.getCollected() == this.chunkSize) {
            Map chunkMap = context.requestChunk();
            Map mapping = MapCombineTask.mapResultToMember(this.supervisor, chunkMap);
            this.supervisor.registerReducerEventInterests(partitionId, mapping.keySet());
            for (Map.Entry entry : mapping.entrySet()) {
                this.mapReduceService.sendNotification(entry.getKey(), new IntermediateChunkNotification(entry.getKey(), this.name, this.jobId, entry.getValue(), partitionId));
            }
        }
    }

    public static <K, V> Map<Address, Map<K, V>> mapResultToMember(JobSupervisor supervisor, Map<K, V> result) {
        Set<Object> unassignedKeys = SetUtil.createHashSet(result.size());
        for (Map.Entry<K, V> entry : result.entrySet()) {
            Address address = supervisor.getReducerAddressByKey(entry.getKey());
            if (address != null) continue;
            unassignedKeys.add(entry.getKey());
        }
        if (unassignedKeys.size() > 0) {
            MapCombineTask.requestAssignment(unassignedKeys, supervisor);
        }
        Map<Address, Map<Address, Map<K, V>>> mapping = MapUtil.createHashMap(result.size());
        for (Map.Entry<K, V> entry : result.entrySet()) {
            Address address = supervisor.getReducerAddressByKey(entry.getKey());
            if (address == null) continue;
            Map<K, V> data = mapping.get(address);
            if (data == null) {
                data = new HashMap();
                mapping.put(address, data);
            }
            data.put(entry.getKey(), entry.getValue());
        }
        return mapping;
    }

    private static void requestAssignment(Set<Object> keys, JobSupervisor supervisor) {
        try {
            MapReduceService mapReduceService = supervisor.getMapReduceService();
            String name = supervisor.getConfiguration().getName();
            String jobId = supervisor.getConfiguration().getJobId();
            KeysAssignmentResult assignmentResult = (KeysAssignmentResult)mapReduceService.processRequest(supervisor.getJobOwner(), new KeysAssignmentOperation(name, jobId, keys));
            if (assignmentResult.getResultState() == RequestPartitionResult.ResultState.SUCCESSFUL) {
                Map<Object, Address> assignment = assignmentResult.getAssignment();
                for (Map.Entry<Object, Address> entry : assignment.entrySet()) {
                    if (supervisor.assignKeyReducerAddress(entry.getKey(), entry.getValue())) continue;
                    throw new IllegalStateException("Key reducer assignment in illegal state");
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void finalizeMapping(int partitionId, DefaultContext<KeyOut, ValueOut> context) throws Exception {
        RequestPartitionResult result = (RequestPartitionResult)this.mapReduceService.processRequest(this.supervisor.getJobOwner(), new RequestPartitionReducing(this.name, this.jobId, partitionId));
        if (result.getResultState() == RequestPartitionResult.ResultState.SUCCESSFUL && this.supervisor.getConfiguration().getReducerFactory() != null) {
            Map chunkMap = context.requestChunk();
            if (chunkMap.size() > 0) {
                this.sendLastChunkToAssignedReducers(partitionId, chunkMap);
            } else {
                this.finalizeProcessing(partitionId);
            }
        }
    }

    private void finalizeProcessing(int partitionId) throws Exception {
        RequestPartitionResult result = (RequestPartitionResult)this.mapReduceService.processRequest(this.supervisor.getJobOwner(), new RequestPartitionProcessed(this.name, this.jobId, partitionId, JobPartitionState.State.REDUCING));
        if (result.getResultState() != RequestPartitionResult.ResultState.SUCCESSFUL) {
            throw new RuntimeException("Could not finalize processing for partitionId " + partitionId);
        }
    }

    private void sendLastChunkToAssignedReducers(int partitionId, Map<KeyOut, Chunk> chunkMap) {
        Address sender = this.mapReduceService.getLocalAddress();
        Map<Address, Map<KeyOut, Chunk>> mapping = MapCombineTask.mapResultToMember(this.supervisor, chunkMap);
        this.supervisor.registerReducerEventInterests(partitionId, mapping.keySet());
        for (Map.Entry<Address, Map<KeyOut, Chunk>> entry : mapping.entrySet()) {
            Address receiver = entry.getKey();
            Map<KeyOut, Chunk> chunk = entry.getValue();
            this.mapReduceService.sendNotification(receiver, new LastChunkNotification<KeyOut, Chunk>(receiver, this.name, this.jobId, sender, partitionId, chunk));
        }
        Set<Address> addresses = mapping.keySet();
        Collection<Address> reducerInterests = this.supervisor.getReducerEventInterests(partitionId);
        if (reducerInterests != null) {
            for (Address address : reducerInterests) {
                if (addresses.contains(address)) continue;
                this.mapReduceService.sendNotification(address, new LastChunkNotification(address, this.name, this.jobId, sender, partitionId, Collections.emptyMap()));
            }
        }
    }

    private void postponePartitionProcessing(int partitionId) throws Exception {
        RequestPartitionResult result = (RequestPartitionResult)this.mapReduceService.processRequest(this.supervisor.getJobOwner(), new PostPonePartitionProcessing(this.name, this.jobId, partitionId));
        if (result.getResultState() != RequestPartitionResult.ResultState.SUCCESSFUL) {
            throw new RuntimeException("Could not postpone processing for partitionId " + partitionId + " -> " + (Object)((Object)result.getResultState()));
        }
    }

    private void handleProcessorThrowable(Throwable t) {
        MapReduceUtil.notifyRemoteException(this.supervisor, t);
        if (t instanceof Error) {
            ExceptionUtil.sneakyThrow(t);
        }
    }

    private void processPartitionMapping(KeyValueSource<KeyIn, ValueIn> delegate, int partitionId, boolean partitionProcessor) throws Exception {
        delegate.reset();
        if (delegate.open(this.nodeEngine)) {
            DefaultContext context = this.supervisor.getOrCreateContext(this);
            this.processMapping(partitionId, context, delegate, partitionProcessor);
            delegate.close();
            this.finalizeMapping(partitionId, context);
        } else {
            this.postponePartitionProcessing(partitionId);
        }
    }

    private class NonPartitionBasedProcessor
    implements Runnable {
        private NonPartitionBasedProcessor() {
        }

        @Override
        public void run() {
            try {
                MapReduceUtil.enforcePartitionTableWarmup(MapCombineTask.this.mapReduceService);
                RequestPartitionResult result = (RequestPartitionResult)MapCombineTask.this.mapReduceService.processRequest(MapCombineTask.this.supervisor.getJobOwner(), new RequestMemberIdAssignment(MapCombineTask.this.name, MapCombineTask.this.jobId));
                if (result.getResultState() == RequestPartitionResult.ResultState.NO_SUPERVISOR) {
                    return;
                }
                if (result.getResultState() == RequestPartitionResult.ResultState.NO_MORE_PARTITIONS) {
                    return;
                }
                int partitionId = result.getPartitionId();
                KeyValueSourceFacade delegate = MapCombineTask.this.keyValueSource;
                if (MapCombineTask.this.supervisor.getConfiguration().isCommunicateStats()) {
                    delegate = new KeyValueSourceFacade(MapCombineTask.this.keyValueSource, MapCombineTask.this.supervisor);
                }
                MapCombineTask.this.processPartitionMapping(delegate, partitionId, false);
            }
            catch (Throwable t) {
                MapCombineTask.this.handleProcessorThrowable(t);
            }
        }
    }

    private class PartitionBasedProcessor
    implements Runnable {
        private PartitionBasedProcessor() {
        }

        @Override
        public void run() {
            KeyValueSourceFacade delegate = MapCombineTask.this.keyValueSource;
            if (MapCombineTask.this.supervisor.getConfiguration().isCommunicateStats()) {
                delegate = new KeyValueSourceFacade(MapCombineTask.this.keyValueSource, MapCombineTask.this.supervisor);
            }
            try {
                MapReduceUtil.enforcePartitionTableWarmup(MapCombineTask.this.mapReduceService);
            }
            catch (TimeoutException e) {
                MapCombineTask.this.handleProcessorThrowable(e);
            }
            this.processPartitions(delegate);
        }

        private void processPartitions(KeyValueSource<KeyIn, ValueIn> delegate) {
            while (!MapCombineTask.this.cancelled.get()) {
                Integer partitionId = this.findNewPartitionProcessing();
                if (partitionId == null) {
                    return;
                }
                if (partitionId == -1) continue;
                try {
                    ((PartitionIdAware)((Object)MapCombineTask.this.keyValueSource)).setPartitionId(partitionId);
                    MapCombineTask.this.processPartitionMapping(delegate, partitionId, true);
                    continue;
                }
                catch (Throwable t) {
                    MapCombineTask.this.handleProcessorThrowable(t);
                    continue;
                }
                break;
            }
            return;
        }

        private Integer findNewPartitionProcessing() {
            try {
                RequestPartitionResult result = (RequestPartitionResult)MapCombineTask.this.mapReduceService.processRequest(MapCombineTask.this.supervisor.getJobOwner(), new RequestPartitionMapping(MapCombineTask.this.name, MapCombineTask.this.jobId));
                if (result.getResultState() == RequestPartitionResult.ResultState.NO_SUPERVISOR) {
                    return null;
                }
                if (result.getResultState() == RequestPartitionResult.ResultState.CHECK_STATE_FAILED) {
                    return -1;
                }
                if (result.getResultState() == RequestPartitionResult.ResultState.NO_MORE_PARTITIONS) {
                    return null;
                }
                return result.getPartitionId();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

