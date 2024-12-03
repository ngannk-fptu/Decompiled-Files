/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider
 *  net.sf.hibernate.SessionFactory
 */
package com.atlassian.user.impl.hibernate.repository;

import com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider;
import net.sf.hibernate.SessionFactory;

public interface HibernateRepository {
    public HibernateConfigurationProvider getHibernateConfigurationProvider();

    public SessionFactory getSessionFactory();
}

