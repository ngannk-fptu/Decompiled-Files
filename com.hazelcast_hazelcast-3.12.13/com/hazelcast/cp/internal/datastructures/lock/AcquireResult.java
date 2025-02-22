/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock;

import com.hazelcast.cp.internal.datastructures.lock.LockInvocationKey;
import java.util.Collection;
import java.util.Collections;

public class AcquireResult {
    private final AcquireStatus status;
    private final long fence;
    private final Collection<LockInvocationKey> cancelledWaitKeys;

    AcquireResult(AcquireStatus status, long fence, Collection<LockInvocationKey> cancelledWaitKeys) {
        this.status = status;
        this.fence = fence;
        this.cancelledWaitKeys = Collections.unmodifiableCollection(cancelledWaitKeys);
    }

    static AcquireResult acquired(long fence) {
        return new AcquireResult(AcquireStatus.SUCCESSFUL, fence, Collections.emptyList());
    }

    static AcquireResult failed(Collection<LockInvocationKey> cancelled) {
        return new AcquireResult(AcquireStatus.FAILED, 0L, cancelled);
    }

    static AcquireResult waitKeyAdded(Collection<LockInvocationKey> cancelled) {
        return new AcquireResult(AcquireStatus.WAIT_KEY_ADDED, 0L, cancelled);
    }

    public AcquireStatus status() {
        return this.status;
    }

    public long fence() {
        return this.fence;
    }

    Collection<LockInvocationKey> cancelledWaitKeys() {
        return this.cancelledWaitKeys;
    }

    public static enum AcquireStatus {
        SUCCESSFUL,
        WAIT_KEY_ADDED,
        FAILED;

    }
}

