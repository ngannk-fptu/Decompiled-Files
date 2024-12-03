/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashSet;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockReleaseFailedException;

class NativeFSLock
extends Lock {
    private RandomAccessFile f;
    private FileChannel channel;
    private FileLock lock;
    private File path;
    private File lockDir;
    private static HashSet<String> LOCK_HELD = new HashSet();

    public NativeFSLock(File lockDir, String lockFileName) {
        this.lockDir = lockDir;
        this.path = new File(lockDir, lockFileName);
    }

    private synchronized boolean lockExists() {
        return this.lock != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized boolean obtain() throws IOException {
        block49: {
            if (this.lockExists()) {
                return false;
            }
            if (!this.lockDir.exists()) {
                if (!this.lockDir.mkdirs()) {
                    throw new IOException("Cannot create directory: " + this.lockDir.getAbsolutePath());
                }
            } else if (!this.lockDir.isDirectory()) {
                throw new IOException("Found regular file where directory expected: " + this.lockDir.getAbsolutePath());
            }
            String canonicalPath = this.path.getCanonicalPath();
            boolean markedHeld = false;
            try {
                HashSet<String> hashSet = LOCK_HELD;
                synchronized (hashSet) {
                    block48: {
                        if (!LOCK_HELD.contains(canonicalPath)) break block48;
                        boolean bl = false;
                        return bl;
                    }
                    LOCK_HELD.add(canonicalPath);
                    markedHeld = true;
                }
                try {
                    this.f = new RandomAccessFile(this.path, "rw");
                }
                catch (IOException e) {
                    this.failureReason = e;
                    this.f = null;
                }
                if (this.f == null) break block49;
                try {
                    this.channel = this.f.getChannel();
                    try {
                        this.lock = this.channel.tryLock();
                    }
                    catch (IOException e) {
                        this.failureReason = e;
                    }
                    finally {
                        if (this.lock == null) {
                            try {
                                this.channel.close();
                            }
                            finally {
                                this.channel = null;
                            }
                        }
                    }
                }
                finally {
                    if (this.channel == null) {
                        try {
                            this.f.close();
                        }
                        finally {
                            this.f = null;
                        }
                    }
                }
            }
            finally {
                if (markedHeld && !this.lockExists()) {
                    HashSet<String> hashSet = LOCK_HELD;
                    synchronized (hashSet) {
                        if (LOCK_HELD.contains(canonicalPath)) {
                            LOCK_HELD.remove(canonicalPath);
                        }
                    }
                }
            }
        }
        return this.lockExists();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void release() throws IOException {
        if (this.lockExists()) {
            try {
                this.lock.release();
            }
            finally {
                this.lock = null;
                try {
                    this.channel.close();
                }
                finally {
                    this.channel = null;
                    try {
                        this.f.close();
                    }
                    finally {
                        this.f = null;
                        HashSet<String> hashSet = LOCK_HELD;
                        synchronized (hashSet) {
                            LOCK_HELD.remove(this.path.getCanonicalPath());
                        }
                    }
                }
            }
            this.path.delete();
        } else {
            boolean obtained = false;
            try {
                obtained = this.obtain();
                if (!obtained) {
                    throw new LockReleaseFailedException("Cannot forcefully unlock a NativeFSLock which is held by another indexer component: " + this.path);
                }
            }
            finally {
                if (obtained) {
                    this.release();
                }
            }
        }
    }

    @Override
    public synchronized boolean isLocked() {
        if (this.lockExists()) {
            return true;
        }
        if (!this.path.exists()) {
            return false;
        }
        try {
            boolean obtained = this.obtain();
            if (obtained) {
                this.release();
            }
            return !obtained;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public String toString() {
        return "NativeFSLock@" + this.path;
    }
}

