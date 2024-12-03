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
package com.atlassian.confluence.plugins.hipchat.emoticons.content.query;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import com.google.common.base.Preconditions;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindCustomEmoticonQueryByShortcut
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        List emoticonShortcut = (List)Preconditions.checkNotNull((Object)((List)parameters[0]));
        Query query = entityManager.createQuery("select content from CustomContentEntityObject content join content.contentProperties contentProperty where content.originalVersion is null and     contentProperty.stringValue in (:emoticonShortcut) and     content.contentStatus = 'current' and     contentProperty.name = :emoticonShortcutPropName and    content.pluginModuleKey = :pluginModuleKey");
        query.setParameter("emoticonShortcutPropName", (Object)"emoticon-shortcut");
        query.setParameter("pluginModuleKey", (Object)"com.atlassian.confluence.plugins.confluence-emoticons-plugin:custom-emoticon");
        query.setParameter("emoticonShortcut", (Object)emoticonShortcut);
        return query;
    }
}

