/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore;

import com.hazelcast.cp.internal.datastructures.semaphore.AcquireInvocationKey;
import java.util.Collection;
import java.util.Collections;

final class ReleaseResult {
    private final boolean success;
    private final Collection<AcquireInvocationKey> acquiredWaitKeys;
    private final Collection<AcquireInvocationKey> cancelledWaitKeys;

    private ReleaseResult(boolean success, Collection<AcquireInvocationKey> acquiredWaitKeys, Collection<AcquireInvocationKey> cancelledWaitKeys) {
        this.success = success;
        this.acquiredWaitKeys = Collections.unmodifiableCollection(acquiredWaitKeys);
        this.cancelledWaitKeys = Collections.unmodifiableCollection(cancelledWaitKeys);
    }

    static ReleaseResult successful(Collection<AcquireInvocationKey> acquiredWaitKeys, Collection<AcquireInvocationKey> cancelledWaitKeys) {
        return new ReleaseResult(true, acquiredWaitKeys, cancelledWaitKeys);
    }

    static ReleaseResult failed(Collection<AcquireInvocationKey> cancelledWaitKeys) {
        return new ReleaseResult(false, Collections.emptyList(), cancelledWaitKeys);
    }

    public boolean success() {
        return this.success;
    }

    public Collection<AcquireInvocationKey> acquiredWaitKeys() {
        return this.acquiredWaitKeys;
    }

    public Collection<AcquireInvocationKey> cancelledWaitKeys() {
        return this.cancelledWaitKeys;
    }
}

