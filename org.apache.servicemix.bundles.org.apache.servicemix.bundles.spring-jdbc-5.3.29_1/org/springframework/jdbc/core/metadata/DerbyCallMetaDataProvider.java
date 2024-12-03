/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.metadata.GenericCallMetaDataProvider;
import org.springframework.lang.Nullable;

public class DerbyCallMetaDataProvider
extends GenericCallMetaDataProvider {
    public DerbyCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        super(databaseMetaData);
    }

    @Override
    @Nullable
    public String metaDataSchemaNameToUse(@Nullable String schemaName) {
        if (schemaName != null) {
            return super.metaDataSchemaNameToUse(schemaName);
        }
        String userName = this.getUserName();
        return userName != null ? userName.toUpperCase() : null;
    }
}

