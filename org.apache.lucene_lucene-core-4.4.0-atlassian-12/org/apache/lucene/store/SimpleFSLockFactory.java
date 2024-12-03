/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.FSLockFactory;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.SimpleFSLock;

public class SimpleFSLockFactory
extends FSLockFactory {
    public SimpleFSLockFactory() {
        this((File)null);
    }

    public SimpleFSLockFactory(File lockDir) {
        this.setLockDir(lockDir);
    }

    public SimpleFSLockFactory(String lockDirName) {
        this.setLockDir(new File(lockDirName));
    }

    @Override
    public Lock makeLock(String lockName) {
        if (this.lockPrefix != null) {
            lockName = this.lockPrefix + "-" + lockName;
        }
        return new SimpleFSLock(this.lockDir, lockName);
    }

    @Override
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

