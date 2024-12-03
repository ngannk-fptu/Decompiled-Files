/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.OperationsFilter;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public class NonStopWriteBehind
implements WriteBehind {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private WriteBehind delegate;
    private boolean isStarted = false;
    private boolean isStopped = false;
    private CacheWriter writer;
    private OperationsFilter filter;

    public void init(WriteBehind writeBehind) {
        this.lock.writeLock().lock();
        try {
            this.delegate = writeBehind;
            if (this.isStarted) {
                this.delegate.start(this.writer);
            }
            if (this.filter != null) {
                this.delegate.setOperationsFilter(this.filter);
            }
            if (this.isStopped) {
                this.delegate.stop();
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void start(CacheWriter writerParam) throws CacheException {
        this.lock.writeLock().lock();
        try {
            if (this.delegate == null) {
                this.isStarted = true;
                this.writer = writerParam;
                return;
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        this.delegate.start(writerParam);
    }

    @Override
    public void write(Element element) {
        this.lock.readLock().lock();
        try {
            if (this.delegate == null) {
                throw new IllegalStateException();
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        this.delegate.write(element);
    }

    @Override
    public void delete(CacheEntry entry) {
        this.lock.readLock().lock();
        try {
            if (this.delegate == null) {
                throw new IllegalStateException();
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        this.delegate.delete(entry);
    }

    @Override
    public void setOperationsFilter(OperationsFilter filter) {
        this.lock.writeLock().lock();
        try {
            if (this.delegate == null) {
                this.filter = filter;
                return;
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        this.delegate.setOperationsFilter(filter);
    }

    @Override
    public void stop() throws CacheException {
        this.lock.writeLock().lock();
        try {
            if (this.delegate == null) {
                this.isStopped = true;
                return;
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        this.delegate.stop();
    }

    @Override
    public long getQueueSize() {
        this.lock.readLock().lock();
        try {
            if (this.delegate == null) {
                long l = 0L;
                return l;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        return this.delegate.getQueueSize();
    }
}

