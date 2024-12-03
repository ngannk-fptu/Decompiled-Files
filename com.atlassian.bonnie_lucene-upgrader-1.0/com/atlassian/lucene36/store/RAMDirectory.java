/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.index.IndexFileNameFilter;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.RAMFile;
import com.atlassian.lucene36.store.RAMInputStream;
import com.atlassian.lucene36.store.RAMOutputStream;
import com.atlassian.lucene36.store.SingleInstanceLockFactory;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RAMDirectory
extends Directory
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final Map<String, RAMFile> fileMap = new ConcurrentHashMap<String, RAMFile>();
    protected final AtomicLong sizeInBytes = new AtomicLong();

    public RAMDirectory() {
        try {
            this.setLockFactory(new SingleInstanceLockFactory());
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public RAMDirectory(Directory dir) throws IOException {
        this(dir, false);
    }

    private RAMDirectory(Directory dir, boolean closeDir) throws IOException {
        this();
        IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
        for (String file : dir.listAll()) {
            if (!filter.accept(null, file)) continue;
            dir.copy(this, file, file);
        }
        if (closeDir) {
            dir.close();
        }
    }

    public final String[] listAll() {
        this.ensureOpen();
        Set<String> fileNames = this.fileMap.keySet();
        ArrayList<String> names = new ArrayList<String>(fileNames.size());
        for (String name : fileNames) {
            names.add(name);
        }
        return names.toArray(new String[names.size()]);
    }

    public final boolean fileExists(String name) {
        this.ensureOpen();
        return this.fileMap.containsKey(name);
    }

    public final long fileModified(String name) throws IOException {
        this.ensureOpen();
        RAMFile file = this.fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return file.getLastModified();
    }

    @Deprecated
    public void touchFile(String name) throws IOException {
        long ts2;
        this.ensureOpen();
        RAMFile file = this.fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        long ts1 = System.currentTimeMillis();
        do {
            try {
                Thread.sleep(0L, 1);
            }
            catch (InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
            }
        } while (ts1 == (ts2 = System.currentTimeMillis()));
        file.setLastModified(ts2);
    }

    public final long fileLength(String name) throws IOException {
        this.ensureOpen();
        RAMFile file = this.fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return file.getLength();
    }

    public final long sizeInBytes() {
        this.ensureOpen();
        return this.sizeInBytes.get();
    }

    public void deleteFile(String name) throws IOException {
        this.ensureOpen();
        RAMFile file = this.fileMap.remove(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        file.directory = null;
        this.sizeInBytes.addAndGet(-file.sizeInBytes);
    }

    public IndexOutput createOutput(String name) throws IOException {
        this.ensureOpen();
        RAMFile file = this.newRAMFile();
        RAMFile existing = this.fileMap.remove(name);
        if (existing != null) {
            this.sizeInBytes.addAndGet(-existing.sizeInBytes);
            existing.directory = null;
        }
        this.fileMap.put(name, file);
        return new RAMOutputStream(file);
    }

    protected RAMFile newRAMFile() {
        return new RAMFile(this);
    }

    public IndexInput openInput(String name) throws IOException {
        this.ensureOpen();
        RAMFile file = this.fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return new RAMInputStream(name, file);
    }

    public void close() {
        this.isOpen = false;
        this.fileMap.clear();
    }
}

