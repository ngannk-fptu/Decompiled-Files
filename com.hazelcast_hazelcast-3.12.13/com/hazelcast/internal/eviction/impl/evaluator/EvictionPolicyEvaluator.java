/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction.impl.evaluator;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.EvictionCandidate;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.internal.eviction.Expirable;
import com.hazelcast.util.Clock;

public class EvictionPolicyEvaluator<A, E extends Evictable> {
    private final EvictionPolicyComparator evictionPolicyComparator;

    public EvictionPolicyEvaluator(EvictionPolicyComparator evictionPolicyComparator) {
        this.evictionPolicyComparator = evictionPolicyComparator;
    }

    public EvictionPolicyComparator getEvictionPolicyComparator() {
        return this.evictionPolicyComparator;
    }

    public <C extends EvictionCandidate<A, E>> C evaluate(Iterable<C> evictionCandidates) {
        EvictionCandidate selectedEvictionCandidate = null;
        long now = Clock.currentTimeMillis();
        for (EvictionCandidate currentEvictionCandidate : evictionCandidates) {
            if (selectedEvictionCandidate == null) {
                selectedEvictionCandidate = currentEvictionCandidate;
                continue;
            }
            Object evictable = currentEvictionCandidate.getEvictable();
            if (this.isExpired(now, (Evictable)evictable)) {
                return (C)currentEvictionCandidate;
            }
            int comparisonResult = this.evictionPolicyComparator.compare(selectedEvictionCandidate, currentEvictionCandidate);
            if (comparisonResult != 1) continue;
            selectedEvictionCandidate = currentEvictionCandidate;
        }
        return (C)selectedEvictionCandidate;
    }

    private boolean isExpired(long now, Evictable evictable) {
        boolean expired = false;
        if (evictable instanceof Expirable) {
            Expirable expirable = (Expirable)((Object)evictable);
            expired = expirable.isExpiredAt(now);
        }
        return expired;
    }
}

