/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.spi.RaftManagedService;
import com.hazelcast.cp.internal.session.AbstractProxySessionManager;
import com.hazelcast.cp.internal.session.SessionResponse;
import com.hazelcast.cp.internal.session.operation.CloseSessionOp;
import com.hazelcast.cp.internal.session.operation.CreateSessionOp;
import com.hazelcast.cp.internal.session.operation.GenerateThreadIdOp;
import com.hazelcast.cp.internal.session.operation.HeartbeatSessionOp;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.GracefulShutdownAwareService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ExceptionUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProxySessionManagerService
extends AbstractProxySessionManager
implements GracefulShutdownAwareService,
RaftManagedService {
    public static final String SERVICE_NAME = "hz:raft:proxySessionManagerService";
    private static final long SHUTDOWN_TASK_PERIOD_IN_MILLIS = TimeUnit.SECONDS.toMillis(1L);
    private final NodeEngine nodeEngine;

    public ProxySessionManagerService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    protected long generateThreadId(RaftGroupId groupId) {
        return (Long)this.getInvocationManager().invoke(groupId, new GenerateThreadIdOp()).join();
    }

    @Override
    protected SessionResponse requestNewSession(RaftGroupId groupId) {
        String instanceName = this.nodeEngine.getConfig().getInstanceName();
        CreateSessionOp op = new CreateSessionOp(this.nodeEngine.getThisAddress(), instanceName, CPSession.CPSessionOwnerType.SERVER);
        InternalCompletableFuture future = this.getInvocationManager().invoke(groupId, op);
        try {
            return (SessionResponse)future.get();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    protected ICompletableFuture<Object> heartbeat(RaftGroupId groupId, long sessionId) {
        return this.getInvocationManager().invoke(groupId, new HeartbeatSessionOp(sessionId));
    }

    @Override
    protected ICompletableFuture<Object> closeSession(RaftGroupId groupId, Long sessionId) {
        return this.getInvocationManager().invoke(groupId, new CloseSessionOp(sessionId));
    }

    @Override
    protected ScheduledFuture<?> scheduleWithRepetition(Runnable task, long period, TimeUnit unit) {
        return this.nodeEngine.getExecutionService().scheduleWithRepetition(task, period, period, unit);
    }

    @Override
    public boolean onShutdown(long timeout, TimeUnit unit) {
        ILogger logger = this.nodeEngine.getLogger(this.getClass());
        Map<RaftGroupId, ICompletableFuture<Object>> futures = this.shutdown();
        boolean successful = true;
        for (long remainingTimeNanos = unit.toNanos(timeout); remainingTimeNanos > 0L && futures.size() > 0; remainingTimeNanos -= TimeUnit.MILLISECONDS.toNanos(SHUTDOWN_TASK_PERIOD_IN_MILLIS)) {
            Iterator<Map.Entry<RaftGroupId, ICompletableFuture<Object>>> it = futures.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<RaftGroupId, ICompletableFuture<Object>> entry = it.next();
                RaftGroupId groupId = entry.getKey();
                ICompletableFuture<Object> f = entry.getValue();
                if (!f.isDone()) continue;
                it.remove();
                try {
                    f.get();
                    logger.fine("Session closed for " + groupId);
                }
                catch (Exception e) {
                    logger.warning("Close session failed for " + groupId, e);
                    successful = false;
                }
            }
            try {
                Thread.sleep(SHUTDOWN_TASK_PERIOD_IN_MILLIS);
                continue;
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return successful && futures.isEmpty();
    }

    private RaftInvocationManager getInvocationManager() {
        RaftService raftService = (RaftService)this.nodeEngine.getService("hz:core:raft");
        return raftService.getInvocationManager();
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
    }

    @Override
    public void onCPSubsystemRestart() {
        this.resetInternalState();
    }
}

