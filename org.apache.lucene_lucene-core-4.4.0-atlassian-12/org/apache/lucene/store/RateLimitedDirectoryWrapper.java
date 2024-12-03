/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.RateLimitedIndexOutput;
import org.apache.lucene.store.RateLimiter;

public final class RateLimitedDirectoryWrapper
extends Directory {
    private final Directory delegate;
    private volatile RateLimiter[] contextRateLimiters = new RateLimiter[IOContext.Context.values().length];

    public RateLimitedDirectoryWrapper(Directory wrapped) {
        this.delegate = wrapped;
    }

    public Directory getDelegate() {
        return this.delegate;
    }

    @Override
    public String[] listAll() throws IOException {
        this.ensureOpen();
        return this.delegate.listAll();
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        this.ensureOpen();
        return this.delegate.fileExists(name);
    }

    @Override
    public void deleteFile(String name) throws IOException {
        this.ensureOpen();
        this.delegate.deleteFile(name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        this.ensureOpen();
        return this.delegate.fileLength(name);
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        IndexOutput output = this.delegate.createOutput(name, context);
        RateLimiter limiter = this.getRateLimiter(context.context);
        if (limiter != null) {
            return new RateLimitedIndexOutput(limiter, output);
        }
        return output;
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
        this.ensureOpen();
        this.delegate.sync(names);
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        return this.delegate.openInput(name, context);
    }

    @Override
    public void close() throws IOException {
        this.isOpen = false;
        this.delegate.close();
    }

    @Override
    public Directory.IndexInputSlicer createSlicer(String name, IOContext context) throws IOException {
        this.ensureOpen();
        return this.delegate.createSlicer(name, context);
    }

    @Override
    public Lock makeLock(String name) {
        this.ensureOpen();
        return this.delegate.makeLock(name);
    }

    @Override
    public void clearLock(String name) throws IOException {
        this.ensureOpen();
        this.delegate.clearLock(name);
    }

    @Override
    public void setLockFactory(LockFactory lockFactory) throws IOException {
        this.ensureOpen();
        this.delegate.setLockFactory(lockFactory);
    }

    @Override
    public LockFactory getLockFactory() {
        this.ensureOpen();
        return this.delegate.getLockFactory();
    }

    @Override
    public String getLockID() {
        this.ensureOpen();
        return this.delegate.getLockID();
    }

    @Override
    public String toString() {
        return "RateLimitedDirectoryWrapper(" + this.delegate.toString() + ")";
    }

    @Override
    public void copy(Directory to, String src, String dest, IOContext context) throws IOException {
        this.ensureOpen();
        this.delegate.copy(to, src, dest, context);
    }

    private RateLimiter getRateLimiter(IOContext.Context context) {
        assert (context != null);
        return this.contextRateLimiters[context.ordinal()];
    }

    public void setMaxWriteMBPerSec(Double mbPerSec, IOContext.Context context) {
        this.ensureOpen();
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        int ord = context.ordinal();
        RateLimiter limiter = this.contextRateLimiters[ord];
        if (mbPerSec == null) {
            if (limiter != null) {
                limiter.setMbPerSec(Double.MAX_VALUE);
                this.contextRateLimiters[ord] = null;
            }
        } else if (limiter != null) {
            limiter.setMbPerSec(mbPerSec);
            this.contextRateLimiters[ord] = limiter;
        } else {
            this.contextRateLimiters[ord] = new RateLimiter.SimpleRateLimiter(mbPerSec);
        }
    }

    public void setRateLimiter(RateLimiter mergeWriteRateLimiter, IOContext.Context context) {
        this.ensureOpen();
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.contextRateLimiters[context.ordinal()] = mergeWriteRateLimiter;
    }

    public Double getMaxWriteMBPerSec(IOContext.Context context) {
        this.ensureOpen();
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        RateLimiter limiter = this.getRateLimiter(context);
        return limiter == null ? null : Double.valueOf(limiter.getMbPerSec());
    }
}

