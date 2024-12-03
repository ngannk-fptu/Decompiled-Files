/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

import java.util.Collections;
import java.util.List;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.env.spi.SQLStateType;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;

public interface ExtractedDatabaseMetaData {
    public JdbcEnvironment getJdbcEnvironment();

    public String getConnectionCatalogName();

    public String getConnectionSchemaName();

    public boolean supportsNamedParameters();

    public boolean supportsRefCursors();

    public boolean supportsScrollableResults();

    public boolean supportsGetGeneratedKeys();

    public boolean supportsBatchUpdates();

    public boolean supportsDataDefinitionInTransaction();

    public boolean doesDataDefinitionCauseTransactionCommit();

    public SQLStateType getSqlStateType();

    default public List<SequenceInformation> getSequenceInformationList() {
        return Collections.emptyList();
    }
}

