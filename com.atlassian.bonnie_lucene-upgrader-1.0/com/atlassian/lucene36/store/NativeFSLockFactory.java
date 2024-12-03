/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.FSLockFactory;
import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.NativeFSLock;
import java.io.File;
import java.io.IOException;

public class NativeFSLockFactory
extends FSLockFactory {
    public NativeFSLockFactory() throws IOException {
        this((File)null);
    }

    public NativeFSLockFactory(String lockDirName) throws IOException {
        this(new File(lockDirName));
    }

    public NativeFSLockFactory(File lockDir) throws IOException {
        this.setLockDir(lockDir);
    }

    public synchronized Lock makeLock(String lockName) {
        if (this.lockPrefix != null) {
            lockName = this.lockPrefix + "-" + lockName;
        }
        return new NativeFSLock(this.lockDir, lockName);
    }

    public void clearLock(String lockName) throws IOException {
        if (this.lockDir.exists()) {
            this.makeLock(lockName).release();
            if (this.lockPrefix != null) {
                lockName = this.lockPrefix + "-" + lockName;
            }
            new File(this.lockDir, lockName).delete();
        }
    }
}

