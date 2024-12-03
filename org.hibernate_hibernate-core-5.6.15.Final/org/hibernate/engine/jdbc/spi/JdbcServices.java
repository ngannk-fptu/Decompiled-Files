/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.spi;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.LobCreationContext;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.service.Service;

public interface JdbcServices
extends Service {
    public JdbcEnvironment getJdbcEnvironment();

    public JdbcConnectionAccess getBootstrapJdbcConnectionAccess();

    public Dialect getDialect();

    public SqlStatementLogger getSqlStatementLogger();

    public SqlExceptionHelper getSqlExceptionHelper();

    public ExtractedDatabaseMetaData getExtractedMetaDataSupport();

    public LobCreator getLobCreator(LobCreationContext var1);

    public ResultSetWrapper getResultSetWrapper();
}

