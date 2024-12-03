/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.PostConstruct
 *  org.hibernate.LockMode
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.manage;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedLock;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.ThreadFactories;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.PostConstruct;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class DenormalisedLockService
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedLockService.class);
    private final EventPublisher eventPublisher;
    private final HibernateTemplate hibernateTemplate;
    private final PlatformTransactionManager transactionManager;
    private final ExecutorService executor = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName(), (ThreadFactories.Type)ThreadFactories.Type.DAEMON));

    public DenormalisedLockService(EventPublisher eventPublisher, SessionFactory sessionFactory, PlatformTransactionManager transactionManager) {
        this.eventPublisher = eventPublisher;
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.transactionManager = transactionManager;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    public void acquireLockForTransaction(LockName lockName) {
        DenormalisedLock lockRecord = (DenormalisedLock)this.hibernateTemplate.get(DenormalisedLock.class, (Serializable)((Object)lockName.name()), LockMode.PESSIMISTIC_WRITE);
        if (lockRecord == null) {
            this.createRecord(lockName);
            log.info("New lock record was created on request: {}", (Object)lockName);
        }
    }

    private void createRecord(LockName lockName) {
        DenormalisedLock lock = new DenormalisedLock();
        lock.setLockName(lockName.name());
        this.hibernateTemplate.save((Object)lock);
    }

    public DenormalisedLock getRecord(LockName lockName) {
        return (DenormalisedLock)this.hibernateTemplate.get(DenormalisedLock.class, (Serializable)((Object)lockName.name()));
    }

    @EventListener
    public void onApplicationStarted(ApplicationStartedEvent applicationStartedEvent) {
        try {
            this.createAllLockRecordsIfTheyDoNotExist();
        }
        catch (Exception e) {
            log.error("Unable to create all lock records: " + e.getMessage(), (Throwable)e);
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
        this.executor.shutdown();
    }

    private void createAllLockRecordsIfTheyDoNotExist() throws ExecutionException, InterruptedException {
        DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
        Future<Void> future = this.executor.submit(() -> this.lambda$createAllLockRecordsIfTheyDoNotExist$1((TransactionDefinition)transactionDefinition));
        future.get();
    }

    private /* synthetic */ Void lambda$createAllLockRecordsIfTheyDoNotExist$1(TransactionDefinition transactionDefinition) throws Exception {
        new TransactionTemplate(this.transactionManager, transactionDefinition).execute(status -> {
            for (LockName lockName : LockName.values()) {
                if (this.getRecord(lockName) != null) continue;
                this.createRecord(lockName);
                log.debug("New lock record was created: {}", (Object)lockName);
            }
            return null;
        });
        return null;
    }

    public static enum LockName {
        SPACE_STATUS,
        SPACE_LOG_PROCESSOR,
        CONTENT_STATUS,
        CONTENT_LOG_PROCESSOR,
        UPDATE_SIDS;

    }
}

