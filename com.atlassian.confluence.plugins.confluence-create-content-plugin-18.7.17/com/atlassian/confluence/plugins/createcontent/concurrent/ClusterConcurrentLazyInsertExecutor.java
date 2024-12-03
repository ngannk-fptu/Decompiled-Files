/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.concurrent;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.plugins.createcontent.concurrent.LazyInsertExecutor;
import com.atlassian.confluence.plugins.createcontent.concurrent.LazyInserter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClusterConcurrentLazyInsertExecutor
implements LazyInsertExecutor {
    private static final Logger log = LoggerFactory.getLogger(ClusterConcurrentLazyInsertExecutor.class);
    private final ClusterLockService lockService;

    @Autowired
    public ClusterConcurrentLazyInsertExecutor(@ComponentImport ClusterLockService lockService) {
        this.lockService = lockService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T lazyInsertAndRead(LazyInserter<T> inserter, String lockKey) {
        T result = inserter.read();
        if (result != null) {
            return result;
        }
        ClusterLock lock = this.lockService.getLockForName(lockKey);
        lock.lock();
        try {
            result = inserter.read();
            if (result != null) {
                log.debug("Read empty, but some other threads/instances inserted data while waiting for lock.");
                T t = result;
                return t;
            }
            log.debug("Read empty, re-read empty and insert data inside a lock.");
            T t = inserter.insert();
            return t;
        }
        finally {
            lock.unlock();
        }
    }
}

