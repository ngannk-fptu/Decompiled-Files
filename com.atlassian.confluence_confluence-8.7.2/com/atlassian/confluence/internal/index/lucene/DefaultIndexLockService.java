/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.math.LongMath
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.internal.index.IndexLockService;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.google.common.math.LongMath;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@LuceneIndependent
public class DefaultIndexLockService
implements IndexLockService {
    private final Map<SearchIndex, ReentrantLock> lockByIndex = Stream.of(SearchIndex.values()).collect(Collectors.toMap(Function.identity(), index -> new ReentrantLock()));

    @Override
    public boolean tryLock(SearchIndex index, long duration, TimeUnit timeUnit) {
        return this.tryLock(EnumSet.of(index), duration, timeUnit);
    }

    @Override
    public boolean tryLock(EnumSet<SearchIndex> indices, long duration, TimeUnit timeUnit) {
        long singleIndexDuration = LongMath.divide((long)duration, (long)indices.size(), (RoundingMode)RoundingMode.UP);
        ArrayList<ReentrantLock> acquiredLocks = new ArrayList<ReentrantLock>();
        try {
            for (SearchIndex index : indices) {
                ReentrantLock lock = this.lockByIndex.get((Object)index);
                boolean gotLock = lock.tryLock(singleIndexDuration, timeUnit);
                if (!gotLock) {
                    acquiredLocks.forEach(ReentrantLock::unlock);
                    return false;
                }
                acquiredLocks.add(lock);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            acquiredLocks.forEach(ReentrantLock::unlock);
            return false;
        }
        return true;
    }

    @Override
    public void lock(SearchIndex index) {
        this.lock(EnumSet.of(index));
    }

    @Override
    public void lock(EnumSet<SearchIndex> indices) {
        for (SearchIndex index : indices) {
            this.lockByIndex.get((Object)index).lock();
        }
    }

    @Override
    public void unlock(SearchIndex index) {
        this.unlock(EnumSet.of(index));
    }

    @Override
    public void unlock(EnumSet<SearchIndex> indices) {
        boolean exceptionThrown = false;
        for (SearchIndex index : indices) {
            try {
                this.lockByIndex.get((Object)index).unlock();
            }
            catch (IllegalMonitorStateException e) {
                exceptionThrown = true;
            }
        }
        if (exceptionThrown) {
            throw new IllegalMonitorStateException("Can't release locks on " + indices + " because they are not held");
        }
    }
}

