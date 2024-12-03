/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.index.ConcurrentMergeScheduler;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.MergeScheduler;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.store.NoSuchDirectoryException;
import com.atlassian.lucene36.store.RAMDirectory;
import com.atlassian.lucene36.util.IOUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NRTCachingDirectory
extends Directory {
    private final RAMDirectory cache = new RAMDirectory();
    private final Directory delegate;
    private final long maxMergeSizeBytes;
    private final long maxCachedBytes;
    private static final boolean VERBOSE = false;
    private final ConcurrentHashMap<Thread, MergePolicy.OneMerge> merges = new ConcurrentHashMap();
    private final Object uncacheLock = new Object();

    public NRTCachingDirectory(Directory delegate, double maxMergeSizeMB, double maxCachedMB) {
        this.delegate = delegate;
        this.maxMergeSizeBytes = (long)(maxMergeSizeMB * 1024.0 * 1024.0);
        this.maxCachedBytes = (long)(maxCachedMB * 1024.0 * 1024.0);
    }

    @Override
    public LockFactory getLockFactory() {
        return this.delegate.getLockFactory();
    }

    @Override
    public void setLockFactory(LockFactory lf) throws IOException {
        this.delegate.setLockFactory(lf);
    }

    @Override
    public String getLockID() {
        return this.delegate.getLockID();
    }

    @Override
    public Lock makeLock(String name) {
        return this.delegate.makeLock(name);
    }

    @Override
    public void clearLock(String name) throws IOException {
        this.delegate.clearLock(name);
    }

    @Override
    public String toString() {
        return "NRTCachingDirectory(" + this.delegate + "; maxCacheMB=" + (double)(this.maxCachedBytes / 1024L) / 1024.0 + " maxMergeSizeMB=" + (double)(this.maxMergeSizeBytes / 1024L) / 1024.0 + ")";
    }

    @Override
    public synchronized String[] listAll() throws IOException {
        HashSet<String> files;
        block4: {
            files = new HashSet<String>();
            for (String f : this.cache.listAll()) {
                files.add(f);
            }
            try {
                for (String f : this.delegate.listAll()) {
                    files.add(f);
                }
            }
            catch (NoSuchDirectoryException ex) {
                if (!files.isEmpty()) break block4;
                throw ex;
            }
        }
        return files.toArray(new String[files.size()]);
    }

    public long sizeInBytes() {
        return this.cache.sizeInBytes();
    }

    @Override
    public synchronized boolean fileExists(String name) throws IOException {
        return this.cache.fileExists(name) || this.delegate.fileExists(name);
    }

    @Override
    public synchronized long fileModified(String name) throws IOException {
        if (this.cache.fileExists(name)) {
            return this.cache.fileModified(name);
        }
        return this.delegate.fileModified(name);
    }

    @Override
    @Deprecated
    public synchronized void touchFile(String name) throws IOException {
        if (this.cache.fileExists(name)) {
            this.cache.touchFile(name);
        } else {
            this.delegate.touchFile(name);
        }
    }

    @Override
    public synchronized void deleteFile(String name) throws IOException {
        if (this.cache.fileExists(name)) {
            assert (!this.delegate.fileExists(name)) : "name=" + name;
            this.cache.deleteFile(name);
        } else {
            this.delegate.deleteFile(name);
        }
    }

    @Override
    public synchronized long fileLength(String name) throws IOException {
        if (this.cache.fileExists(name)) {
            return this.cache.fileLength(name);
        }
        return this.delegate.fileLength(name);
    }

    public String[] listCachedFiles() {
        return this.cache.listAll();
    }

    @Override
    public IndexOutput createOutput(String name) throws IOException {
        if (this.doCacheWrite(name)) {
            try {
                this.delegate.deleteFile(name);
            }
            catch (IOException ioe) {
                // empty catch block
            }
            return this.cache.createOutput(name);
        }
        try {
            this.cache.deleteFile(name);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return this.delegate.createOutput(name);
    }

    @Override
    public void sync(Collection<String> fileNames) throws IOException {
        for (String fileName : fileNames) {
            this.unCache(fileName);
        }
        this.delegate.sync(fileNames);
    }

    @Override
    public synchronized IndexInput openInput(String name) throws IOException {
        if (this.cache.fileExists(name)) {
            return this.cache.openInput(name);
        }
        return this.delegate.openInput(name);
    }

    @Override
    public synchronized IndexInput openInput(String name, int bufferSize) throws IOException {
        if (this.cache.fileExists(name)) {
            return this.cache.openInput(name, bufferSize);
        }
        return this.delegate.openInput(name, bufferSize);
    }

    @Override
    public void close() throws IOException {
        for (String fileName : this.cache.listAll()) {
            this.unCache(fileName);
        }
        this.cache.close();
        this.delegate.close();
    }

    public MergeScheduler getMergeScheduler() {
        return new ConcurrentMergeScheduler(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            protected void doMerge(MergePolicy.OneMerge merge) throws IOException {
                try {
                    NRTCachingDirectory.this.merges.put(Thread.currentThread(), merge);
                    super.doMerge(merge);
                    Object var3_2 = null;
                    NRTCachingDirectory.this.merges.remove(Thread.currentThread());
                }
                catch (Throwable throwable) {
                    Object var3_3 = null;
                    NRTCachingDirectory.this.merges.remove(Thread.currentThread());
                    throw throwable;
                }
            }
        };
    }

    protected boolean doCacheWrite(String name) {
        MergePolicy.OneMerge merge = this.merges.get(Thread.currentThread());
        return !name.equals("segments.gen") && (merge == null || merge.estimatedMergeBytes <= this.maxMergeSizeBytes) && this.cache.sizeInBytes() <= this.maxCachedBytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void unCache(String fileName) throws IOException {
        Object object = this.uncacheLock;
        synchronized (object) {
            if (!this.cache.fileExists(fileName)) {
                return;
            }
            if (this.delegate.fileExists(fileName)) {
                throw new IOException("cannot uncache file=\"" + fileName + "\": it was separately also created in the delegate directory");
            }
            IndexOutput out = this.delegate.createOutput(fileName);
            IndexInput in = null;
            try {
                in = this.cache.openInput(fileName);
                in.copyBytes(out, in.length());
                Object var6_5 = null;
            }
            catch (Throwable throwable) {
                Object var6_6 = null;
                IOUtils.close(in, out);
                throw throwable;
            }
            IOUtils.close(in, out);
            NRTCachingDirectory nRTCachingDirectory = this;
            synchronized (nRTCachingDirectory) {
                this.cache.deleteFile(fileName);
            }
        }
    }
}

