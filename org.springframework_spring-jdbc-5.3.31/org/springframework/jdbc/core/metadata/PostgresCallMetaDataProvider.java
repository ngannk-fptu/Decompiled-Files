/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.metadata.CallParameterMetaData;
import org.springframework.jdbc.core.metadata.GenericCallMetaDataProvider;
import org.springframework.lang.Nullable;

public class PostgresCallMetaDataProvider
extends GenericCallMetaDataProvider {
    private static final String RETURN_VALUE_NAME = "returnValue";

    public PostgresCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        super(databaseMetaData);
    }

    @Override
    public boolean isReturnResultSetSupported() {
        return false;
    }

    @Override
    public boolean isRefCursorSupported() {
        return true;
    }

    @Override
    public int getRefCursorSqlType() {
        return 1111;
    }

    @Override
    @Nullable
    public String metaDataSchemaNameToUse(@Nullable String schemaName) {
        return schemaName == null ? "public" : super.metaDataSchemaNameToUse(schemaName);
    }

    @Override
    public SqlParameter createDefaultOutParameter(String parameterName, CallParameterMetaData meta) {
        if (meta.getSqlType() == 1111 && "refcursor".equals(meta.getTypeName())) {
            return new SqlOutParameter(parameterName, this.getRefCursorSqlType(), new ColumnMapRowMapper());
        }
        return super.createDefaultOutParameter(parameterName, meta);
    }

    @Override
    public boolean byPassReturnParameter(String parameterName) {
        return RETURN_VALUE_NAME.equals(parameterName);
    }
}

