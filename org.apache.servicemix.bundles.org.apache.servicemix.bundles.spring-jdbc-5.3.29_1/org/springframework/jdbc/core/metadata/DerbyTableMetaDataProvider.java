/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.metadata.GenericTableMetaDataProvider;

public class DerbyTableMetaDataProvider
extends GenericTableMetaDataProvider {
    private boolean supportsGeneratedKeysOverride = false;

    public DerbyTableMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        super(databaseMetaData);
    }

    @Override
    public void initializeWithMetaData(DatabaseMetaData databaseMetaData) throws SQLException {
        super.initializeWithMetaData(databaseMetaData);
        if (!databaseMetaData.supportsGetGeneratedKeys()) {
            if (logger.isInfoEnabled()) {
                logger.info((Object)("Overriding supportsGetGeneratedKeys from DatabaseMetaData to 'true'; it was reported as 'false' by " + databaseMetaData.getDriverName() + " " + databaseMetaData.getDriverVersion()));
            }
            this.supportsGeneratedKeysOverride = true;
        }
    }

    @Override
    public boolean isGetGeneratedKeysSupported() {
        return super.isGetGeneratedKeysSupported() || this.supportsGeneratedKeysOverride;
    }
}

