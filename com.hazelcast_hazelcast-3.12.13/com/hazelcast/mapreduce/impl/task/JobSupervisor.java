/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.JobProcessInformation;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.impl.AbstractJobTracker;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.notification.IntermediateChunkNotification;
import com.hazelcast.mapreduce.impl.notification.LastChunkNotification;
import com.hazelcast.mapreduce.impl.notification.MapReduceNotification;
import com.hazelcast.mapreduce.impl.notification.ReducingFinishedNotification;
import com.hazelcast.mapreduce.impl.operation.CancelJobSupervisorOperation;
import com.hazelcast.mapreduce.impl.operation.GetResultOperationFactory;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionProcessed;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.task.DefaultContext;
import com.hazelcast.mapreduce.impl.task.JobProcessInformationImpl;
import com.hazelcast.mapreduce.impl.task.JobTaskConfiguration;
import com.hazelcast.mapreduce.impl.task.MapCombineTask;
import com.hazelcast.mapreduce.impl.task.MappingPhase;
import com.hazelcast.mapreduce.impl.task.ReducerTask;
import com.hazelcast.mapreduce.impl.task.TrackableJobFuture;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class JobSupervisor {
    private final ConcurrentMap<Object, Reducer> reducers = new ConcurrentHashMap<Object, Reducer>();
    private final ConcurrentMap<Integer, Set<Address>> remoteReducers = new ConcurrentHashMap<Integer, Set<Address>>();
    private final AtomicReference<DefaultContext> context = new AtomicReference();
    private final ConcurrentMap<Object, Address> keyAssignments = new ConcurrentHashMap<Object, Address>();
    private final Address jobOwner;
    private final boolean ownerNode;
    private final AbstractJobTracker jobTracker;
    private final JobTaskConfiguration configuration;
    private final MapReduceService mapReduceService;
    private final ExecutorService executorService;
    private final JobProcessInformationImpl jobProcessInformation;

    public JobSupervisor(JobTaskConfiguration configuration, AbstractJobTracker jobTracker, boolean ownerNode, MapReduceService mapReduceService) {
        this.jobTracker = jobTracker;
        this.ownerNode = ownerNode;
        this.configuration = configuration;
        this.mapReduceService = mapReduceService;
        this.jobOwner = configuration.getJobOwner();
        this.executorService = mapReduceService.getExecutorService(configuration.getName());
        this.jobProcessInformation = MapReduceUtil.createJobProcessInformation(configuration, this);
        String name = configuration.getName();
        String jobId = configuration.getJobId();
        jobTracker.registerReducerTask(new ReducerTask(name, jobId, this));
    }

    public MapReduceService getMapReduceService() {
        return this.mapReduceService;
    }

    public JobTracker getJobTracker() {
        return this.jobTracker;
    }

    public void startTasks(MappingPhase mappingPhase) {
        this.jobTracker.registerMapCombineTask(new MapCombineTask(this.configuration, this, mappingPhase));
    }

    public void onNotification(MapReduceNotification notification) {
        if (notification instanceof IntermediateChunkNotification) {
            IntermediateChunkNotification icn = (IntermediateChunkNotification)notification;
            ReducerTask reducerTask = this.jobTracker.getReducerTask(icn.getJobId());
            reducerTask.processChunk(icn.getChunk());
        } else if (notification instanceof LastChunkNotification) {
            LastChunkNotification lcn = (LastChunkNotification)notification;
            ReducerTask reducerTask = this.jobTracker.getReducerTask(lcn.getJobId());
            reducerTask.processChunk(lcn.getPartitionId(), lcn.getSender(), lcn.getChunk());
        } else if (notification instanceof ReducingFinishedNotification) {
            final ReducingFinishedNotification rfn = (ReducingFinishedNotification)notification;
            this.executorService.submit(new Runnable(){

                @Override
                public void run() {
                    JobSupervisor.this.processReducerFinished0(rfn);
                }
            });
        }
    }

    public void notifyRemoteException(Address remoteAddress, Throwable throwable) {
        this.jobProcessInformation.cancelPartitionState();
        Set<Address> addresses = this.collectRemoteAddresses();
        TrackableJobFuture future = this.cancel();
        this.asyncCancelRemoteOperations(addresses);
        if (future != null) {
            ExceptionUtil.fixAsyncStackTrace(throwable, Thread.currentThread().getStackTrace(), "Operation failed on node: " + remoteAddress);
            future.setResult(throwable);
        }
    }

    public boolean cancelAndNotify(Exception exception) {
        this.jobProcessInformation.cancelPartitionState();
        Set<Address> addresses = this.collectRemoteAddresses();
        TrackableJobFuture future = this.cancel();
        this.asyncCancelRemoteOperations(addresses);
        if (future != null) {
            future.setResult(exception);
        }
        return true;
    }

    public TrackableJobFuture cancel() {
        ReducerTask reducerTask;
        String jobId = this.getConfiguration().getJobId();
        TrackableJobFuture future = this.jobTracker.unregisterTrackableJob(jobId);
        MapCombineTask mapCombineTask = this.jobTracker.unregisterMapCombineTask(jobId);
        if (mapCombineTask != null) {
            mapCombineTask.cancel();
        }
        if ((reducerTask = this.jobTracker.unregisterReducerTask(jobId)) != null) {
            reducerTask.cancel();
        }
        this.mapReduceService.destroyJobSupervisor(this);
        return future;
    }

    public Map<Object, Object> getJobResults() {
        Map<Object, Object> result;
        DefaultContext currentContext = this.context.get();
        if (this.configuration.getReducerFactory() != null) {
            int mapSize = MapReduceUtil.mapSize(this.reducers.size());
            result = MapUtil.createHashMapAdapter(mapSize);
            for (Map.Entry entry : this.reducers.entrySet()) {
                Object reducedResults = ((Reducer)entry.getValue()).finalizeReduce();
                if (reducedResults == null) continue;
                result.put(entry.getKey(), reducedResults);
            }
        } else {
            result = currentContext.requestChunk();
        }
        currentContext.finalizeCombiners();
        return result;
    }

    public <KeyIn, ValueIn, ValueOut> Reducer<ValueIn, ValueOut> getReducerByKey(Object key) {
        Reducer reducer = (Reducer)this.reducers.get(key);
        if (reducer == null && this.configuration.getReducerFactory() != null) {
            reducer = this.configuration.getReducerFactory().newReducer(key);
            Reducer oldReducer = this.reducers.putIfAbsent(key, reducer);
            if (oldReducer != null) {
                reducer = oldReducer;
            } else {
                reducer.beginReduce();
            }
        }
        return reducer;
    }

    public Address getReducerAddressByKey(Object key) {
        Address address = (Address)this.keyAssignments.get(key);
        if (address != null) {
            return address;
        }
        return null;
    }

    public Address assignKeyReducerAddress(Object key) {
        Address oldAddress;
        Address address = (Address)this.keyAssignments.get(key);
        if (address == null && (oldAddress = this.keyAssignments.putIfAbsent(key, address = this.mapReduceService.getKeyMember(key))) != null) {
            address = oldAddress;
        }
        return address;
    }

    public boolean checkAssignedMembersAvailable() {
        return this.mapReduceService.checkAssignedMembersAvailable(this.keyAssignments.values());
    }

    public boolean assignKeyReducerAddress(Object key, Address address) {
        Address oldAssignment = this.keyAssignments.putIfAbsent(key, address);
        return oldAssignment == null || oldAssignment.equals(address);
    }

    public void checkFullyProcessed(JobProcessInformation processInformation) {
        if (this.isOwnerNode()) {
            JobPartitionState[] partitionStates;
            for (JobPartitionState partitionState : partitionStates = processInformation.getPartitionStates()) {
                if (partitionState != null && partitionState.getState() == JobPartitionState.State.PROCESSED) continue;
                return;
            }
            String name = this.configuration.getName();
            String jobId = this.configuration.getJobId();
            NodeEngine nodeEngine = this.configuration.getNodeEngine();
            GetResultOperationFactory operationFactory = new GetResultOperationFactory(name, jobId);
            TrackableJobFuture future = this.jobTracker.unregisterTrackableJob(jobId);
            if (future == null) {
                return;
            }
            JobSupervisor jobSupervisor = this;
            GetResultsRunnable runnable = new GetResultsRunnable(nodeEngine, operationFactory, jobId, jobSupervisor, future);
            ExecutionService executionService = nodeEngine.getExecutionService();
            ManagedExecutorService executor = executionService.getExecutor("hz:async");
            executor.submit(runnable);
        }
    }

    public <K, V> DefaultContext<K, V> getOrCreateContext(MapCombineTask mapCombineTask) {
        DefaultContext newContext = new DefaultContext(this.configuration.getCombinerFactory(), mapCombineTask);
        if (this.context.compareAndSet(null, newContext)) {
            return newContext;
        }
        return this.context.get();
    }

    public void registerReducerEventInterests(int partitionId, Set<Address> remoteReducers) {
        Set<Address> addresses = (CopyOnWriteArraySet<Address>)this.remoteReducers.get(partitionId);
        if (addresses == null) {
            addresses = new CopyOnWriteArraySet<Address>();
            Set oldSet = this.remoteReducers.putIfAbsent(partitionId, addresses);
            if (oldSet != null) {
                addresses = oldSet;
            }
        }
        addresses.addAll(remoteReducers);
    }

    public Collection<Address> getReducerEventInterests(int partitionId) {
        return (Collection)this.remoteReducers.get(partitionId);
    }

    public JobProcessInformationImpl getJobProcessInformation() {
        return this.jobProcessInformation;
    }

    public Address getJobOwner() {
        return this.jobOwner;
    }

    public boolean isOwnerNode() {
        return this.ownerNode;
    }

    public JobTaskConfiguration getConfiguration() {
        return this.configuration;
    }

    private void collectResults(boolean reducedResult, Map<Object, Object> mergedResults, Map.Entry entry) {
        if (reducedResult) {
            mergedResults.put(entry.getKey(), entry.getValue());
        } else {
            ArrayList list = (ArrayList)mergedResults.get(entry.getKey());
            if (list == null) {
                list = new ArrayList();
                mergedResults.put(entry.getKey(), list);
            }
            for (Object value : (List)entry.getValue()) {
                list.add(value);
            }
        }
    }

    private Set<Address> collectRemoteAddresses() {
        HashSet<Address> addresses = new HashSet<Address>();
        for (Set remoteReducerAddresses : this.remoteReducers.values()) {
            this.addAllFilterJobOwner(addresses, remoteReducerAddresses);
        }
        for (JobPartitionState partitionState : this.jobProcessInformation.getPartitionStates()) {
            if (partitionState == null || partitionState.getOwner() == null || partitionState.getOwner().equals(this.jobOwner)) continue;
            addresses.add(partitionState.getOwner());
        }
        return addresses;
    }

    private void asyncCancelRemoteOperations(final Set<Address> addresses) {
        final NodeEngine nodeEngine = this.mapReduceService.getNodeEngine();
        TaskScheduler taskScheduler = nodeEngine.getExecutionService().getGlobalTaskScheduler();
        taskScheduler.execute(new Runnable(){

            @Override
            public void run() {
                String name = JobSupervisor.this.getConfiguration().getName();
                String jobId = JobSupervisor.this.getConfiguration().getJobId();
                for (Address address : addresses) {
                    try {
                        CancelJobSupervisorOperation operation = new CancelJobSupervisorOperation(name, jobId);
                        JobSupervisor.this.mapReduceService.processRequest(address, operation);
                    }
                    catch (Exception ignore) {
                        ILogger logger = nodeEngine.getLogger(JobSupervisor.class);
                        logger.finest("Remote node may already be down", ignore);
                    }
                }
            }
        });
    }

    private void addAllFilterJobOwner(Set<Address> target, Set<Address> source) {
        for (Address address : source) {
            if (this.jobOwner.equals(address)) continue;
            target.add(address);
        }
    }

    private void processReducerFinished0(ReducingFinishedNotification notification) {
        block4: {
            Address reducerAddress;
            String name = this.configuration.getName();
            String jobId = this.configuration.getJobId();
            int partitionId = notification.getPartitionId();
            if (this.checkPartitionReductionCompleted(partitionId, reducerAddress = notification.getAddress())) {
                try {
                    RequestPartitionResult result = (RequestPartitionResult)this.mapReduceService.processRequest(this.jobOwner, new RequestPartitionProcessed(name, jobId, partitionId, JobPartitionState.State.REDUCING));
                    if (result.getResultState() != RequestPartitionResult.ResultState.SUCCESSFUL) {
                        throw new RuntimeException("Could not finalize processing for partitionId " + partitionId);
                    }
                }
                catch (Throwable t) {
                    MapReduceUtil.notifyRemoteException(this, t);
                    if (!(t instanceof Error)) break block4;
                    ExceptionUtil.sneakyThrow(t);
                }
            }
        }
    }

    private boolean checkPartitionReductionCompleted(int partitionId, Address reducerAddress) {
        Set remoteAddresses = (Set)this.remoteReducers.get(partitionId);
        if (remoteAddresses == null) {
            throw new RuntimeException("Reducer for partition " + partitionId + " not registered");
        }
        remoteAddresses.remove(reducerAddress);
        return remoteAddresses.size() == 0 && this.remoteReducers.remove(partitionId) != null;
    }

    private class GetResultsRunnable
    implements Runnable {
        private final NodeEngine nodeEngine;
        private final GetResultOperationFactory operationFactory;
        private final String jobId;
        private final JobSupervisor jobSupervisor;
        private final TrackableJobFuture future;

        public GetResultsRunnable(NodeEngine nodeEngine, GetResultOperationFactory operationFactory, String jobId, JobSupervisor jobSupervisor2, TrackableJobFuture future) {
            this.nodeEngine = nodeEngine;
            this.operationFactory = operationFactory;
            this.jobId = jobId;
            this.jobSupervisor = jobSupervisor2;
            this.future = future;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Serializable finalResult = null;
            try {
                boolean reducedResult;
                ClusterService clusterService = this.nodeEngine.getClusterService();
                Collection<Member> members = clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
                List<Map> results = MapReduceUtil.executeOperation(members, this.operationFactory, JobSupervisor.this.mapReduceService, this.nodeEngine);
                boolean bl = reducedResult = JobSupervisor.this.configuration.getReducerFactory() != null;
                if (results != null) {
                    HashMap mergedResults = new HashMap();
                    for (Map map : results) {
                        for (Map.Entry entry : map.entrySet()) {
                            JobSupervisor.this.collectResults(reducedResult, mergedResults, entry);
                        }
                    }
                    finalResult = mergedResults;
                }
            }
            catch (Exception e) {
                finalResult = e;
            }
            finally {
                JobSupervisor.this.jobTracker.unregisterMapCombineTask(this.jobId);
                JobSupervisor.this.jobTracker.unregisterReducerTask(this.jobId);
                JobSupervisor.this.mapReduceService.destroyJobSupervisor(this.jobSupervisor);
                this.future.setResult(finalResult);
            }
        }
    }
}

