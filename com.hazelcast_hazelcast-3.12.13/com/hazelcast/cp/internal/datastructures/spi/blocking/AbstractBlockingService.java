/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.blocking;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftNodeLifecycleAwareService;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.spi.RaftManagedService;
import com.hazelcast.cp.internal.datastructures.spi.RaftRemoteService;
import com.hazelcast.cp.internal.datastructures.spi.blocking.BlockingResource;
import com.hazelcast.cp.internal.datastructures.spi.blocking.ResourceRegistry;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKey;
import com.hazelcast.cp.internal.datastructures.spi.blocking.operation.ExpireWaitKeysOp;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.session.SessionAccessor;
import com.hazelcast.cp.internal.session.SessionAwareService;
import com.hazelcast.cp.internal.session.SessionExpiredException;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.LiveOperations;
import com.hazelcast.spi.LiveOperationsTracker;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.executionservice.InternalExecutionService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.collection.Long2ObjectHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBlockingService<W extends WaitKey, R extends BlockingResource<W>, RR extends ResourceRegistry<W, R>>
implements RaftManagedService,
RaftNodeLifecycleAwareService,
RaftRemoteService,
SessionAwareService,
SnapshotAwareService<RR>,
LiveOperationsTracker {
    public static final long WAIT_TIMEOUT_TASK_UPPER_BOUND_MILLIS = 1500L;
    private static final long WAIT_TIMEOUT_TASK_PERIOD_MILLIS = 500L;
    protected final NodeEngineImpl nodeEngine;
    protected final ILogger logger;
    protected volatile RaftService raftService;
    private final ConcurrentMap<CPGroupId, RR> registries = new ConcurrentHashMap<CPGroupId, RR>();
    private volatile SessionAccessor sessionAccessor;

    protected AbstractBlockingService(NodeEngine nodeEngine) {
        this.nodeEngine = (NodeEngineImpl)nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    @Override
    public final void init(NodeEngine nodeEngine, Properties properties) {
        this.raftService = (RaftService)nodeEngine.getService("hz:core:raft");
        ExecutionService executionService = nodeEngine.getExecutionService();
        executionService.scheduleWithRepetition(new ExpireWaitKeysPeriodicTask(), 500L, 500L, TimeUnit.MILLISECONDS);
        this.initImpl();
    }

    protected void initImpl() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void onCPSubsystemRestart() {
        this.registries.clear();
    }

    @Override
    public final void shutdown(boolean terminate) {
        this.registries.clear();
        this.shutdownImpl(terminate);
    }

    protected void shutdownImpl(boolean terminate) {
    }

    protected abstract String serviceName();

    protected abstract RR createNewRegistry(CPGroupId var1);

    protected abstract Object expiredWaitKeyResponse();

    protected void onRegistryRestored(RR registry) {
    }

    @Override
    public boolean destroyRaftObject(CPGroupId groupId, String name) {
        Collection keys = ((ResourceRegistry)this.getOrInitRegistry(groupId)).destroyResource(name);
        if (keys == null) {
            return false;
        }
        ArrayList<Long> commitIndices = new ArrayList<Long>();
        for (WaitKey key : keys) {
            commitIndices.add(key.commitIndex());
        }
        this.completeFutures(groupId, commitIndices, new DistributedObjectDestroyedException(name + " is destroyed"));
        return true;
    }

    @Override
    public final RR takeSnapshot(CPGroupId groupId, long commitIndex) {
        RR registry = this.getRegistryOrNull(groupId);
        return (RR)(registry != null ? ((ResourceRegistry)registry).cloneForSnapshot() : null);
    }

    @Override
    public final void restoreSnapshot(CPGroupId groupId, long commitIndex, RR registry) {
        ResourceRegistry prev = (ResourceRegistry)this.registries.put(((ResourceRegistry)registry).getGroupId(), registry);
        Map<Tuple2<String, UUID>, Tuple2<Long, Long>> existingWaitTimeouts = prev != null ? prev.getWaitTimeouts() : Collections.emptyMap();
        Map<Tuple2<String, UUID>, Long> newWaitKeys = ((ResourceRegistry)registry).overwriteWaitTimeouts(existingWaitTimeouts);
        for (Map.Entry<Tuple2<String, UUID>, Long> e : newWaitKeys.entrySet()) {
            this.scheduleTimeout(groupId, (String)e.getKey().element1, (UUID)e.getKey().element2, e.getValue());
        }
        ((ResourceRegistry)registry).onSnapshotRestore();
        this.onRegistryRestored(registry);
    }

    @Override
    public void setSessionAccessor(SessionAccessor accessor) {
        this.sessionAccessor = accessor;
    }

    @Override
    public final void onSessionClose(CPGroupId groupId, long sessionId) {
        ResourceRegistry registry = (ResourceRegistry)this.registries.get(groupId);
        if (registry == null) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Resource registry of " + groupId + " not found to handle closed Session[" + sessionId + "]");
            }
            return;
        }
        ArrayList<Long> expiredWaitKeys = new ArrayList<Long>();
        Long2ObjectHashMap<Object> completedWaitKeys = new Long2ObjectHashMap<Object>();
        registry.closeSession(sessionId, expiredWaitKeys, completedWaitKeys);
        if (this.logger.isFineEnabled() && (expiredWaitKeys.size() > 0 || completedWaitKeys.size() > 0)) {
            this.logger.fine("Closed Session[" + sessionId + "] in " + groupId + " expired wait key commit indices: " + expiredWaitKeys + " completed wait keys: " + completedWaitKeys);
        }
        this.completeFutures(groupId, expiredWaitKeys, new SessionExpiredException());
        RaftNodeImpl raftNode = (RaftNodeImpl)this.raftService.getRaftNode(groupId);
        for (Map.Entry<Long, Object> entry : completedWaitKeys.entrySet()) {
            raftNode.completeFuture(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public final Collection<Long> getAttachedSessions(CPGroupId groupId) {
        RR registry = this.getRegistryOrNull(groupId);
        return registry != null ? ((ResourceRegistry)registry).getAttachedSessions() : Collections.emptyList();
    }

    @Override
    public final void onRaftGroupDestroyed(CPGroupId groupId) {
        ResourceRegistry registry = (ResourceRegistry)this.registries.get(groupId);
        if (registry != null) {
            Collection<Long> indices = registry.destroy();
            this.completeFutures(groupId, indices, new DistributedObjectDestroyedException(groupId + " is destroyed"));
        }
    }

    @Override
    public final void onRaftNodeSteppedDown(CPGroupId groupId) {
    }

    @Override
    public final void populate(LiveOperations liveOperations) {
        long now = Clock.currentTimeMillis();
        for (ResourceRegistry registry : this.registries.values()) {
            registry.populate(liveOperations, now);
        }
    }

    public final void expireWaitKeys(CPGroupId groupId, Collection<Tuple2<String, UUID>> keys) {
        ResourceRegistry registry = (ResourceRegistry)this.registries.get(groupId);
        if (registry == null) {
            this.logger.severe("Registry of " + groupId + " not found to expire wait keys: " + keys);
            return;
        }
        ArrayList expired = new ArrayList();
        for (Tuple2<String, UUID> key : keys) {
            registry.expireWaitKey((String)key.element1, (UUID)key.element2, expired);
        }
        ArrayList<Long> commitIndices = new ArrayList<Long>();
        for (WaitKey key : expired) {
            commitIndices.add(key.commitIndex());
            registry.removeLiveOperation(key);
        }
        this.completeFutures(groupId, commitIndices, this.expiredWaitKeyResponse());
    }

    public final RR getRegistryOrNull(CPGroupId groupId) {
        return (RR)((ResourceRegistry)this.registries.get(groupId));
    }

    public Collection<Tuple2<Address, Long>> getLiveOperations(CPGroupId groupId) {
        ResourceRegistry registry = (ResourceRegistry)this.registries.get(groupId);
        if (registry == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(registry.getLiveOperations());
    }

    protected final RR getOrInitRegistry(CPGroupId groupId) {
        Preconditions.checkNotNull(groupId);
        ResourceRegistry registry = (ResourceRegistry)this.registries.get(groupId);
        if (registry == null) {
            registry = this.createNewRegistry(groupId);
            this.registries.put(groupId, registry);
        }
        return (RR)registry;
    }

    protected final void scheduleTimeout(CPGroupId groupId, String name, UUID invocationUid, long timeoutMs) {
        if (timeoutMs > 0L && timeoutMs <= 1500L) {
            InternalExecutionService executionService = this.nodeEngine.getExecutionService();
            executionService.schedule(new ExpireWaitKeysTask(groupId, Tuple2.of(name, invocationUid)), timeoutMs, TimeUnit.MILLISECONDS);
        }
    }

    protected final void heartbeatSession(CPGroupId groupId, long sessionId) {
        if (sessionId == -1L) {
            return;
        }
        if (this.sessionAccessor.isActive(groupId, sessionId)) {
            this.sessionAccessor.heartbeat(groupId, sessionId);
            return;
        }
        throw new SessionExpiredException("active session: " + sessionId + " does not exist in " + groupId);
    }

    protected final void notifyWaitKeys(CPGroupId groupId, String name, Collection<W> keys, Object result) {
        if (keys.isEmpty()) {
            return;
        }
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Resource[" + name + "] in " + groupId + " completed wait keys: " + keys + " result: " + result);
        }
        ArrayList<Long> indices = new ArrayList<Long>(keys.size());
        for (WaitKey key : keys) {
            indices.add(key.commitIndex());
        }
        this.completeFutures(groupId, indices, result);
    }

    private void completeFutures(CPGroupId groupId, Collection<Long> indices, Object result) {
        if (!indices.isEmpty()) {
            RaftNodeImpl raftNode = (RaftNodeImpl)this.raftService.getRaftNode(groupId);
            if (raftNode != null) {
                for (Long index : indices) {
                    raftNode.completeFuture(index, result);
                }
            } else {
                this.logger.severe("RaftNode not found for " + groupId + " to notify commit indices " + indices + " with " + result);
            }
        }
    }

    private void tryReplicateExpiredWaitKeys(CPGroupId groupId, Collection<Tuple2<String, UUID>> keys) {
        block3: {
            try {
                RaftNode raftNode = this.raftService.getRaftNode(groupId);
                if (raftNode != null) {
                    raftNode.replicate(new ExpireWaitKeysOp(this.serviceName(), keys)).get();
                }
            }
            catch (Exception e) {
                if (!this.logger.isFineEnabled()) break block3;
                this.logger.fine("Could not expire wait keys: " + keys + " in " + groupId, e);
            }
        }
    }

    private class ExpireWaitKeysPeriodicTask
    implements Runnable {
        private ExpireWaitKeysPeriodicTask() {
        }

        @Override
        public void run() {
            for (Map.Entry<CPGroupId, Collection<Tuple2<String, UUID>>> e : this.getWaitKeysToExpire().entrySet()) {
                AbstractBlockingService.this.tryReplicateExpiredWaitKeys(e.getKey(), e.getValue());
            }
        }

        private Map<CPGroupId, Collection<Tuple2<String, UUID>>> getWaitKeysToExpire() {
            HashMap<CPGroupId, Collection<Tuple2<String, UUID>>> timeouts = new HashMap<CPGroupId, Collection<Tuple2<String, UUID>>>();
            long now = Clock.currentTimeMillis();
            for (ResourceRegistry registry : AbstractBlockingService.this.registries.values()) {
                Collection<Tuple2<String, UUID>> t = registry.getWaitKeysToExpire(now);
                if (t.size() <= 0) continue;
                timeouts.put(registry.getGroupId(), t);
            }
            return timeouts;
        }
    }

    private class ExpireWaitKeysTask
    implements Runnable {
        final CPGroupId groupId;
        final Collection<Tuple2<String, UUID>> keys;

        ExpireWaitKeysTask(CPGroupId groupId, Tuple2<String, UUID> key) {
            this.groupId = groupId;
            this.keys = Collections.singleton(key);
        }

        @Override
        public void run() {
            AbstractBlockingService.this.tryReplicateExpiredWaitKeys(this.groupId, this.keys);
        }
    }
}

