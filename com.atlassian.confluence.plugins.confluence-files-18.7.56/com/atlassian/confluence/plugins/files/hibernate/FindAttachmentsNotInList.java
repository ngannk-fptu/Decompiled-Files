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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindAttachmentsNotInList
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        long pageId = (Long)Preconditions.checkNotNull((Object)((Long)parameters[0]));
        List ids = (List)Preconditions.checkNotNull((Object)((List)parameters[1]));
        Query query = entityManager.createQuery("FROM Attachment attachment\nWHERE attachment.containerContent.id = :pageId and attachment.originalVersion is null and attachment.id not in (:ids) and attachment.contentStatus = 'current'\nORDER BY attachment.id");
        query.setParameter("pageId", (Object)pageId);
        query.setParameter("ids", (Object)ids);
        return query;
    }
}

