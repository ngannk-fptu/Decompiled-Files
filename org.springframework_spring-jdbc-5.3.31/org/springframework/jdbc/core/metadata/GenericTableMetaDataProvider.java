/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.jdbc.core.metadata.TableParameterMetaData;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

public class GenericTableMetaDataProvider
implements TableMetaDataProvider {
    protected static final Log logger = LogFactory.getLog(TableMetaDataProvider.class);
    private boolean tableColumnMetaDataUsed = false;
    @Nullable
    private String databaseVersion;
    @Nullable
    private String userName;
    private boolean storesUpperCaseIdentifiers = true;
    private boolean storesLowerCaseIdentifiers = false;
    private boolean getGeneratedKeysSupported = true;
    private boolean generatedKeysColumnNameArraySupported = true;
    private List<String> productsNotSupportingGeneratedKeysColumnNameArray = Arrays.asList("Apache Derby", "HSQL Database Engine");
    private List<TableParameterMetaData> tableParameterMetaData = new ArrayList<TableParameterMetaData>();

    protected GenericTableMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
        this.userName = databaseMetaData.getUserName();
    }

    public void setStoresUpperCaseIdentifiers(boolean storesUpperCaseIdentifiers) {
        this.storesUpperCaseIdentifiers = storesUpperCaseIdentifiers;
    }

    public boolean isStoresUpperCaseIdentifiers() {
        return this.storesUpperCaseIdentifiers;
    }

    public void setStoresLowerCaseIdentifiers(boolean storesLowerCaseIdentifiers) {
        this.storesLowerCaseIdentifiers = storesLowerCaseIdentifiers;
    }

    public boolean isStoresLowerCaseIdentifiers() {
        return this.storesLowerCaseIdentifiers;
    }

    @Override
    public boolean isTableColumnMetaDataUsed() {
        return this.tableColumnMetaDataUsed;
    }

    @Override
    public List<TableParameterMetaData> getTableParameterMetaData() {
        return this.tableParameterMetaData;
    }

    @Override
    public boolean isGetGeneratedKeysSupported() {
        return this.getGeneratedKeysSupported;
    }

    @Override
    public boolean isGetGeneratedKeysSimulated() {
        return false;
    }

    @Override
    @Nullable
    public String getSimpleQueryForGetGeneratedKey(String tableName, String keyColumnName) {
        return null;
    }

    public void setGetGeneratedKeysSupported(boolean getGeneratedKeysSupported) {
        this.getGeneratedKeysSupported = getGeneratedKeysSupported;
    }

    public void setGeneratedKeysColumnNameArraySupported(boolean generatedKeysColumnNameArraySupported) {
        this.generatedKeysColumnNameArraySupported = generatedKeysColumnNameArraySupported;
    }

    @Override
    public boolean isGeneratedKeysColumnNameArraySupported() {
        return this.generatedKeysColumnNameArraySupported;
    }

    @Override
    public void initializeWithMetaData(DatabaseMetaData databaseMetaData) throws SQLException {
        block22: {
            block21: {
                block20: {
                    block19: {
                        block18: {
                            try {
                                if (databaseMetaData.supportsGetGeneratedKeys()) {
                                    logger.debug((Object)"GetGeneratedKeys is supported");
                                    this.setGetGeneratedKeysSupported(true);
                                } else {
                                    logger.debug((Object)"GetGeneratedKeys is not supported");
                                    this.setGetGeneratedKeysSupported(false);
                                }
                            }
                            catch (SQLException ex) {
                                if (!logger.isWarnEnabled()) break block18;
                                logger.warn((Object)("Error retrieving 'DatabaseMetaData.getGeneratedKeys': " + ex.getMessage()));
                            }
                        }
                        try {
                            String databaseProductName = databaseMetaData.getDatabaseProductName();
                            if (this.productsNotSupportingGeneratedKeysColumnNameArray.contains(databaseProductName)) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug((Object)("GeneratedKeysColumnNameArray is not supported for " + databaseProductName));
                                }
                                this.setGeneratedKeysColumnNameArraySupported(false);
                            } else if (this.isGetGeneratedKeysSupported()) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug((Object)("GeneratedKeysColumnNameArray is supported for " + databaseProductName));
                                }
                                this.setGeneratedKeysColumnNameArraySupported(true);
                            } else {
                                this.setGeneratedKeysColumnNameArraySupported(false);
                            }
                        }
                        catch (SQLException ex) {
                            if (!logger.isWarnEnabled()) break block19;
                            logger.warn((Object)("Error retrieving 'DatabaseMetaData.getDatabaseProductName': " + ex.getMessage()));
                        }
                    }
                    try {
                        this.databaseVersion = databaseMetaData.getDatabaseProductVersion();
                    }
                    catch (SQLException ex) {
                        if (!logger.isWarnEnabled()) break block20;
                        logger.warn((Object)("Error retrieving 'DatabaseMetaData.getDatabaseProductVersion': " + ex.getMessage()));
                    }
                }
                try {
                    this.setStoresUpperCaseIdentifiers(databaseMetaData.storesUpperCaseIdentifiers());
                }
                catch (SQLException ex) {
                    if (!logger.isWarnEnabled()) break block21;
                    logger.warn((Object)("Error retrieving 'DatabaseMetaData.storesUpperCaseIdentifiers': " + ex.getMessage()));
                }
            }
            try {
                this.setStoresLowerCaseIdentifiers(databaseMetaData.storesLowerCaseIdentifiers());
            }
            catch (SQLException ex) {
                if (!logger.isWarnEnabled()) break block22;
                logger.warn((Object)("Error retrieving 'DatabaseMetaData.storesLowerCaseIdentifiers': " + ex.getMessage()));
            }
        }
    }

    @Override
    public void initializeWithTableColumnMetaData(DatabaseMetaData databaseMetaData, @Nullable String catalogName, @Nullable String schemaName, @Nullable String tableName) throws SQLException {
        this.tableColumnMetaDataUsed = true;
        this.locateTableAndProcessMetaData(databaseMetaData, catalogName, schemaName, tableName);
    }

    @Override
    @Nullable
    public String tableNameToUse(@Nullable String tableName) {
        if (tableName == null) {
            return null;
        }
        if (this.isStoresUpperCaseIdentifiers()) {
            return tableName.toUpperCase();
        }
        if (this.isStoresLowerCaseIdentifiers()) {
            return tableName.toLowerCase();
        }
        return tableName;
    }

    @Override
    @Nullable
    public String catalogNameToUse(@Nullable String catalogName) {
        if (catalogName == null) {
            return null;
        }
        if (this.isStoresUpperCaseIdentifiers()) {
            return catalogName.toUpperCase();
        }
        if (this.isStoresLowerCaseIdentifiers()) {
            return catalogName.toLowerCase();
        }
        return catalogName;
    }

    @Override
    @Nullable
    public String schemaNameToUse(@Nullable String schemaName) {
        if (schemaName == null) {
            return null;
        }
        if (this.isStoresUpperCaseIdentifiers()) {
            return schemaName.toUpperCase();
        }
        if (this.isStoresLowerCaseIdentifiers()) {
            return schemaName.toLowerCase();
        }
        return schemaName;
    }

    @Override
    @Nullable
    public String metaDataCatalogNameToUse(@Nullable String catalogName) {
        return this.catalogNameToUse(catalogName);
    }

    @Override
    @Nullable
    public String metaDataSchemaNameToUse(@Nullable String schemaName) {
        if (schemaName == null) {
            return this.schemaNameToUse(this.getDefaultSchema());
        }
        return this.schemaNameToUse(schemaName);
    }

    @Nullable
    protected String getDefaultSchema() {
        return this.userName;
    }

    @Nullable
    protected String getDatabaseVersion() {
        return this.databaseVersion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void locateTableAndProcessMetaData(DatabaseMetaData databaseMetaData, @Nullable String catalogName, @Nullable String schemaName, @Nullable String tableName) {
        HashMap<String, TableMetaData> tableMeta = new HashMap<String, TableMetaData>();
        ResultSet tables = null;
        try {
            tables = databaseMetaData.getTables(this.catalogNameToUse(catalogName), this.schemaNameToUse(schemaName), this.tableNameToUse(tableName), null);
            while (tables != null && tables.next()) {
                TableMetaData tmd = new TableMetaData();
                tmd.setCatalogName(tables.getString("TABLE_CAT"));
                tmd.setSchemaName(tables.getString("TABLE_SCHEM"));
                tmd.setTableName(tables.getString("TABLE_NAME"));
                if (tmd.getSchemaName() == null) {
                    tableMeta.put(this.userName != null ? this.userName.toUpperCase() : "", tmd);
                    continue;
                }
                tableMeta.put(tmd.getSchemaName().toUpperCase(), tmd);
            }
            JdbcUtils.closeResultSet(tables);
        }
        catch (SQLException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn((Object)("Error while accessing table meta-data results: " + ex.getMessage()));
            }
        }
        finally {
            JdbcUtils.closeResultSet(tables);
        }
        if (tableMeta.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info((Object)("Unable to locate table meta-data for '" + tableName + "': column names must be provided"));
            }
        } else {
            this.processTableColumns(databaseMetaData, this.findTableMetaData(schemaName, tableName, tableMeta));
        }
    }

    private TableMetaData findTableMetaData(@Nullable String schemaName, @Nullable String tableName, Map<String, TableMetaData> tableMeta) {
        if (schemaName != null) {
            TableMetaData tmd = tableMeta.get(schemaName.toUpperCase());
            if (tmd == null) {
                throw new DataAccessResourceFailureException("Unable to locate table meta-data for '" + tableName + "' in the '" + schemaName + "' schema");
            }
            return tmd;
        }
        if (tableMeta.size() == 1) {
            return tableMeta.values().iterator().next();
        }
        TableMetaData tmd = tableMeta.get(this.getDefaultSchema());
        if (tmd == null) {
            tmd = tableMeta.get(this.userName != null ? this.userName.toUpperCase() : "");
        }
        if (tmd == null) {
            tmd = tableMeta.get("PUBLIC");
        }
        if (tmd == null) {
            tmd = tableMeta.get("DBO");
        }
        if (tmd == null) {
            throw new DataAccessResourceFailureException("Unable to locate table meta-data for '" + tableName + "' in the default schema");
        }
        return tmd;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processTableColumns(DatabaseMetaData databaseMetaData, TableMetaData tmd) {
        ResultSet tableColumns = null;
        String metaDataCatalogName = this.metaDataCatalogNameToUse(tmd.getCatalogName());
        String metaDataSchemaName = this.metaDataSchemaNameToUse(tmd.getSchemaName());
        String metaDataTableName = this.tableNameToUse(tmd.getTableName());
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Retrieving meta-data for " + metaDataCatalogName + '/' + metaDataSchemaName + '/' + metaDataTableName));
        }
        try {
            tableColumns = databaseMetaData.getColumns(metaDataCatalogName, metaDataSchemaName, metaDataTableName, null);
            while (tableColumns.next()) {
                String columnName = tableColumns.getString("COLUMN_NAME");
                int dataType = tableColumns.getInt("DATA_TYPE");
                if (dataType == 3) {
                    String typeName = tableColumns.getString("TYPE_NAME");
                    int decimalDigits = tableColumns.getInt("DECIMAL_DIGITS");
                    if ("NUMBER".equals(typeName) && decimalDigits == 0) {
                        dataType = 2;
                        if (logger.isDebugEnabled()) {
                            logger.debug((Object)("Overriding meta-data: " + columnName + " now NUMERIC instead of DECIMAL"));
                        }
                    }
                }
                boolean nullable = tableColumns.getBoolean("NULLABLE");
                TableParameterMetaData meta = new TableParameterMetaData(columnName, dataType, nullable);
                this.tableParameterMetaData.add(meta);
                if (!logger.isDebugEnabled()) continue;
                logger.debug((Object)("Retrieved meta-data: '" + meta.getParameterName() + "', sqlType=" + meta.getSqlType() + ", nullable=" + meta.isNullable()));
            }
        }
        catch (SQLException ex) {
            try {
                if (logger.isWarnEnabled()) {
                    logger.warn((Object)"Error while retrieving meta-data for table columns. Consider specifying explicit column names -- for example, via SimpleJdbcInsert#usingColumns().", (Throwable)ex);
                }
                this.tableParameterMetaData.clear();
            }
            catch (Throwable throwable) {
                JdbcUtils.closeResultSet(tableColumns);
                throw throwable;
            }
            JdbcUtils.closeResultSet(tableColumns);
        }
        JdbcUtils.closeResultSet(tableColumns);
    }

    private static class TableMetaData {
        @Nullable
        private String catalogName;
        @Nullable
        private String schemaName;
        @Nullable
        private String tableName;

        private TableMetaData() {
        }

        public void setCatalogName(String catalogName) {
            this.catalogName = catalogName;
        }

        @Nullable
        public String getCatalogName() {
            return this.catalogName;
        }

        public void setSchemaName(String schemaName) {
            this.schemaName = schemaName;
        }

        @Nullable
        public String getSchemaName() {
            return this.schemaName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        @Nullable
        public String getTableName() {
            return this.tableName;
        }
    }
}

