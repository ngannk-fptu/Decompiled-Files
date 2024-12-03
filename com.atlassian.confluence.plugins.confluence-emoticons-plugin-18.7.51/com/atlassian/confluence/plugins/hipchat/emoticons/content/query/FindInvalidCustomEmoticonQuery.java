/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.content.query;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindInvalidCustomEmoticonQuery
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... objects) throws PersistenceException {
        Query query = entityManager.createQuery("select invalidContent from CustomContentEntityObject invalidContent where invalidContent.id not in (select content.id from CustomContentEntityObject content join content.contentProperties contentProperty where content.originalVersion is null and     contentProperty.name = :emoticonShortcutPropName and     content.contentStatus = 'current' and     content.pluginModuleKey = :pluginModuleKey) and invalidContent.contentStatus = 'current' and invalidContent.pluginModuleKey = :pluginModuleKey");
        query.setParameter("emoticonShortcutPropName", (Object)"emoticon-shortcut");
        query.setParameter("pluginModuleKey", (Object)"com.atlassian.confluence.plugins.confluence-emoticons-plugin:custom-emoticon");
        return query;
    }
}

