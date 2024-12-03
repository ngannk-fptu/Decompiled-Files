/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ManagedLock$ReadWrite
 *  io.atlassian.util.concurrent.ManagedLocks
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager.store;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.util.EnumUtils;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ManagedLock;
import io.atlassian.util.concurrent.ManagedLocks;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedPluginPersistentStateStore
implements PluginPersistentStateStore {
    private static final Logger log = LoggerFactory.getLogger(SynchronizedPluginPersistentStateStore.class);
    private final PluginPersistentStateStore delegate;
    private final ManagedLock.ReadWrite lock;
    private final AtomicInteger loadConcurrency = new AtomicInteger(0);
    private final AtomicInteger saveConcurrency = new AtomicInteger(0);

    @Internal
    public static String getLockModeProperty() {
        return LockMode.PROPERTY_NAME;
    }

    public SynchronizedPluginPersistentStateStore(PluginPersistentStateStore delegate) {
        this(delegate, LockMode.current().getReadWriteLock());
    }

    public SynchronizedPluginPersistentStateStore(PluginPersistentStateStore delegate, ReadWriteLock lock) {
        this.delegate = (PluginPersistentStateStore)Preconditions.checkNotNull((Object)delegate);
        this.lock = ManagedLocks.manageReadWrite((ReadWriteLock)((ReadWriteLock)Preconditions.checkNotNull((Object)lock)));
    }

    @Override
    public void save(PluginPersistentState state) {
        int writes = this.saveConcurrency.incrementAndGet();
        log.debug("save concurrency {}", (Object)writes);
        this.lock.write().withLock(() -> this.delegate.save(state));
        this.saveConcurrency.decrementAndGet();
    }

    @Override
    public PluginPersistentState load() {
        int reads = this.loadConcurrency.incrementAndGet();
        log.debug("load concurrency {}", (Object)reads);
        PluginPersistentState pluginPersistentState = (PluginPersistentState)this.lock.read().withLock(this.delegate::load);
        this.loadConcurrency.decrementAndGet();
        return pluginPersistentState;
    }

    static class CommonReadWriteLock
    implements ReadWriteLock {
        private final Lock lock;

        public CommonReadWriteLock(Lock lock) {
            this.lock = lock;
        }

        @Override
        public Lock readLock() {
            return this.lock;
        }

        @Override
        public Lock writeLock() {
            return this.lock;
        }
    }

    static class NoOpLock
    implements Lock {
        NoOpLock() {
        }

        @Override
        public void lock() {
        }

        @Override
        public void lockInterruptibly() {
        }

        @Override
        public boolean tryLock() {
            return true;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) {
            return true;
        }

        @Override
        public void unlock() {
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    @Internal
    public static enum LockMode {
        UNLOCKED{

            @Override
            ReadWriteLock getReadWriteLock() {
                return new CommonReadWriteLock(new NoOpLock());
            }
        }
        ,
        SIMPLE{

            @Override
            ReadWriteLock getReadWriteLock() {
                return new CommonReadWriteLock(new ReentrantLock());
            }
        }
        ,
        FAIRSIMPLE{

            @Override
            ReadWriteLock getReadWriteLock() {
                return new CommonReadWriteLock(new ReentrantLock(true));
            }
        }
        ,
        READWRITE{

            @Override
            ReadWriteLock getReadWriteLock() {
                return new ReentrantReadWriteLock();
            }
        }
        ,
        FAIRREADWRITE{

            @Override
            ReadWriteLock getReadWriteLock() {
                return new ReentrantReadWriteLock(true);
            }
        };

        private static final String PROPERTY_NAME;

        static LockMode current() {
            return (LockMode)EnumUtils.enumValueFromProperty((String)PROPERTY_NAME, (Enum[])LockMode.values(), (Enum)READWRITE);
        }

        abstract ReadWriteLock getReadWriteLock();

        static {
            PROPERTY_NAME = SynchronizedPluginPersistentStateStore.class.getName() + ".lockMode";
        }
    }
}

