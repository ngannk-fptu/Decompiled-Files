/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.dao.support.DaoSupport
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.orm.hibernate5.support;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.support.DaoSupport;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.Assert;

public abstract class HibernateDaoSupport
extends DaoSupport {
    @Nullable
    private HibernateTemplate hibernateTemplate;

    public final void setSessionFactory(SessionFactory sessionFactory) {
        if (this.hibernateTemplate == null || sessionFactory != this.hibernateTemplate.getSessionFactory()) {
            this.hibernateTemplate = this.createHibernateTemplate(sessionFactory);
        }
    }

    protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
        return new HibernateTemplate(sessionFactory);
    }

    @Nullable
    public final SessionFactory getSessionFactory() {
        return this.hibernateTemplate != null ? this.hibernateTemplate.getSessionFactory() : null;
    }

    public final void setHibernateTemplate(@Nullable HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Nullable
    public final HibernateTemplate getHibernateTemplate() {
        return this.hibernateTemplate;
    }

    protected final void checkDaoConfig() {
        if (this.hibernateTemplate == null) {
            throw new IllegalArgumentException("'sessionFactory' or 'hibernateTemplate' is required");
        }
    }

    protected final Session currentSession() throws DataAccessResourceFailureException {
        SessionFactory sessionFactory = this.getSessionFactory();
        Assert.state((sessionFactory != null ? 1 : 0) != 0, (String)"No SessionFactory set");
        return sessionFactory.getCurrentSession();
    }
}

