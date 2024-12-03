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

public class FindInSpaceByMessageIdQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        long spaceId = (Long)parameters[0];
        String messageId = (String)parameters[1];
        Query query = entityManager.createQuery("from CustomContentEntityObject content left join content.contentProperties as props where content.originalVersion is null and content.space.id = :spaceId and props.name = 'messageId' and props.stringValue = :messageId ");
        query.setParameter("spaceId", (Object)spaceId);
        query.setParameter("messageId", (Object)messageId);
        return query;
    }
}

