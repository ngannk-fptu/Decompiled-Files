/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugin.copyspace.hibernate;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindTotalAttachmentsCountQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        return entityManager.createQuery("select CAST(count(*) AS integer) from Attachment att where (att.containerContent.space.key = :spaceKey) and att.contentStatus = 'current' and att.originalVersion is null").setParameter("spaceKey", parameters[0]);
    }
}

