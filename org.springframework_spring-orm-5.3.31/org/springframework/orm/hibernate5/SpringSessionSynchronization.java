/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.FlushMode
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.core.Ordered
 *  org.springframework.dao.DataAccessException
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.orm.hibernate5;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringSessionSynchronization
implements TransactionSynchronization,
Ordered {
    private final SessionHolder sessionHolder;
    private final SessionFactory sessionFactory;
    private final boolean newSession;
    private boolean holderActive = true;

    public SpringSessionSynchronization(SessionHolder sessionHolder, SessionFactory sessionFactory) {
        this(sessionHolder, sessionFactory, false);
    }

    public SpringSessionSynchronization(SessionHolder sessionHolder, SessionFactory sessionFactory, boolean newSession) {
        this.sessionHolder = sessionHolder;
        this.sessionFactory = sessionFactory;
        this.newSession = newSession;
    }

    private Session getCurrentSession() {
        return this.sessionHolder.getSession();
    }

    public int getOrder() {
        return 900;
    }

    public void suspend() {
        if (this.holderActive) {
            TransactionSynchronizationManager.unbindResource((Object)this.sessionFactory);
            this.getCurrentSession().disconnect();
        }
    }

    public void resume() {
        if (this.holderActive) {
            TransactionSynchronizationManager.bindResource((Object)this.sessionFactory, (Object)((Object)this.sessionHolder));
        }
    }

    public void flush() {
        SessionFactoryUtils.flush(this.getCurrentSession(), false);
    }

    public void beforeCommit(boolean readOnly) throws DataAccessException {
        Session session;
        if (!readOnly && !FlushMode.MANUAL.equals((Object)(session = this.getCurrentSession()).getHibernateFlushMode())) {
            SessionFactoryUtils.flush(this.getCurrentSession(), true);
        }
    }

    public void beforeCompletion() {
        try {
            Session session = this.sessionHolder.getSession();
            if (this.sessionHolder.getPreviousFlushMode() != null) {
                session.setHibernateFlushMode(this.sessionHolder.getPreviousFlushMode());
            }
            session.disconnect();
        }
        finally {
            if (this.newSession) {
                TransactionSynchronizationManager.unbindResource((Object)this.sessionFactory);
                this.holderActive = false;
            }
        }
    }

    public void afterCommit() {
    }

    public void afterCompletion(int status) {
        try {
            if (status != 0) {
                this.sessionHolder.getSession().clear();
            }
        }
        finally {
            this.sessionHolder.setSynchronizedWithTransaction(false);
            if (this.newSession) {
                SessionFactoryUtils.closeSession(this.sessionHolder.getSession());
            }
        }
    }
}

