/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.audit.AuditLogChangesetEntity
 *  com.atlassian.crowd.util.persistence.hibernate.batch.AbstractBatchProcessor
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.Transaction
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.batch;

import com.atlassian.crowd.model.audit.AuditLogChangesetEntity;
import com.atlassian.crowd.util.persistence.hibernate.batch.AbstractBatchProcessor;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hibernate5BatchProcessor
extends AbstractBatchProcessor<Session> {
    private static final boolean INDIVIDUAL_PROCESSING_ENABLED = true;
    private static final ThreadLocal<Session> currentSessionHolder = new ThreadLocal();
    private static final Logger logger = LoggerFactory.getLogger(Hibernate5BatchProcessor.class);
    private final SessionFactory sessionFactory;

    public Hibernate5BatchProcessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected void beforeProcessCollection() {
        this.openSession();
    }

    protected void afterProcessCollection() {
        this.releaseSession();
    }

    protected void beforeProcessBatch() {
        this.beginTransaction();
    }

    protected void afterProcessBatch() {
        this.commitTransaction();
    }

    protected void rollbackProcessBatch() {
        this.rollbackTransaction();
    }

    protected void beforeProcessIndividual() {
        this.beginTransaction();
    }

    protected void afterProcessIndividual() {
        this.commitTransaction();
    }

    protected void rollbackProcessIndividual() {
        this.rollbackTransaction();
    }

    private void openSession() {
        Session session = this.sessionFactory.openSession();
        logger.debug("using session [ {} ]", (Object)session);
        currentSessionHolder.set(session);
    }

    private void releaseSession() {
        Session session = this.getSession();
        try {
            logger.debug("closing session [ {} ]", (Object)session);
            session.close();
            currentSessionHolder.set(this.sessionFactory.getCurrentSession());
        }
        catch (HibernateException e) {
            throw new RuntimeException("could not close session", e);
        }
    }

    public Session getSession() {
        Session session = currentSessionHolder.get();
        if (session == null) {
            throw new IllegalStateException("no session available");
        }
        return session;
    }

    protected void auditOperations(List<AuditLogChangesetEntity> changesetEntities) {
    }

    private void clearSession() {
        Session session = this.getSession();
        logger.debug("clear session [ {} ]", (Object)session);
        session.clear();
    }

    private void flushSession() {
        Session session = this.getSession();
        logger.debug("flush session [ {} ]", (Object)session);
        try {
            session.flush();
        }
        catch (HibernateException e) {
            throw new RuntimeException("could not flush session", e);
        }
    }

    private void beginTransaction() {
        try {
            Session session = this.getSession();
            if (!session.getTransaction().isActive()) {
                session.beginTransaction();
                logger.debug("begin transaction [ {} ]", (Object)session.getTransaction());
            }
        }
        catch (HibernateException e) {
            throw new RuntimeException("could not start transaction", e);
        }
    }

    private void commitTransaction() {
        Transaction transaction = this.getSession().getTransaction();
        if (transaction.isActive()) {
            logger.debug("commit transaction [ {} ]", (Object)transaction);
            this.flushSession();
            try {
                transaction.commit();
            }
            catch (HibernateException e) {
                throw new RuntimeException("could not commit transaction", e);
            }
            this.clearSession();
        }
    }

    private void rollbackTransaction() {
        Transaction transaction = this.getSession().getTransaction();
        if (transaction.isActive()) {
            try {
                transaction.rollback();
            }
            catch (HibernateException e) {
                throw new RuntimeException("could not commit transaction", e);
            }
            this.clearSession();
        }
    }
}

