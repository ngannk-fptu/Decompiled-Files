/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import org.hibernate.JDBCException;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.naming.DatabaseIdentifier;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.tool.schema.extract.internal.ColumnInformationImpl;
import org.hibernate.tool.schema.extract.internal.ForeignKeyInformationImpl;
import org.hibernate.tool.schema.extract.internal.IndexInformationImpl;
import org.hibernate.tool.schema.extract.internal.PrimaryKeyInformationImpl;
import org.hibernate.tool.schema.extract.internal.TableInformationImpl;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.ForeignKeyInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.InformationExtractor;
import org.hibernate.tool.schema.extract.spi.NameSpaceTablesInformation;
import org.hibernate.tool.schema.extract.spi.PrimaryKeyInformation;
import org.hibernate.tool.schema.extract.spi.SchemaExtractionException;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.spi.SchemaManagementException;

public abstract class AbstractInformationExtractorImpl
implements InformationExtractor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractInformationExtractorImpl.class);
    private final String[] tableTypes;
    private String[] extraPhysicalTableTypes;
    private final ExtractionContext extractionContext;
    private final boolean useJdbcMetadataDefaultsSetting;
    private Identifier currentCatalog;
    private Identifier currentSchema;
    private String currentCatalogFilter;
    private String currentSchemaFilter;

    public AbstractInformationExtractorImpl(ExtractionContext extractionContext) {
        this.extractionContext = extractionContext;
        ConfigurationService configService = extractionContext.getServiceRegistry().getService(ConfigurationService.class);
        this.useJdbcMetadataDefaultsSetting = configService.getSetting("hibernate.temp.use_jdbc_metadata_defaults", StandardConverters.BOOLEAN, Boolean.TRUE);
        String extraPhysicalTableTypesConfig = configService.getSetting("hibernate.hbm2ddl.extra_physical_table_types", StandardConverters.STRING, configService.getSetting("hibernate.hbm2dll.extra_physical_table_types", StandardConverters.STRING, ""));
        ArrayList<String> physicalTableTypesList = new ArrayList<String>();
        if (!StringHelper.isBlank(extraPhysicalTableTypesConfig)) {
            Collections.addAll(physicalTableTypesList, StringHelper.splitTrimmingTokens(",;", extraPhysicalTableTypesConfig, false));
        }
        Dialect dialect = extractionContext.getJdbcEnvironment().getDialect();
        dialect.augmentPhysicalTableTypes(physicalTableTypesList);
        this.extraPhysicalTableTypes = physicalTableTypesList.toArray(new String[0]);
        ArrayList<String> tableTypesList = new ArrayList<String>();
        tableTypesList.add("TABLE");
        tableTypesList.add("VIEW");
        if (ConfigurationHelper.getBoolean("hibernate.synonyms", configService.getSettings(), false)) {
            if (dialect instanceof DB2Dialect) {
                tableTypesList.add("ALIAS");
            }
            tableTypesList.add("SYNONYM");
        }
        Collections.addAll(tableTypesList, this.extraPhysicalTableTypes);
        dialect.augmentRecognizedTableTypes(tableTypesList);
        this.tableTypes = tableTypesList.toArray(new String[tableTypesList.size()]);
    }

    protected IdentifierHelper identifierHelper() {
        return this.extractionContext.getJdbcEnvironment().getIdentifierHelper();
    }

    protected JDBCException convertSQLException(SQLException sqlException, String message) {
        return this.extractionContext.getJdbcEnvironment().getSqlExceptionHelper().convert(sqlException, message);
    }

    protected String toMetaDataObjectName(Identifier identifier) {
        return this.extractionContext.getJdbcEnvironment().getIdentifierHelper().toMetaDataObjectName(identifier);
    }

    protected ExtractionContext getExtractionContext() {
        return this.extractionContext;
    }

    protected String getResultSetCatalogLabel() {
        return "TABLE_CAT";
    }

    protected String getResultSetSchemaLabel() {
        return "TABLE_SCHEM";
    }

    protected String getResultSetTableNameLabel() {
        return "TABLE_NAME";
    }

    protected String getResultSetTableTypeLabel() {
        return "TABLE_TYPE";
    }

    protected String getResultSetRemarksLabel() {
        return "REMARKS";
    }

    protected String getResultSetPrimaryKeyCatalogLabel() {
        return "PKTABLE_CAT";
    }

    protected String getResultSetPrimaryKeySchemaLabel() {
        return "PKTABLE_SCHEM";
    }

    protected String getResultSetPrimaryKeyTableLabel() {
        return "PKTABLE_NAME";
    }

    protected String getResultSetColumnNameLabel() {
        return "COLUMN_NAME";
    }

    protected String getResultSetSqlTypeCodeLabel() {
        return "DATA_TYPE";
    }

    protected String getResultSetTypeNameLabel() {
        return "TYPE_NAME";
    }

    protected String getResultSetColumnSizeLabel() {
        return "COLUMN_SIZE";
    }

    protected String getResultSetDecimalDigitsLabel() {
        return "DECIMAL_DIGITS";
    }

    protected String getResultSetIsNullableLabel() {
        return "IS_NULLABLE";
    }

    protected String getResultSetIndexTypeLabel() {
        return "TYPE";
    }

    protected String getResultSetIndexNameLabel() {
        return "INDEX_NAME";
    }

    protected String getResultSetForeignKeyLabel() {
        return "FK_NAME";
    }

    protected String getResultSetPrimaryKeyNameLabel() {
        return "PK_NAME";
    }

    protected String getResultSetColumnPositionColumn() {
        return "KEY_SEQ";
    }

    protected String getResultSetPrimaryKeyColumnNameLabel() {
        return "PKCOLUMN_NAME";
    }

    protected String getResultSetForeignKeyColumnNameLabel() {
        return "FKCOLUMN_NAME";
    }

    protected abstract <T> T processCatalogsResultSet(ExtractionContext.ResultSetProcessor<T> var1) throws SQLException;

    @Override
    public boolean catalogExists(Identifier catalog) {
        try {
            return this.processCatalogsResultSet(resultSet -> {
                while (resultSet.next()) {
                    String existingCatalogName = resultSet.getString(this.getResultSetCatalogLabel());
                    if (!catalog.getText().equalsIgnoreCase(existingCatalogName)) continue;
                    return true;
                }
                return false;
            });
        }
        catch (SQLException sqlException) {
            throw this.convertSQLException(sqlException, "Unable to query ResultSet for existing catalogs");
        }
    }

    protected abstract <T> T processSchemaResultSet(String var1, String var2, ExtractionContext.ResultSetProcessor<T> var3) throws SQLException;

    @Override
    public boolean schemaExists(Identifier catalog, Identifier schema) {
        String catalogFilter = this.determineCatalogFilter(catalog);
        String schemaFilter = this.determineSchemaFilter(schema);
        try {
            return this.processSchemaResultSet(catalogFilter, schemaFilter, resultSet -> {
                if (!resultSet.next()) {
                    return false;
                }
                if (resultSet.next()) {
                    String catalogName = catalog == null ? "" : catalog.getCanonicalName();
                    String schemaName = schema == null ? "" : schema.getCanonicalName();
                    LOG.debugf("Multiple schemas found with that name [%s.%s]", catalogName, schemaName);
                }
                return true;
            });
        }
        catch (SQLException sqlException) {
            throw this.convertSQLException(sqlException, "Unable to query ResultSet for existing schemas");
        }
    }

    protected String determineCatalogFilter(Identifier catalog) {
        Identifier identifierToUse = catalog;
        if (identifierToUse == null) {
            identifierToUse = this.extractionContext.getDefaultCatalog();
        }
        return this.extractionContext.getJdbcEnvironment().getIdentifierHelper().toMetaDataCatalogName(identifierToUse);
    }

    protected String determineSchemaFilter(Identifier schema) {
        Identifier identifierToUse = schema;
        if (identifierToUse == null) {
            identifierToUse = this.extractionContext.getDefaultSchema();
        }
        return this.extractionContext.getJdbcEnvironment().getIdentifierHelper().toMetaDataSchemaName(identifierToUse);
    }

    private TableInformation extractTableInformation(ResultSet resultSet) throws SQLException {
        QualifiedTableName tableName = this.extractTableName(resultSet);
        TableInformationImpl tableInformation = new TableInformationImpl(this, this.identifierHelper(), tableName, this.isPhysicalTableType(resultSet.getString(this.getResultSetTableTypeLabel())), resultSet.getString(this.getResultSetRemarksLabel()));
        return tableInformation;
    }

    @Override
    public TableInformation getTable(Identifier catalog, Identifier schema, Identifier tableName) {
        TableInformation tableInfo;
        if (catalog != null || schema != null) {
            return this.locateTableInNamespace(catalog, schema, tableName);
        }
        JdbcEnvironment jdbcEnvironment = this.extractionContext.getJdbcEnvironment();
        Identifier currentSchema = this.getCurrentSchema(jdbcEnvironment);
        Identifier currentCatalog = this.getCurrentCatalog(jdbcEnvironment);
        if ((currentCatalog != null || currentSchema != null) && (tableInfo = this.locateTableInNamespace(currentCatalog, currentSchema, tableName)) != null) {
            return tableInfo;
        }
        if ((this.extractionContext.getDefaultCatalog() != null || this.extractionContext.getDefaultSchema() != null) && (tableInfo = this.locateTableInNamespace(this.extractionContext.getDefaultCatalog(), this.extractionContext.getDefaultSchema(), tableName)) != null) {
            return tableInfo;
        }
        try {
            String tableNameFilter = this.toMetaDataObjectName(tableName);
            return this.processTableResultSet(null, null, tableNameFilter, this.tableTypes, resultSet -> this.extractTableInformation(null, null, tableName, resultSet));
        }
        catch (SQLException sqlException) {
            throw this.convertSQLException(sqlException, "Error accessing table metadata");
        }
    }

    private Identifier getCurrentSchema(JdbcEnvironment jdbcEnvironment) {
        if (jdbcEnvironment.getNameQualifierSupport() == NameQualifierSupport.CATALOG) {
            return null;
        }
        if (this.currentSchema != null) {
            return this.currentSchema;
        }
        Identifier schema = jdbcEnvironment.getCurrentSchema();
        if (schema != null) {
            this.currentSchema = schema;
        }
        if (!this.useJdbcMetadataDefaultsSetting) {
            try {
                this.currentSchema = this.extractionContext.getJdbcEnvironment().getIdentifierHelper().toIdentifier(this.extractionContext.getJdbcConnection().getSchema());
            }
            catch (SQLException ignore) {
                LOG.sqlWarning(ignore.getErrorCode(), ignore.getSQLState());
            }
        }
        return this.currentSchema;
    }

    private Identifier getCurrentCatalog(JdbcEnvironment jdbcEnvironment) {
        if (jdbcEnvironment.getNameQualifierSupport() == NameQualifierSupport.SCHEMA) {
            return null;
        }
        if (this.currentCatalog != null) {
            return this.currentCatalog;
        }
        Identifier catalog = jdbcEnvironment.getCurrentCatalog();
        if (catalog != null) {
            this.currentCatalog = catalog;
        }
        if (!this.useJdbcMetadataDefaultsSetting) {
            try {
                this.currentCatalog = this.extractionContext.getJdbcEnvironment().getIdentifierHelper().toIdentifier(this.extractionContext.getJdbcConnection().getCatalog());
            }
            catch (SQLException ignore) {
                LOG.sqlWarning(ignore.getErrorCode(), ignore.getSQLState());
            }
        }
        return this.currentCatalog;
    }

    private String getCurrentCatalogFilter(JdbcEnvironment jdbcEnvironment) {
        if (this.currentCatalogFilter != null) {
            return this.currentCatalogFilter;
        }
        Identifier currentCatalog = jdbcEnvironment.getCurrentCatalog();
        if (currentCatalog != null) {
            this.currentCatalogFilter = this.toMetaDataObjectName(currentCatalog);
        }
        if (!this.useJdbcMetadataDefaultsSetting) {
            try {
                this.currentCatalogFilter = this.extractionContext.getJdbcConnection().getCatalog();
            }
            catch (SQLException ignore) {
                LOG.sqlWarning(ignore.getErrorCode(), ignore.getSQLState());
            }
        }
        return this.currentCatalogFilter;
    }

    private String getCurrentSchemaFilter(JdbcEnvironment jdbcEnvironment) {
        if (this.currentSchemaFilter != null) {
            return this.currentSchemaFilter;
        }
        Identifier currentSchema = jdbcEnvironment.getCurrentSchema();
        if (currentSchema != null) {
            this.currentSchemaFilter = this.toMetaDataObjectName(currentSchema);
        }
        if (!this.useJdbcMetadataDefaultsSetting) {
            try {
                this.currentSchemaFilter = this.extractionContext.getJdbcConnection().getSchema();
            }
            catch (SQLException ignore) {
                LOG.sqlWarning(ignore.getErrorCode(), ignore.getSQLState());
            }
        }
        return this.currentSchemaFilter;
    }

    @Override
    public NameSpaceTablesInformation getTables(Identifier catalog, Identifier schema) {
        String currentSchemaFilter;
        String currentCatalogFilter;
        JdbcEnvironment jdbcEnvironment = this.extractionContext.getJdbcEnvironment();
        NameQualifierSupport nameQualifierSupport = jdbcEnvironment.getNameQualifierSupport();
        String catalogFilter = nameQualifierSupport.supportsCatalogs() ? (catalog == null ? ((currentCatalogFilter = this.getCurrentCatalogFilter(jdbcEnvironment)) != null ? currentCatalogFilter : (this.extractionContext.getDefaultCatalog() != null ? this.toMetaDataObjectName(this.extractionContext.getDefaultCatalog()) : "")) : this.toMetaDataObjectName(catalog)) : null;
        String schemaFilter = nameQualifierSupport.supportsSchemas() ? (schema == null ? ((currentSchemaFilter = this.getCurrentSchemaFilter(jdbcEnvironment)) != null ? currentSchemaFilter : (this.extractionContext.getDefaultSchema() != null ? this.toMetaDataObjectName(this.extractionContext.getDefaultSchema()) : "")) : this.toMetaDataObjectName(schema)) : null;
        try {
            return this.processTableResultSet(catalogFilter, schemaFilter, "%", this.tableTypes, resultSet -> {
                NameSpaceTablesInformation tablesInformation = this.extractNameSpaceTablesInformation(resultSet);
                this.populateTablesWithColumns(catalogFilter, schemaFilter, tablesInformation);
                return tablesInformation;
            });
        }
        catch (SQLException sqlException) {
            throw this.convertSQLException(sqlException, "Error accessing table metadata");
        }
    }

    protected abstract <T> T processColumnsResultSet(String var1, String var2, String var3, String var4, ExtractionContext.ResultSetProcessor<T> var5) throws SQLException;

    private void populateTablesWithColumns(String catalogFilter, String schemaFilter, NameSpaceTablesInformation tables) {
        try {
            this.processColumnsResultSet(catalogFilter, schemaFilter, null, "%", resultSet -> {
                String currentTableName = "";
                TableInformation currentTable = null;
                while (resultSet.next()) {
                    if (!currentTableName.equals(resultSet.getString(this.getResultSetTableNameLabel()))) {
                        currentTableName = resultSet.getString(this.getResultSetTableNameLabel());
                        currentTable = tables.getTableInformation(currentTableName);
                    }
                    if (currentTable == null) continue;
                    this.addExtractedColumnInformation(currentTable, resultSet);
                }
                return null;
            });
        }
        catch (SQLException e) {
            throw this.convertSQLException(e, "Error accessing tables metadata");
        }
    }

    protected void addExtractedColumnInformation(TableInformation tableInformation, ResultSet resultSet) throws SQLException {
        ColumnInformationImpl columnInformation = new ColumnInformationImpl(tableInformation, DatabaseIdentifier.toIdentifier(resultSet.getString(this.getResultSetColumnNameLabel())), resultSet.getInt(this.getResultSetSqlTypeCodeLabel()), new StringTokenizer(resultSet.getString(this.getResultSetTypeNameLabel()), "() ").nextToken(), resultSet.getInt(this.getResultSetColumnSizeLabel()), resultSet.getInt(this.getResultSetDecimalDigitsLabel()), this.interpretTruthValue(resultSet.getString(this.getResultSetIsNullableLabel())));
        tableInformation.addColumn(columnInformation);
    }

    private NameSpaceTablesInformation extractNameSpaceTablesInformation(ResultSet resultSet) throws SQLException {
        NameSpaceTablesInformation tables = new NameSpaceTablesInformation(this.identifierHelper());
        while (resultSet.next()) {
            TableInformation tableInformation = this.extractTableInformation(resultSet);
            tables.addTableInformation(tableInformation);
        }
        return tables;
    }

    protected abstract <T> T processTableResultSet(String var1, String var2, String var3, String[] var4, ExtractionContext.ResultSetProcessor<T> var5) throws SQLException;

    private TableInformation locateTableInNamespace(Identifier catalog, Identifier schema, Identifier tableName) {
        String schemaFilter;
        Identifier schemaToUse;
        String catalogFilter;
        Identifier catalogToUse;
        if (this.extractionContext.getJdbcEnvironment().getNameQualifierSupport().supportsCatalogs()) {
            if (catalog == null) {
                String defaultCatalog = "";
                if (this.extractionContext.getJdbcEnvironment().getNameQualifierSupport().supportsCatalogs()) {
                    try {
                        defaultCatalog = this.extractionContext.getJdbcConnection().getCatalog();
                    }
                    catch (SQLException sQLException) {
                        // empty catch block
                    }
                }
                catalogToUse = null;
                catalogFilter = defaultCatalog;
            } else {
                catalogToUse = catalog;
                catalogFilter = this.toMetaDataObjectName(catalog);
            }
        } else {
            catalogToUse = null;
            catalogFilter = null;
        }
        if (this.extractionContext.getJdbcEnvironment().getNameQualifierSupport().supportsSchemas()) {
            if (schema == null) {
                schemaToUse = null;
                schemaFilter = "";
            } else {
                schemaToUse = schema;
                schemaFilter = this.toMetaDataObjectName(schema);
            }
        } else {
            schemaToUse = null;
            schemaFilter = null;
        }
        String tableNameFilter = this.toMetaDataObjectName(tableName);
        try {
            return this.processTableResultSet(catalogFilter, schemaFilter, tableNameFilter, this.tableTypes, resultSet -> this.extractTableInformation(catalogToUse, schemaToUse, tableName, resultSet));
        }
        catch (SQLException sqlException) {
            throw this.convertSQLException(sqlException, "Error accessing table metadata");
        }
    }

    private TableInformation extractTableInformation(Identifier catalog, Identifier schema, Identifier tableName, ResultSet resultSet) throws SQLException {
        boolean found = false;
        TableInformation tableInformation = null;
        while (resultSet.next()) {
            if (!tableName.equals(Identifier.toIdentifier(resultSet.getString(this.getResultSetTableNameLabel()), tableName.isQuoted()))) continue;
            if (found) {
                LOG.multipleTablesFound(tableName.render());
                String catalogName = catalog == null ? "" : catalog.render();
                String schemaName = schema == null ? "" : schema.render();
                throw new SchemaExtractionException(String.format(Locale.ENGLISH, "More than one table found in namespace (%s, %s) : %s", catalogName, schemaName, tableName.render()));
            }
            found = true;
            tableInformation = this.extractTableInformation(resultSet);
            this.addColumns(tableInformation);
        }
        if (!found) {
            LOG.tableNotFound(tableName.render());
        }
        return tableInformation;
    }

    protected abstract String getResultSetTableTypesPhysicalTableConstant();

    protected boolean isPhysicalTableType(String tableType) {
        if (this.extraPhysicalTableTypes == null) {
            return this.getResultSetTableTypesPhysicalTableConstant().equalsIgnoreCase(tableType);
        }
        if (this.getResultSetTableTypesPhysicalTableConstant().equalsIgnoreCase(tableType)) {
            return true;
        }
        for (String extraPhysicalTableType : this.extraPhysicalTableTypes) {
            if (!extraPhysicalTableType.equalsIgnoreCase(tableType)) continue;
            return true;
        }
        return false;
    }

    protected void addColumns(TableInformation tableInformation) {
        QualifiedTableName tableName = tableInformation.getName();
        Identifier catalog = tableName.getCatalogName();
        Identifier schema = tableName.getSchemaName();
        String catalogFilter = catalog == null ? "" : catalog.getText();
        String schemaFilter = schema == null ? "" : schema.getText();
        try {
            this.processColumnsResultSet(catalogFilter, schemaFilter, tableName.getTableName().getText(), "%", resultSet -> {
                while (resultSet.next()) {
                    this.addExtractedColumnInformation(tableInformation, resultSet);
                }
                return null;
            });
        }
        catch (SQLException e) {
            throw this.convertSQLException(e, "Error accessing tables metadata");
        }
    }

    protected TruthValue interpretNullable(int nullable) {
        switch (nullable) {
            case 1: {
                return TruthValue.TRUE;
            }
            case 0: {
                return TruthValue.FALSE;
            }
        }
        return TruthValue.UNKNOWN;
    }

    private TruthValue interpretTruthValue(String nullable) {
        if ("yes".equalsIgnoreCase(nullable)) {
            return TruthValue.TRUE;
        }
        if ("no".equalsIgnoreCase(nullable)) {
            return TruthValue.FALSE;
        }
        return TruthValue.UNKNOWN;
    }

    protected abstract <T> T processPrimaryKeysResultSet(String var1, String var2, Identifier var3, ExtractionContext.ResultSetProcessor<T> var4) throws SQLException;

    @Override
    public PrimaryKeyInformation getPrimaryKey(TableInformationImpl tableInformation) {
        QualifiedTableName tableName = tableInformation.getName();
        Identifier catalog = tableName.getCatalogName();
        Identifier schema = tableName.getSchemaName();
        String catalogFilter = catalog == null ? "" : catalog.getText();
        String schemaFilter = schema == null ? "" : schema.getText();
        try {
            return this.processPrimaryKeysResultSet(catalogFilter, schemaFilter, tableInformation.getName().getTableName(), resultSet -> this.extractPrimaryKeyInformation(tableInformation, resultSet));
        }
        catch (SQLException e) {
            throw this.convertSQLException(e, "Error while reading primary key meta data for " + tableInformation.getName().toString());
        }
    }

    private PrimaryKeyInformation extractPrimaryKeyInformation(TableInformation tableInformation, ResultSet resultSet) throws SQLException {
        ArrayList<ColumnInformation> pkColumns = new ArrayList<ColumnInformation>();
        boolean firstPass = true;
        DatabaseIdentifier pkIdentifier = null;
        while (resultSet.next()) {
            DatabaseIdentifier currentPkIdentifier;
            String currentPkName = resultSet.getString(this.getResultSetPrimaryKeyNameLabel());
            DatabaseIdentifier databaseIdentifier = currentPkIdentifier = currentPkName == null ? null : DatabaseIdentifier.toIdentifier(currentPkName);
            if (firstPass) {
                pkIdentifier = currentPkIdentifier;
                firstPass = false;
            } else if (!Objects.equals(pkIdentifier, currentPkIdentifier)) {
                throw new SchemaExtractionException(String.format("Encountered primary keys differing name on table %s", tableInformation.getName().toString()));
            }
            int columnPosition = resultSet.getInt(this.getResultSetColumnPositionColumn());
            DatabaseIdentifier columnIdentifier = DatabaseIdentifier.toIdentifier(resultSet.getString(this.getResultSetColumnNameLabel()));
            ColumnInformation column = tableInformation.getColumn(columnIdentifier);
            pkColumns.add(columnPosition - 1, column);
        }
        if (firstPass) {
            return null;
        }
        for (int i = 0; i < pkColumns.size(); ++i) {
            if (pkColumns.get(i) != null) continue;
            throw new SchemaExtractionException("Primary Key information was missing for KEY_SEQ = " + (i + 1));
        }
        return new PrimaryKeyInformationImpl(pkIdentifier, pkColumns);
    }

    protected abstract <T> T processIndexInfoResultSet(String var1, String var2, String var3, boolean var4, boolean var5, ExtractionContext.ResultSetProcessor<T> var6) throws SQLException;

    @Override
    public Iterable<IndexInformation> getIndexes(TableInformation tableInformation) {
        HashMap builders = new HashMap();
        QualifiedTableName tableName = tableInformation.getName();
        Identifier catalog = tableName.getCatalogName();
        Identifier schema = tableName.getSchemaName();
        String catalogFilter = catalog == null ? "" : catalog.getText();
        String schemaFilter = schema == null ? "" : schema.getText();
        try {
            this.processIndexInfoResultSet(catalogFilter, schemaFilter, tableName.getTableName().getText(), false, true, resultSet -> {
                while (resultSet.next()) {
                    DatabaseIdentifier columnIdentifier;
                    ColumnInformation columnInformation;
                    if (resultSet.getShort(this.getResultSetIndexTypeLabel()) == 0) continue;
                    DatabaseIdentifier indexIdentifier = DatabaseIdentifier.toIdentifier(resultSet.getString(this.getResultSetIndexNameLabel()));
                    IndexInformationImpl.Builder builder = (IndexInformationImpl.Builder)builders.get(indexIdentifier);
                    if (builder == null) {
                        builder = IndexInformationImpl.builder(indexIdentifier);
                        builders.put(indexIdentifier, builder);
                    }
                    if ((columnInformation = tableInformation.getColumn(columnIdentifier = DatabaseIdentifier.toIdentifier(resultSet.getString(this.getResultSetColumnNameLabel())))) == null) {
                        LOG.logCannotLocateIndexColumnInformation(columnIdentifier.getText(), indexIdentifier.getText());
                    }
                    builder.addColumn(columnInformation);
                }
                return null;
            });
        }
        catch (SQLException e) {
            throw this.convertSQLException(e, "Error accessing index information: " + tableInformation.getName().toString());
        }
        ArrayList<IndexInformation> indexes = new ArrayList<IndexInformation>();
        for (IndexInformationImpl.Builder builder : builders.values()) {
            IndexInformationImpl index = builder.build();
            indexes.add(index);
        }
        return indexes;
    }

    protected abstract <T> T processImportedKeysResultSet(String var1, String var2, String var3, ExtractionContext.ResultSetProcessor<T> var4) throws SQLException;

    @Override
    public Iterable<ForeignKeyInformation> getForeignKeys(TableInformation tableInformation) {
        HashMap fkBuilders = new HashMap();
        QualifiedTableName tableName = tableInformation.getName();
        Identifier catalog = tableName.getCatalogName();
        Identifier schema = tableName.getSchemaName();
        String catalogFilter = catalog == null ? "" : catalog.getText();
        String schemaFilter = schema == null ? "" : schema.getText();
        try {
            this.processImportedKeysResultSet(catalogFilter, schemaFilter, tableInformation.getName().getTableName().getText(), resultSet -> {
                while (resultSet.next()) {
                    DatabaseIdentifier fkIdentifier = DatabaseIdentifier.toIdentifier(resultSet.getString(this.getResultSetForeignKeyLabel()));
                    ForeignKeyBuilder fkBuilder = (ForeignKeyBuilder)fkBuilders.get(fkIdentifier);
                    if (fkBuilder == null) {
                        fkBuilder = this.generateForeignKeyBuilder(fkIdentifier);
                        fkBuilders.put(fkIdentifier, fkBuilder);
                    }
                    QualifiedTableName incomingPkTableName = this.extractPrimaryKeyTableName(resultSet);
                    TableInformation pkTableInformation = this.extractionContext.getDatabaseObjectAccess().locateTableInformation(incomingPkTableName);
                    if (pkTableInformation == null) continue;
                    DatabaseIdentifier fkColumnIdentifier = DatabaseIdentifier.toIdentifier(resultSet.getString(this.getResultSetForeignKeyColumnNameLabel()));
                    DatabaseIdentifier pkColumnIdentifier = DatabaseIdentifier.toIdentifier(resultSet.getString(this.getResultSetPrimaryKeyColumnNameLabel()));
                    fkBuilder.addColumnMapping(tableInformation.getColumn(fkColumnIdentifier), pkTableInformation.getColumn(pkColumnIdentifier));
                }
                return null;
            });
        }
        catch (SQLException e) {
            throw this.convertSQLException(e, "Error accessing column metadata: " + tableInformation.getName().toString());
        }
        ArrayList<ForeignKeyInformation> fks = new ArrayList<ForeignKeyInformation>();
        for (ForeignKeyBuilder fkBuilder : fkBuilders.values()) {
            ForeignKeyInformation fk = fkBuilder.build();
            fks.add(fk);
        }
        return fks;
    }

    private ForeignKeyBuilder generateForeignKeyBuilder(Identifier fkIdentifier) {
        return new ForeignKeyBuilderImpl(fkIdentifier);
    }

    private QualifiedTableName extractPrimaryKeyTableName(ResultSet resultSet) throws SQLException {
        String incomingCatalogName = resultSet.getString(this.getResultSetPrimaryKeyCatalogLabel());
        String incomingSchemaName = resultSet.getString(this.getResultSetPrimaryKeySchemaLabel());
        String incomingTableName = resultSet.getString(this.getResultSetPrimaryKeyTableLabel());
        DatabaseIdentifier catalog = DatabaseIdentifier.toIdentifier(incomingCatalogName);
        DatabaseIdentifier schema = DatabaseIdentifier.toIdentifier(incomingSchemaName);
        DatabaseIdentifier table = DatabaseIdentifier.toIdentifier(incomingTableName);
        return new QualifiedTableName(catalog, schema, table);
    }

    private QualifiedTableName extractTableName(ResultSet resultSet) throws SQLException {
        String incomingCatalogName = resultSet.getString(this.getResultSetCatalogLabel());
        String incomingSchemaName = resultSet.getString(this.getResultSetSchemaLabel());
        String incomingTableName = resultSet.getString(this.getResultSetTableNameLabel());
        DatabaseIdentifier catalog = DatabaseIdentifier.toIdentifier(incomingCatalogName);
        DatabaseIdentifier schema = DatabaseIdentifier.toIdentifier(incomingSchemaName);
        DatabaseIdentifier table = DatabaseIdentifier.toIdentifier(incomingTableName);
        return new QualifiedTableName(catalog, schema, table);
    }

    protected static class ForeignKeyBuilderImpl
    implements ForeignKeyBuilder {
        private final Identifier fkIdentifier;
        private final List<ForeignKeyInformation.ColumnReferenceMapping> columnMappingList = new ArrayList<ForeignKeyInformation.ColumnReferenceMapping>();

        public ForeignKeyBuilderImpl(Identifier fkIdentifier) {
            this.fkIdentifier = fkIdentifier;
        }

        @Override
        public ForeignKeyBuilder addColumnMapping(ColumnInformation referencing, ColumnInformation referenced) {
            this.columnMappingList.add(new ForeignKeyInformationImpl.ColumnReferenceMappingImpl(referencing, referenced));
            return this;
        }

        @Override
        public ForeignKeyInformationImpl build() {
            if (this.columnMappingList.isEmpty()) {
                throw new SchemaManagementException("Attempt to resolve foreign key metadata from JDBC metadata failed to find column mappings for foreign key named [" + this.fkIdentifier.getText() + "]");
            }
            return new ForeignKeyInformationImpl(this.fkIdentifier, this.columnMappingList);
        }
    }

    protected static interface ForeignKeyBuilder {
        public ForeignKeyBuilder addColumnMapping(ColumnInformation var1, ColumnInformation var2);

        public ForeignKeyInformation build();
    }
}

