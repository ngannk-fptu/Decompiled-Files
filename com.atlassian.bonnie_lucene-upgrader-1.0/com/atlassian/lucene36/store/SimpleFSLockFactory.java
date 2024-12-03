/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.FSLockFactory;
import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.SimpleFSLock;
import java.io.File;
import java.io.IOException;

public class SimpleFSLockFactory
extends FSLockFactory {
    public SimpleFSLockFactory() throws IOException {
        this((File)null);
    }

    public SimpleFSLockFactory(File lockDir) throws IOException {
        this.setLockDir(lockDir);
    }

    public SimpleFSLockFactory(String lockDirName) throws IOException {
        this.setLockDir(new File(lockDirName));
    }

    public Lock makeLock(String lockName) {
        if (this.lockPrefix != null) {
            lockName = this.lockPrefix + "-" + lockName;
        }
        return new SimpleFSLock(this.lockDir, lockName);
    }

    public void clearLock(String lockName) throws IOException {
        if (this.lockDir.exists()) {
            File lockFile;
            if (this.lockPrefix != null) {
                lockName = this.lockPrefix + "-" + lockName;
            }
            if ((lockFile = new File(this.lockDir, lockName)).exists() && !lockFile.delete()) {
                throw new IOException("Cannot delete " + lockFile);
            }
        }
    }
}

