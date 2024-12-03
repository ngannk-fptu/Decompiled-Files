/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.hibernate;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateSessionManager5 {
    private static final TransactionDefinition REQUIRES_NEW_TRANSACTION = new DefaultTransactionAttribute(3);
    private static final Logger log = LoggerFactory.getLogger(HibernateSessionManager5.class);
    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager transactionManager;

    public HibernateSessionManager5(SessionFactory sessionFactory, PlatformTransactionManager transactionManager) {
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
    }

    public <I, O> Iterable<O> executeThenClearSession(Iterable<I> input, int batchSize, int expectedTotal, Function<I, O> task) {
        this.sessionFactory.getCurrentSession().clear();
        AtomicInteger count = new AtomicInteger();
        ArrayList result = expectedTotal > 0 ? new ArrayList(expectedTotal) : new ArrayList();
        for (List batch : Iterables.partition(input, (int)batchSize)) {
            new TransactionTemplate(this.transactionManager, REQUIRES_NEW_TRANSACTION).execute(status -> {
                for (Object i : batch) {
                    result.add(task.apply(i));
                    count.getAndIncrement();
                }
                return null;
            });
            if (expectedTotal > 0) {
                log.info("Processed {} of {} in '{}'...", new Object[]{count.get(), expectedTotal, task});
            } else {
                log.info("Processed {} elements in '{}'", (Object)count.get(), task);
            }
            this.sessionFactory.getCurrentSession().clear();
        }
        return result;
    }

    public <I, O> Iterable<O> executeThenFlushAndClearSession(Iterable<I> input, int batchSize, int expectedTotal, Function<I, O> task) {
        return this.execute(input, batchSize, expectedTotal, task, HibernateSessionManager5::flushAndClear);
    }

    public <I, O> Iterable<O> executeThenClearSessionWithoutCommitOrFlush(Iterable<I> input, int batchSize, int expectedTotal, Function<I, O> task) {
        return this.execute(input, batchSize, expectedTotal, task, Session::clear);
    }

    private <I, O> List<O> execute(Iterable<I> input, int batchSize, int expectedTotal, Function<I, O> task, Consumer<Session> performAfterEveryBatch) {
        this.sessionFactory.getCurrentSession().clear();
        AtomicInteger count = new AtomicInteger();
        ArrayList<O> result = expectedTotal > 0 ? new ArrayList<O>(expectedTotal) : new ArrayList();
        for (List batch : Iterables.partition(input, (int)batchSize)) {
            for (Object i : batch) {
                result.add(task.apply(i));
                count.getAndIncrement();
            }
            if (expectedTotal > 0) {
                log.info("Processed {} of {} in '{}'...", new Object[]{count.get(), expectedTotal, task});
            } else {
                log.info("Processed {} elements in '{}'", (Object)count.get(), task);
            }
            performAfterEveryBatch.accept(this.sessionFactory.getCurrentSession());
        }
        return result;
    }

    public int executeThenClearSessionWithoutCommitOrFlush(int batchSize, int expectedTotal, Function<Integer, Integer> taskExecutor) {
        this.sessionFactory.getCurrentSession().clear();
        int totalTasksExecuted = 0;
        int totalTasksCompleted = 0;
        while (totalTasksExecuted < expectedTotal) {
            int totalTasksRemaining = expectedTotal - totalTasksExecuted;
            int clearSessionBatchSize = Math.min(totalTasksRemaining, batchSize);
            totalTasksCompleted += taskExecutor.apply(clearSessionBatchSize).intValue();
            this.sessionFactory.getCurrentSession().clear();
            log.debug("Processed {} of {} in '{}'...", new Object[]{totalTasksExecuted += clearSessionBatchSize, expectedTotal, taskExecutor});
        }
        return totalTasksCompleted;
    }

    private static void flushAndClear(Session session) {
        try {
            session.flush();
            session.clear();
        }
        catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean withNewTransaction(Callable<Boolean> callable) {
        return Optional.ofNullable((Boolean)this.getTransactionTemplate().execute(status -> {
            try {
                return (Boolean)callable.call();
            }
            catch (Exception e) {
                return false;
            }
        })).orElse(false);
    }

    public TransactionTemplate getTransactionTemplate() {
        return new TransactionTemplate(this.transactionManager, REQUIRES_NEW_TRANSACTION);
    }
}

