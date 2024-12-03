/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.CorruptIndexException;
import com.atlassian.lucene36.index.IndexFileNames;
import com.atlassian.lucene36.store.BufferedIndexInput;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.Lock;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

class CompoundFileReader
extends Directory {
    private int readBufferSize;
    private Directory directory;
    private String fileName;
    private IndexInput stream;
    private HashMap<String, FileEntry> entries = new HashMap();

    public CompoundFileReader(Directory dir, String name) throws IOException {
        this(dir, name, 1024);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public CompoundFileReader(Directory dir, String name, int readBufferSize) throws IOException {
        assert (!(dir instanceof CompoundFileReader)) : "compound file inside of compound file: " + name;
        this.directory = dir;
        this.fileName = name;
        this.readBufferSize = readBufferSize;
        boolean success = false;
        try {
            boolean stripSegmentName;
            int count;
            this.stream = dir.openInput(name, readBufferSize);
            int firstInt = this.stream.readVInt();
            if (firstInt < 0) {
                if (firstInt < -1) {
                    throw new CorruptIndexException("Incompatible format version: " + firstInt + " expected " + -1 + " (resource: " + this.stream + ")");
                }
                count = this.stream.readVInt();
                stripSegmentName = false;
            } else {
                count = firstInt;
                stripSegmentName = true;
            }
            FileEntry entry = null;
            for (int i = 0; i < count; ++i) {
                long offset = this.stream.readLong();
                String id = this.stream.readString();
                if (stripSegmentName) {
                    id = IndexFileNames.stripSegmentName(id);
                }
                if (entry != null) {
                    entry.length = offset - entry.offset;
                }
                entry = new FileEntry();
                entry.offset = offset;
                this.entries.put(id, entry);
            }
            if (entry != null) {
                entry.length = this.stream.length() - entry.offset;
            }
            success = true;
            Object var14_12 = null;
            if (success) return;
            if (this.stream == null) return;
        }
        catch (Throwable throwable) {
            Object var14_13 = null;
            if (success || this.stream == null) throw throwable;
            try {
                this.stream.close();
                throw throwable;
            }
            catch (IOException e) {
                // empty catch block
            }
            throw throwable;
        }
        try {
            this.stream.close();
            return;
        }
        catch (IOException e) {}
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public String getName() {
        return this.fileName;
    }

    public synchronized void close() throws IOException {
        if (this.stream == null) {
            return;
        }
        this.entries.clear();
        this.stream.close();
        this.stream = null;
    }

    public synchronized IndexInput openInput(String id) throws IOException {
        return this.openInput(id, this.readBufferSize);
    }

    public synchronized IndexInput openInput(String id, int readBufferSize) throws IOException {
        if (this.stream == null) {
            throw new IOException("Stream closed");
        }
        FileEntry entry = this.entries.get(id = IndexFileNames.stripSegmentName(id));
        if (entry == null) {
            throw new FileNotFoundException("No sub-file with id " + id + " found (fileName=" + this.fileName + " files: " + this.entries.keySet() + ")");
        }
        return new CSIndexInput(this.stream, entry.offset, entry.length, readBufferSize);
    }

    public String[] listAll() {
        String[] res = this.entries.keySet().toArray(new String[this.entries.size()]);
        String seg = this.fileName.substring(0, this.fileName.indexOf(46));
        for (int i = 0; i < res.length; ++i) {
            res[i] = seg + res[i];
        }
        return res;
    }

    public boolean fileExists(String name) {
        return this.entries.containsKey(IndexFileNames.stripSegmentName(name));
    }

    public long fileModified(String name) throws IOException {
        return this.directory.fileModified(this.fileName);
    }

    @Deprecated
    public void touchFile(String name) throws IOException {
        this.directory.touchFile(this.fileName);
    }

    public void deleteFile(String name) {
        throw new UnsupportedOperationException();
    }

    public void renameFile(String from, String to) {
        throw new UnsupportedOperationException();
    }

    public long fileLength(String name) throws IOException {
        FileEntry e = this.entries.get(IndexFileNames.stripSegmentName(name));
        if (e == null) {
            throw new FileNotFoundException(name);
        }
        return e.length;
    }

    public IndexOutput createOutput(String name) {
        throw new UnsupportedOperationException();
    }

    public Lock makeLock(String name) {
        throw new UnsupportedOperationException();
    }

    static final class CSIndexInput
    extends BufferedIndexInput {
        IndexInput base;
        long fileOffset;
        long length;

        CSIndexInput(IndexInput base, long fileOffset, long length) {
            this(base, fileOffset, length, 1024);
        }

        CSIndexInput(IndexInput base, long fileOffset, long length, int readBufferSize) {
            super(readBufferSize);
            this.base = (IndexInput)base.clone();
            this.fileOffset = fileOffset;
            this.length = length;
        }

        public Object clone() {
            CSIndexInput clone = (CSIndexInput)super.clone();
            clone.base = (IndexInput)this.base.clone();
            clone.fileOffset = this.fileOffset;
            clone.length = this.length;
            return clone;
        }

        protected void readInternal(byte[] b, int offset, int len) throws IOException {
            long start = this.getFilePointer();
            if (start + (long)len > this.length) {
                throw new EOFException("read past EOF: " + this.base);
            }
            this.base.seek(this.fileOffset + start);
            this.base.readBytes(b, offset, len, false);
        }

        protected void seekInternal(long pos) {
        }

        public void close() throws IOException {
            this.base.close();
        }

        public long length() {
            return this.length;
        }

        public void copyBytes(IndexOutput out, long numBytes) throws IOException {
            if ((numBytes -= (long)this.flushBuffer(out, numBytes)) > 0L) {
                long start = this.getFilePointer();
                if (start + numBytes > this.length) {
                    throw new EOFException("read past EOF: " + this.base);
                }
                this.base.seek(this.fileOffset + start);
                this.base.copyBytes(out, numBytes);
            }
        }
    }

    private static final class FileEntry {
        long offset;
        long length;

        private FileEntry() {
        }
    }
}

