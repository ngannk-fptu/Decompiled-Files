/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 */
package org.hibernate;

import java.io.Closeable;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import javax.naming.Referenceable;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

public interface SessionFactory
extends EntityManagerFactory,
HibernateEntityManagerFactory,
Referenceable,
Serializable,
Closeable {
    public SessionFactoryOptions getSessionFactoryOptions();

    public SessionBuilder withOptions();

    public Session openSession() throws HibernateException;

    public Session getCurrentSession() throws HibernateException;

    public StatelessSessionBuilder withStatelessOptions();

    public StatelessSession openStatelessSession();

    public StatelessSession openStatelessSession(Connection var1);

    public Statistics getStatistics();

    @Override
    public void close() throws HibernateException;

    public boolean isClosed();

    public Cache getCache();

    public Set getDefinedFilterNames();

    public FilterDefinition getFilterDefinition(String var1) throws HibernateException;

    public boolean containsFetchProfileDefinition(String var1);

    public TypeHelper getTypeHelper();

    @Deprecated
    public ClassMetadata getClassMetadata(Class var1);

    @Deprecated
    public ClassMetadata getClassMetadata(String var1);

    @Deprecated
    public CollectionMetadata getCollectionMetadata(String var1);

    @Deprecated
    public Map<String, ClassMetadata> getAllClassMetadata();

    @Deprecated
    public Map getAllCollectionMetadata();
}

