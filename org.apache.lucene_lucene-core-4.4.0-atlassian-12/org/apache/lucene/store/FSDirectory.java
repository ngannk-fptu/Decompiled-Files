/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.BufferedIndexOutput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSLockFactory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.NativeFSLockFactory;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.ThreadInterruptedException;

public abstract class FSDirectory
extends Directory {
    public static final int DEFAULT_READ_CHUNK_SIZE = Constants.JRE_IS_64BIT ? Integer.MAX_VALUE : 0x6400000;
    protected final File directory;
    protected final Set<String> staleFiles = Collections.synchronizedSet(new HashSet());
    private int chunkSize = DEFAULT_READ_CHUNK_SIZE;

    private static File getCanonicalPath(File file) throws IOException {
        return new File(file.getCanonicalPath());
    }

    protected FSDirectory(File path, LockFactory lockFactory) throws IOException {
        if (lockFactory == null) {
            lockFactory = new NativeFSLockFactory();
        }
        this.directory = FSDirectory.getCanonicalPath(path);
        if (this.directory.exists() && !this.directory.isDirectory()) {
            throw new NoSuchDirectoryException("file '" + this.directory + "' exists but is not a directory");
        }
        this.setLockFactory(lockFactory);
    }

    public static FSDirectory open(File path) throws IOException {
        return FSDirectory.open(path, null);
    }

    public static FSDirectory open(File path, LockFactory lockFactory) throws IOException {
        if ((Constants.WINDOWS || Constants.SUN_OS || Constants.LINUX) && Constants.JRE_IS_64BIT && MMapDirectory.UNMAP_SUPPORTED) {
            return new MMapDirectory(path, lockFactory);
        }
        if (Constants.WINDOWS) {
            return new SimpleFSDirectory(path, lockFactory);
        }
        return new NIOFSDirectory(path, lockFactory);
    }

    @Override
    public void setLockFactory(LockFactory lockFactory) throws IOException {
        super.setLockFactory(lockFactory);
        if (lockFactory instanceof FSLockFactory) {
            FSLockFactory lf = (FSLockFactory)lockFactory;
            File dir = lf.getLockDir();
            if (dir == null) {
                lf.setLockDir(this.directory);
                lf.setLockPrefix(null);
            } else if (dir.getCanonicalPath().equals(this.directory.getCanonicalPath())) {
                lf.setLockPrefix(null);
            }
        }
    }

    public static String[] listAll(File dir) throws IOException {
        if (!dir.exists()) {
            throw new NoSuchDirectoryException("directory '" + dir + "' does not exist");
        }
        if (!dir.isDirectory()) {
            throw new NoSuchDirectoryException("file '" + dir + "' exists but is not a directory");
        }
        String[] result = dir.list(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String file) {
                return !new File(dir, file).isDirectory();
            }
        });
        if (result == null) {
            throw new IOException("directory '" + dir + "' exists and is a directory, but cannot be listed: list() returned null");
        }
        return result;
    }

    @Override
    public String[] listAll() throws IOException {
        this.ensureOpen();
        return FSDirectory.listAll(this.directory);
    }

    @Override
    public boolean fileExists(String name) {
        this.ensureOpen();
        File file = new File(this.directory, name);
        return file.exists();
    }

    public static long fileModified(File directory, String name) {
        File file = new File(directory, name);
        return file.lastModified();
    }

    @Override
    public long fileLength(String name) throws IOException {
        this.ensureOpen();
        File file = new File(this.directory, name);
        long len = file.length();
        if (len == 0L && !file.exists()) {
            throw new FileNotFoundException(name);
        }
        return len;
    }

    @Override
    public void deleteFile(String name) throws IOException {
        this.ensureOpen();
        File file = new File(this.directory, name);
        if (!file.delete()) {
            throw new IOException("Cannot delete " + file);
        }
        this.staleFiles.remove(name);
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        this.ensureCanWrite(name);
        return new FSIndexOutput(this, name);
    }

    protected void ensureCanWrite(String name) throws IOException {
        if (!this.directory.exists() && !this.directory.mkdirs()) {
            throw new IOException("Cannot create directory: " + this.directory);
        }
        File file = new File(this.directory, name);
        if (file.exists() && !file.delete()) {
            throw new IOException("Cannot overwrite: " + file);
        }
    }

    protected void onIndexOutputClosed(FSIndexOutput io) {
        this.staleFiles.add(io.name);
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
        this.ensureOpen();
        HashSet<String> toSync = new HashSet<String>(names);
        toSync.retainAll(this.staleFiles);
        for (String name : toSync) {
            this.fsync(name);
        }
        this.staleFiles.removeAll(toSync);
    }

    @Override
    public String getLockID() {
        String dirName;
        this.ensureOpen();
        try {
            dirName = this.directory.getCanonicalPath();
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }
        int digest = 0;
        for (int charIDX = 0; charIDX < dirName.length(); ++charIDX) {
            char ch = dirName.charAt(charIDX);
            digest = 31 * digest + ch;
        }
        return "lucene-" + Integer.toHexString(digest);
    }

    @Override
    public synchronized void close() {
        this.isOpen = false;
    }

    public File getDirectory() {
        this.ensureOpen();
        return this.directory;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "@" + this.directory + " lockFactory=" + this.getLockFactory();
    }

    public final void setReadChunkSize(int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }
        if (!Constants.JRE_IS_64BIT) {
            this.chunkSize = chunkSize;
        }
    }

    public final int getReadChunkSize() {
        return this.chunkSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void fsync(String name) throws IOException {
        File fullFile = new File(this.directory, name);
        boolean success = false;
        int retryCount = 0;
        IOException exc = null;
        while (!success && retryCount < 5) {
            ++retryCount;
            RandomAccessFile file = null;
            try {
                try {
                    file = new RandomAccessFile(fullFile, "rw");
                    file.getFD().sync();
                    success = true;
                }
                finally {
                    if (file == null) continue;
                    file.close();
                }
            }
            catch (IOException ioe) {
                if (exc == null) {
                    exc = ioe;
                }
                try {
                    Thread.sleep(5L);
                }
                catch (InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
            }
        }
        if (!success) {
            throw exc;
        }
    }

    protected static class FSIndexOutput
    extends BufferedIndexOutput {
        private final FSDirectory parent;
        private final String name;
        private final RandomAccessFile file;
        private volatile boolean isOpen;

        public FSIndexOutput(FSDirectory parent, String name) throws IOException {
            this.parent = parent;
            this.name = name;
            this.file = new RandomAccessFile(new File(parent.directory, name), "rw");
            this.isOpen = true;
        }

        @Override
        public void flushBuffer(byte[] b, int offset, int size) throws IOException {
            assert (this.isOpen);
            this.file.write(b, offset, size);
        }

        @Override
        public void close() throws IOException {
            this.parent.onIndexOutputClosed(this);
            if (this.isOpen) {
                boolean success = false;
                try {
                    super.close();
                    success = true;
                }
                finally {
                    this.isOpen = false;
                    if (!success) {
                        try {
                            this.file.close();
                        }
                        catch (Throwable throwable) {}
                    } else {
                        this.file.close();
                    }
                }
            }
        }

        @Override
        public void seek(long pos) throws IOException {
            super.seek(pos);
            this.file.seek(pos);
        }

        @Override
        public long length() throws IOException {
            return this.file.length();
        }

        @Override
        public void setLength(long length) throws IOException {
            this.file.setLength(length);
        }
    }

    protected static abstract class FSIndexInput
    extends BufferedIndexInput {
        protected final RandomAccessFile file;
        boolean isClone = false;
        protected final int chunkSize;
        protected final long off;
        protected final long end;

        protected FSIndexInput(String resourceDesc, File path, IOContext context, int chunkSize) throws IOException {
            super(resourceDesc, context);
            this.file = new RandomAccessFile(path, "r");
            this.chunkSize = chunkSize;
            this.off = 0L;
            this.end = this.file.length();
        }

        protected FSIndexInput(String resourceDesc, RandomAccessFile file, long off, long length, int bufferSize, int chunkSize) {
            super(resourceDesc, bufferSize);
            this.file = file;
            this.chunkSize = chunkSize;
            this.off = off;
            this.end = off + length;
            this.isClone = true;
        }

        @Override
        public void close() throws IOException {
            if (!this.isClone) {
                this.file.close();
            }
        }

        @Override
        public FSIndexInput clone() {
            FSIndexInput clone = (FSIndexInput)super.clone();
            clone.isClone = true;
            return clone;
        }

        @Override
        public final long length() {
            return this.end - this.off;
        }

        boolean isFDValid() throws IOException {
            return this.file.getFD().valid();
        }
    }
}

