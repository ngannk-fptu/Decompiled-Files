/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.rdbms.RdbmsException
 *  com.atlassian.sal.spring.connection.SpringHostConnectionAccessor$ConnectionProvider
 *  io.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.HibernateException
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionImplementor
 */
package com.atlassian.confluence.api.impl.sal.rdbms;

import com.atlassian.sal.api.rdbms.RdbmsException;
import com.atlassian.sal.spring.connection.SpringHostConnectionAccessor;
import io.atlassian.fugue.Option;
import java.sql.Connection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;

public class ConfluenceConnectionProvider
implements SpringHostConnectionAccessor.ConnectionProvider {
    private final SessionFactory sessionFactory;

    public ConfluenceConnectionProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public @NonNull Connection getConnection() {
        try {
            return ((SessionImplementor)this.sessionFactory.getCurrentSession()).connection();
        }
        catch (ClassCastException | HibernateException e) {
            throw new RdbmsException("unable to retrieve a java.sql.Connection from the org.hibernate.Session", e);
        }
    }

    public @NonNull Option<String> getSchemaName() {
        return Option.none();
    }
}

