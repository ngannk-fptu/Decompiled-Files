/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionException
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.event.service.spi.EventListenerGroup
 *  org.hibernate.event.service.spi.EventListenerRegistry
 *  org.hibernate.event.spi.EventSource
 *  org.hibernate.event.spi.EventType
 *  org.hibernate.event.spi.SaveOrUpdateEvent
 *  org.hibernate.event.spi.SaveOrUpdateEventListener
 *  org.hibernate.internal.SessionImpl
 *  org.hibernate.query.NativeQuery
 *  org.hibernate.query.Query
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.core.persistence.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.Type;

public final class SessionHelper {
    private SessionHelper() {
    }

    public static void save(Session session, Object object, Serializable id) throws HibernateException {
        if (!(session instanceof SessionImpl)) {
            throw new IllegalArgumentException("Save with identifier requires a real Session object (org.hibernate.internal.SessionImpl)");
        }
        if (object != null && id != null) {
            SessionHelper.fireSave(session, new SaveOrUpdateEvent(null, object, id, (EventSource)session));
        } else {
            session.save(object);
        }
    }

    private static Serializable fireSave(Session session, SaveOrUpdateEvent event) {
        SessionImplementor sessionImplementor = (SessionImplementor)session;
        SessionHelper.errorIfClosed(sessionImplementor);
        SessionHelper.checkNoUnresolvedActionsBeforeOperation(sessionImplementor);
        for (SaveOrUpdateEventListener listener : SessionHelper.eventListenerGroup(session, EventType.SAVE).listeners()) {
            listener.onSaveOrUpdate(event);
        }
        SessionHelper.checkNoUnresolvedActionsAfterOperation(sessionImplementor);
        return event.getResultId();
    }

    private static void errorIfClosed(SessionImplementor sessionImplementor) {
        if (sessionImplementor.isClosed()) {
            throw new SessionException("Session is closed!");
        }
    }

    private static void checkNoUnresolvedActionsBeforeOperation(SessionImplementor sessionImplementor) {
        EventSource eventSource = (EventSource)sessionImplementor;
        if (sessionImplementor.getPersistenceContext().getCascadeLevel() == 0 && eventSource.getActionQueue().hasUnresolvedEntityInsertActions()) {
            throw new IllegalStateException("There are delayed insert actions before operation as cascade level 0.");
        }
    }

    private static void checkNoUnresolvedActionsAfterOperation(SessionImplementor sessionImplementor) {
        EventSource eventSource = (EventSource)sessionImplementor;
        if (sessionImplementor.getPersistenceContext().getCascadeLevel() == 0) {
            eventSource.getActionQueue().checkNoUnresolvedActionsAfterOperation();
        }
    }

    private static <T> EventListenerGroup<T> eventListenerGroup(Session session, EventType<T> type) {
        SessionFactoryImplementor sessionFactoryImpl = (SessionFactoryImplementor)session.getSessionFactory();
        return ((EventListenerRegistry)sessionFactoryImpl.getServiceRegistry().getService(EventListenerRegistry.class)).getEventListenerGroup(type);
    }

    public static int delete(Session session, String query, Object[] values, Type[] types) throws HibernateException {
        List list = SessionHelper.createQuery(session, query, values, types).list();
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            session.delete(list.get(i));
        }
        return size;
    }

    public static Query createQuery(Session session, String queryString, Object value, Type type) throws HibernateException {
        return SessionHelper.createQuery(session, queryString, new Object[]{value}, new Type[]{type});
    }

    public static Query createQuery(Session session, String queryString, Object[] values, Type[] types) throws HibernateException {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        if (types == null) {
            throw new IllegalArgumentException("types is null");
        }
        if (values.length != types.length) {
            throw new IllegalArgumentException("values.length != types.length");
        }
        Query query = session.createQuery(queryString);
        List<String> parameterNames = SessionHelper.findQueryParameterNames(queryString);
        if (parameterNames.size() != 0 && parameterNames.size() != values.length) {
            throw new IllegalArgumentException("parameter-names.length != values.length");
        }
        if (parameterNames.size() > 0) {
            for (int i = 0; i < values.length; ++i) {
                query.setParameter(parameterNames.get(i), values[i], types[i]);
            }
        } else {
            for (int i = 0; i < values.length; ++i) {
                query.setParameter(i, values[i], types[i]);
            }
        }
        return query;
    }

    private static List<String> findQueryParameterNames(String queryString) {
        ArrayList<String> list = new ArrayList<String>();
        int i = StringUtils.indexOfIgnoreCase((CharSequence)queryString, (CharSequence)"where");
        while (i != -1) {
            int start = queryString.indexOf(":", i);
            int end = SessionHelper.findParameterNameEnd(queryString, start);
            if (end != -1) {
                list.add(queryString.substring(start + 1, end));
            }
            i = end != -1 ? end : -1;
        }
        return list;
    }

    private static int findParameterNameEnd(String queryString, int start) {
        if (start == -1) {
            return -1;
        }
        for (int i = start + 1; i < queryString.length(); ++i) {
            char c = queryString.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') continue;
            return i;
        }
        return queryString.length();
    }

    public static NativeQuery createNativeQuery(Session session, String sql, String[] returnAliases, Class[] returnClasses) {
        NativeQuery query = session.createNativeQuery(sql);
        for (int i = 0; i < returnAliases.length; ++i) {
            query = query.addEntity(returnAliases[i], returnClasses[i]);
        }
        return query;
    }
}

