/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.IOUtils;

public class NRTCachingDirectory
extends Directory {
    private final RAMDirectory cache = new RAMDirectory();
    private final Directory delegate;
    private final long maxMergeSizeBytes;
    private final long maxCachedBytes;
    private static final boolean VERBOSE = false;
    private final Object uncacheLock = new Object();

    public NRTCachingDirectory(Directory delegate, double maxMergeSizeMB, double maxCachedMB) {
        this.delegate = delegate;
        this.maxMergeSizeBytes = (long)(maxMergeSizeMB * 1024.0 * 1024.0);
        this.maxCachedBytes = (long)(maxCachedMB * 1024.0 * 1024.0);
    }

    public Directory getDelegate() {
        return this.delegate;
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
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        if (this.doCacheWrite(name, context)) {
            try {
                this.delegate.deleteFile(name);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return this.cache.createOutput(name, context);
        }
        try {
            this.cache.deleteFile(name);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return this.delegate.createOutput(name, context);
    }

    @Override
    public void sync(Collection<String> fileNames) throws IOException {
        for (String fileName : fileNames) {
            this.unCache(fileName);
        }
        this.delegate.sync(fileNames);
    }

    @Override
    public synchronized IndexInput openInput(String name, IOContext context) throws IOException {
        if (this.cache.fileExists(name)) {
            return this.cache.openInput(name, context);
        }
        return this.delegate.openInput(name, context);
    }

    @Override
    public synchronized Directory.IndexInputSlicer createSlicer(String name, IOContext context) throws IOException {
        this.ensureOpen();
        if (this.cache.fileExists(name)) {
            return this.cache.createSlicer(name, context);
        }
        return this.delegate.createSlicer(name, context);
    }

    @Override
    public void close() throws IOException {
        for (String fileName : this.cache.listAll()) {
            this.unCache(fileName);
        }
        this.cache.close();
        this.delegate.close();
    }

    protected boolean doCacheWrite(String name, IOContext context) {
        long bytes = 0L;
        if (context.mergeInfo != null) {
            bytes = context.mergeInfo.estimatedMergeBytes;
        } else if (context.flushInfo != null) {
            bytes = context.flushInfo.estimatedSegmentSize;
        }
        return !name.equals("segments.gen") && bytes <= this.maxMergeSizeBytes && bytes + this.cache.sizeInBytes() <= this.maxCachedBytes;
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
            IOContext context = IOContext.DEFAULT;
            IndexOutput out = this.delegate.createOutput(fileName, context);
            IndexInput in = null;
            try {
                in = this.cache.openInput(fileName, context);
                out.copyBytes(in, in.length());
            }
            catch (Throwable throwable) {
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

