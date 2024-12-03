/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolEvictor;
import net.sf.ehcache.pool.PoolParticipant;

public class BalancedAccessEvictor
implements PoolEvictor<PoolParticipant> {
    private static final int SAMPLE_SIZE = 5;

    @Override
    public boolean freeSpace(Collection<PoolAccessor<PoolParticipant>> from, long bytes) {
        if (from == null || from.isEmpty()) {
            return false;
        }
        ArrayList<PoolAccessor<PoolParticipant>> random = new ArrayList<PoolAccessor<PoolParticipant>>(from);
        Collections.shuffle(random);
        for (int i = 0; i < random.size(); i += 5) {
            List<PoolAccessor> sorted = random.subList(i, Math.min(5 + i, random.size()));
            Collections.sort(sorted, new EvictionCostComparator(this.getDesiredUnloadedSize(sorted), sorted.size() + 1));
            for (PoolAccessor accessor : sorted) {
                long byteSize = accessor.getSize();
                long countSize = accessor.getParticipant().getApproximateCountSize();
                int count = countSize == 0L || byteSize == 0L ? 1 : (int)Math.max(bytes * countSize / byteSize, 1L);
                if (!accessor.getParticipant().evict(count, bytes)) continue;
                return true;
            }
        }
        return false;
    }

    private float evictionCost(PoolAccessor accessor, long unloadedSize) {
        float missRate;
        float hitRate = accessor.getParticipant().getApproximateHitRate();
        float accessRate = hitRate + (missRate = accessor.getParticipant().getApproximateMissRate());
        if (accessRate == 0.0f) {
            if (accessor.getSize() > unloadedSize) {
                return 0.0f;
            }
            return Float.MIN_NORMAL;
        }
        long countSize = accessor.getParticipant().getApproximateCountSize();
        float cost = accessRate / (float)countSize;
        if (Float.isNaN(cost)) {
            throw new AssertionError((Object)String.format("NaN Eviction Cost [hit:%f miss:%f size:%d]", Float.valueOf(hitRate), Float.valueOf(missRate), countSize));
        }
        return cost;
    }

    private long getDesiredUnloadedSize(Collection<PoolAccessor> from) {
        long unloadedSize = 0L;
        for (PoolAccessor accessor : from) {
            unloadedSize += accessor.getSize();
        }
        return unloadedSize / (long)from.size();
    }

    private final class EvictionCostComparator
    implements Comparator<PoolAccessor> {
        private final long unloadedSize;
        private final Map<PoolAccessor, Float> evictionCostCache;

        public EvictionCostComparator(long unloadedSize, int collectionSize) {
            this.unloadedSize = unloadedSize;
            this.evictionCostCache = new IdentityHashMap<PoolAccessor, Float>(collectionSize);
        }

        @Override
        public int compare(PoolAccessor s1, PoolAccessor s2) {
            Float f2;
            Float f1 = this.evictionCostCache.get(s1);
            if (f1 == null) {
                f1 = Float.valueOf(BalancedAccessEvictor.this.evictionCost(s1, this.unloadedSize));
                this.evictionCostCache.put(s1, f1);
            }
            if ((f2 = this.evictionCostCache.get(s2)) == null) {
                f2 = Float.valueOf(BalancedAccessEvictor.this.evictionCost(s2, this.unloadedSize));
                this.evictionCostCache.put(s2, f2);
            }
            return Float.compare(f1.floatValue(), f2.floatValue());
        }
    }
}

