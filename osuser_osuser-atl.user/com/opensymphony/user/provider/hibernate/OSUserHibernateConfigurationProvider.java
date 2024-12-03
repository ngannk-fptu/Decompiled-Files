/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider
 */
package com.opensymphony.user.provider.hibernate;

import com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider;
import com.opensymphony.user.provider.hibernate.dao.HibernateGroupDAO;
import com.opensymphony.user.provider.hibernate.dao.HibernateUserDAO;

public interface OSUserHibernateConfigurationProvider
extends HibernateConfigurationProvider {
    public HibernateGroupDAO getGroupDAO();

    public HibernateUserDAO getUserDAO();
}

