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

public class FindAllCustomEmoticonQuery
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Query query = entityManager.createQuery("select content from CustomContentEntityObject content join content.contentProperties contentProperty where content.originalVersion is null and     content.contentStatus = 'current' and     content.pluginModuleKey = :pluginModuleKey and     contentProperty.name = :emoticonShortcutPropName order by content.creationDate asc");
        query.setParameter("emoticonShortcutPropName", (Object)"emoticon-shortcut");
        query.setParameter("pluginModuleKey", (Object)"com.atlassian.confluence.plugins.confluence-emoticons-plugin:custom-emoticon");
        return query;
    }
}

