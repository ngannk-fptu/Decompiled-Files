/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireInvocationKey;
import com.hazelcast.cp.internal.datastructures.semaphore.AcquireResult;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.semaphore.ReleaseResult;
import com.hazelcast.cp.internal.datastructures.semaphore.SemaphoreEndpoint;
import com.hazelcast.cp.internal.datastructures.spi.blocking.BlockingResource;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKeyContainer;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.collection.Long2ObjectHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RaftSemaphore
extends BlockingResource<AcquireInvocationKey>
implements IdentifiedDataSerializable {
    private boolean initialized;
    private int available;
    private final Long2ObjectHashMap<SessionSemaphoreState> sessionStates = new Long2ObjectHashMap();

    RaftSemaphore() {
    }

    RaftSemaphore(CPGroupId groupId, String name) {
        super(groupId, name);
    }

    Collection<AcquireInvocationKey> init(int permits) {
        if (this.initialized || this.available != 0) {
            throw new IllegalStateException();
        }
        this.available = permits;
        this.initialized = true;
        return this.assignPermitsToWaitKeys();
    }

    int getAvailable() {
        return this.available;
    }

    boolean isAvailable(int permits) {
        Preconditions.checkPositive(permits, "Permits should be positive!");
        return this.available >= permits;
    }

    AcquireResult acquire(AcquireInvocationKey key, boolean wait) {
        Integer acquired;
        SemaphoreEndpoint endpoint = key.endpoint();
        SessionSemaphoreState state = this.sessionStates.get(key.sessionId());
        if (state != null && (acquired = state.getInvocationResponse(endpoint.threadId(), key.invocationUid())) != null) {
            AcquireResult.AcquireStatus status = acquired > 0 ? AcquireResult.AcquireStatus.SUCCESSFUL : AcquireResult.AcquireStatus.FAILED;
            return new AcquireResult(status, acquired, Collections.emptyList());
        }
        Collection<AcquireInvocationKey> cancelled = this.cancelWaitKeys(endpoint, key.invocationUid());
        if (!this.isAvailable(key.permits())) {
            AcquireResult.AcquireStatus status;
            if (wait) {
                this.addWaitKey(endpoint, key);
                status = AcquireResult.AcquireStatus.WAIT_KEY_ADDED;
            } else {
                this.assignPermitsToInvocation(endpoint, key.invocationUid(), 0);
                status = AcquireResult.AcquireStatus.FAILED;
            }
            return new AcquireResult(status, 0, cancelled);
        }
        this.assignPermitsToInvocation(endpoint, key.invocationUid(), key.permits());
        return new AcquireResult(AcquireResult.AcquireStatus.SUCCESSFUL, key.permits(), cancelled);
    }

    private void assignPermitsToInvocation(SemaphoreEndpoint endpoint, UUID invocationUid, int permits) {
        Tuple2<UUID, Integer> prev;
        long sessionId = endpoint.sessionId();
        if (sessionId == -1L) {
            this.available -= permits;
            return;
        }
        SessionSemaphoreState state = this.sessionStates.get(sessionId);
        if (state == null) {
            state = new SessionSemaphoreState();
            this.sessionStates.put(sessionId, state);
        }
        if ((prev = state.invocationRefUids.put(endpoint.threadId(), Tuple2.of(invocationUid, permits))) == null || !((UUID)prev.element1).equals(invocationUid)) {
            SessionSemaphoreState sessionSemaphoreState = state;
            sessionSemaphoreState.acquiredPermits = sessionSemaphoreState.acquiredPermits + permits;
            this.available -= permits;
        }
    }

    ReleaseResult release(SemaphoreEndpoint endpoint, UUID invocationUid, int permits) {
        Preconditions.checkPositive(permits, "Permits should be positive!");
        long sessionId = endpoint.sessionId();
        if (sessionId != -1L) {
            SessionSemaphoreState state = this.sessionStates.get(sessionId);
            if (state == null) {
                return ReleaseResult.failed(this.cancelWaitKeys(endpoint, invocationUid));
            }
            Integer response = state.getInvocationResponse(endpoint.threadId(), invocationUid);
            if (response != null) {
                if (response > 0) {
                    return ReleaseResult.successful(Collections.emptyList(), Collections.emptyList());
                }
                return ReleaseResult.failed(this.cancelWaitKeys(endpoint, invocationUid));
            }
            if (state.acquiredPermits < permits) {
                state.invocationRefUids.put(endpoint.threadId(), Tuple2.of(invocationUid, 0));
                return ReleaseResult.failed(this.cancelWaitKeys(endpoint, invocationUid));
            }
            SessionSemaphoreState sessionSemaphoreState = state;
            sessionSemaphoreState.acquiredPermits = sessionSemaphoreState.acquiredPermits - permits;
            state.invocationRefUids.put(endpoint.threadId(), Tuple2.of(invocationUid, permits));
        }
        this.available += permits;
        Collection<AcquireInvocationKey> cancelled = this.cancelWaitKeys(endpoint, invocationUid);
        Collection<AcquireInvocationKey> acquired = this.assignPermitsToWaitKeys();
        return ReleaseResult.successful(acquired, cancelled);
    }

    RaftSemaphore cloneForSnapshot() {
        RaftSemaphore clone = new RaftSemaphore();
        this.cloneForSnapshot(clone);
        clone.initialized = this.initialized;
        clone.available = this.available;
        for (Map.Entry<Long, SessionSemaphoreState> e : this.sessionStates.entrySet()) {
            SessionSemaphoreState s = new SessionSemaphoreState();
            s.acquiredPermits = e.getValue().acquiredPermits;
            s.invocationRefUids.putAll(e.getValue().invocationRefUids);
            clone.sessionStates.put(e.getKey(), s);
        }
        return clone;
    }

    private Collection<AcquireInvocationKey> cancelWaitKeys(SemaphoreEndpoint endpoint, UUID invocationUid) {
        Collection<AcquireInvocationKey> cancelled = null;
        WaitKeyContainer container = this.getWaitKeyContainer(endpoint);
        if (container != null && ((AcquireInvocationKey)container.key()).isDifferentInvocationOf(endpoint, invocationUid)) {
            cancelled = container.keyAndRetries();
            this.removeWaitKey(endpoint);
        }
        return cancelled != null ? cancelled : Collections.emptyList();
    }

    private Collection<AcquireInvocationKey> assignPermitsToWaitKeys() {
        ArrayList<AcquireInvocationKey> assigned = new ArrayList<AcquireInvocationKey>();
        Iterator iterator = this.waitKeyContainersIterator();
        while (iterator.hasNext() && this.available > 0) {
            WaitKeyContainer container = iterator.next();
            AcquireInvocationKey key = (AcquireInvocationKey)container.key();
            if (key.permits() > this.available) continue;
            iterator.remove();
            assigned.addAll(container.keyAndRetries());
            this.assignPermitsToInvocation(key.endpoint(), key.invocationUid(), key.permits());
        }
        return assigned;
    }

    AcquireResult drain(SemaphoreEndpoint endpoint, UUID invocationUid) {
        Integer permits;
        SessionSemaphoreState state = this.sessionStates.get(endpoint.sessionId());
        if (state != null && (permits = state.getInvocationResponse(endpoint.threadId(), invocationUid)) != null) {
            return new AcquireResult(AcquireResult.AcquireStatus.SUCCESSFUL, permits, Collections.emptyList());
        }
        Collection<AcquireInvocationKey> cancelled = this.cancelWaitKeys(endpoint, invocationUid);
        int drained = this.available;
        this.assignPermitsToInvocation(endpoint, invocationUid, drained);
        this.available = 0;
        return new AcquireResult(AcquireResult.AcquireStatus.SUCCESSFUL, drained, cancelled);
    }

    ReleaseResult change(SemaphoreEndpoint endpoint, UUID invocationUid, int permits) {
        if (permits == 0) {
            return ReleaseResult.failed(Collections.emptyList());
        }
        Collection<AcquireInvocationKey> cancelled = this.cancelWaitKeys(endpoint, invocationUid);
        long sessionId = endpoint.sessionId();
        if (sessionId != -1L) {
            long threadId;
            Integer response;
            SessionSemaphoreState state = this.sessionStates.get(sessionId);
            if (state == null) {
                state = new SessionSemaphoreState();
                this.sessionStates.put(sessionId, state);
            }
            if ((response = state.getInvocationResponse(threadId = endpoint.threadId(), invocationUid)) != null) {
                List<AcquireInvocationKey> c = Collections.emptyList();
                return ReleaseResult.successful(c, c);
            }
            state.invocationRefUids.put(threadId, Tuple2.of(invocationUid, permits));
        }
        this.available += permits;
        this.initialized = true;
        List<AcquireInvocationKey> acquired = permits > 0 ? this.assignPermitsToWaitKeys() : Collections.emptyList();
        return ReleaseResult.successful(acquired, cancelled);
    }

    @Override
    protected void onSessionClose(long sessionId, Map<Long, Object> responses) {
        SessionSemaphoreState state = this.sessionStates.get(sessionId);
        if (state != null) {
            if (state.acquiredPermits > 0) {
                SemaphoreEndpoint endpoint = new SemaphoreEndpoint(sessionId, 0L);
                ReleaseResult result = this.release(endpoint, UuidUtil.newUnsecureUUID(), state.acquiredPermits);
                assert (result.cancelledWaitKeys().isEmpty());
                for (AcquireInvocationKey key : result.acquiredWaitKeys()) {
                    responses.put(key.commitIndex(), Boolean.TRUE);
                }
            }
            this.sessionStates.remove(sessionId);
        }
    }

    @Override
    protected Collection<Long> getActivelyAttachedSessions() {
        HashSet<Long> activeSessionIds = new HashSet<Long>();
        for (Map.Entry<Long, SessionSemaphoreState> e : this.sessionStates.entrySet()) {
            if (e.getValue().acquiredPermits <= 0) continue;
            activeSessionIds.add(e.getKey());
        }
        return activeSessionIds;
    }

    @Override
    protected void onWaitKeyExpire(AcquireInvocationKey key) {
        this.assignPermitsToInvocation(key.endpoint(), key.invocationUid(), 0);
    }

    @Override
    public int getFactoryId() {
        return RaftSemaphoreDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeBoolean(this.initialized);
        out.writeInt(this.available);
        out.writeInt(this.sessionStates.size());
        for (Map.Entry<Long, SessionSemaphoreState> e1 : this.sessionStates.entrySet()) {
            out.writeLong(e1.getKey());
            SessionSemaphoreState state = e1.getValue();
            out.writeInt(state.invocationRefUids.size());
            for (Map.Entry e2 : state.invocationRefUids.entrySet()) {
                out.writeLong(e2.getKey());
                Tuple2 t = (Tuple2)e2.getValue();
                UUIDSerializationUtil.writeUUID(out, (UUID)t.element1);
                out.writeInt((Integer)t.element2);
            }
            out.writeInt(state.acquiredPermits);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.initialized = in.readBoolean();
        this.available = in.readInt();
        int count = in.readInt();
        for (int i = 0; i < count; ++i) {
            long sessionId = in.readLong();
            SessionSemaphoreState state = new SessionSemaphoreState();
            int refUidCount = in.readInt();
            for (int j = 0; j < refUidCount; ++j) {
                long threadId = in.readLong();
                UUID invocationUid = UUIDSerializationUtil.readUUID(in);
                int permits = in.readInt();
                state.invocationRefUids.put(threadId, Tuple2.of(invocationUid, permits));
            }
            state.acquiredPermits = in.readInt();
            this.sessionStates.put(sessionId, state);
        }
    }

    public String toString() {
        return "RaftSemaphore{" + this.internalToString() + ", initialized=" + this.initialized + ", available=" + this.available + ", sessionStates=" + this.sessionStates + '}';
    }

    private static class SessionSemaphoreState {
        private final Long2ObjectHashMap<Tuple2<UUID, Integer>> invocationRefUids = new Long2ObjectHashMap();
        private int acquiredPermits;

        private SessionSemaphoreState() {
        }

        Integer getInvocationResponse(long threadId, UUID invocationUid) {
            Tuple2<UUID, Integer> t = this.invocationRefUids.get(threadId);
            return t != null && ((UUID)t.element1).equals(invocationUid) ? (Integer)t.element2 : null;
        }

        public String toString() {
            return "SessionState{invocationRefUids=" + this.invocationRefUids + ", acquiredPermits=" + this.acquiredPermits + '}';
        }
    }
}

