/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nonnull
 */
package com.hazelcast.cp.internal.datastructures.lock.proxy;

import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.datastructures.exception.WaitKeyCancelledException;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import com.hazelcast.cp.internal.session.AbstractProxySessionManager;
import com.hazelcast.cp.internal.session.SessionAwareProxy;
import com.hazelcast.cp.internal.session.SessionExpiredException;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.cp.lock.exception.LockAcquireLimitReachedException;
import com.hazelcast.cp.lock.exception.LockOwnershipLostException;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.UuidUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import javax.annotation.Nonnull;

public abstract class AbstractRaftFencedLockProxy
extends SessionAwareProxy
implements FencedLock {
    protected final String proxyName;
    protected final String objectName;
    private final Map<Long, Long> lockedSessionIds = new ConcurrentHashMap<Long, Long>();

    public AbstractRaftFencedLockProxy(AbstractProxySessionManager sessionManager, RaftGroupId groupId, String proxyName, String objectName) {
        super(sessionManager, groupId);
        this.proxyName = proxyName;
        this.objectName = objectName;
    }

    protected abstract InternalCompletableFuture<Long> doLock(long var1, long var3, UUID var5);

    protected abstract InternalCompletableFuture<Long> doTryLock(long var1, long var3, UUID var5, long var6);

    protected abstract InternalCompletableFuture<Boolean> doUnlock(long var1, long var3, UUID var5);

    protected abstract InternalCompletableFuture<RaftLockOwnershipState> doGetLockOwnershipState();

    @Override
    public void lock() {
        this.lockAndGetFence();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        while (true) {
            long sessionId = this.acquireSession();
            this.verifyLockedSessionIdIfPresent(threadId, sessionId, true);
            try {
                long fence = (Long)this.doLock(sessionId, threadId, invocationUid).get();
                if (fence != 0L) {
                    this.lockedSessionIds.put(threadId, sessionId);
                    return;
                }
                throw new LockAcquireLimitReachedException("Lock[" + this.proxyName + "] reentrant lock limit is already reached!");
            }
            catch (SessionExpiredException e) {
                this.invalidateSession(sessionId);
                this.verifyNoLockedSessionIdPresent(threadId);
                continue;
            }
            catch (WaitKeyCancelledException e) {
                this.releaseSession(sessionId);
                throw new IllegalMonitorStateException("Lock[" + this.proxyName + "] not acquired because its wait is cancelled!");
            }
            catch (Throwable t) {
                this.releaseSession(sessionId);
                if (t instanceof InterruptedException) {
                    throw (InterruptedException)t;
                }
                throw ExceptionUtil.rethrow(t);
            }
            break;
        }
    }

    @Override
    public final long lockAndGetFence() {
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        while (true) {
            long sessionId = this.acquireSession();
            this.verifyLockedSessionIdIfPresent(threadId, sessionId, true);
            try {
                long fence = this.doLock(sessionId, threadId, invocationUid).join();
                if (fence != 0L) {
                    this.lockedSessionIds.put(threadId, sessionId);
                    return fence;
                }
                throw new LockAcquireLimitReachedException("Lock[" + this.proxyName + "] reentrant lock limit is already reached!");
            }
            catch (SessionExpiredException e) {
                this.invalidateSession(sessionId);
                this.verifyNoLockedSessionIdPresent(threadId);
                continue;
            }
            catch (WaitKeyCancelledException e) {
                this.releaseSession(sessionId);
                throw new IllegalMonitorStateException("Lock[" + this.proxyName + "] not acquired because its wait is cancelled!");
            }
            catch (Throwable t) {
                this.releaseSession(sessionId);
                throw ExceptionUtil.rethrow(t);
            }
            break;
        }
    }

    @Override
    public boolean tryLock() {
        return this.tryLockAndGetFence() != 0L;
    }

    @Override
    public final long tryLockAndGetFence() {
        return this.tryLockAndGetFence(0L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(long time, @Nonnull TimeUnit unit) {
        return this.tryLockAndGetFence(time, unit) != 0L;
    }

    @Override
    public final long tryLockAndGetFence(long time, @Nonnull TimeUnit unit) {
        Preconditions.checkNotNull(unit);
        long threadId = ThreadUtil.getThreadId();
        UUID invocationUid = UuidUtil.newUnsecureUUID();
        long timeoutMillis = Math.max(0L, unit.toMillis(time));
        while (true) {
            long start = Clock.currentTimeMillis();
            long sessionId = this.acquireSession();
            this.verifyLockedSessionIdIfPresent(threadId, sessionId, true);
            try {
                long fence = this.doTryLock(sessionId, threadId, invocationUid, timeoutMillis).join();
                if (fence != 0L) {
                    this.lockedSessionIds.put(threadId, sessionId);
                } else {
                    this.releaseSession(sessionId);
                }
                return fence;
            }
            catch (WaitKeyCancelledException e) {
                this.releaseSession(sessionId);
                return 0L;
            }
            catch (SessionExpiredException e) {
                this.invalidateSession(sessionId);
                this.verifyNoLockedSessionIdPresent(threadId);
                if ((timeoutMillis -= Clock.currentTimeMillis() - start) > 0L) continue;
                return 0L;
            }
            catch (Throwable t) {
                this.releaseSession(sessionId);
                throw ExceptionUtil.rethrow(t);
            }
            break;
        }
    }

    @Override
    @SuppressFBWarnings(value={"IMSE_DONT_CATCH_IMSE"})
    public final void unlock() {
        long threadId = ThreadUtil.getThreadId();
        long sessionId = this.getSession();
        this.verifyLockedSessionIdIfPresent(threadId, sessionId, false);
        if (sessionId == -1L) {
            this.lockedSessionIds.remove(threadId);
            throw this.newIllegalMonitorStateException();
        }
        try {
            boolean stillLockedByCurrentThread = this.doUnlock(sessionId, threadId, UuidUtil.newUnsecureUUID()).join();
            if (stillLockedByCurrentThread) {
                this.lockedSessionIds.put(threadId, sessionId);
            } else {
                this.lockedSessionIds.remove(threadId);
            }
            this.releaseSession(sessionId);
        }
        catch (SessionExpiredException e) {
            this.invalidateSession(sessionId);
            this.lockedSessionIds.remove(threadId);
            throw this.newLockOwnershipLostException(sessionId);
        }
        catch (IllegalMonitorStateException e) {
            this.lockedSessionIds.remove(threadId);
            throw e;
        }
    }

    @Override
    public final Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final long getFence() {
        long threadId = ThreadUtil.getThreadId();
        long sessionId = this.getSession();
        this.verifyLockedSessionIdIfPresent(threadId, sessionId, false);
        if (sessionId == -1L) {
            this.lockedSessionIds.remove(threadId);
            throw this.newIllegalMonitorStateException();
        }
        RaftLockOwnershipState ownership = this.doGetLockOwnershipState().join();
        if (ownership.isLockedBy(sessionId, threadId)) {
            this.lockedSessionIds.put(threadId, sessionId);
            return ownership.getFence();
        }
        this.verifyNoLockedSessionIdPresent(threadId);
        throw this.newIllegalMonitorStateException();
    }

    @Override
    public final boolean isLocked() {
        long threadId = ThreadUtil.getThreadId();
        long sessionId = this.getSession();
        this.verifyLockedSessionIdIfPresent(threadId, sessionId, false);
        RaftLockOwnershipState ownership = this.doGetLockOwnershipState().join();
        if (ownership.isLockedBy(sessionId, threadId)) {
            this.lockedSessionIds.put(threadId, sessionId);
            return true;
        }
        this.verifyNoLockedSessionIdPresent(threadId);
        return ownership.isLocked();
    }

    @Override
    public final boolean isLockedByCurrentThread() {
        long threadId = ThreadUtil.getThreadId();
        long sessionId = this.getSession();
        this.verifyLockedSessionIdIfPresent(threadId, sessionId, false);
        RaftLockOwnershipState ownership = this.doGetLockOwnershipState().join();
        boolean lockedByCurrentThread = ownership.isLockedBy(sessionId, threadId);
        if (lockedByCurrentThread) {
            this.lockedSessionIds.put(threadId, sessionId);
        } else {
            this.verifyNoLockedSessionIdPresent(threadId);
        }
        return lockedByCurrentThread;
    }

    @Override
    public final int getLockCount() {
        long threadId = ThreadUtil.getThreadId();
        long sessionId = this.getSession();
        this.verifyLockedSessionIdIfPresent(threadId, sessionId, false);
        RaftLockOwnershipState ownership = this.doGetLockOwnershipState().join();
        if (ownership.isLockedBy(sessionId, threadId)) {
            this.lockedSessionIds.put(threadId, sessionId);
        } else {
            this.verifyNoLockedSessionIdPresent(threadId);
        }
        return ownership.getLockCount();
    }

    @Override
    public void destroy() {
        this.lockedSessionIds.clear();
    }

    @Override
    public final String getName() {
        return this.proxyName;
    }

    public String getObjectName() {
        return this.objectName;
    }

    @Override
    public String getPartitionKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServiceName() {
        return "hz:raft:lockService";
    }

    public Long getLockedSessionId(long threadId) {
        return this.lockedSessionIds.get(threadId);
    }

    private void verifyLockedSessionIdIfPresent(long threadId, long sessionId, boolean releaseSession) {
        Long lockedSessionId = this.lockedSessionIds.get(threadId);
        if (lockedSessionId != null && lockedSessionId != sessionId) {
            this.lockedSessionIds.remove(threadId);
            if (releaseSession) {
                this.releaseSession(sessionId);
            }
            throw this.newLockOwnershipLostException(lockedSessionId);
        }
    }

    private void verifyNoLockedSessionIdPresent(long threadId) {
        Long lockedSessionId = this.lockedSessionIds.remove(threadId);
        if (lockedSessionId != null) {
            this.lockedSessionIds.remove(threadId);
            throw this.newLockOwnershipLostException(lockedSessionId);
        }
    }

    private IllegalMonitorStateException newIllegalMonitorStateException() {
        return new IllegalMonitorStateException("Current thread is not owner of the Lock[" + this.proxyName + "]");
    }

    private LockOwnershipLostException newLockOwnershipLostException(long sessionId) {
        return new LockOwnershipLostException("Current thread is not owner of the Lock[" + this.proxyName + "] because its Session[" + sessionId + "] is closed by server!");
    }
}

