/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockReleaseFailedException;

class SimpleFSLock
extends Lock {
    File lockFile;
    File lockDir;

    public SimpleFSLock(File lockDir, String lockFileName) {
        this.lockDir = lockDir;
        this.lockFile = new File(lockDir, lockFileName);
    }

    @Override
    public boolean obtain() throws IOException {
        if (!this.lockDir.exists()) {
            if (!this.lockDir.mkdirs()) {
                throw new IOException("Cannot create directory: " + this.lockDir.getAbsolutePath());
            }
        } else if (!this.lockDir.isDirectory()) {
            throw new IOException("Found regular file where directory expected: " + this.lockDir.getAbsolutePath());
        }
        return this.lockFile.createNewFile();
    }

    @Override
    public void release() throws LockReleaseFailedException {
        if (this.lockFile.exists() && !this.lockFile.delete()) {
            throw new LockReleaseFailedException("failed to delete " + this.lockFile);
        }
    }

    @Override
    public boolean isLocked() {
        return this.lockFile.exists();
    }

    public String toString() {
        return "SimpleFSLock@" + this.lockFile;
    }
}

