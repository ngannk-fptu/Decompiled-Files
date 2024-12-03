/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.store.LockObtainFailedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VerifyingLockFactory
extends LockFactory {
    LockFactory lf;
    byte id;
    String host;
    int port;

    public VerifyingLockFactory(byte id, LockFactory lf, String host, int port) throws IOException {
        this.id = id;
        this.lf = lf;
        this.host = host;
        this.port = port;
    }

    public synchronized Lock makeLock(String lockName) {
        return new CheckedLock(this.lf.makeLock(lockName));
    }

    public synchronized void clearLock(String lockName) throws IOException {
        this.lf.clearLock(lockName);
    }

    private class CheckedLock
    extends Lock {
        private Lock lock;

        public CheckedLock(Lock lock) {
            this.lock = lock;
        }

        private void verify(byte message) {
            try {
                Socket s = new Socket(VerifyingLockFactory.this.host, VerifyingLockFactory.this.port);
                OutputStream out = s.getOutputStream();
                out.write(VerifyingLockFactory.this.id);
                out.write(message);
                InputStream in = s.getInputStream();
                int result = in.read();
                in.close();
                out.close();
                s.close();
                if (result != 0) {
                    throw new RuntimeException("lock was double acquired");
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized boolean obtain(long lockWaitTimeout) throws LockObtainFailedException, IOException {
            boolean obtained = this.lock.obtain(lockWaitTimeout);
            if (obtained) {
                this.verify((byte)1);
            }
            return obtained;
        }

        public synchronized boolean obtain() throws LockObtainFailedException, IOException {
            return this.lock.obtain();
        }

        public synchronized boolean isLocked() throws IOException {
            return this.lock.isLocked();
        }

        public synchronized void release() throws IOException {
            if (this.isLocked()) {
                this.verify((byte)0);
                this.lock.release();
            }
        }
    }
}

