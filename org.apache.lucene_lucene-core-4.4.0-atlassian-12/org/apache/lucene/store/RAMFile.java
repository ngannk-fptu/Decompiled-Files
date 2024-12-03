/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.util.ArrayList;
import org.apache.lucene.store.RAMDirectory;

public class RAMFile {
    protected ArrayList<byte[]> buffers = new ArrayList();
    long length;
    RAMDirectory directory;
    protected long sizeInBytes;

    public RAMFile() {
    }

    RAMFile(RAMDirectory directory) {
        this.directory = directory;
    }

    public synchronized long getLength() {
        return this.length;
    }

    protected synchronized void setLength(long length) {
        this.length = length;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final byte[] addBuffer(int size) {
        byte[] buffer = this.newBuffer(size);
        RAMFile rAMFile = this;
        synchronized (rAMFile) {
            this.buffers.add(buffer);
            this.sizeInBytes += (long)size;
        }
        if (this.directory != null) {
            this.directory.sizeInBytes.getAndAdd(size);
        }
        return buffer;
    }

    protected final synchronized byte[] getBuffer(int index) {
        return this.buffers.get(index);
    }

    protected final synchronized int numBuffers() {
        return this.buffers.size();
    }

    protected byte[] newBuffer(int size) {
        return new byte[size];
    }

    public synchronized long getSizeInBytes() {
        return this.sizeInBytes;
    }
}

