/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.context.internal;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.AbstractCurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class ManagedSessionContext
extends AbstractCurrentSessionContext {
    private static final ThreadLocal<Map<SessionFactory, Session>> CONTEXT_TL = new ThreadLocal();

    public ManagedSessionContext(SessionFactoryImplementor factory) {
        super(factory);
    }

    @Override
    public Session currentSession() {
        Session current = ManagedSessionContext.existingSession(this.factory());
        if (current == null) {
            throw new HibernateException("No session currently bound to execution context");
        }
        this.validateExistingSession(current);
        return current;
    }

    public static boolean hasBind(SessionFactory factory) {
        return ManagedSessionContext.existingSession(factory) != null;
    }

    public static Session bind(Session session) {
        return ManagedSessionContext.sessionMap(true).put(session.getSessionFactory(), session);
    }

    public static Session unbind(SessionFactory factory) {
        Map<SessionFactory, Session> sessionMap = ManagedSessionContext.sessionMap();
        Session existing = null;
        if (sessionMap != null) {
            existing = sessionMap.remove(factory);
            ManagedSessionContext.doCleanup();
        }
        return existing;
    }

    private static Session existingSession(SessionFactory factory) {
        Map<SessionFactory, Session> sessionMap = ManagedSessionContext.sessionMap();
        if (sessionMap == null) {
            return null;
        }
        return sessionMap.get(factory);
    }

    protected static Map<SessionFactory, Session> sessionMap() {
        return ManagedSessionContext.sessionMap(false);
    }

    private static Map<SessionFactory, Session> sessionMap(boolean createMap) {
        Map<SessionFactory, Session> sessionMap = CONTEXT_TL.get();
        if (sessionMap == null && createMap) {
            sessionMap = new HashMap<SessionFactory, Session>();
            CONTEXT_TL.set(sessionMap);
        }
        return sessionMap;
    }

    private static void doCleanup() {
        Map<SessionFactory, Session> sessionMap = ManagedSessionContext.sessionMap(false);
        if (sessionMap != null && sessionMap.isEmpty()) {
            CONTEXT_TL.remove();
        }
    }
}

