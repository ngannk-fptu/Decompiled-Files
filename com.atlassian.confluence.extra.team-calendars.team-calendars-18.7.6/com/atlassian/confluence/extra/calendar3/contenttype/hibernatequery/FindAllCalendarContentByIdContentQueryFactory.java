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
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindAllCalendarContentByIdContentQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        List calendarIds = (List)parameters[0];
        Query query = entityManager.createQuery("select content from CustomContentEntityObject content inner join content.contentProperties as props where content.originalVersion is null and content.pluginModuleKey = :pluginModuleKey and props.name = :subcalendarIdProperty and props.stringValue in (:calendarIds) ");
        query.setParameter("pluginModuleKey", (Object)"com.atlassian.confluence.extra.team-calendars:calendar-content-type");
        query.setParameter("subcalendarIdProperty", (Object)"subCalendarId");
        String calendarIdsConcat = calendarIds.stream().collect(Collectors.joining("','", "'", "'"));
        query.setParameter("calendarIds", (Object)calendarIdsConcat);
        return query;
    }
}

