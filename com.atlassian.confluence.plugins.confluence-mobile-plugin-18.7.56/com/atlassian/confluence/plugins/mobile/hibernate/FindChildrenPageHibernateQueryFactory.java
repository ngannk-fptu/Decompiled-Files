/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.mobile.hibernate;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindChildrenPageHibernateQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Query query = entityManager.createQuery("select page from Page page where page.parent.id = :parentId and page.originalVersion is null and page.contentStatus = 'current' order by (case when page.position is null then 1 else 0 end), page.position, page.title");
        query.setParameter("parentId", parameters[0]);
        return query;
    }
}

