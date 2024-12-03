/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.mail.archive.content.hibernate;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindPreviousInSpaceHibernateQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Long spaceId = (Long)parameters[0];
        Long mailId = (Long)parameters[1];
        Query query = entityManager.createQuery("from CustomContentEntityObject content where content.originalVersion is null and content.space.id = :spaceId and content.contentStatus = 'current' and  content.id < :contentId  order by content.creationDate desc");
        query.setParameter("spaceId", (Object)spaceId);
        query.setParameter("contentId", (Object)mailId);
        return query;
    }
}

