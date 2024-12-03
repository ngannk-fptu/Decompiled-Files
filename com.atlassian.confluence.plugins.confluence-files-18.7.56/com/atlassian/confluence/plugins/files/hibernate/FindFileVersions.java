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

public class FindFileVersions
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Long attachmentId = (Long)Preconditions.checkNotNull((Object)((Long)parameters[0]));
        Query query = entityManager.createQuery("select attachment.id, attachment.originalVersion.id, attachment.version, attachment.title, attachment.lastModificationDate, attachment.versionComment from Attachment attachment where (attachment.originalVersion.id = :attachmentId or attachment.id = :attachmentId) order by attachment.version desc");
        query.setParameter("attachmentId", (Object)attachmentId);
        return query;
    }
}

