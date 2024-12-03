/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.persistence.EntityManager
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.google.common.base.Preconditions;
import javax.persistence.EntityManager;
import org.hibernate.SessionFactory;

public final class HibernateEntityManagerProvider
implements EntityManagerProvider {
    private final SessionFactory sessionFactory;

    public HibernateEntityManagerProvider(SessionFactory sessionFactory) {
        this.sessionFactory = (SessionFactory)Preconditions.checkNotNull((Object)sessionFactory);
    }

    @Override
    public EntityManager getEntityManager() {
        return this.sessionFactory.getCurrentSession();
    }
}

