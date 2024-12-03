/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.filelock;

import java.io.File;

public class DirectoryLock {
    final File lock;
    final long timeout;
    public static final String LOCKNAME = ".lock";

    public DirectoryLock(File directory, long timeout) {
        this.lock = new File(directory, LOCKNAME);
        this.lock.deleteOnExit();
        this.timeout = timeout;
    }

    public void release() {
        this.lock.delete();
    }

    public void lock() throws InterruptedException {
        if (this.lock.mkdir()) {
            return;
        }
        long deadline = System.currentTimeMillis() + this.timeout;
        while (System.currentTimeMillis() < deadline) {
            if (this.lock.mkdir()) {
                return;
            }
            Thread.sleep(50L);
        }
    }
}

