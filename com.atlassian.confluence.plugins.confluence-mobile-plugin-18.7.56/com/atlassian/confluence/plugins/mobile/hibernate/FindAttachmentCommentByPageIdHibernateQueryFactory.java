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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindAttachmentCommentByPageIdHibernateQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Query query = entityManager.createQuery("select comment from Comment comment where comment.parent is null and comment.containerContent.id in (:contentIds)");
        query.setParameter("contentIds", (Object)((List)parameters[0]));
        return query;
    }
}

