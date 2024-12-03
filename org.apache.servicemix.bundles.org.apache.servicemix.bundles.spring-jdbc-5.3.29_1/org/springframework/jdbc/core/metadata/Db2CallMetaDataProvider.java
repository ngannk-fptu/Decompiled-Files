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

public class Db2CallMetaDataProvider
extends GenericCallMetaDataProvider {
    public Db2CallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        super(databaseMetaData);
    }

    @Override
    public void initializeWithMetaData(DatabaseMetaData databaseMetaData) throws SQLException {
        try {
            this.setSupportsCatalogsInProcedureCalls(databaseMetaData.supportsCatalogsInProcedureCalls());
        }
        catch (SQLException ex) {
            logger.debug((Object)("Error retrieving 'DatabaseMetaData.supportsCatalogsInProcedureCalls' - " + ex.getMessage()));
        }
        try {
            this.setSupportsSchemasInProcedureCalls(databaseMetaData.supportsSchemasInProcedureCalls());
        }
        catch (SQLException ex) {
            logger.debug((Object)("Error retrieving 'DatabaseMetaData.supportsSchemasInProcedureCalls' - " + ex.getMessage()));
        }
        try {
            this.setStoresUpperCaseIdentifiers(databaseMetaData.storesUpperCaseIdentifiers());
        }
        catch (SQLException ex) {
            logger.debug((Object)("Error retrieving 'DatabaseMetaData.storesUpperCaseIdentifiers' - " + ex.getMessage()));
        }
        try {
            this.setStoresLowerCaseIdentifiers(databaseMetaData.storesLowerCaseIdentifiers());
        }
        catch (SQLException ex) {
            logger.debug((Object)("Error retrieving 'DatabaseMetaData.storesLowerCaseIdentifiers' - " + ex.getMessage()));
        }
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

