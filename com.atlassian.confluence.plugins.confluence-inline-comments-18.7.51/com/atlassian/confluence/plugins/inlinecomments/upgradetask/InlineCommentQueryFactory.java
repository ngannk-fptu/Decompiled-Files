/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.inlinecomments.upgradetask;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class InlineCommentQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) {
        return entityManager.createQuery("select comment from Comment comment left outer join comment.contentProperties as cp where cp.name = 'resolved'");
    }
}

