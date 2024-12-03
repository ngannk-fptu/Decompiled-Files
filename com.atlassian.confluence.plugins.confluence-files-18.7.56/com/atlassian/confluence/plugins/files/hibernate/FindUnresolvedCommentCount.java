/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  com.google.common.base.Preconditions
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.files.hibernate;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import com.google.common.base.Preconditions;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindUnresolvedCommentCount
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Long attachmentId = (Long)Preconditions.checkNotNull((Object)((Long)parameters[0]));
        Query query = entityManager.createQuery("select count(comments.id) from Attachment attachment left join attachment.comments as comments left outer join comments.contentProperties as cp where attachment.id = :attachmentId and comments.parent is null and (cp is null or (cp.name = :propertyName and cp.stringValue = :propertyValue ))");
        query.setParameter("attachmentId", (Object)attachmentId);
        query.setParameter("propertyName", (Object)"status");
        query.setParameter("propertyValue", (Object)"open");
        return query;
    }
}

