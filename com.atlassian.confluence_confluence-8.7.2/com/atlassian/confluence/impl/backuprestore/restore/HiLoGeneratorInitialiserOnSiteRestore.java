/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.ResettableTableHiLoGenerator
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.persister.entity.EntityPersister
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.event.events.admin.ResetHibernateIdRangeEvent;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.hibernate.ResettableTableHiLoGenerator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiLoGeneratorInitialiserOnSiteRestore {
    private static final Logger log = LoggerFactory.getLogger(HiLoGeneratorInitialiserOnSiteRestore.class);
    private final AtomicLong maxId = new AtomicLong();
    private final SessionFactoryImplementor sessionFactory;
    private final EventPublisher eventPublisher;
    private final RestoreDao restoreDao;

    public HiLoGeneratorInitialiserOnSiteRestore(EventPublisher eventPublisher, SessionFactory sessionFactory, RestoreDao restoreDao) {
        this.eventPublisher = eventPublisher;
        this.sessionFactory = (SessionFactoryImplementor)sessionFactory;
        this.restoreDao = restoreDao;
    }

    public void registerNewId(Object id) {
        Long currentLongValue = HiLoGeneratorInitialiserOnSiteRestore.convertToLongIfItHasNumericValue(id);
        while (currentLongValue != null && !this.updateValue(currentLongValue)) {
        }
    }

    private boolean updateValue(Long currentLongValue) {
        long currentMaxId = this.maxId.get();
        if (currentLongValue > currentMaxId) {
            return this.maxId.compareAndSet(currentMaxId, currentLongValue);
        }
        return true;
    }

    public void updateHiLoIdGenerator() throws BackupRestoreException {
        try {
            log.debug("Start resetting hibernate ID generators.");
            int nextHi = (int)(this.maxId.get() / (this.getLo() + 1L)) + 1;
            Long previousHiValue = this.restoreDao.getNextHiValue();
            if (previousHiValue > (long)nextHi) {
                log.warn("New hi value ({}) is not set because the existing value ({}) is greater than the new value.", (Object)nextHi, (Object)previousHiValue);
                return;
            }
            log.debug("Updating hibernate HiLo identifier table. Setting next_hi to {}", (Object)nextHi);
            this.restoreDao.setNextHiValue(nextHi);
            this.eventPublisher.publish((Object)new ResetHibernateIdRangeEvent(this));
            log.debug("Hibernate ID generators have been updated.");
        }
        catch (Exception e) {
            throw new BackupRestoreException("Failed to reset hibernate ID generators: " + e.getMessage());
        }
        finally {
            this.resetMaxValue();
        }
    }

    private long getLo() {
        EntityPersister persister = this.sessionFactory.getMetamodel().entityPersister(Page.class);
        ResettableTableHiLoGenerator generator = (ResettableTableHiLoGenerator)persister.getIdentifierGenerator();
        return generator.getMaxLo();
    }

    private void resetMaxValue() {
        this.maxId.set(0L);
    }

    public static Long convertToLongIfItHasNumericValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).longValue();
        }
        if (value instanceof Long) {
            return (Long)value;
        }
        return null;
    }

    @VisibleForTesting
    public long getMaxId() {
        return this.maxId.get();
    }
}

