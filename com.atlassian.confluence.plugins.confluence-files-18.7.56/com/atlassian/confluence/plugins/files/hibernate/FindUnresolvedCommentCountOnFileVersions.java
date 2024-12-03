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

public class FindUnresolvedCommentCountOnFileVersions
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Long attachmentId = (Long)Preconditions.checkNotNull((Object)((Long)parameters[0]));
        Query query = entityManager.createQuery("select attachment.version, count(theComments.id) from Attachment attachment left outer join attachment.comments as theComments left join theComments.contentProperties as cp where (attachment.originalVersion.id = :attachmentId or attachment.id = :attachmentId) and (theComments is null or (theComments.parent is null and cp.name = :propertyName and cp.stringValue = :propertyValue ))group by attachment.id, attachment.version order by attachment.version desc");
        query.setParameter("attachmentId", (Object)attachmentId);
        query.setParameter("propertyName", (Object)"status");
        query.setParameter("propertyValue", (Object)"open");
        return query;
    }
}

