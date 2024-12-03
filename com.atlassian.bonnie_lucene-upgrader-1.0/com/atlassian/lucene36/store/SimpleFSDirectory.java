/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.BufferedIndexInput;
import com.atlassian.lucene36.store.FSDirectory;
import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.LockFactory;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SimpleFSDirectory
extends FSDirectory {
    public SimpleFSDirectory(File path, LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
    }

    public SimpleFSDirectory(File path) throws IOException {
        super(path, null);
    }

    public IndexInput openInput(String name, int bufferSize) throws IOException {
        this.ensureOpen();
        File path = new File(this.directory, name);
        return new SimpleFSIndexInput("SimpleFSIndexInput(path=\"" + path.getPath() + "\")", path, bufferSize, this.getReadChunkSize());
    }

    protected static class SimpleFSIndexInput
    extends BufferedIndexInput {
        protected final Descriptor file;
        boolean isClone;
        protected final int chunkSize;

        @Deprecated
        public SimpleFSIndexInput(File path, int bufferSize, int chunkSize) throws IOException {
            this("anonymous SimpleFSIndexInput", path, bufferSize, chunkSize);
        }

        public SimpleFSIndexInput(String resourceDesc, File path, int bufferSize, int chunkSize) throws IOException {
            super(resourceDesc, bufferSize);
            this.file = new Descriptor(path, "r");
            this.chunkSize = chunkSize;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void readInternal(byte[] b, int offset, int len) throws IOException {
            Descriptor descriptor = this.file;
            synchronized (descriptor) {
                long position = this.getFilePointer();
                if (position != this.file.position) {
                    this.file.seek(position);
                    this.file.position = position;
                }
                int total = 0;
                try {
                    int i;
                    do {
                        int readLength;
                        if ((i = this.file.read(b, offset + total, readLength = total + this.chunkSize > len ? len - total : this.chunkSize)) == -1) {
                            throw new EOFException("read past EOF: " + this);
                        }
                        this.file.position += (long)i;
                    } while ((total += i) < len);
                }
                catch (OutOfMemoryError e) {
                    OutOfMemoryError outOfMemoryError = new OutOfMemoryError("OutOfMemoryError likely caused by the Sun VM Bug described in https://issues.apache.org/jira/browse/LUCENE-1566; try calling FSDirectory.setReadChunkSize with a value smaller than the current chunk size (" + this.chunkSize + ")");
                    outOfMemoryError.initCause(e);
                    throw outOfMemoryError;
                }
                catch (IOException ioe) {
                    IOException newIOE = new IOException(ioe.getMessage() + ": " + this);
                    newIOE.initCause(ioe);
                    throw newIOE;
                }
            }
        }

        public void close() throws IOException {
            if (!this.isClone) {
                this.file.close();
            }
        }

        protected void seekInternal(long position) {
        }

        public long length() {
            return this.file.length;
        }

        public Object clone() {
            SimpleFSIndexInput clone = (SimpleFSIndexInput)super.clone();
            clone.isClone = true;
            return clone;
        }

        boolean isFDValid() throws IOException {
            return this.file.getFD().valid();
        }

        public void copyBytes(IndexOutput out, long numBytes) throws IOException {
            numBytes -= (long)this.flushBuffer(out, numBytes);
            out.copyBytes(this, numBytes);
        }

        protected static class Descriptor
        extends RandomAccessFile {
            protected volatile boolean isOpen = true;
            long position;
            final long length = this.length();

            public Descriptor(File file, String mode) throws IOException {
                super(file, mode);
            }

            public void close() throws IOException {
                if (this.isOpen) {
                    this.isOpen = false;
                    super.close();
                }
            }
        }
    }
}

