/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.OperationsFilter;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import net.sf.ehcache.writer.writebehind.WriteBehindQueue;

public class WriteBehindQueueManager
implements WriteBehind {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = this.rwLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = this.rwLock.writeLock();
    private final List<WriteBehind> queues = new ArrayList<WriteBehind>();

    protected WriteBehindQueueManager(CacheConfiguration config, WriteBehindQueueFactory queueFactory) {
        CacheWriterConfiguration cacheWriterConfiguration = config.getCacheWriterConfiguration();
        int writeBehindConcurrency = cacheWriterConfiguration.getWriteBehindConcurrency();
        for (int i = 0; i < writeBehindConcurrency; ++i) {
            this.queues.add(queueFactory.createQueue(i, config));
        }
    }

    public WriteBehindQueueManager(CacheConfiguration config) {
        this(config, new WriteBehindQueueFactory());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void start(CacheWriter writer) throws CacheException {
        this.writeLock.lock();
        try {
            for (WriteBehind queue : this.queues) {
                queue.start(writer);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void write(Element element) {
        this.readLock.lock();
        try {
            this.getQueue(element.getKey()).write(element);
        }
        finally {
            this.readLock.unlock();
        }
    }

    private WriteBehind getQueue(Object key) {
        return this.queues.get(Math.abs(key.hashCode() % this.queues.size()));
    }

    @Override
    public void delete(CacheEntry entry) {
        this.readLock.lock();
        try {
            this.getQueue(entry.getKey()).delete(entry);
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOperationsFilter(OperationsFilter filter) {
        this.readLock.lock();
        try {
            for (WriteBehind queue : this.queues) {
                queue.setOperationsFilter(filter);
            }
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void stop() throws CacheException {
        this.writeLock.lock();
        try {
            for (WriteBehind queue : this.queues) {
                queue.stop();
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getQueueSize() {
        int size = 0;
        this.readLock.lock();
        try {
            for (WriteBehind queue : this.queues) {
                size = (int)((long)size + queue.getQueueSize());
            }
        }
        finally {
            this.readLock.unlock();
        }
        return size;
    }

    protected static class WriteBehindQueueFactory {
        protected WriteBehindQueueFactory() {
        }

        protected WriteBehind createQueue(int index, CacheConfiguration config) {
            return new WriteBehindQueue(config);
        }
    }
}

