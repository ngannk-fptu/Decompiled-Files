/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  com.google.common.base.Preconditions
 *  javax.persistence.EntityManager
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.contentproperty.hibernate2;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import com.google.common.base.Preconditions;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FindByContentIdAndKeyHibernateQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) {
        Long contentId = (Long)Preconditions.checkNotNull((Object)((Long)parameters[0]));
        String key = (String)Preconditions.checkNotNull((Object)((String)parameters[1]));
        Query query = entityManager.createQuery("select content from CustomContentEntityObject content where content.originalVersion is null and     content.contentStatus = 'current' and     content.pluginModuleKey = :pluginModuleKey and     content.containerContent.id = :contentId and     content.title = :key");
        query.setParameter("pluginModuleKey", (Object)"com.atlassian.confluence.plugins.confluence-content-property-storage:content-property");
        query.setParameter("contentId", (Object)contentId);
        query.setParameter("key", (Object)key);
        return query;
    }
}

