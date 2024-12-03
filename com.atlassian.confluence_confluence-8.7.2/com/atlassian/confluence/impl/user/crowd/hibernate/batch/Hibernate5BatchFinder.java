/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.util.persistence.hibernate.batch.AbstractBatchFinder
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.batch;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.util.persistence.hibernate.batch.AbstractBatchFinder;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Hibernate5BatchFinder
extends AbstractBatchFinder {
    private static final ThreadLocal<Session> currentSessionHolder = new ThreadLocal();
    private final SessionFactory sessionFactory;

    public Hibernate5BatchFinder(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected void beforeFind() {
        currentSessionHolder.set(this.sessionFactory.openSession());
    }

    protected void afterFind() {
        try {
            currentSessionHolder.get().close();
        }
        finally {
            currentSessionHolder.set(null);
        }
    }

    protected <E> Collection<E> processBatchFind(long directoryID, Collection<String> names, Class<E> persistentClass) {
        Collection lowercaseNames = Collections2.transform(names, (Function)IdentifierUtils.TO_LOWER_CASE);
        CriteriaQuery<E> criteriaQuery = Hibernate5BatchFinder.createCriteriaQuery(persistentClass, directoryID, lowercaseNames);
        List entities = currentSessionHolder.get().createQuery(criteriaQuery).setCacheable(true).list();
        for (Object entity : entities) {
            Hibernate.initialize(entity);
        }
        return entities;
    }

    private static <E> CriteriaQuery<E> createCriteriaQuery(Class<E> persistentClass, long directoryID, Collection<String> lowercaseNames) {
        CriteriaBuilder builder = currentSessionHolder.get().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = builder.createQuery(persistentClass);
        Root from = criteriaQuery.from(persistentClass);
        criteriaQuery.where(new Predicate[]{builder.equal((Expression)from.get("directory").get("id"), (Object)directoryID), from.get("lowerName").in(lowercaseNames)});
        return criteriaQuery;
    }
}

