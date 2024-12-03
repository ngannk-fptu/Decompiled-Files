/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.SessionFactory
 */
package com.atlassian.user.impl.hibernate.configuration;

import net.sf.hibernate.SessionFactory;

public interface HibernateAccessor {
    public SessionFactory getSessionFactory();
}

