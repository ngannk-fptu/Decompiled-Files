/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMFile;
import org.apache.lucene.store.RAMInputStream;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.store.SingleInstanceLockFactory;

public class RAMDirectory
extends Directory {
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

    public RAMDirectory(Directory dir, IOContext context) throws IOException {
        this(dir, false, context);
    }

    private RAMDirectory(Directory dir, boolean closeDir, IOContext context) throws IOException {
        this();
        for (String file : dir.listAll()) {
            dir.copy(this, file, file, context);
        }
        if (closeDir) {
            dir.close();
        }
    }

    @Override
    public final String[] listAll() {
        this.ensureOpen();
        Set<String> fileNames = this.fileMap.keySet();
        ArrayList<String> names = new ArrayList<String>(fileNames.size());
        for (String name : fileNames) {
            names.add(name);
        }
        return names.toArray(new String[names.size()]);
    }

    @Override
    public final boolean fileExists(String name) {
        this.ensureOpen();
        return this.fileMap.containsKey(name);
    }

    @Override
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

    @Override
    public void deleteFile(String name) throws IOException {
        this.ensureOpen();
        RAMFile file = this.fileMap.remove(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        file.directory = null;
        this.sizeInBytes.addAndGet(-file.sizeInBytes);
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
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

    @Override
    public void sync(Collection<String> names) throws IOException {
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        RAMFile file = this.fileMap.get(name);
        if (file == null) {
            throw new FileNotFoundException(name);
        }
        return new RAMInputStream(name, file);
    }

    @Override
    public void close() {
        this.isOpen = false;
        this.fileMap.clear();
    }
}

