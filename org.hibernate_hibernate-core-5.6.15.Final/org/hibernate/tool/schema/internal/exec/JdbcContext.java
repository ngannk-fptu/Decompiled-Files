/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.service.ServiceRegistry;

public interface JdbcContext {
    public JdbcConnectionAccess getJdbcConnectionAccess();

    public Dialect getDialect();

    public SqlStatementLogger getSqlStatementLogger();

    public SqlExceptionHelper getSqlExceptionHelper();

    public ServiceRegistry getServiceRegistry();
}

