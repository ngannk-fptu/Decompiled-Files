/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.FSLockFactory;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.NativeFSLock;

public class NativeFSLockFactory
extends FSLockFactory {
    public NativeFSLockFactory() {
        this((File)null);
    }

    public NativeFSLockFactory(String lockDirName) {
        this(new File(lockDirName));
    }

    public NativeFSLockFactory(File lockDir) {
        this.setLockDir(lockDir);
    }

    @Override
    public synchronized Lock makeLock(String lockName) {
        if (this.lockPrefix != null) {
            lockName = this.lockPrefix + "-" + lockName;
        }
        return new NativeFSLock(this.lockDir, lockName);
    }

    @Override
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

