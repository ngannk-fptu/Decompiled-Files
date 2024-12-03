/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.metadata.GenericTableMetaDataProvider;

public class PostgresTableMetaDataProvider
extends GenericTableMetaDataProvider {
    public PostgresTableMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        super(databaseMetaData);
    }

    @Override
    public boolean isGetGeneratedKeysSimulated() {
        return true;
    }

    @Override
    public String getSimpleQueryForGetGeneratedKey(String tableName, String keyColumnName) {
        return "RETURNING " + keyColumnName;
    }
}

