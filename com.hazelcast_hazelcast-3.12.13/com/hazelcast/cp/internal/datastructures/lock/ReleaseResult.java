/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.internal.datastructures.lock.LockInvocationKey;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import java.util.Collection;
import java.util.Collections;

class ReleaseResult {
    static final ReleaseResult FAILED = new ReleaseResult(false, RaftLockOwnershipState.NOT_LOCKED, Collections.emptyList());
    private final boolean success;
    private final RaftLockOwnershipState ownership;
    private final Collection<LockInvocationKey> completedWaitKeys;

    ReleaseResult(boolean success, RaftLockOwnershipState ownership, Collection<LockInvocationKey> completedWaitKeys) {
        this.success = success;
        this.ownership = ownership;
        this.completedWaitKeys = Collections.unmodifiableCollection(completedWaitKeys);
    }

    static ReleaseResult successful(RaftLockOwnershipState ownership) {
        return new ReleaseResult(true, ownership, Collections.emptyList());
    }

    static ReleaseResult successful(RaftLockOwnershipState ownership, Collection<LockInvocationKey> notifications) {
        return new ReleaseResult(true, ownership, notifications);
    }

    static ReleaseResult failed(Collection<LockInvocationKey> notifications) {
        return new ReleaseResult(false, RaftLockOwnershipState.NOT_LOCKED, notifications);
    }

    public boolean success() {
        return this.success;
    }

    public RaftLockOwnershipState ownership() {
        return this.ownership;
    }

    Collection<LockInvocationKey> completedWaitKeys() {
        return this.completedWaitKeys;
    }
}

