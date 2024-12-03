/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.config.util.BootstrapUtils
 *  org.hibernate.Session
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.config.util.BootstrapUtils;
import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public final class DataAccessUtils {
    private DataAccessUtils() {
    }

    public static JdbcTemplate getJdbcTemplate(Session session) {
        Connection connection = DataAccessUtils.getConnection(session);
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(connection, true);
        return new JdbcTemplate((DataSource)dataSource);
    }

    private static Connection getConnection(Session session) {
        return ((SessionImplementor)session).connection();
    }

    @Deprecated(since="8.0", forRemoval=true)
    public static Dialect getDialect() throws DataAccessException {
        return DataAccessUtils.getDialect(BootstrapUtils.getBootstrapManager().getHibernateConfig());
    }

    private static Dialect getDialect(HibernateConfig hibernateConfig) throws DataAccessException {
        return Dialect.getDialect((Properties)hibernateConfig.getHibernateProperties());
    }
}

