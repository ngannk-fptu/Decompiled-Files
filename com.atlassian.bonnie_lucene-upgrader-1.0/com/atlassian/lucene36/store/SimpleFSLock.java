/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockReleaseFailedException;
import java.io.File;
import java.io.IOException;

class SimpleFSLock
extends Lock {
    File lockFile;
    File lockDir;

    public SimpleFSLock(File lockDir, String lockFileName) {
        this.lockDir = lockDir;
        this.lockFile = new File(lockDir, lockFileName);
    }

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

    public void release() throws LockReleaseFailedException {
        if (this.lockFile.exists() && !this.lockFile.delete()) {
            throw new LockReleaseFailedException("failed to delete " + this.lockFile);
        }
    }

    public boolean isLocked() {
        return this.lockFile.exists();
    }

    public String toString() {
        return "SimpleFSLock@" + this.lockFile;
    }
}

