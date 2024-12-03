/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import java.util.Collection;
import java.util.Collections;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolEvictor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.SizeOfEngine;

public class UnboundedPool
implements Pool {
    public static final PoolAccessor<PoolParticipant> UNBOUNDED_ACCESSOR = new UnboundedPoolAccessor();

    @Override
    public long getSize() {
        return -1L;
    }

    @Override
    public long getMaxSize() {
        return -1L;
    }

    @Override
    public void setMaxSize(long newSize) {
    }

    @Override
    public PoolAccessor createPoolAccessor(PoolParticipant participant, int maxDepth, boolean abortWhenMaxDepthExceeded) {
        return new UnboundedPoolAccessor();
    }

    @Override
    public PoolAccessor createPoolAccessor(PoolParticipant participant, SizeOfEngine sizeOfEngine) {
        return new UnboundedPoolAccessor();
    }

    @Override
    public void registerPoolAccessor(PoolAccessor accessor) {
    }

    @Override
    public void removePoolAccessor(PoolAccessor accessor) {
    }

    @Override
    public Collection<PoolAccessor> getPoolAccessors() {
        return Collections.emptyList();
    }

    @Override
    public PoolEvictor getEvictor() {
        throw new UnsupportedOperationException();
    }

    private static final class UnboundedPoolAccessor
    implements PoolAccessor<PoolParticipant> {
        private UnboundedPoolAccessor() {
        }

        @Override
        public long add(Object key, Object value, Object container, boolean force) {
            return 0L;
        }

        @Override
        public boolean canAddWithoutEvicting(Object key, Object value, Object container) {
            return true;
        }

        @Override
        public long delete(long sizeOf) throws IllegalArgumentException {
            return 0L;
        }

        @Override
        public long replace(long currentSize, Object key, Object value, Object container, boolean force) {
            return 0L;
        }

        @Override
        public long getSize() {
            return -1L;
        }

        @Override
        public void unlink() {
        }

        @Override
        public void clear() {
        }

        @Override
        public PoolParticipant getParticipant() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxSize(long newValue) {
        }

        @Override
        public long getPoolOccupancy() {
            return -1L;
        }

        @Override
        public long getPoolSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean hasAbortedSizeOf() {
            return false;
        }
    }
}

