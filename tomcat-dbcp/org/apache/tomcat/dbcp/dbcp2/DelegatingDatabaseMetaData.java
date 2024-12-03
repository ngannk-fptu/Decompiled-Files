/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingResultSet;
import org.apache.tomcat.dbcp.dbcp2.Jdbc41Bridge;

public class DelegatingDatabaseMetaData
implements DatabaseMetaData {
    private final DatabaseMetaData databaseMetaData;
    private final DelegatingConnection<?> connection;

    public DelegatingDatabaseMetaData(DelegatingConnection<?> connection, DatabaseMetaData databaseMetaData) {
        this.connection = connection;
        this.databaseMetaData = databaseMetaData;
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        try {
            return this.databaseMetaData.allProceduresAreCallable();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        try {
            return this.databaseMetaData.allTablesAreSelectable();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        try {
            return this.databaseMetaData.autoCommitFailureClosesAllResultSets();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        try {
            return this.databaseMetaData.dataDefinitionCausesTransactionCommit();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        try {
            return this.databaseMetaData.dataDefinitionIgnoredInTransactions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        try {
            return this.databaseMetaData.deletesAreDetected(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        try {
            return this.databaseMetaData.doesMaxRowSizeIncludeBlobs();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        this.connection.checkOpen();
        try {
            return Jdbc41Bridge.generatedKeyAlwaysReturned(this.databaseMetaData);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getBestRowIdentifier(catalog, schema, table, scope, nullable));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getCatalogs());
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        try {
            return this.databaseMetaData.getCatalogSeparator();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        try {
            return this.databaseMetaData.getCatalogTerm();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getClientInfoProperties());
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getColumnPrivileges(catalog, schema, table, columnNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog, foreignSchema, foreignTable));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseMajorVersion();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseMinorVersion();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseProductName();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseProductVersion();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        try {
            return this.databaseMetaData.getDefaultTransactionIsolation();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    public DatabaseMetaData getDelegate() {
        return this.databaseMetaData;
    }

    @Override
    public int getDriverMajorVersion() {
        return this.databaseMetaData.getDriverMajorVersion();
    }

    @Override
    public int getDriverMinorVersion() {
        return this.databaseMetaData.getDriverMinorVersion();
    }

    @Override
    public String getDriverName() throws SQLException {
        try {
            return this.databaseMetaData.getDriverName();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getDriverVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDriverVersion();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getExportedKeys(catalog, schema, table));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        try {
            return this.databaseMetaData.getExtraNameCharacters();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getFunctions(catalog, schemaPattern, functionNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        try {
            return this.databaseMetaData.getIdentifierQuoteString();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getImportedKeys(catalog, schema, table));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getIndexInfo(catalog, schema, table, unique, approximate));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    public DatabaseMetaData getInnermostDelegate() {
        DatabaseMetaData m = this.databaseMetaData;
        while (m instanceof DelegatingDatabaseMetaData) {
            if (this != (m = ((DelegatingDatabaseMetaData)m).getDelegate())) continue;
            return null;
        }
        return m;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getJDBCMajorVersion();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getJDBCMinorVersion();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxBinaryLiteralLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxCatalogNameLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxCharLiteralLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnNameLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInGroupBy();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInIndex();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInOrderBy();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInSelect();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInTable();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxConnections() throws SQLException {
        try {
            return this.databaseMetaData.getMaxConnections();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxCursorNameLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxIndexLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public long getMaxLogicalLobSize() throws SQLException {
        try {
            return this.databaseMetaData.getMaxLogicalLobSize();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxProcedureNameLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        try {
            return this.databaseMetaData.getMaxRowSize();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxSchemaNameLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxStatementLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxStatements() throws SQLException {
        try {
            return this.databaseMetaData.getMaxStatements();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxTableNameLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        try {
            return this.databaseMetaData.getMaxTablesInSelect();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxUserNameLength();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getNumericFunctions();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getPrimaryKeys(catalog, schema, table));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getProcedures(catalog, schemaPattern, procedureNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        try {
            return this.databaseMetaData.getProcedureTerm();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, Jdbc41Bridge.getPseudoColumns(this.databaseMetaData, catalog, schemaPattern, tableNamePattern, columnNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            return this.databaseMetaData.getResultSetHoldability();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        try {
            return this.databaseMetaData.getRowIdLifetime();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSchemas());
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSchemas(catalog, schemaPattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        try {
            return this.databaseMetaData.getSchemaTerm();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        try {
            return this.databaseMetaData.getSearchStringEscape();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        try {
            return this.databaseMetaData.getSQLKeywords();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public int getSQLStateType() throws SQLException {
        try {
            return this.databaseMetaData.getSQLStateType();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public String getStringFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getStringFunctions();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSuperTables(catalog, schemaPattern, tableNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSuperTypes(catalog, schemaPattern, typeNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getSystemFunctions();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTablePrivileges(catalog, schemaPattern, tableNamePattern));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTableTypes());
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getTimeDateFunctions();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTypeInfo());
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getUDTs(catalog, schemaPattern, typeNamePattern, types));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getURL() throws SQLException {
        try {
            return this.databaseMetaData.getURL();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public String getUserName() throws SQLException {
        try {
            return this.databaseMetaData.getUserName();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getVersionColumns(catalog, schema, table));
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    protected void handleException(SQLException e) throws SQLException {
        if (this.connection == null) {
            throw e;
        }
        this.connection.handleException(e);
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        try {
            return this.databaseMetaData.insertsAreDetected(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        try {
            return this.databaseMetaData.isCatalogAtStart();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        try {
            return this.databaseMetaData.isReadOnly();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return true;
        }
        if (iface.isAssignableFrom(this.databaseMetaData.getClass())) {
            return true;
        }
        return this.databaseMetaData.isWrapperFor(iface);
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        try {
            return this.databaseMetaData.locatorsUpdateCopy();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        try {
            return this.databaseMetaData.nullPlusNonNullIsNull();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedAtEnd();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedAtStart();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedHigh();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedLow();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        try {
            return this.databaseMetaData.othersDeletesAreVisible(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        try {
            return this.databaseMetaData.othersInsertsAreVisible(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        try {
            return this.databaseMetaData.othersUpdatesAreVisible(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        try {
            return this.databaseMetaData.ownDeletesAreVisible(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        try {
            return this.databaseMetaData.ownInsertsAreVisible(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        try {
            return this.databaseMetaData.ownUpdatesAreVisible(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesLowerCaseIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesLowerCaseQuotedIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesMixedCaseIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesMixedCaseQuotedIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesUpperCaseIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesUpperCaseQuotedIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        try {
            return this.databaseMetaData.supportsAlterTableWithAddColumn();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        try {
            return this.databaseMetaData.supportsAlterTableWithDropColumn();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        try {
            return this.databaseMetaData.supportsANSI92EntryLevelSQL();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        try {
            return this.databaseMetaData.supportsANSI92FullSQL();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        try {
            return this.databaseMetaData.supportsANSI92IntermediateSQL();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        try {
            return this.databaseMetaData.supportsBatchUpdates();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInDataManipulation();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInIndexDefinitions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInPrivilegeDefinitions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInProcedureCalls();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInTableDefinitions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        try {
            return this.databaseMetaData.supportsColumnAliasing();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        try {
            return this.databaseMetaData.supportsConvert();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        try {
            return this.databaseMetaData.supportsConvert(fromType, toType);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        try {
            return this.databaseMetaData.supportsCoreSQLGrammar();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        try {
            return this.databaseMetaData.supportsCorrelatedSubqueries();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        try {
            return this.databaseMetaData.supportsDataDefinitionAndDataManipulationTransactions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        try {
            return this.databaseMetaData.supportsDataManipulationTransactionsOnly();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        try {
            return this.databaseMetaData.supportsDifferentTableCorrelationNames();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        try {
            return this.databaseMetaData.supportsExpressionsInOrderBy();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        try {
            return this.databaseMetaData.supportsExtendedSQLGrammar();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        try {
            return this.databaseMetaData.supportsFullOuterJoins();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        try {
            return this.databaseMetaData.supportsGetGeneratedKeys();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        try {
            return this.databaseMetaData.supportsGroupBy();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        try {
            return this.databaseMetaData.supportsGroupByBeyondSelect();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        try {
            return this.databaseMetaData.supportsGroupByUnrelated();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        try {
            return this.databaseMetaData.supportsIntegrityEnhancementFacility();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        try {
            return this.databaseMetaData.supportsLikeEscapeClause();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        try {
            return this.databaseMetaData.supportsLimitedOuterJoins();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        try {
            return this.databaseMetaData.supportsMinimumSQLGrammar();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.supportsMixedCaseIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.supportsMixedCaseQuotedIdentifiers();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        try {
            return this.databaseMetaData.supportsMultipleOpenResults();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        try {
            return this.databaseMetaData.supportsMultipleResultSets();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        try {
            return this.databaseMetaData.supportsMultipleTransactions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        try {
            return this.databaseMetaData.supportsNamedParameters();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        try {
            return this.databaseMetaData.supportsNonNullableColumns();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenCursorsAcrossCommit();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenCursorsAcrossRollback();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenStatementsAcrossCommit();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenStatementsAcrossRollback();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        try {
            return this.databaseMetaData.supportsOrderByUnrelated();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        try {
            return this.databaseMetaData.supportsOuterJoins();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        try {
            return this.databaseMetaData.supportsPositionedDelete();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        try {
            return this.databaseMetaData.supportsPositionedUpdate();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsRefCursors() throws SQLException {
        try {
            return this.databaseMetaData.supportsRefCursors();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        try {
            return this.databaseMetaData.supportsResultSetConcurrency(type, concurrency);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        try {
            return this.databaseMetaData.supportsResultSetHoldability(holdability);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        try {
            return this.databaseMetaData.supportsResultSetType(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        try {
            return this.databaseMetaData.supportsSavepoints();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInDataManipulation();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInIndexDefinitions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInPrivilegeDefinitions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInProcedureCalls();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInTableDefinitions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        try {
            return this.databaseMetaData.supportsSelectForUpdate();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        try {
            return this.databaseMetaData.supportsStatementPooling();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        try {
            return this.databaseMetaData.supportsStoredFunctionsUsingCallSyntax();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        try {
            return this.databaseMetaData.supportsStoredProcedures();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInComparisons();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInExists();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInIns();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInQuantifieds();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        try {
            return this.databaseMetaData.supportsTableCorrelationNames();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        try {
            return this.databaseMetaData.supportsTransactionIsolationLevel(level);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        try {
            return this.databaseMetaData.supportsTransactions();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        try {
            return this.databaseMetaData.supportsUnion();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        try {
            return this.databaseMetaData.supportsUnionAll();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        if (iface.isAssignableFrom(this.databaseMetaData.getClass())) {
            return iface.cast(this.databaseMetaData);
        }
        return this.databaseMetaData.unwrap(iface);
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        try {
            return this.databaseMetaData.updatesAreDetected(type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        try {
            return this.databaseMetaData.usesLocalFilePerTable();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        try {
            return this.databaseMetaData.usesLocalFiles();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }
}

