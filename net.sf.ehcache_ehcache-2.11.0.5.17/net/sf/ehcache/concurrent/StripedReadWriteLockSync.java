/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.concurrent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.concurrent.ConcurrencyUtil;
import net.sf.ehcache.concurrent.ReadWriteLockSync;
import net.sf.ehcache.concurrent.StripedReadWriteLock;

public class StripedReadWriteLockSync
implements StripedReadWriteLock {
    public static final int DEFAULT_NUMBER_OF_MUTEXES = 2048;
    private final ReadWriteLockSync[] mutexes;
    private final List<ReadWriteLockSync> mutexesAsList;

    public StripedReadWriteLockSync() {
        this(2048);
    }

    public StripedReadWriteLockSync(int numberOfStripes) {
        if ((numberOfStripes & numberOfStripes - 1) != 0) {
            throw new CacheException("Cannot create a CacheLockProvider with a non power-of-two number of stripes");
        }
        if (numberOfStripes == 0) {
            throw new CacheException("A zero size CacheLockProvider does not have useful semantics.");
        }
        this.mutexes = new ReadWriteLockSync[numberOfStripes];
        for (int i = 0; i < this.mutexes.length; ++i) {
            this.mutexes[i] = new ReadWriteLockSync();
        }
        this.mutexesAsList = Collections.unmodifiableList(Arrays.asList(this.mutexes));
    }

    @Override
    public ReadWriteLockSync getSyncForKey(Object key) {
        int lockNumber = ConcurrencyUtil.selectLock(key, this.mutexes.length);
        return this.mutexes[lockNumber];
    }

    @Override
    public ReadWriteLock getLockForKey(Object key) {
        int lockNumber = ConcurrencyUtil.selectLock(key, this.mutexes.length);
        return this.mutexes[lockNumber].getReadWriteLock();
    }

    @Override
    public List<ReadWriteLockSync> getAllSyncs() {
        return this.mutexesAsList;
    }
}

