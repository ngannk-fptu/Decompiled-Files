/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.NodeJobTracker;
import com.hazelcast.mapreduce.impl.notification.MapReduceNotification;
import com.hazelcast.mapreduce.impl.operation.CancelJobSupervisorOperation;
import com.hazelcast.mapreduce.impl.operation.FireNotificationOperation;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.JobTaskConfiguration;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class MapReduceService
implements ManagedService,
RemoteService {
    public static final String SERVICE_NAME = "hz:impl:mapReduceService";
    private static final ILogger LOGGER = Logger.getLogger(MapReduceService.class);
    private final ConstructorFunction<String, NodeJobTracker> constructor = new ConstructorFunction<String, NodeJobTracker>(){

        @Override
        public NodeJobTracker createNew(String arg) {
            JobTrackerConfig jobTrackerConfig = MapReduceService.this.config.findJobTrackerConfig(arg);
            return new NodeJobTracker(arg, jobTrackerConfig.getAsReadOnly(), MapReduceService.this.nodeEngine, MapReduceService.this);
        }
    };
    private final ConcurrentMap<String, NodeJobTracker> jobTrackers;
    private final ConcurrentMap<JobSupervisorKey, JobSupervisor> jobSupervisors;
    private final IPartitionService partitionService;
    private final ClusterService clusterService;
    private final NodeEngineImpl nodeEngine;
    private final Config config;

    public MapReduceService(NodeEngine nodeEngine) {
        this.config = nodeEngine.getConfig();
        this.nodeEngine = (NodeEngineImpl)nodeEngine;
        this.clusterService = nodeEngine.getClusterService();
        this.partitionService = nodeEngine.getPartitionService();
        this.jobTrackers = new ConcurrentHashMap<String, NodeJobTracker>();
        this.jobSupervisors = new ConcurrentHashMap<JobSupervisorKey, JobSupervisor>();
    }

    public JobTracker getJobTracker(String name) {
        return (JobTracker)this.createDistributedObject(name);
    }

    public JobSupervisor getJobSupervisor(String name, String jobId) {
        JobSupervisorKey key = new JobSupervisorKey(name, jobId);
        return (JobSupervisor)this.jobSupervisors.get(key);
    }

    public boolean registerJobSupervisorCancellation(String name, String jobId, Address jobOwner) {
        NodeJobTracker jobTracker = (NodeJobTracker)this.createDistributedObject(name);
        if (jobTracker.registerJobSupervisorCancellation(jobId) && this.getLocalAddress().equals(jobOwner)) {
            for (Member member : this.clusterService.getMembers()) {
                if (member.getAddress().equals(jobOwner)) continue;
                try {
                    CancelJobSupervisorOperation operation = new CancelJobSupervisorOperation(name, jobId);
                    this.processRequest(member.getAddress(), operation);
                }
                catch (Exception ignore) {
                    LOGGER.finest("Member might be already unavailable", ignore);
                }
            }
            return true;
        }
        return false;
    }

    public boolean unregisterJobSupervisorCancellation(String name, String jobId) {
        NodeJobTracker jobTracker = (NodeJobTracker)this.createDistributedObject(name);
        return jobTracker.unregisterJobSupervisorCancellation(jobId);
    }

    public JobSupervisor createJobSupervisor(JobTaskConfiguration configuration) {
        boolean ownerNode;
        JobSupervisor jobSupervisor;
        NodeJobTracker jobTracker = (NodeJobTracker)this.createDistributedObject(configuration.getName());
        if (jobTracker.unregisterJobSupervisorCancellation(configuration.getJobId())) {
            return null;
        }
        JobSupervisorKey key = new JobSupervisorKey(configuration.getName(), configuration.getJobId());
        JobSupervisor oldSupervisor = this.jobSupervisors.putIfAbsent(key, jobSupervisor = new JobSupervisor(configuration, jobTracker, ownerNode = this.nodeEngine.getThisAddress().equals(configuration.getJobOwner()), this));
        return oldSupervisor != null ? oldSupervisor : jobSupervisor;
    }

    public boolean destroyJobSupervisor(JobSupervisor supervisor) {
        JobSupervisorKey key;
        String name = supervisor.getConfiguration().getName();
        String jobId = supervisor.getConfiguration().getJobId();
        NodeJobTracker jobTracker = (NodeJobTracker)this.createDistributedObject(name);
        if (jobTracker != null) {
            jobTracker.unregisterJobSupervisorCancellation(jobId);
        }
        return this.jobSupervisors.remove(key = new JobSupervisorKey(supervisor)) == supervisor;
    }

    public ExecutorService getExecutorService(String name) {
        return this.nodeEngine.getExecutionService().getExecutor(MapReduceUtil.buildExecutorName(name));
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
        for (JobTracker jobTracker : this.jobTrackers.values()) {
            jobTracker.destroy();
        }
        this.jobTrackers.clear();
    }

    @Override
    public DistributedObject createDistributedObject(String objectName) {
        return ConcurrencyUtil.getOrPutSynchronized(this.jobTrackers, objectName, this.jobTrackers, this.constructor);
    }

    @Override
    public void destroyDistributedObject(String objectName) {
        JobTracker jobTracker = (JobTracker)this.jobTrackers.remove(objectName);
        if (jobTracker != null) {
            jobTracker.destroy();
        }
    }

    public Address getKeyMember(Object key) {
        int partitionId = this.partitionService.getPartitionId(key);
        return this.partitionService.getPartitionOwnerOrWait(partitionId);
    }

    public boolean checkAssignedMembersAvailable(Collection<Address> assignedMembers) {
        Set<Member> members = this.clusterService.getMembers();
        ArrayList<Address> addresses = new ArrayList<Address>(members.size());
        for (Member member : members) {
            addresses.add(member.getAddress());
        }
        for (Address address : assignedMembers) {
            if (addresses.contains(address)) continue;
            return false;
        }
        return true;
    }

    public <R> R processRequest(Address address, ProcessingOperation processingOperation) throws ExecutionException, InterruptedException {
        InvocationBuilder invocation = this.nodeEngine.getOperationService().createInvocationBuilder(SERVICE_NAME, (Operation)processingOperation, address);
        InternalCompletableFuture future = invocation.invoke();
        return (R)future.get();
    }

    public void sendNotification(Address address, MapReduceNotification notification) {
        try {
            FireNotificationOperation operation = new FireNotificationOperation(notification);
            this.processRequest(address, operation);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final Address getLocalAddress() {
        return this.nodeEngine.getThisAddress();
    }

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public void dispatchEvent(MapReduceNotification notification) {
        String jobId;
        String name = notification.getName();
        JobSupervisor supervisor = this.getJobSupervisor(name, jobId = notification.getJobId());
        if (supervisor == null) {
            throw new NullPointerException("JobSupervisor name=" + name + ", jobId=" + jobId + " not found");
        }
        supervisor.onNotification(notification);
    }

    private static final class JobSupervisorKey {
        private final String name;
        private final String jobId;

        private JobSupervisorKey(String name, String jobId) {
            this.name = name;
            this.jobId = jobId;
        }

        private JobSupervisorKey(JobSupervisor supervisor) {
            this.name = supervisor.getConfiguration().getName();
            this.jobId = supervisor.getConfiguration().getJobId();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            JobSupervisorKey that = (JobSupervisorKey)o;
            if (!this.jobId.equals(that.jobId)) {
                return false;
            }
            return this.name.equals(that.name);
        }

        public int hashCode() {
            int result = this.name != null ? this.name.hashCode() : 0;
            result = 31 * result + (this.jobId != null ? this.jobId.hashCode() : 0);
            return result;
        }
    }
}

