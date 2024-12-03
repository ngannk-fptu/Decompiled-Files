/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.AtomicBiInteger;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class Pool<T>
implements AutoCloseable,
Dumpable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pool.class);
    private final List<Entry> entries = new CopyOnWriteArrayList<Entry>();
    private final int maxEntries;
    private final StrategyType strategyType;
    private final AutoLock lock = new AutoLock();
    private final ThreadLocal<Entry> cache;
    private final AtomicInteger nextIndex;
    private volatile boolean closed;
    @Deprecated
    private volatile int maxUsage = -1;
    @Deprecated
    private volatile int maxMultiplex = -1;

    public Pool(StrategyType strategyType, int maxEntries) {
        this(strategyType, maxEntries, false);
    }

    public Pool(StrategyType strategyType, int maxEntries, boolean cache) {
        this.maxEntries = maxEntries;
        this.strategyType = Objects.requireNonNull(strategyType);
        this.cache = cache ? new ThreadLocal() : null;
        this.nextIndex = strategyType == StrategyType.ROUND_ROBIN ? new AtomicInteger() : null;
    }

    @ManagedAttribute(value="The number of reserved entries")
    public int getReservedCount() {
        return (int)this.entries.stream().filter(Entry::isReserved).count();
    }

    @ManagedAttribute(value="The number of idle entries")
    public int getIdleCount() {
        return (int)this.entries.stream().filter(Entry::isIdle).count();
    }

    @ManagedAttribute(value="The number of in-use entries")
    public int getInUseCount() {
        return (int)this.entries.stream().filter(Entry::isInUse).count();
    }

    @ManagedAttribute(value="The number of closed entries")
    public int getClosedCount() {
        return (int)this.entries.stream().filter(Entry::isClosed).count();
    }

    @ManagedAttribute(value="The maximum number of entries")
    public int getMaxEntries() {
        return this.maxEntries;
    }

    @ManagedAttribute(value="The default maximum multiplex count of entries")
    @Deprecated
    public int getMaxMultiplex() {
        return this.maxMultiplex == -1 ? 1 : this.maxMultiplex;
    }

    @Deprecated
    protected int getMaxMultiplex(T pooled) {
        return this.getMaxMultiplex();
    }

    @Deprecated
    public final void setMaxMultiplex(int maxMultiplex) {
        if (maxMultiplex < 1) {
            throw new IllegalArgumentException("Max multiplex must be >= 1");
        }
        try (AutoLock l = this.lock.lock();){
            if (this.closed) {
                return;
            }
            if (this.entries.stream().anyMatch(MonoEntry.class::isInstance)) {
                throw new IllegalStateException("Pool entries do not support multiplexing");
            }
            this.maxMultiplex = maxMultiplex;
        }
    }

    @ManagedAttribute(value="The default maximum usage count of entries")
    @Deprecated
    public int getMaxUsageCount() {
        return this.maxUsage;
    }

    @Deprecated
    protected int getMaxUsageCount(T pooled) {
        return this.getMaxUsageCount();
    }

    @Deprecated
    public final void setMaxUsageCount(int maxUsageCount) {
        List<Closeable> copy;
        if (maxUsageCount == 0) {
            throw new IllegalArgumentException("Max usage count must be != 0");
        }
        try (AutoLock l = this.lock.lock();){
            if (this.closed) {
                return;
            }
            if (this.entries.stream().anyMatch(MonoEntry.class::isInstance)) {
                throw new IllegalStateException("Pool entries do not support max usage");
            }
            this.maxUsage = maxUsageCount;
            copy = this.entries.stream().filter(entry -> entry.isIdleAndOverUsed() && this.remove((Entry)entry) && entry.pooled instanceof Closeable).map(entry -> (Closeable)entry.pooled).collect(Collectors.toList());
        }
        copy.forEach(IO::close);
    }

    @Deprecated
    public Entry reserve(int allotment) {
        try (AutoLock l = this.lock.lock();){
            if (this.closed) {
                Entry entry = null;
                return entry;
            }
            int space = this.maxEntries - this.entries.size();
            if (space <= 0) {
                Entry entry = null;
                return entry;
            }
            if (allotment >= 0 && this.getReservedCount() * this.getMaxMultiplex() >= allotment) {
                Entry entry = null;
                return entry;
            }
            Entry entry = this.newEntry();
            this.entries.add(entry);
            Entry entry2 = entry;
            return entry2;
        }
    }

    public Entry reserve() {
        try (AutoLock l = this.lock.lock();){
            if (this.closed) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("{} is closed, returning null reserved entry", (Object)this);
                }
                Entry entry = null;
                return entry;
            }
            int entriesSize = this.entries.size();
            if (this.maxEntries > 0 && entriesSize >= this.maxEntries) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("{} has no space: {} >= {}, returning null reserved entry", new Object[]{this, entriesSize, this.maxEntries});
                }
                Entry entry = null;
                return entry;
            }
            Entry entry = this.newEntry();
            this.entries.add(entry);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} returning new reserved entry {}", (Object)this, (Object)entry);
            }
            Entry entry2 = entry;
            return entry2;
        }
    }

    private Entry newEntry() {
        if (this.maxMultiplex >= 0 || this.maxUsage >= 0) {
            return new MultiEntry();
        }
        return new MonoEntry();
    }

    public Entry acquire() {
        Entry entry;
        if (this.closed) {
            return null;
        }
        int size = this.entries.size();
        if (size == 0) {
            return null;
        }
        if (this.cache != null && (entry = this.cache.get()) != null && entry.tryAcquire()) {
            return entry;
        }
        int index = this.startIndex(size);
        int tries = size;
        while (tries-- > 0) {
            try {
                Entry entry2 = this.entries.get(index);
                if (entry2 != null && entry2.tryAcquire()) {
                    return entry2;
                }
            }
            catch (IndexOutOfBoundsException e) {
                LOGGER.trace("IGNORED", (Throwable)e);
                size = this.entries.size();
                if (size == 0) break;
            }
            index = (index + 1) % size;
        }
        return null;
    }

    private int startIndex(int size) {
        switch (this.strategyType) {
            case FIRST: {
                return 0;
            }
            case RANDOM: {
                return ThreadLocalRandom.current().nextInt(size);
            }
            case ROUND_ROBIN: {
                return this.nextIndex.getAndUpdate(c -> Math.max(0, c + 1)) % size;
            }
            case THREAD_ID: {
                return (int)(Thread.currentThread().getId() % (long)size);
            }
        }
        throw new IllegalArgumentException("Unknown strategy type: " + this.strategyType);
    }

    public Entry acquire(Function<Entry, T> creator) {
        T value;
        Entry entry = this.acquire();
        if (entry != null) {
            return entry;
        }
        entry = this.reserve();
        if (entry == null) {
            return null;
        }
        try {
            value = creator.apply(entry);
        }
        catch (Throwable th) {
            this.remove(entry);
            throw th;
        }
        if (value == null) {
            this.remove(entry);
            return null;
        }
        return entry.enable(value, true) ? entry : null;
    }

    public boolean release(Entry entry) {
        if (this.closed) {
            return false;
        }
        boolean released = entry.tryRelease();
        if (released && this.cache != null) {
            this.cache.set(entry);
        }
        return released;
    }

    public boolean remove(Entry entry) {
        if (this.closed) {
            return false;
        }
        if (!entry.tryRemove()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Attempt to remove an object from the pool that is still in use: {}", (Object)entry);
            }
            return false;
        }
        boolean removed = this.entries.remove(entry);
        if (!removed && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Attempt to remove an object from the pool that does not exist: {}", (Object)entry);
        }
        return removed;
    }

    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        ArrayList<Entry> copy;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Closing {}", (Object)this);
        }
        try (AutoLock l = this.lock.lock();){
            this.closed = true;
            copy = new ArrayList<Entry>(this.entries);
            this.entries.clear();
        }
        for (Entry entry : copy) {
            boolean removed = entry.tryRemove();
            if (removed) {
                if (!(entry.pooled instanceof Closeable)) continue;
                IO.close((Closeable)entry.pooled);
                continue;
            }
            if (!LOGGER.isDebugEnabled()) continue;
            LOGGER.debug("Pooled object still in use: {}", (Object)entry);
        }
    }

    public int size() {
        return this.entries.size();
    }

    public Collection<Entry> values() {
        return Collections.unmodifiableCollection(this.entries);
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        Dumpable.dumpObjects(out, indent, this, new DumpableCollection("entries", this.entries));
    }

    public String toString() {
        return String.format("%s@%x[inUse=%d,size=%d,max=%d,closed=%b]", this.getClass().getSimpleName(), this.hashCode(), this.getInUseCount(), this.size(), this.getMaxEntries(), this.isClosed());
    }

    public static enum StrategyType {
        FIRST,
        RANDOM,
        THREAD_ID,
        ROUND_ROBIN;

    }

    private class MonoEntry
    extends Entry {
        private final AtomicInteger state;

        private MonoEntry() {
            this.state = new AtomicInteger(Integer.MIN_VALUE);
        }

        @Override
        protected boolean tryEnable(boolean acquire) {
            return this.state.compareAndSet(Integer.MIN_VALUE, acquire ? 1 : 0);
        }

        @Override
        boolean tryAcquire() {
            int s;
            do {
                if ((s = this.state.get()) == 0) continue;
                return false;
            } while (!this.state.compareAndSet(s, 1));
            return true;
        }

        @Override
        boolean tryRelease() {
            int s;
            do {
                if ((s = this.state.get()) < 0) {
                    return false;
                }
                if (s != 0) continue;
                throw new IllegalStateException("Cannot release an already released entry");
            } while (!this.state.compareAndSet(s, 0));
            return true;
        }

        @Override
        boolean tryRemove() {
            this.state.set(-1);
            return true;
        }

        @Override
        public boolean isClosed() {
            return this.state.get() < 0;
        }

        @Override
        public boolean isReserved() {
            return this.state.get() == Integer.MIN_VALUE;
        }

        @Override
        public boolean isIdle() {
            return this.state.get() == 0;
        }

        @Override
        public boolean isInUse() {
            return this.state.get() == 1;
        }

        public String toString() {
            String s;
            switch (this.state.get()) {
                case -2147483648: {
                    s = "PENDING";
                    break;
                }
                case -1: {
                    s = "CLOSED";
                    break;
                }
                case 0: {
                    s = "IDLE";
                    break;
                }
                default: {
                    s = "ACTIVE";
                }
            }
            return String.format("%s@%x{%s,pooled=%s}", this.getClass().getSimpleName(), this.hashCode(), s, this.getPooled());
        }
    }

    public abstract class Entry {
        private T pooled;

        public boolean enable(T pooled, boolean acquire) {
            Objects.requireNonNull(pooled);
            if (!this.isReserved()) {
                if (this.isClosed()) {
                    return false;
                }
                throw new IllegalStateException("Entry already enabled: " + this);
            }
            this.pooled = pooled;
            if (this.tryEnable(acquire)) {
                return true;
            }
            this.pooled = null;
            if (this.isClosed()) {
                return false;
            }
            throw new IllegalStateException("Entry already enabled: " + this);
        }

        public T getPooled() {
            return this.pooled;
        }

        public boolean release() {
            return Pool.this.release(this);
        }

        public boolean remove() {
            return Pool.this.remove(this);
        }

        abstract boolean tryEnable(boolean var1);

        abstract boolean tryAcquire();

        abstract boolean tryRelease();

        abstract boolean tryRemove();

        public abstract boolean isClosed();

        public abstract boolean isReserved();

        public abstract boolean isIdle();

        public abstract boolean isInUse();

        @Deprecated
        public boolean isOverUsed() {
            return false;
        }

        boolean isIdleAndOverUsed() {
            return false;
        }

        int getUsageCount() {
            return 0;
        }

        void setUsageCount(int usageCount) {
        }
    }

    class MultiEntry
    extends Entry {
        private final AtomicBiInteger state = new AtomicBiInteger(Integer.MIN_VALUE, 0);

        MultiEntry() {
        }

        @Override
        void setUsageCount(int usageCount) {
            this.state.getAndSetHi(usageCount);
        }

        @Override
        protected boolean tryEnable(boolean acquire) {
            int usage = acquire ? 1 : 0;
            return this.state.compareAndSet(Integer.MIN_VALUE, usage, 0, usage);
        }

        @Override
        boolean tryAcquire() {
            int multiplexCount;
            int usageCount;
            int newUsageCount;
            long encoded;
            do {
                boolean closed;
                encoded = this.state.get();
                usageCount = AtomicBiInteger.getHi(encoded);
                multiplexCount = AtomicBiInteger.getLo(encoded);
                boolean bl = closed = usageCount < 0;
                if (closed) {
                    return false;
                }
                Object pooled = this.getPooled();
                int maxUsageCount = Pool.this.getMaxUsageCount(pooled);
                if (maxUsageCount > 0 && usageCount >= maxUsageCount) {
                    return false;
                }
                int maxMultiplexed = Pool.this.getMaxMultiplex(pooled);
                if (maxMultiplexed <= 0 || multiplexCount < maxMultiplexed) continue;
                return false;
            } while (!this.state.compareAndSet(encoded, newUsageCount = usageCount == Integer.MAX_VALUE ? Integer.MAX_VALUE : usageCount + 1, multiplexCount + 1));
            return true;
        }

        @Override
        boolean tryRelease() {
            int newMultiplexCount;
            int usageCount;
            long encoded;
            do {
                boolean closed;
                boolean bl = closed = (usageCount = AtomicBiInteger.getHi(encoded = this.state.get())) < 0;
                if (closed) {
                    return false;
                }
                newMultiplexCount = AtomicBiInteger.getLo(encoded) - 1;
                if (newMultiplexCount >= 0) continue;
                throw new IllegalStateException("Cannot release an already released entry");
            } while (!this.state.compareAndSet(encoded, usageCount, newMultiplexCount));
            int currentMaxUsageCount = Pool.this.getMaxUsageCount(this.getPooled());
            boolean overUsed = currentMaxUsageCount > 0 && usageCount >= currentMaxUsageCount;
            return !overUsed || newMultiplexCount != 0;
        }

        @Override
        boolean tryRemove() {
            int newMultiplexCount;
            int multiplexCount;
            long encoded;
            int usageCount;
            boolean removed;
            while (!(removed = this.state.compareAndSet(usageCount = AtomicBiInteger.getHi(encoded = this.state.get()), -1, multiplexCount = AtomicBiInteger.getLo(encoded), newMultiplexCount = Math.max(multiplexCount - 1, 0)))) {
            }
            return newMultiplexCount == 0;
        }

        @Override
        public boolean isClosed() {
            return this.state.getHi() < 0;
        }

        @Override
        public boolean isReserved() {
            return this.state.getHi() == Integer.MIN_VALUE;
        }

        @Override
        public boolean isIdle() {
            long encoded = this.state.get();
            return AtomicBiInteger.getHi(encoded) >= 0 && AtomicBiInteger.getLo(encoded) == 0;
        }

        @Override
        public boolean isInUse() {
            long encoded = this.state.get();
            return AtomicBiInteger.getHi(encoded) >= 0 && AtomicBiInteger.getLo(encoded) > 0;
        }

        @Override
        public boolean isOverUsed() {
            int maxUsageCount = Pool.this.getMaxUsageCount();
            int usageCount = this.state.getHi();
            return maxUsageCount > 0 && usageCount >= maxUsageCount;
        }

        @Override
        boolean isIdleAndOverUsed() {
            int maxUsageCount = Pool.this.getMaxUsageCount();
            long encoded = this.state.get();
            int usageCount = AtomicBiInteger.getHi(encoded);
            int multiplexCount = AtomicBiInteger.getLo(encoded);
            return maxUsageCount > 0 && usageCount >= maxUsageCount && multiplexCount == 0;
        }

        @Override
        int getUsageCount() {
            return Math.max(this.state.getHi(), 0);
        }

        public String toString() {
            long encoded = this.state.get();
            int usageCount = AtomicBiInteger.getHi(encoded);
            int multiplexCount = AtomicBiInteger.getLo(encoded);
            String state = usageCount < 0 ? (usageCount == Integer.MIN_VALUE ? "PENDING" : "CLOSED") : (multiplexCount == 0 ? "IDLE" : "ACTIVE");
            return String.format("%s@%x{%s,usage=%d,multiplex=%d,pooled=%s}", this.getClass().getSimpleName(), this.hashCode(), state, Math.max(usageCount, 0), Math.max(multiplexCount, 0), this.getPooled());
        }
    }
}

