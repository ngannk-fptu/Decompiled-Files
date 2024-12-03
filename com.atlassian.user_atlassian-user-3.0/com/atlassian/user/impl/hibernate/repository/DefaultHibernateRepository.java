/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider
 *  net.sf.hibernate.SessionFactory
 */
package com.atlassian.user.impl.hibernate.repository;

import com.atlassian.user.impl.hibernate.repository.HibernateRepository;
import com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider;
import net.sf.hibernate.SessionFactory;

public class DefaultHibernateRepository
implements HibernateRepository {
    protected transient HibernateConfigurationProvider hibernateConfigProvider;
    protected transient SessionFactory sessionFactory;

    public DefaultHibernateRepository(HibernateConfigurationProvider hibernateConfigProvider, SessionFactory sessionFactory) {
        this.hibernateConfigProvider = hibernateConfigProvider;
        this.sessionFactory = sessionFactory;
    }

    public HibernateConfigurationProvider getHibernateConfigurationProvider() {
        return this.hibernateConfigProvider;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
}

