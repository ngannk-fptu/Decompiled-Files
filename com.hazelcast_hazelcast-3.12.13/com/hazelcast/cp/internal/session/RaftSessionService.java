/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftNodeLifecycleAwareService;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.TermChangeAwareService;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.session.CPSessionInfo;
import com.hazelcast.cp.internal.session.RaftSessionRegistry;
import com.hazelcast.cp.internal.session.SessionAccessor;
import com.hazelcast.cp.internal.session.SessionAwareService;
import com.hazelcast.cp.internal.session.SessionResponse;
import com.hazelcast.cp.internal.session.operation.CloseInactiveSessionsOp;
import com.hazelcast.cp.internal.session.operation.CloseSessionOp;
import com.hazelcast.cp.internal.session.operation.ExpireSessionsOp;
import com.hazelcast.cp.internal.session.operation.GetSessionsOp;
import com.hazelcast.cp.internal.util.PartitionSpecificRunnableAdaptor;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.cp.session.CPSessionManagementService;
import com.hazelcast.internal.util.SimpleCompletableFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RaftSessionService
implements ManagedService,
SnapshotAwareService<RaftSessionRegistry>,
SessionAccessor,
TermChangeAwareService,
RaftNodeLifecycleAwareService,
CPSessionManagementService {
    public static final String SERVICE_NAME = "hz:core:raftSession";
    private static final long CHECK_EXPIRED_SESSIONS_TASK_PERIOD_IN_MILLIS = TimeUnit.SECONDS.toMillis(1L);
    private static final long CHECK_INACTIVE_SESSIONS_TASK_PERIOD_IN_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    private static final long COLLECT_INACTIVE_SESSIONS_TASK_TIMEOUT_SECONDS = 5L;
    private final NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private volatile RaftService raftService;
    private final Map<CPGroupId, RaftSessionRegistry> registries = new ConcurrentHashMap<CPGroupId, RaftSessionRegistry>();

    public RaftSessionService(NodeEngine nodeEngine) {
        this.nodeEngine = (NodeEngineImpl)nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.raftService = (RaftService)nodeEngine.getService("hz:core:raft");
        for (SessionAwareService service : nodeEngine.getServices(SessionAwareService.class)) {
            service.setSessionAccessor(this);
        }
        ExecutionService executionService = nodeEngine.getExecutionService();
        executionService.scheduleWithRepetition(new CheckSessionsToExpire(), CHECK_EXPIRED_SESSIONS_TASK_PERIOD_IN_MILLIS, CHECK_EXPIRED_SESSIONS_TASK_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS);
        executionService.scheduleWithRepetition(new CheckInactiveSessions(), CHECK_INACTIVE_SESSIONS_TASK_PERIOD_IN_MILLIS, CHECK_INACTIVE_SESSIONS_TASK_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
        this.registries.clear();
    }

    @Override
    public RaftSessionRegistry takeSnapshot(CPGroupId groupId, long commitIndex) {
        RaftSessionRegistry registry = this.registries.get(groupId);
        return registry != null ? registry.cloneForSnapshot() : null;
    }

    @Override
    public void restoreSnapshot(CPGroupId groupId, long commitIndex, RaftSessionRegistry registry) {
        if (registry != null) {
            this.registries.put(groupId, registry);
        }
    }

    @Override
    public void onNewTermCommit(CPGroupId groupId, long commitIndex) {
        RaftSessionRegistry registry = this.registries.get(groupId);
        if (registry != null) {
            registry.shiftExpirationTimes(this.getHeartbeatIntervalMillis());
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Session expiration times are shifted in " + groupId);
            }
        }
    }

    @Override
    public void onRaftGroupDestroyed(CPGroupId groupId) {
        this.registries.remove(groupId);
    }

    @Override
    public void onRaftNodeSteppedDown(CPGroupId groupId) {
    }

    @Override
    public ICompletableFuture<Collection<CPSession>> getAllSessions(String groupName) {
        Preconditions.checkTrue(!"METADATA".equals(groupName), "Cannot query CP sessions on the METADATA CP group!");
        ManagedExecutorService executor = this.nodeEngine.getExecutionService().getExecutor("hz:system");
        final SimpleCompletableFuture<Collection<CPSession>> future = new SimpleCompletableFuture<Collection<CPSession>>(executor, this.logger);
        final ExecutionCallback<Collection<CPSession>> callback = new ExecutionCallback<Collection<CPSession>>(){

            @Override
            public void onResponse(Collection<CPSession> sessions) {
                future.setResult(sessions);
            }

            @Override
            public void onFailure(Throwable t) {
                future.setResult(new ExecutionException(t));
            }
        };
        this.raftService.getCPGroup(groupName).andThen(new ExecutionCallback<CPGroup>(){

            @Override
            public void onResponse(CPGroup group) {
                if (group != null) {
                    RaftSessionService.this.raftService.getInvocationManager().invoke(group.id(), new GetSessionsOp()).andThen(callback);
                } else {
                    future.setResult(new ExecutionException(new IllegalArgumentException()));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                future.setResult(new ExecutionException(t));
            }
        });
        return future;
    }

    @Override
    public ICompletableFuture<Boolean> forceCloseSession(String groupName, final long sessionId) {
        ManagedExecutorService executor = this.nodeEngine.getExecutionService().getExecutor("hz:system");
        final SimpleCompletableFuture<Boolean> future = new SimpleCompletableFuture<Boolean>(executor, this.logger);
        final ExecutionCallback<Boolean> callback = new ExecutionCallback<Boolean>(){

            @Override
            public void onResponse(Boolean response) {
                future.setResult(response);
            }

            @Override
            public void onFailure(Throwable t) {
                future.setResult(new ExecutionException(t));
            }
        };
        this.raftService.getCPGroup(groupName).andThen(new ExecutionCallback<CPGroup>(){

            @Override
            public void onResponse(CPGroup group) {
                if (group != null) {
                    RaftSessionService.this.raftService.getInvocationManager().invoke(group.id(), new CloseSessionOp(sessionId)).andThen(callback);
                } else {
                    future.setResult(false);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                future.setResult(new ExecutionException(t));
            }
        });
        return future;
    }

    public SessionResponse createNewSession(CPGroupId groupId, Address endpoint, String endpointName, CPSession.CPSessionOwnerType endpointType) {
        RaftSessionRegistry registry = this.getOrInitRegistry(groupId);
        long creationTime = Clock.currentTimeMillis();
        long sessionTTLMillis = this.getSessionTTLMillis();
        long sessionId = registry.createNewSession(sessionTTLMillis, endpoint, endpointName, endpointType, creationTime);
        this.logger.info("Created new session: " + sessionId + " in " + groupId + " for " + (Object)((Object)endpointType) + " -> " + endpoint);
        return new SessionResponse(sessionId, sessionTTLMillis, this.getHeartbeatIntervalMillis());
    }

    private RaftSessionRegistry getOrInitRegistry(CPGroupId groupId) {
        RaftSessionRegistry registry = this.registries.get(groupId);
        if (registry == null) {
            registry = new RaftSessionRegistry(groupId);
            this.registries.put(groupId, registry);
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Created new session registry for " + groupId);
            }
        }
        return registry;
    }

    @Override
    public void heartbeat(CPGroupId groupId, long sessionId) {
        RaftSessionRegistry registry = this.registries.get(groupId);
        if (registry == null) {
            throw new IllegalStateException("No session: " + sessionId + " for CP group: " + groupId);
        }
        registry.heartbeat(sessionId, this.getSessionTTLMillis());
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Session: " + sessionId + " heartbeat in " + groupId);
        }
    }

    public boolean closeSession(CPGroupId groupId, long sessionId) {
        RaftSessionRegistry registry = this.registries.get(groupId);
        if (registry == null) {
            return false;
        }
        if (registry.closeSession(sessionId)) {
            this.logger.info("Session: " + sessionId + " is closed in " + groupId);
            this.notifyServices(groupId, Collections.singleton(sessionId));
            return true;
        }
        return false;
    }

    public void expireSessions(CPGroupId groupId, Collection<Tuple2<Long, Long>> sessionsToExpire) {
        RaftSessionRegistry registry = this.registries.get(groupId);
        if (registry == null) {
            return;
        }
        ArrayList<Long> expired = new ArrayList<Long>();
        for (Tuple2<Long, Long> s : sessionsToExpire) {
            long version;
            long sessionId = (Long)s.element1;
            if (!registry.expireSession(sessionId, version = ((Long)s.element2).longValue())) continue;
            expired.add(sessionId);
        }
        if (expired.size() > 0) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Sessions: " + expired + " are expired in " + groupId);
            }
            this.notifyServices(groupId, expired);
        }
    }

    public void closeInactiveSessions(CPGroupId groupId, Collection<Long> inactiveSessions) {
        RaftSessionRegistry registry = this.registries.get(groupId);
        if (registry == null) {
            return;
        }
        HashSet<Long> closed = new HashSet<Long>(inactiveSessions);
        for (SessionAwareService service : this.nodeEngine.getServices(SessionAwareService.class)) {
            closed.removeAll(service.getAttachedSessions(groupId));
        }
        Iterator<SessionAwareService> iterator = closed.iterator();
        while (iterator.hasNext()) {
            long sessionId = (Long)((Object)iterator.next());
            registry.closeSession(sessionId);
        }
        if (closed.size() > 0) {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("Inactive sessions: " + closed + " are closed in " + groupId);
            }
            this.notifyServices(groupId, closed);
        }
    }

    public long generateThreadId(CPGroupId groupId) {
        return this.getOrInitRegistry(groupId).generateThreadId();
    }

    public Collection<CPSession> getSessionsLocally(CPGroupId groupId) {
        RaftSessionRegistry registry = this.getSessionRegistryOrNull(groupId);
        if (registry == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(registry.getSessions());
    }

    RaftSessionRegistry getSessionRegistryOrNull(CPGroupId groupId) {
        return this.registries.get(groupId);
    }

    private long getHeartbeatIntervalMillis() {
        return TimeUnit.SECONDS.toMillis(this.raftService.getConfig().getSessionHeartbeatIntervalSeconds());
    }

    private long getSessionTTLMillis() {
        return TimeUnit.SECONDS.toMillis(this.raftService.getConfig().getSessionTimeToLiveSeconds());
    }

    private void notifyServices(CPGroupId groupId, Collection<Long> sessionIds) {
        Collection<SessionAwareService> services = this.nodeEngine.getServices(SessionAwareService.class);
        for (SessionAwareService sessionAwareService : services) {
            for (long sessionId : sessionIds) {
                sessionAwareService.onSessionClose(groupId, sessionId);
            }
        }
    }

    @Override
    public boolean isActive(CPGroupId groupId, long sessionId) {
        RaftSessionRegistry sessionRegistry = this.registries.get(groupId);
        if (sessionRegistry == null) {
            return false;
        }
        CPSessionInfo session = sessionRegistry.getSession(sessionId);
        return session != null;
    }

    private Map<CPGroupId, Collection<Tuple2<Long, Long>>> getSessionsToExpire() {
        HashMap<CPGroupId, Collection<Tuple2<Long, Long>>> expired = new HashMap<CPGroupId, Collection<Tuple2<Long, Long>>>();
        for (RaftSessionRegistry registry : this.registries.values()) {
            Collection<Tuple2<Long, Long>> e = registry.getSessionsToExpire();
            if (e.isEmpty()) continue;
            expired.put(registry.groupId(), e);
        }
        return expired;
    }

    private Map<CPGroupId, Collection<Long>> getInactiveSessions() {
        final ConcurrentHashMap<CPGroupId, Collection<Long>> response = new ConcurrentHashMap<CPGroupId, Collection<Long>>();
        final Semaphore semaphore = new Semaphore(0);
        OperationServiceImpl operationService = (OperationServiceImpl)this.nodeEngine.getOperationService();
        ArrayList<RaftSessionRegistry> registries = new ArrayList<RaftSessionRegistry>(this.registries.values());
        for (final RaftSessionRegistry registry : registries) {
            final CPGroupId groupId = registry.groupId();
            operationService.execute(new PartitionSpecificRunnableAdaptor(new Runnable(){

                @Override
                public void run() {
                    HashSet<Long> activeSessionIds = new HashSet<Long>();
                    for (SessionAwareService service : RaftSessionService.this.nodeEngine.getServices(SessionAwareService.class)) {
                        activeSessionIds.addAll(service.getAttachedSessions(groupId));
                    }
                    HashSet<Long> inactiveSessionIds = new HashSet<Long>();
                    for (CPSession cPSession : registry.getSessions()) {
                        if (activeSessionIds.contains(cPSession.id()) || cPSession.creationTime() + RaftSessionService.this.getSessionTTLMillis() >= Clock.currentTimeMillis()) continue;
                        inactiveSessionIds.add(cPSession.id());
                    }
                    if (inactiveSessionIds.size() > 0) {
                        response.put(groupId, inactiveSessionIds);
                    }
                    semaphore.release();
                }
            }, this.nodeEngine.getPartitionService().getPartitionId(groupId)));
        }
        try {
            semaphore.tryAcquire(registries.size(), 5L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return response;
    }

    private class CheckInactiveSessions
    implements Runnable {
        private CheckInactiveSessions() {
        }

        @Override
        public void run() {
            Map inactiveSessions = RaftSessionService.this.getInactiveSessions();
            for (Map.Entry entry : inactiveSessions.entrySet()) {
                CPGroupId groupId = (CPGroupId)entry.getKey();
                RaftNode raftNode = RaftSessionService.this.raftService.getRaftNode(groupId);
                if (raftNode == null) continue;
                Collection sessions = (Collection)entry.getValue();
                try {
                    ICompletableFuture f = raftNode.replicate(new CloseInactiveSessionsOp(sessions));
                    f.get();
                }
                catch (Exception e) {
                    if (!RaftSessionService.this.logger.isFineEnabled()) continue;
                    RaftSessionService.this.logger.fine("Could not close inactive sessions: " + sessions + " of " + groupId, e);
                }
            }
        }
    }

    private class CheckSessionsToExpire
    implements Runnable {
        private CheckSessionsToExpire() {
        }

        @Override
        public void run() {
            Map sessionsToExpire = RaftSessionService.this.getSessionsToExpire();
            for (Map.Entry entry : sessionsToExpire.entrySet()) {
                CPGroupId groupId = (CPGroupId)entry.getKey();
                RaftNode raftNode = RaftSessionService.this.raftService.getRaftNode(groupId);
                if (raftNode == null) continue;
                Collection sessions = (Collection)entry.getValue();
                try {
                    ICompletableFuture f = raftNode.replicate(new ExpireSessionsOp(sessions));
                    f.get();
                }
                catch (Exception e) {
                    if (!RaftSessionService.this.logger.isFineEnabled()) continue;
                    RaftSessionService.this.logger.fine("Could not invalidate sessions: " + sessions + " of " + groupId, e);
                }
            }
        }
    }
}

