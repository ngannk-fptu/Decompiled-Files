/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store.disk.ods;

import java.io.IOException;
import java.io.RandomAccessFile;
import net.sf.ehcache.store.disk.ods.Region;
import net.sf.ehcache.store.disk.ods.RegionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileAllocationTree
extends RegionSet {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileAllocationTree.class);
    private long fileSize;
    private final RandomAccessFile data;

    public FileAllocationTree(long maxSize, RandomAccessFile file) {
        super(maxSize);
        this.data = file;
    }

    public synchronized Region alloc(long size) {
        Region r = this.find(size);
        this.mark(r);
        return r;
    }

    public synchronized void mark(Region r) {
        Region current = this.removeAndReturn(r.start());
        if (current == null) {
            throw new IllegalArgumentException();
        }
        Region newRange = current.remove(r);
        if (newRange != null) {
            this.add(current);
            this.add(newRange);
        } else if (!current.isNull()) {
            this.add(current);
        }
        this.checkGrow(r);
    }

    public synchronized void free(Region r) {
        Region prev = this.removeAndReturn(r.start() - 1L);
        if (prev != null) {
            prev.merge(r);
            Region next = this.removeAndReturn(r.end() + 1L);
            if (next != null) {
                prev.merge(next);
            }
            this.add(prev);
            this.checkShrink(prev);
            return;
        }
        Region next = this.removeAndReturn(r.end() + 1L);
        if (next != null) {
            next.merge(r);
            this.add(next);
            this.checkShrink(next);
            return;
        }
        this.add(r);
        this.checkShrink(r);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

    private void checkGrow(Region alloc) {
        if (alloc.end() >= this.fileSize) {
            this.fileSize = alloc.end() + 1L;
            this.grow(this.fileSize);
        }
    }

    private void checkShrink(Region free) {
        if (free.end() >= this.fileSize - 1L) {
            this.fileSize = free.start();
            this.shrink(this.fileSize);
        }
    }

    private void grow(long size) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void shrink(long size) {
        if (this.data == null) {
            return;
        }
        RandomAccessFile randomAccessFile = this.data;
        synchronized (randomAccessFile) {
            try {
                this.data.setLength(size);
            }
            catch (IOException e) {
                LOGGER.info("Exception while trying to shrink file", (Throwable)e);
            }
        }
    }

    public synchronized long getFileSize() {
        return this.fileSize;
    }
}

