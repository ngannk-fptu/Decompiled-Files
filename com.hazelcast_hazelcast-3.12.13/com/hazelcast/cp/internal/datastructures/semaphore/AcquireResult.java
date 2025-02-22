/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore;

import com.hazelcast.cp.internal.datastructures.semaphore.AcquireInvocationKey;
import java.util.Collection;
import java.util.Collections;

public final class AcquireResult {
    private final AcquireStatus status;
    private final int permits;
    private final Collection<AcquireInvocationKey> cancelledWaitKeys;

    AcquireResult(AcquireStatus status, int permits, Collection<AcquireInvocationKey> cancelledWaitKeys) {
        this.status = status;
        this.permits = permits;
        this.cancelledWaitKeys = Collections.unmodifiableCollection(cancelledWaitKeys);
    }

    public AcquireStatus status() {
        return this.status;
    }

    public int permits() {
        return this.permits;
    }

    Collection<AcquireInvocationKey> cancelledWaitKeys() {
        return this.cancelledWaitKeys;
    }

    public static enum AcquireStatus {
        SUCCESSFUL,
        WAIT_KEY_ADDED,
        FAILED;

    }
}

