/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.LobCreatorBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.jdbc.env.spi.QualifiedObjectNameFormatter;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.jdbc.spi.TypeInfo;
import org.hibernate.service.Service;

public interface JdbcEnvironment
extends Service {
    public Dialect getDialect();

    public ExtractedDatabaseMetaData getExtractedDatabaseMetaData();

    public Identifier getCurrentCatalog();

    public Identifier getCurrentSchema();

    @Deprecated
    public QualifiedObjectNameFormatter getQualifiedObjectNameFormatter();

    public IdentifierHelper getIdentifierHelper();

    public NameQualifierSupport getNameQualifierSupport();

    public SqlExceptionHelper getSqlExceptionHelper();

    public LobCreatorBuilder getLobCreatorBuilder();

    @Deprecated
    default public TypeInfo getTypeInfoForJdbcCode(int jdbcTypeCode) {
        throw new UnsupportedOperationException("Support for getting TypeInfo from jdbcTypeCode has been disabled as it wasn't used. Use org.hibernate.engine.jdbc.spi.TypeInfo.extractTypeInfo as alternative, or report an issue and explain.");
    }
}

