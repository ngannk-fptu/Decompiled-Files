/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.metadata.GenericCallMetaDataProvider;

public class HanaCallMetaDataProvider
extends GenericCallMetaDataProvider {
    public HanaCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        super(databaseMetaData);
    }

    @Override
    public void initializeWithMetaData(DatabaseMetaData databaseMetaData) throws SQLException {
        super.initializeWithMetaData(databaseMetaData);
        this.setStoresUpperCaseIdentifiers(false);
    }
}

