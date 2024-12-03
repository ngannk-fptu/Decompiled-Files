/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.HibernateException
 *  net.sf.hibernate.Session
 *  net.sf.hibernate.SessionFactory
 *  net.sf.hibernate.cfg.Configuration
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.hibernate.dao;

import java.sql.SQLException;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionManager {
    private static Log log = LogFactory.getLog((String)(class$com$opensymphony$user$provider$hibernate$dao$SessionManager == null ? (class$com$opensymphony$user$provider$hibernate$dao$SessionManager = SessionManager.class$("com.opensymphony.user.provider.hibernate.dao.SessionManager")) : class$com$opensymphony$user$provider$hibernate$dao$SessionManager).getName());
    private SessionFactory sessionFactory;
    static /* synthetic */ Class class$com$opensymphony$user$provider$hibernate$dao$SessionManager;

    public SessionManager(Configuration configuration) throws HibernateException {
        try {
            this.sessionFactory = configuration.buildSessionFactory();
        }
        catch (HibernateException he) {
            log.error((Object)"Problem creating SessionFactory.", (Throwable)he);
            throw he;
        }
    }

    public SessionManager(SessionFactory sessionFactory) throws HibernateException {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() throws HibernateException {
        Session session = null;
        try {
            session = this.sessionFactory.openSession();
        }
        catch (HibernateException he) {
            log.error((Object)"Problem obtaining a session", (Throwable)he);
        }
        return session;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void closeSession(Session session) {
        block2: {
            try {
                session.flush();
                session.close();
            }
            catch (HibernateException he) {
                if (!log.isDebugEnabled()) break block2;
                log.debug((Object)"HibernateException caught closing connection.");
            }
        }
    }

    public void flushCloseSession(Session session) {
        block5: {
            try {
                session.flush();
                if (!session.connection().getAutoCommit()) {
                    session.connection().commit();
                }
                this.closeSession(session);
            }
            catch (HibernateException he) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"HibernateException caught during flush/commit.", (Throwable)he);
                }
            }
            catch (SQLException sqle) {
                if (!log.isDebugEnabled()) break block5;
                log.debug((Object)"SQLException caught during commit.", (Throwable)sqle);
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

