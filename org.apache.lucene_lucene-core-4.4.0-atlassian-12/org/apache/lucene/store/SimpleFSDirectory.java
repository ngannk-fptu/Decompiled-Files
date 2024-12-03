/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.LockFactory;

public class SimpleFSDirectory
extends FSDirectory {
    public SimpleFSDirectory(File path, LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
    }

    public SimpleFSDirectory(File path) throws IOException {
        super(path, null);
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        File path = new File(this.directory, name);
        return new SimpleFSIndexInput("SimpleFSIndexInput(path=\"" + path.getPath() + "\")", path, context, this.getReadChunkSize());
    }

    @Override
    public Directory.IndexInputSlicer createSlicer(String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final File file = new File(this.getDirectory(), name);
        final RandomAccessFile descriptor = new RandomAccessFile(file, "r");
        return new Directory.IndexInputSlicer(){

            @Override
            public void close() throws IOException {
                descriptor.close();
            }

            @Override
            public IndexInput openSlice(String sliceDescription, long offset, long length) {
                return new SimpleFSIndexInput("SimpleFSIndexInput(" + sliceDescription + " in path=\"" + file.getPath() + "\" slice=" + offset + ":" + (offset + length) + ")", descriptor, offset, length, BufferedIndexInput.bufferSize(context), SimpleFSDirectory.this.getReadChunkSize());
            }

            @Override
            public IndexInput openFullSlice() {
                try {
                    return this.openSlice("full-slice", 0L, descriptor.length());
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    protected static class SimpleFSIndexInput
    extends FSDirectory.FSIndexInput {
        public SimpleFSIndexInput(String resourceDesc, File path, IOContext context, int chunkSize) throws IOException {
            super(resourceDesc, path, context, chunkSize);
        }

        public SimpleFSIndexInput(String resourceDesc, RandomAccessFile file, long off, long length, int bufferSize, int chunkSize) {
            super(resourceDesc, file, off, length, bufferSize, chunkSize);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void readInternal(byte[] b, int offset, int len) throws IOException {
            RandomAccessFile randomAccessFile = this.file;
            synchronized (randomAccessFile) {
                long position = this.off + this.getFilePointer();
                this.file.seek(position);
                int total = 0;
                if (position + (long)len > this.end) {
                    throw new EOFException("read past EOF: " + this);
                }
                try {
                    int readLength;
                    int i;
                    while ((total += (i = this.file.read(b, offset + total, readLength = total + this.chunkSize > len ? len - total : this.chunkSize))) < len) {
                    }
                }
                catch (OutOfMemoryError e) {
                    OutOfMemoryError outOfMemoryError = new OutOfMemoryError("OutOfMemoryError likely caused by the Sun VM Bug described in https://issues.apache.org/jira/browse/LUCENE-1566; try calling FSDirectory.setReadChunkSize with a value smaller than the current chunk size (" + this.chunkSize + ")");
                    outOfMemoryError.initCause(e);
                    throw outOfMemoryError;
                }
                catch (IOException ioe) {
                    throw new IOException(ioe.getMessage() + ": " + this, ioe);
                }
            }
        }

        @Override
        protected void seekInternal(long position) {
        }
    }
}

