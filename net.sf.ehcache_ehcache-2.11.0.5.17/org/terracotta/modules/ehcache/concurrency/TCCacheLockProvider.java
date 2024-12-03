/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.cache.ToolkitCache
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLockType
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 */
package org.terracotta.modules.ehcache.concurrency;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import org.terracotta.modules.ehcache.store.ValueModeHandler;
import org.terracotta.toolkit.cache.ToolkitCache;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitLockType;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

public class TCCacheLockProvider
implements CacheLockProvider {
    private final ToolkitCache backend;
    private final ValueModeHandler valueModeHandler;

    public TCCacheLockProvider(ToolkitCache backend, ValueModeHandler valueModeHandler) {
        this.backend = backend;
        this.valueModeHandler = valueModeHandler;
    }

    @Override
    public Sync getSyncForKey(Object key) {
        ToolkitReadWriteLock lock = this.createLock(key);
        return new TCSync(lock.writeLock(), lock.readLock());
    }

    private ToolkitReadWriteLock createLock(Object key) {
        try {
            String portableKey = this.valueModeHandler.createPortableKey(key);
            return this.backend.createLockForKey((Object)portableKey);
        }
        catch (IOException e) {
            throw new CacheException(e);
        }
    }

    private static class TCSync
    implements Sync {
        private final ToolkitLock writeLock;
        private final ToolkitLock readLock;

        public TCSync(ToolkitLock writeLock, ToolkitLock readLock) {
            this.writeLock = writeLock;
            this.readLock = readLock;
            if (writeLock.getLockType() != ToolkitLockType.WRITE) {
                throw new AssertionError();
            }
            if (readLock.getLockType() != ToolkitLockType.READ) {
                throw new AssertionError();
            }
        }

        private ToolkitLock getLockForType(LockType type) {
            switch (type) {
                case READ: {
                    return this.readLock;
                }
                case WRITE: {
                    return this.writeLock;
                }
            }
            throw new AssertionError((Object)("Unknown lock type - " + type));
        }

        @Override
        public void lock(LockType type) {
            try {
                this.getLockForType(type).lock();
            }
            catch (RuntimeException e) {
                this.handleTCNotRunningException(e);
            }
        }

        @Override
        public boolean tryLock(LockType type, long msec) throws InterruptedException {
            try {
                return this.getLockForType(type).tryLock(msec, TimeUnit.MILLISECONDS);
            }
            catch (RuntimeException e) {
                return this.handleTCNotRunningException(e);
            }
        }

        @Override
        public void unlock(LockType type) {
            try {
                this.getLockForType(type).unlock();
            }
            catch (RuntimeException e) {
                this.handleTCNotRunningException(e);
            }
        }

        @Override
        public boolean isHeldByCurrentThread(LockType type) {
            try {
                return this.getLockForType(type).isHeldByCurrentThread();
            }
            catch (RuntimeException e) {
                return this.handleTCNotRunningException(e);
            }
        }

        private boolean handleTCNotRunningException(RuntimeException e) {
            if (e.getClass().getSimpleName().equals("TCNotRunningException")) {
                throw new TerracottaNotRunningException("Clustered Cache is probably shutdown or Terracotta backend is down.", e);
            }
            throw e;
        }
    }
}

