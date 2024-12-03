/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.pagetree;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class PageAncestorsBulkQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Set pageIds = Arrays.stream(parameters).map(Long.class::cast).collect(Collectors.toSet());
        return entityManager.createQuery("select p from Page p left join fetch p.ancestors where p.id in (:pageIds)").setParameter("pageIds", pageIds);
    }
}

