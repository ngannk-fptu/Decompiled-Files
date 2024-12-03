/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

public final class TrackingDirectoryWrapper
extends Directory
implements Closeable {
    private final Directory other;
    private final Set<String> createdFileNames = Collections.synchronizedSet(new HashSet());

    public TrackingDirectoryWrapper(Directory other) {
        this.other = other;
    }

    @Override
    public String[] listAll() throws IOException {
        return this.other.listAll();
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        return this.other.fileExists(name);
    }

    @Override
    public void deleteFile(String name) throws IOException {
        this.createdFileNames.remove(name);
        this.other.deleteFile(name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        return this.other.fileLength(name);
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        this.createdFileNames.add(name);
        return this.other.createOutput(name, context);
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
        this.other.sync(names);
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        return this.other.openInput(name, context);
    }

    @Override
    public Lock makeLock(String name) {
        return this.other.makeLock(name);
    }

    @Override
    public void clearLock(String name) throws IOException {
        this.other.clearLock(name);
    }

    @Override
    public void close() throws IOException {
        this.other.close();
    }

    @Override
    public void setLockFactory(LockFactory lockFactory) throws IOException {
        this.other.setLockFactory(lockFactory);
    }

    @Override
    public LockFactory getLockFactory() {
        return this.other.getLockFactory();
    }

    @Override
    public String getLockID() {
        return this.other.getLockID();
    }

    @Override
    public String toString() {
        return "TrackingDirectoryWrapper(" + this.other.toString() + ")";
    }

    @Override
    public void copy(Directory to, String src, String dest, IOContext context) throws IOException {
        this.createdFileNames.add(dest);
        this.other.copy(to, src, dest, context);
    }

    @Override
    public Directory.IndexInputSlicer createSlicer(String name, IOContext context) throws IOException {
        return this.other.createSlicer(name, context);
    }

    public Set<String> getCreatedFiles() {
        return this.createdFileNames;
    }

    public Directory getDelegate() {
        return this.other;
    }
}

