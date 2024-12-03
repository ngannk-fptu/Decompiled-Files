/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.spi.support;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractCachedDomainDataAccess;
import org.hibernate.cache.spi.support.AccessedDataClassification;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.jboss.logging.Logger;

public abstract class AbstractReadWriteAccess
extends AbstractCachedDomainDataAccess {
    private static final Logger log = Logger.getLogger(AbstractReadWriteAccess.class);
    private final UUID uuid = UUID.randomUUID();
    private final AtomicLong nextLockId = new AtomicLong();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = this.reentrantReadWriteLock.readLock();
    private final Lock writeLock = this.reentrantReadWriteLock.writeLock();

    protected AbstractReadWriteAccess(DomainDataRegion domainDataRegion, DomainDataStorageAccess storageAccess) {
        super(domainDataRegion, storageAccess);
    }

    protected abstract Comparator getVersionComparator();

    protected UUID uuid() {
        return this.uuid;
    }

    protected long nextLockId() {
        return this.nextLockId.getAndIncrement();
    }

    protected Lock readLock() {
        return this.readLock;
    }

    protected Lock writeLock() {
        return this.writeLock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object get(SharedSessionContractImplementor session, Object key) {
        log.debugf("Getting cached data from region [`%s` (%s)] by key [%s]", (Object)this.getRegion().getName(), (Object)this.getAccessType(), key);
        try {
            this.readLock.lock();
            Lockable item = (Lockable)this.getStorageAccess().getFromCache(key, session);
            if (item == null) {
                log.debugf("Cache miss : region = `%s`, key = `%s`", (Object)this.getRegion().getName(), key);
                Object var4_4 = null;
                return var4_4;
            }
            boolean readable = item.isReadable(session.getTransactionStartTimestamp());
            if (readable) {
                log.debugf("Cache hit : region = `%s`, key = `%s`", (Object)this.getRegion().getName(), key);
                Object object = item.getValue();
                return object;
            }
            log.debugf("Cache hit, but item is unreadable/invalid : region = `%s`, key = `%s`", (Object)this.getRegion().getName(), key);
            Object var5_7 = null;
            return var5_7;
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        try {
            boolean writable;
            log.debugf("Caching data from load [region=`%s` (%s)] : key[%s] -> value[%s]", new Object[]{this.getRegion().getName(), this.getAccessType(), key, value});
            this.writeLock.lock();
            Lockable item = (Lockable)this.getStorageAccess().getFromCache(key, session);
            boolean bl = writable = item == null || item.isWriteable(session.getTransactionStartTimestamp(), version, this.getVersionComparator());
            if (writable) {
                this.getStorageAccess().putIntoCache(key, new Item(value, version, session.getTransactionStartTimestamp()), session);
                boolean bl2 = true;
                return bl2;
            }
            log.debugf("Cache put-from-load [region=`%s` (%s), key=`%s`, value=`%s`] failed due to being non-writable", new Object[]{this.getAccessType(), this.getRegion().getName(), key, value});
            boolean bl3 = false;
            return bl3;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    protected abstract AccessedDataClassification getAccessedDataClassification();

    @Override
    public final boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, Object version, boolean minimalPutOverride) {
        return this.putFromLoad(session, key, value, version);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) {
        try {
            this.writeLock.lock();
            long timeout = this.getRegion().getRegionFactory().nextTimestamp() + this.getRegion().getRegionFactory().getTimeout();
            log.debugf("Locking cache item [region=`%s` (%s)] : `%s` (timeout=%s, version=%s)", new Object[]{this.getRegion().getName(), this.getAccessType(), key, timeout, version});
            Lockable item = (Lockable)this.getStorageAccess().getFromCache(key, session);
            SoftLockImpl lock = item == null ? new SoftLockImpl(timeout, this.uuid, this.nextLockId(), version) : item.lock(timeout, this.uuid, this.nextLockId());
            this.getStorageAccess().putIntoCache(key, lock, session);
            SoftLockImpl softLockImpl = lock;
            return softLockImpl;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) {
        try {
            log.debugf("Unlocking cache item [region=`%s` (%s)] : %s", (Object)this.getRegion().getName(), (Object)this.getAccessType(), key);
            this.writeLock.lock();
            Lockable item = (Lockable)this.getStorageAccess().getFromCache(key, session);
            if (item != null && item.isUnlockable(lock)) {
                this.decrementLock(session, key, (SoftLockImpl)item);
            } else {
                this.handleLockExpiry(session, key, item);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    protected void decrementLock(SharedSessionContractImplementor session, Object key, SoftLockImpl lock) {
        lock.unlock(this.getRegion().getRegionFactory().nextTimestamp());
        this.getStorageAccess().putIntoCache(key, lock, session);
    }

    protected void handleLockExpiry(SharedSessionContractImplementor session, Object key, Lockable lock) {
        SecondLevelCacheLogger.INSTANCE.softLockedCacheExpired(this.getRegion().getName(), key);
        log.info((Object)("Cached entry expired : " + key));
        long ts = this.getRegion().getRegionFactory().nextTimestamp() + this.getRegion().getRegionFactory().getTimeout();
        SoftLockImpl newLock = new SoftLockImpl(ts, this.uuid, this.nextLockId.getAndIncrement(), null);
        newLock.unlock(ts - this.getRegion().getRegionFactory().getTimeout());
        this.getStorageAccess().putIntoCache(key, newLock, session);
    }

    @Override
    public void remove(SharedSessionContractImplementor session, Object key) {
        if (this.getStorageAccess().getFromCache(key, session) instanceof SoftLock) {
            log.debugf("Skipping #remove call in read-write access to maintain SoftLock : %s", key);
        } else {
            super.remove(session, key);
        }
    }

    @Override
    public void removeAll(SharedSessionContractImplementor session) {
    }

    public static class SoftLockImpl
    implements Serializable,
    Lockable,
    SoftLock {
        private static final long serialVersionUID = 2L;
        private final UUID sourceUuid;
        private final long lockId;
        private final Object version;
        private long timeout;
        private boolean concurrent;
        private int multiplicity = 1;
        private long unlockTimestamp;

        SoftLockImpl(long timeout, UUID sourceUuid, long lockId, Object version) {
            this.timeout = timeout;
            this.lockId = lockId;
            this.version = version;
            this.sourceUuid = sourceUuid;
        }

        @Override
        public boolean isReadable(long txTimestamp) {
            return false;
        }

        @Override
        public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
            if (log.isDebugEnabled()) {
                log.debugf("Checking writeability of read-write cache lock [timeout=`%s`, lockId=`%s`, version=`%s`, sourceUuid=%s, multiplicity=`%s`, unlockTimestamp=`%s`] : txTimestamp=`%s`, newVersion=`%s`", new Object[]{this.timeout, this.lockId, this.version, this.sourceUuid, this.multiplicity, this.unlockTimestamp, txTimestamp, newVersion});
            }
            if (txTimestamp > this.timeout) {
                return true;
            }
            if (this.multiplicity > 0) {
                return false;
            }
            return this.version == null ? txTimestamp > this.unlockTimestamp : versionComparator.compare(this.version, newVersion) < 0;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public boolean isUnlockable(SoftLock lock) {
            return this.equals(lock);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof SoftLockImpl) {
                return this.lockId == ((SoftLockImpl)o).lockId && this.sourceUuid.equals(((SoftLockImpl)o).sourceUuid);
            }
            return false;
        }

        public int hashCode() {
            int hash = this.sourceUuid != null ? this.sourceUuid.hashCode() : 0;
            int temp = (int)this.lockId;
            for (int i = 1; i < 2; ++i) {
                temp = (int)((long)temp ^ this.lockId >>> i * 32);
            }
            return hash + temp;
        }

        public boolean wasLockedConcurrently() {
            return this.concurrent;
        }

        @Override
        public SoftLockImpl lock(long timeout, UUID uuid, long lockId) {
            this.concurrent = true;
            ++this.multiplicity;
            this.timeout = timeout;
            return this;
        }

        public void unlock(long timestamp) {
            if (--this.multiplicity == 0) {
                this.unlockTimestamp = timestamp;
            }
        }

        public String toString() {
            return "Lock Source-UUID:" + this.sourceUuid + " Lock-ID:" + this.lockId;
        }
    }

    public static final class Item
    implements Serializable,
    Lockable {
        private static final long serialVersionUID = 1L;
        private final Object value;
        private final Object version;
        private final long timestamp;

        Item(Object value, Object version, long timestamp) {
            this.value = value;
            this.version = version;
            this.timestamp = timestamp;
        }

        @Override
        public boolean isReadable(long txTimestamp) {
            if (log.isDebugEnabled()) {
                log.debugf("Checking readability of read-write cache item [timestamp=`%s`, version=`%s`] : txTimestamp=`%s`", (Object)this.timestamp, this.version, (Object)txTimestamp);
            }
            return txTimestamp > this.timestamp;
        }

        @Override
        public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
            if (log.isDebugEnabled()) {
                log.debugf("Checking writeability of read-write cache item [timestamp=`%s`, version=`%s`] : txTimestamp=`%s`, newVersion=`%s`", new Object[]{this.timestamp, this.version, txTimestamp, newVersion});
            }
            return this.version != null && versionComparator.compare(this.version, newVersion) < 0;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public boolean isUnlockable(SoftLock lock) {
            return false;
        }

        @Override
        public SoftLockImpl lock(long timeout, UUID uuid, long lockId) {
            return new SoftLockImpl(timeout, uuid, lockId, this.version);
        }

        public String toString() {
            return String.format(Locale.ROOT, "read-write Item(%s)", this.getValue());
        }
    }

    public static interface Lockable {
        public boolean isReadable(long var1);

        public boolean isWriteable(long var1, Object var3, Comparator var4);

        public Object getValue();

        public boolean isUnlockable(SoftLock var1);

        public SoftLockImpl lock(long var1, UUID var3, long var4);
    }
}

