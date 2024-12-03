/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.Member;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.NamedTask;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.scheduledexecutor.impl.InvokeOnMembers;
import com.hazelcast.scheduledexecutor.impl.NamedTaskDecorator;
import com.hazelcast.scheduledexecutor.impl.ScheduledFutureProxy;
import com.hazelcast.scheduledexecutor.impl.ScheduledRunnableAdapter;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskHandlerImpl;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnMemberOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetAllScheduledOnPartitionOperationFactory;
import com.hazelcast.scheduledexecutor.impl.operations.ScheduleTaskOperation;
import com.hazelcast.scheduledexecutor.impl.operations.ShutdownOperation;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.function.Supplier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ScheduledExecutorServiceProxy
extends AbstractDistributedObject<DistributedScheduledExecutorService>
implements IScheduledExecutorService {
    private static final int SHUTDOWN_TIMEOUT = 10;
    private final FutureUtil.ExceptionHandler shutdownExceptionHandler = new FutureUtil.ExceptionHandler(){

        @Override
        public void handleException(Throwable throwable) {
            if (throwable != null) {
                if (throwable instanceof QuorumException) {
                    ExceptionUtil.sneakyThrow(throwable);
                }
                if (throwable.getCause() instanceof QuorumException) {
                    ExceptionUtil.sneakyThrow(throwable.getCause());
                }
            }
            if (ScheduledExecutorServiceProxy.this.logger.isLoggable(Level.FINEST)) {
                ScheduledExecutorServiceProxy.this.logger.log(Level.FINEST, "Exception while ExecutorService shutdown", throwable);
            }
        }
    };
    private final String name;
    private final ILogger logger;

    ScheduledExecutorServiceProxy(String name, NodeEngine nodeEngine, DistributedScheduledExecutorService service) {
        super(nodeEngine, service);
        this.name = name;
        this.logger = nodeEngine.getLogger(ScheduledExecutorServiceProxy.class);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    public IScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        ScheduledRunnableAdapter callable = this.createScheduledRunnableAdapter(command);
        return this.schedule(callable, delay, unit);
    }

    @Override
    public <V> IScheduledFuture<V> schedule(Callable<V> command, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        String name = this.extractNameOrGenerateOne(command);
        int partitionId = this.getTaskOrKeyPartitionId(command, (Object)name);
        TaskDefinition<V> definition = new TaskDefinition<V>(TaskDefinition.Type.SINGLE_RUN, name, command, delay, unit);
        return this.submitOnPartitionSync(name, new ScheduleTaskOperation(this.getName(), definition), partitionId);
    }

    @Override
    public IScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        String name = this.extractNameOrGenerateOne(command);
        int partitionId = this.getTaskOrKeyPartitionId(command, (Object)name);
        ScheduledRunnableAdapter adapter = this.createScheduledRunnableAdapter(command);
        TaskDefinition definition = new TaskDefinition(TaskDefinition.Type.AT_FIXED_RATE, name, adapter, initialDelay, period, unit);
        return this.submitOnPartitionSync(name, new ScheduleTaskOperation(this.getName(), definition), partitionId);
    }

    @Override
    public IScheduledFuture<?> scheduleOnMember(Runnable command, Member member, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(member, "Member is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        return this.scheduleOnMembers(command, Collections.singleton(member), delay, unit).get(member);
    }

    @Override
    public <V> IScheduledFuture<V> scheduleOnMember(Callable<V> command, Member member, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(member, "Member is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        return this.scheduleOnMembers(command, Collections.singleton(member), delay, unit).get(member);
    }

    @Override
    public IScheduledFuture<?> scheduleOnMemberAtFixedRate(Runnable command, Member member, long initialDelay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(member, "Member is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        return this.scheduleOnMembersAtFixedRate(command, Collections.singleton(member), initialDelay, period, unit).get(member);
    }

    @Override
    public IScheduledFuture<?> scheduleOnKeyOwner(Runnable command, Object key, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        ScheduledRunnableAdapter callable = this.createScheduledRunnableAdapter(command);
        return this.scheduleOnKeyOwner(callable, key, delay, unit);
    }

    @Override
    public <V> IScheduledFuture<V> scheduleOnKeyOwner(Callable<V> command, Object key, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(key, "Key is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        String name = this.extractNameOrGenerateOne(command);
        int partitionId = this.getKeyPartitionId(key);
        TaskDefinition<V> definition = new TaskDefinition<V>(TaskDefinition.Type.SINGLE_RUN, name, command, delay, unit);
        return this.submitOnPartitionSync(name, new ScheduleTaskOperation(this.getName(), definition), partitionId);
    }

    @Override
    public IScheduledFuture<?> scheduleOnKeyOwnerAtFixedRate(Runnable command, Object key, long initialDelay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(key, "Key is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        String name = this.extractNameOrGenerateOne(command);
        int partitionId = this.getKeyPartitionId(key);
        ScheduledRunnableAdapter adapter = this.createScheduledRunnableAdapter(command);
        TaskDefinition definition = new TaskDefinition(TaskDefinition.Type.AT_FIXED_RATE, name, adapter, initialDelay, period, unit);
        return this.submitOnPartitionSync(name, new ScheduleTaskOperation(this.getName(), definition), partitionId);
    }

    @Override
    public Map<Member, IScheduledFuture<?>> scheduleOnAllMembers(Runnable command, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        return this.scheduleOnMembers(command, this.getNodeEngine().getClusterService().getMembers(), delay, unit);
    }

    @Override
    public <V> Map<Member, IScheduledFuture<V>> scheduleOnAllMembers(Callable<V> command, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        return this.scheduleOnMembers(command, this.getNodeEngine().getClusterService().getMembers(), delay, unit);
    }

    @Override
    public Map<Member, IScheduledFuture<?>> scheduleOnAllMembersAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        return this.scheduleOnMembersAtFixedRate(command, this.getNodeEngine().getClusterService().getMembers(), initialDelay, period, unit);
    }

    @Override
    public Map<Member, IScheduledFuture<?>> scheduleOnMembers(Runnable command, Collection<Member> members, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(members, "Members is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        ScheduledRunnableAdapter callable = this.createScheduledRunnableAdapter(command);
        return this.scheduleOnMembers(callable, members, delay, unit);
    }

    @Override
    public <V> Map<Member, IScheduledFuture<V>> scheduleOnMembers(Callable<V> command, Collection<Member> members, long delay, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(members, "Members is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        String name = this.extractNameOrGenerateOne(command);
        Map<Member, IScheduledFuture<IScheduledFuture<V>>> futures = MapUtil.createHashMap(members.size());
        for (Member member : members) {
            TaskDefinition<V> definition = new TaskDefinition<V>(TaskDefinition.Type.SINGLE_RUN, name, command, delay, unit);
            futures.put(member, this.submitOnMemberSync(name, new ScheduleTaskOperation(this.getName(), definition), member));
        }
        return futures;
    }

    @Override
    public Map<Member, IScheduledFuture<?>> scheduleOnMembersAtFixedRate(Runnable command, Collection<Member> members, long initialDelay, long period, TimeUnit unit) {
        Preconditions.checkNotNull(command, "Command is null");
        Preconditions.checkNotNull(members, "Members is null");
        Preconditions.checkNotNull(unit, "Unit is null");
        this.initializeManagedContext(command);
        String name = this.extractNameOrGenerateOne(command);
        ScheduledRunnableAdapter adapter = this.createScheduledRunnableAdapter(command);
        Map<Member, IScheduledFuture<?>> futures = MapUtil.createHashMapAdapter(members.size());
        for (Member member : members) {
            TaskDefinition definition = new TaskDefinition(TaskDefinition.Type.AT_FIXED_RATE, name, adapter, initialDelay, period, unit);
            futures.put(member, this.submitOnMemberSync(name, new ScheduleTaskOperation(this.getName(), definition), member));
        }
        return futures;
    }

    public IScheduledFuture<?> getScheduledFuture(ScheduledTaskHandler handler) {
        Preconditions.checkNotNull(handler, "Handler is null");
        ScheduledFutureProxy proxy = new ScheduledFutureProxy(handler, this);
        this.initializeManagedContext(proxy);
        return proxy;
    }

    @Override
    public <V> Map<Member, List<IScheduledFuture<V>>> getAllScheduledFutures() {
        LinkedHashMap<Member, List<IScheduledFuture<V>>> accumulator = new LinkedHashMap<Member, List<IScheduledFuture<V>>>();
        this.retrieveAllPartitionOwnedScheduled(accumulator);
        this.retrieveAllMemberOwnedScheduled(accumulator);
        return accumulator;
    }

    @Override
    public void shutdown() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Set<Member> members = nodeEngine.getClusterService().getMembers();
        OperationService operationService = nodeEngine.getOperationService();
        LinkedList calls = new LinkedList();
        for (Member member : members) {
            ShutdownOperation op = new ShutdownOperation(this.name);
            calls.add(operationService.invokeOnTarget("hz:impl:scheduledExecutorService", op, member.getAddress()));
        }
        FutureUtil.waitWithDeadline(calls, 10L, TimeUnit.SECONDS, this.shutdownExceptionHandler);
    }

    private <V> void retrieveAllMemberOwnedScheduled(Map<Member, List<IScheduledFuture<V>>> accumulator) {
        try {
            InvokeOnMembers invokeOnMembers = new InvokeOnMembers(this.getNodeEngine(), this.getServiceName(), new GetAllScheduledOnMemberOperationFactory(this.name), this.getNodeEngine().getClusterService().getMembers());
            this.accumulateTaskHandlersAsScheduledFutures(accumulator, invokeOnMembers.invoke());
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private <V> void retrieveAllPartitionOwnedScheduled(Map<Member, List<IScheduledFuture<V>>> accumulator) {
        try {
            this.accumulateTaskHandlersAsScheduledFutures(accumulator, this.getNodeEngine().getOperationService().invokeOnAllPartitions(this.getServiceName(), new GetAllScheduledOnPartitionOperationFactory(this.name)));
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private <V> void accumulateTaskHandlersAsScheduledFutures(Map<Member, List<IScheduledFuture<V>>> accumulator, Map<?, ?> taskHandlersMap) {
        ClusterService clusterService = this.getNodeEngine().getClusterService();
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        for (Map.Entry<?, ?> entry : taskHandlersMap.entrySet()) {
            Object key = entry.getKey();
            Member owner = key instanceof Number ? clusterService.getMember(partitionService.getPartitionOwner((Integer)key)) : (Member)key;
            List handlers = (List)entry.getValue();
            ArrayList futures = new ArrayList();
            for (ScheduledTaskHandler handler : handlers) {
                ScheduledFutureProxy future = new ScheduledFutureProxy(handler, this);
                this.initializeManagedContext(future);
                futures.add(future);
            }
            if (accumulator.containsKey(owner)) {
                List<IScheduledFuture<V>> memberFutures = accumulator.get(owner);
                memberFutures.addAll(futures);
                continue;
            }
            accumulator.put(owner, futures);
        }
    }

    private <T> ScheduledRunnableAdapter<T> createScheduledRunnableAdapter(Runnable command) {
        Preconditions.checkNotNull(command, "Command can't be null");
        return new ScheduledRunnableAdapter(command);
    }

    private <V> IScheduledFuture<V> createFutureProxy(int partitionId, String taskName) {
        ScheduledFutureProxy proxy = new ScheduledFutureProxy(ScheduledTaskHandlerImpl.of(partitionId, this.getName(), taskName), this);
        proxy.setHazelcastInstance(this.getNodeEngine().getHazelcastInstance());
        return proxy;
    }

    private <V> IScheduledFuture<V> createFutureProxy(Address address, String taskName) {
        ScheduledFutureProxy proxy = new ScheduledFutureProxy(ScheduledTaskHandlerImpl.of(address, this.getName(), taskName), this);
        proxy.setHazelcastInstance(this.getNodeEngine().getHazelcastInstance());
        return proxy;
    }

    private int getKeyPartitionId(Object key) {
        return this.getNodeEngine().getPartitionService().getPartitionId(key);
    }

    private int getTaskOrKeyPartitionId(Callable task, Object key) {
        Object newKey;
        if (task instanceof PartitionAware && (newKey = ((PartitionAware)((Object)task)).getPartitionKey()) != null) {
            key = newKey;
        }
        return this.getKeyPartitionId(key);
    }

    private int getTaskOrKeyPartitionId(Runnable task, Object key) {
        Object newKey;
        if (task instanceof PartitionAware && (newKey = ((PartitionAware)((Object)task)).getPartitionKey()) != null) {
            key = newKey;
        }
        return this.getKeyPartitionId(key);
    }

    private String extractNameOrGenerateOne(Object command) {
        String name = null;
        if (command instanceof NamedTask) {
            name = ((NamedTask)command).getName();
        }
        return name != null ? name : UuidUtil.newUnsecureUuidString();
    }

    private <V> IScheduledFuture<V> submitOnPartitionSync(String taskName, Operation op, int partitionId) {
        op.setPartitionId(partitionId);
        this.invokeOnPartition(op).join();
        return this.createFutureProxy(partitionId, taskName);
    }

    private <V> IScheduledFuture<V> submitOnMemberSync(String taskName, Operation op, Member member) {
        Address address = member.getAddress();
        this.getOperationService().invokeOnTarget(this.getServiceName(), op, address).join();
        return this.createFutureProxy(address, taskName);
    }

    private void initializeManagedContext(Object object) {
        ManagedContext context = this.getNodeEngine().getSerializationService().getManagedContext();
        if (object instanceof NamedTaskDecorator) {
            ((NamedTaskDecorator)object).initializeContext(context);
        } else {
            context.initialize(object);
        }
    }

    private static class GetAllScheduledOnMemberOperationFactory
    implements Supplier<Operation> {
        private final String schedulerName;

        GetAllScheduledOnMemberOperationFactory(String schedulerName) {
            this.schedulerName = schedulerName;
        }

        @Override
        public Operation get() {
            return new GetAllScheduledOnMemberOperation(this.schedulerName);
        }
    }
}

