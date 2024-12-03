/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.extra.calendar3.contenttype.hibernatequery;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindCalendarContentBySpaceKeyContentQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        String spaceKey = (String)parameters[0];
        Query query = entityManager.createQuery("select content from CustomContentEntityObject content inner join content.contentProperties as props where content.originalVersion is null and content.pluginModuleKey = :pluginModuleKey and props.name = 'spaceKey' and props.stringValue = :spaceKey ");
        query.setParameter("pluginModuleKey", (Object)"com.atlassian.confluence.extra.team-calendars:space-calendars-view-content-type");
        query.setParameter("spaceKey", (Object)spaceKey);
        return query;
    }
}

