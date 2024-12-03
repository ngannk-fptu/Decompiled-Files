/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import java.util.ArrayList;
import java.util.Collection;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolEvictor;
import net.sf.ehcache.pool.PoolParticipant;

public class FromLargestCachePoolEvictor
implements PoolEvictor<PoolParticipant> {
    @Override
    public boolean freeSpace(Collection<PoolAccessor<PoolParticipant>> from, long bytes) {
        if (from == null || from.isEmpty()) {
            return false;
        }
        long remainingSizeInBytes = bytes;
        ArrayList<PoolAccessor<PoolParticipant>> tried = new ArrayList<PoolAccessor<PoolParticipant>>();
        while (tried.size() != from.size()) {
            PoolAccessor<PoolParticipant> largestPoolAccessor = this.findUntriedLargestPoolableStore(from, tried);
            long beforeEvictionSize = largestPoolAccessor.getSize();
            if (!largestPoolAccessor.getParticipant().evict(1, bytes)) {
                tried.add(largestPoolAccessor);
                continue;
            }
            long afterEvictionSize = largestPoolAccessor.getSize();
            if ((remainingSizeInBytes -= beforeEvictionSize - afterEvictionSize) > 0L) continue;
            return true;
        }
        return false;
    }

    private PoolAccessor<PoolParticipant> findUntriedLargestPoolableStore(Collection<PoolAccessor<PoolParticipant>> from, Collection<PoolAccessor<PoolParticipant>> tried) {
        PoolAccessor<PoolParticipant> largestPoolAccessor = null;
        for (PoolAccessor<PoolParticipant> accessor : from) {
            if (this.alreadyTried(tried, accessor) || largestPoolAccessor != null && accessor.getSize() <= largestPoolAccessor.getSize()) continue;
            largestPoolAccessor = accessor;
        }
        return largestPoolAccessor;
    }

    private boolean alreadyTried(Collection<PoolAccessor<PoolParticipant>> tried, PoolAccessor<PoolParticipant> from) {
        for (PoolAccessor<PoolParticipant> accessor : tried) {
            if (accessor != from) continue;
            return true;
        }
        return false;
    }
}

