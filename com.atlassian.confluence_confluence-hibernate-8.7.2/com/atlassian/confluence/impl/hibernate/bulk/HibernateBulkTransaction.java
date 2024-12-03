/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.LockMode
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.resource.transaction.spi.TransactionStatus
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate.bulk;

import com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction;
import java.util.Objects;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateBulkTransaction
implements BulkTransaction {
    private static final Logger log = LoggerFactory.getLogger(HibernateBulkTransaction.class);
    private final SessionFactory sessionFactory;
    private Session session;

    public HibernateBulkTransaction(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
    }

    @Override
    public boolean shouldStartTransaction() {
        return this.session == null || this.session.getTransaction().getStatus() == TransactionStatus.COMMITTED;
    }

    @Override
    public <T> boolean beginTransaction(T ... reattachObjects) {
        this.session = this.sessionFactory.getCurrentSession();
        this.clearSession(reattachObjects);
        if (this.session.getTransaction().getStatus() != TransactionStatus.ACTIVE) {
            this.session.beginTransaction();
        }
        return true;
    }

    @Override
    public <T> boolean rollbackTransaciton() {
        this.clearSession(new Object[0]);
        if (this.session.getTransaction() == null) {
            log.error("Could not rollback transaction because it is null");
            return false;
        }
        this.session.getTransaction().rollback();
        return true;
    }

    @Override
    public boolean commitTransaciton() {
        if (this.session.getTransaction() == null) {
            log.error("Could not commit transaction because it is null");
            return false;
        }
        this.session.getTransaction().commit();
        this.clearSession(new Object[0]);
        return this.beginTransaction(new Object[0]);
    }

    private <T> void clearSession(T ... reattachObjects) {
        if (this.session.isDirty()) {
            this.session.flush();
        }
        this.session.clear();
        for (T objectToRefresh : reattachObjects) {
            this.session.refresh(objectToRefresh, LockMode.NONE);
        }
    }
}

