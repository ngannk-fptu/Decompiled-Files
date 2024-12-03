/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.session.SessionExpiredException;
import com.hazelcast.cp.internal.session.SessionResponse;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractProxySessionManager {
    public static final long NO_SESSION_ID = -1L;
    private final ConcurrentMap<RaftGroupId, Object> mutexes = new ConcurrentHashMap<RaftGroupId, Object>();
    private final ConcurrentMap<RaftGroupId, SessionState> sessions = new ConcurrentHashMap<RaftGroupId, SessionState>();
    private final ConcurrentMap<Tuple2<RaftGroupId, Long>, Long> threadIds = new ConcurrentHashMap<Tuple2<RaftGroupId, Long>, Long>();
    private final AtomicBoolean scheduleHeartbeat = new AtomicBoolean(false);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean running = true;

    protected abstract long generateThreadId(RaftGroupId var1);

    protected abstract SessionResponse requestNewSession(RaftGroupId var1);

    protected abstract ICompletableFuture<Object> heartbeat(RaftGroupId var1, long var2);

    protected abstract ICompletableFuture<Object> closeSession(RaftGroupId var1, Long var2);

    protected abstract ScheduledFuture<?> scheduleWithRepetition(Runnable var1, long var2, TimeUnit var4);

    protected final void resetInternalState() {
        this.lock.writeLock().lock();
        try {
            this.mutexes.clear();
            this.sessions.clear();
            this.threadIds.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final Long getOrCreateUniqueThreadId(RaftGroupId groupId) {
        this.lock.readLock().lock();
        try {
            Tuple2<RaftGroupId, Long> key = Tuple2.of(groupId, ThreadUtil.getThreadId());
            Long globalThreadId = (Long)this.threadIds.get(key);
            if (globalThreadId != null) {
                Long l = globalThreadId;
                return l;
            }
            globalThreadId = this.generateThreadId(groupId);
            Long existing = this.threadIds.putIfAbsent(key, globalThreadId);
            Long l = existing != null ? existing : globalThreadId;
            return l;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public final long acquireSession(RaftGroupId groupId) {
        return this.getOrCreateSession(groupId).acquire(1);
    }

    public final long acquireSession(RaftGroupId groupId, int count) {
        return this.getOrCreateSession(groupId).acquire(count);
    }

    public final void releaseSession(RaftGroupId groupId, long id) {
        this.releaseSession(groupId, id, 1);
    }

    public final void releaseSession(RaftGroupId groupId, long id, int count) {
        SessionState session = (SessionState)this.sessions.get(groupId);
        if (session != null && session.id == id) {
            session.release(count);
        }
    }

    public final void invalidateSession(RaftGroupId groupId, long id) {
        SessionState session = (SessionState)this.sessions.get(groupId);
        if (session != null && session.id == id) {
            this.sessions.remove(groupId, session);
        }
    }

    public final long getSession(RaftGroupId groupId) {
        SessionState session = (SessionState)this.sessions.get(groupId);
        return session != null ? session.id : -1L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<RaftGroupId, ICompletableFuture<Object>> shutdown() {
        this.lock.writeLock().lock();
        try {
            HashMap<RaftGroupId, ICompletableFuture<Object>> futures = new HashMap<RaftGroupId, ICompletableFuture<Object>>();
            for (Map.Entry e : this.sessions.entrySet()) {
                RaftGroupId groupId = (RaftGroupId)e.getKey();
                long sessionId = ((SessionState)e.getValue()).id;
                ICompletableFuture<Object> f = this.closeSession(groupId, sessionId);
                futures.put(groupId, f);
            }
            this.sessions.clear();
            this.running = false;
            HashMap<RaftGroupId, ICompletableFuture<Object>> hashMap = futures;
            return hashMap;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SessionState getOrCreateSession(RaftGroupId groupId) {
        this.lock.readLock().lock();
        try {
            Object object;
            Preconditions.checkState(this.running, "Session manager is already shut down!");
            SessionState session = (SessionState)this.sessions.get(groupId);
            if (session == null || !session.isValid()) {
                object = this.mutex(groupId);
                synchronized (object) {
                    session = (SessionState)this.sessions.get(groupId);
                    if (session == null || !session.isValid()) {
                        session = this.createNewSession(groupId);
                    }
                }
            }
            object = session;
            return object;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SessionState createNewSession(RaftGroupId groupId) {
        Object object = this.mutex(groupId);
        synchronized (object) {
            SessionResponse response = this.requestNewSession(groupId);
            SessionState session = new SessionState(response.getSessionId(), response.getTtlMillis());
            this.sessions.put(groupId, session);
            this.scheduleHeartbeatTask(response.getHeartbeatMillis());
            return session;
        }
    }

    private Object mutex(RaftGroupId groupId) {
        Object mutex = this.mutexes.get(groupId);
        if (mutex != null) {
            return mutex;
        }
        mutex = new Object();
        Object current = this.mutexes.putIfAbsent(groupId, mutex);
        return current != null ? current : mutex;
    }

    private void scheduleHeartbeatTask(long heartbeatMillis) {
        if (this.scheduleHeartbeat.compareAndSet(false, true)) {
            this.scheduleWithRepetition(new HeartbeatTask(), heartbeatMillis, TimeUnit.MILLISECONDS);
        }
    }

    public final long getSessionAcquireCount(RaftGroupId groupId, long sessionId) {
        SessionState session = (SessionState)this.sessions.get(groupId);
        return session != null && session.id == sessionId ? (long)session.acquireCount.get() : 0L;
    }

    private class HeartbeatTask
    implements Runnable {
        private final Collection<ICompletableFuture<Object>> prevHeartbeats = new ArrayList<ICompletableFuture<Object>>();

        private HeartbeatTask() {
        }

        @Override
        public void run() {
            for (ICompletableFuture<Object> iCompletableFuture : this.prevHeartbeats) {
                iCompletableFuture.cancel(true);
            }
            this.prevHeartbeats.clear();
            for (Map.Entry entry : AbstractProxySessionManager.this.sessions.entrySet()) {
                final RaftGroupId groupId = (RaftGroupId)entry.getKey();
                final SessionState session = (SessionState)entry.getValue();
                if (!session.isInUse()) continue;
                ICompletableFuture<Object> f = AbstractProxySessionManager.this.heartbeat(groupId, session.id);
                f.andThen(new ExecutionCallback<Object>(){

                    @Override
                    public void onResponse(Object response) {
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        RuntimeException cause = ExceptionUtil.peel(t);
                        if (cause instanceof SessionExpiredException || cause instanceof CPGroupDestroyedException) {
                            AbstractProxySessionManager.this.invalidateSession(groupId, session.id);
                        }
                    }
                });
                this.prevHeartbeats.add(f);
            }
        }
    }

    private static class SessionState {
        private final long id;
        private final AtomicInteger acquireCount = new AtomicInteger();
        private final long creationTime;
        private final long ttlMillis;

        SessionState(long id, long ttlMillis) {
            this.id = id;
            this.creationTime = Clock.currentTimeMillis();
            this.ttlMillis = ttlMillis;
        }

        boolean isValid() {
            return this.isInUse() || !this.isExpired(Clock.currentTimeMillis());
        }

        boolean isInUse() {
            return this.acquireCount.get() > 0;
        }

        private boolean isExpired(long timestamp) {
            long expirationTime = this.creationTime + this.ttlMillis;
            if (expirationTime < 0L) {
                expirationTime = Long.MAX_VALUE;
            }
            return timestamp > expirationTime;
        }

        long acquire(int count) {
            this.acquireCount.addAndGet(count);
            return this.id;
        }

        void release(int count) {
            this.acquireCount.addAndGet(-count);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SessionState)) {
                return false;
            }
            SessionState that = (SessionState)o;
            return this.id == that.id;
        }

        public int hashCode() {
            return (int)(this.id ^ this.id >>> 32);
        }
    }
}

