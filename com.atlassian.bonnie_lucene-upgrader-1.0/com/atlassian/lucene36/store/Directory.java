/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.index.IndexFileNameFilter;
import com.atlassian.lucene36.store.AlreadyClosedException;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.util.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Directory
implements Closeable {
    protected volatile boolean isOpen = true;
    protected LockFactory lockFactory;

    public abstract String[] listAll() throws IOException;

    public abstract boolean fileExists(String var1) throws IOException;

    @Deprecated
    public abstract long fileModified(String var1) throws IOException;

    @Deprecated
    public abstract void touchFile(String var1) throws IOException;

    public abstract void deleteFile(String var1) throws IOException;

    public abstract long fileLength(String var1) throws IOException;

    public abstract IndexOutput createOutput(String var1) throws IOException;

    @Deprecated
    public void sync(String name) throws IOException {
    }

    public void sync(Collection<String> names) throws IOException {
        for (String name : names) {
            this.sync(name);
        }
    }

    public abstract IndexInput openInput(String var1) throws IOException;

    public IndexInput openInput(String name, int bufferSize) throws IOException {
        return this.openInput(name);
    }

    public Lock makeLock(String name) {
        return this.lockFactory.makeLock(name);
    }

    public void clearLock(String name) throws IOException {
        if (this.lockFactory != null) {
            this.lockFactory.clearLock(name);
        }
    }

    @Override
    public abstract void close() throws IOException;

    public void setLockFactory(LockFactory lockFactory) throws IOException {
        assert (lockFactory != null);
        this.lockFactory = lockFactory;
        lockFactory.setLockPrefix(this.getLockID());
    }

    public LockFactory getLockFactory() {
        return this.lockFactory;
    }

    public String getLockID() {
        return this.toString();
    }

    public String toString() {
        return super.toString() + " lockFactory=" + this.getLockFactory();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void copy(Directory to, String src, String dest) throws IOException {
        IndexOutput os = null;
        IndexInput is = null;
        IOException priorException = null;
        try {
            os = to.createOutput(dest);
            is = this.openInput(src);
            is.copyBytes(os, is.length());
        }
        catch (IOException ioe) {
            try {
                priorException = ioe;
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException(priorException, os, is);
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(priorException, os, is);
        }
        IOUtils.closeWhileHandlingException(priorException, os, is);
    }

    @Deprecated
    public static void copy(Directory src, Directory dest, boolean closeDirSrc) throws IOException {
        IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
        for (String file : src.listAll()) {
            if (!filter.accept(null, file)) continue;
            src.copy(dest, file, file);
        }
        if (closeDirSrc) {
            src.close();
        }
    }

    protected final void ensureOpen() throws AlreadyClosedException {
        if (!this.isOpen) {
            throw new AlreadyClosedException("this Directory is closed");
        }
    }
}

