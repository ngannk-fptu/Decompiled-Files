/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.FlushMode
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.context.spi.AbstractCurrentSessionContext
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode
 *  org.hibernate.resource.transaction.spi.TransactionStatus
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.AbstractCurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;

public class ThreadBoundSessionContext
extends AbstractCurrentSessionContext {
    private static final Logger log = ContextLoggerFactory.getLogger(ThreadBoundSessionContext.class);
    private static final ThreadLocal<Map<SessionFactory, Session>> CONTEXT_TL = new ThreadLocal();

    public ThreadBoundSessionContext(SessionFactoryImplementor factory) {
        super(factory);
    }

    public final Session currentSession() {
        Session current = ThreadBoundSessionContext.existingSession((SessionFactory)this.factory());
        if (current == null) {
            current = this.buildOrObtainSession();
            ThreadBoundSessionContext.doBind(current, (SessionFactory)this.factory());
        } else {
            this.validateExistingSession(current);
        }
        return current;
    }

    protected SessionFactoryImplementor getFactory() {
        return this.factory();
    }

    protected SessionImplementor buildOrObtainSession() {
        return (SessionImplementor)this.baseSessionBuilder().autoClose(this.isAutoCloseEnabled()).connectionHandlingMode(this.getConnectionHandlingMode()).flushMode(this.getFlushMode()).openSession();
    }

    protected boolean isAutoCloseEnabled() {
        return true;
    }

    protected FlushMode getFlushMode() {
        return FlushMode.AUTO;
    }

    protected PhysicalConnectionHandlingMode getConnectionHandlingMode() {
        return this.factory().getSessionFactoryOptions().getPhysicalConnectionHandlingMode();
    }

    public static void bind(Session session) {
        SessionFactory factory = session.getSessionFactory();
        ThreadBoundSessionContext.cleanupAnyOrphanedSession(factory);
        ThreadBoundSessionContext.doBind(session, factory);
    }

    public static Session unbind(SessionFactory factory) {
        return ThreadBoundSessionContext.doUnbind(factory, true);
    }

    private static Session existingSession(SessionFactory factory) {
        Map<SessionFactory, Session> sessionMap = ThreadBoundSessionContext.sessionMap();
        if (sessionMap == null) {
            return null;
        }
        return sessionMap.get(factory);
    }

    protected static Map<SessionFactory, Session> sessionMap() {
        return CONTEXT_TL.get();
    }

    private static void doBind(Session session, SessionFactory factory) {
        Map<SessionFactory, Session> sessionMap = ThreadBoundSessionContext.sessionMap();
        if (sessionMap == null) {
            sessionMap = new HashMap<SessionFactory, Session>();
            CONTEXT_TL.set(sessionMap);
        }
        sessionMap.put(factory, session);
    }

    private static Session doUnbind(SessionFactory factory, boolean releaseMapIfEmpty) {
        Session session = null;
        Map<SessionFactory, Session> sessionMap = ThreadBoundSessionContext.sessionMap();
        if (sessionMap != null) {
            session = sessionMap.remove(factory);
            if (releaseMapIfEmpty && sessionMap.isEmpty()) {
                CONTEXT_TL.remove();
            }
        }
        return session;
    }

    private static void cleanupAnyOrphanedSession(SessionFactory factory) {
        Session orphan = ThreadBoundSessionContext.doUnbind(factory, false);
        if (orphan != null) {
            log.warn("Found an orphan session");
            try {
                if (orphan.getTransaction() != null && orphan.getTransaction().getStatus() == TransactionStatus.ACTIVE) {
                    orphan.getTransaction().rollback();
                }
                orphan.close();
            }
            catch (Exception t) {
                log.error("Unable to close orphaned session", (Throwable)t);
            }
        }
    }

    public static void cleanupAnyOrphanedSessions() {
        Map<SessionFactory, Session> sessionMap = ThreadBoundSessionContext.sessionMap();
        if (sessionMap != null) {
            for (Session session : sessionMap.values()) {
                session.close();
            }
            CONTEXT_TL.remove();
        }
    }
}

