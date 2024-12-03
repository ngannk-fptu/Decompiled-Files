/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public abstract class FilterDatabaseMetaData
implements DatabaseMetaData {
    protected DatabaseMetaData inner;

    private void __setInner(DatabaseMetaData databaseMetaData) {
        this.inner = databaseMetaData;
    }

    public FilterDatabaseMetaData(DatabaseMetaData databaseMetaData) {
        this.__setInner(databaseMetaData);
    }

    public FilterDatabaseMetaData() {
    }

    public void setInner(DatabaseMetaData databaseMetaData) {
        this.__setInner(databaseMetaData);
    }

    public DatabaseMetaData getInner() {
        return this.inner;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return this.inner.autoCommitFailureClosesAllResultSets();
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        return this.inner.getCatalogs();
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return this.inner.allProceduresAreCallable();
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return this.inner.allTablesAreSelectable();
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return this.inner.dataDefinitionCausesTransactionCommit();
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return this.inner.dataDefinitionIgnoredInTransactions();
    }

    @Override
    public boolean deletesAreDetected(int n) throws SQLException {
        return this.inner.deletesAreDetected(n);
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return this.inner.doesMaxRowSizeIncludeBlobs();
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return this.inner.generatedKeyAlwaysReturned();
    }

    @Override
    public ResultSet getBestRowIdentifier(String string, String string2, String string3, int n, boolean bl) throws SQLException {
        return this.inner.getBestRowIdentifier(string, string2, string3, n, bl);
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return this.inner.getCatalogSeparator();
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return this.inner.getCatalogTerm();
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        return this.inner.getClientInfoProperties();
    }

    @Override
    public ResultSet getColumnPrivileges(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getColumnPrivileges(string, string2, string3, string4);
    }

    @Override
    public ResultSet getColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getColumns(string, string2, string3, string4);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public ResultSet getCrossReference(String string, String string2, String string3, String string4, String string5, String string6) throws SQLException {
        return this.inner.getCrossReference(string, string2, string3, string4, string5, string6);
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return this.inner.getDatabaseMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return this.inner.getDatabaseMinorVersion();
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return this.inner.getDatabaseProductName();
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return this.inner.getDatabaseProductVersion();
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return this.inner.getDefaultTransactionIsolation();
    }

    @Override
    public int getDriverMajorVersion() {
        return this.inner.getDriverMajorVersion();
    }

    @Override
    public int getDriverMinorVersion() {
        return this.inner.getDriverMinorVersion();
    }

    @Override
    public String getDriverName() throws SQLException {
        return this.inner.getDriverName();
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return this.inner.getDriverVersion();
    }

    @Override
    public ResultSet getExportedKeys(String string, String string2, String string3) throws SQLException {
        return this.inner.getExportedKeys(string, string2, string3);
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return this.inner.getExtraNameCharacters();
    }

    @Override
    public ResultSet getFunctionColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getFunctionColumns(string, string2, string3, string4);
    }

    @Override
    public ResultSet getFunctions(String string, String string2, String string3) throws SQLException {
        return this.inner.getFunctions(string, string2, string3);
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return this.inner.getIdentifierQuoteString();
    }

    @Override
    public ResultSet getImportedKeys(String string, String string2, String string3) throws SQLException {
        return this.inner.getImportedKeys(string, string2, string3);
    }

    @Override
    public ResultSet getIndexInfo(String string, String string2, String string3, boolean bl, boolean bl2) throws SQLException {
        return this.inner.getIndexInfo(string, string2, string3, bl, bl2);
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return this.inner.getJDBCMajorVersion();
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return this.inner.getJDBCMinorVersion();
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return this.inner.getMaxBinaryLiteralLength();
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return this.inner.getMaxCatalogNameLength();
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return this.inner.getMaxCharLiteralLength();
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return this.inner.getMaxColumnNameLength();
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return this.inner.getMaxColumnsInGroupBy();
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return this.inner.getMaxColumnsInIndex();
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return this.inner.getMaxColumnsInOrderBy();
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return this.inner.getMaxColumnsInSelect();
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return this.inner.getMaxColumnsInTable();
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return this.inner.getMaxConnections();
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return this.inner.getMaxCursorNameLength();
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return this.inner.getMaxIndexLength();
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return this.inner.getMaxProcedureNameLength();
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return this.inner.getMaxRowSize();
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return this.inner.getMaxSchemaNameLength();
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return this.inner.getMaxStatementLength();
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return this.inner.getMaxStatements();
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return this.inner.getMaxTableNameLength();
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return this.inner.getMaxTablesInSelect();
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return this.inner.getMaxUserNameLength();
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return this.inner.getNumericFunctions();
    }

    @Override
    public ResultSet getPrimaryKeys(String string, String string2, String string3) throws SQLException {
        return this.inner.getPrimaryKeys(string, string2, string3);
    }

    @Override
    public ResultSet getProcedureColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getProcedureColumns(string, string2, string3, string4);
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return this.inner.getProcedureTerm();
    }

    @Override
    public ResultSet getProcedures(String string, String string2, String string3) throws SQLException {
        return this.inner.getProcedures(string, string2, string3);
    }

    @Override
    public ResultSet getPseudoColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getPseudoColumns(string, string2, string3, string4);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.inner.getResultSetHoldability();
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return this.inner.getRowIdLifetime();
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        return this.inner.getSQLKeywords();
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return this.inner.getSQLStateType();
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return this.inner.getSchemaTerm();
    }

    @Override
    public ResultSet getSchemas(String string, String string2) throws SQLException {
        return this.inner.getSchemas(string, string2);
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        return this.inner.getSchemas();
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return this.inner.getSearchStringEscape();
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return this.inner.getStringFunctions();
    }

    @Override
    public ResultSet getSuperTables(String string, String string2, String string3) throws SQLException {
        return this.inner.getSuperTables(string, string2, string3);
    }

    @Override
    public ResultSet getSuperTypes(String string, String string2, String string3) throws SQLException {
        return this.inner.getSuperTypes(string, string2, string3);
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return this.inner.getSystemFunctions();
    }

    @Override
    public ResultSet getTablePrivileges(String string, String string2, String string3) throws SQLException {
        return this.inner.getTablePrivileges(string, string2, string3);
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        return this.inner.getTableTypes();
    }

    @Override
    public ResultSet getTables(String string, String string2, String string3, String[] stringArray) throws SQLException {
        return this.inner.getTables(string, string2, string3, stringArray);
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return this.inner.getTimeDateFunctions();
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return this.inner.getTypeInfo();
    }

    @Override
    public ResultSet getUDTs(String string, String string2, String string3, int[] nArray) throws SQLException {
        return this.inner.getUDTs(string, string2, string3, nArray);
    }

    @Override
    public String getUserName() throws SQLException {
        return this.inner.getUserName();
    }

    @Override
    public ResultSet getVersionColumns(String string, String string2, String string3) throws SQLException {
        return this.inner.getVersionColumns(string, string2, string3);
    }

    @Override
    public boolean insertsAreDetected(int n) throws SQLException {
        return this.inner.insertsAreDetected(n);
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return this.inner.isCatalogAtStart();
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return this.inner.locatorsUpdateCopy();
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return this.inner.nullPlusNonNullIsNull();
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return this.inner.nullsAreSortedAtEnd();
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return this.inner.nullsAreSortedAtStart();
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return this.inner.nullsAreSortedHigh();
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return this.inner.nullsAreSortedLow();
    }

    @Override
    public boolean othersDeletesAreVisible(int n) throws SQLException {
        return this.inner.othersDeletesAreVisible(n);
    }

    @Override
    public boolean othersInsertsAreVisible(int n) throws SQLException {
        return this.inner.othersInsertsAreVisible(n);
    }

    @Override
    public boolean othersUpdatesAreVisible(int n) throws SQLException {
        return this.inner.othersUpdatesAreVisible(n);
    }

    @Override
    public boolean ownDeletesAreVisible(int n) throws SQLException {
        return this.inner.ownDeletesAreVisible(n);
    }

    @Override
    public boolean ownInsertsAreVisible(int n) throws SQLException {
        return this.inner.ownInsertsAreVisible(n);
    }

    @Override
    public boolean ownUpdatesAreVisible(int n) throws SQLException {
        return this.inner.ownUpdatesAreVisible(n);
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return this.inner.storesLowerCaseIdentifiers();
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return this.inner.storesLowerCaseQuotedIdentifiers();
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return this.inner.storesMixedCaseIdentifiers();
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return this.inner.storesMixedCaseQuotedIdentifiers();
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return this.inner.storesUpperCaseIdentifiers();
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return this.inner.storesUpperCaseQuotedIdentifiers();
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return this.inner.supportsANSI92EntryLevelSQL();
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return this.inner.supportsANSI92FullSQL();
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return this.inner.supportsANSI92IntermediateSQL();
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return this.inner.supportsAlterTableWithAddColumn();
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return this.inner.supportsAlterTableWithDropColumn();
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return this.inner.supportsBatchUpdates();
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return this.inner.supportsCatalogsInDataManipulation();
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return this.inner.supportsCatalogsInIndexDefinitions();
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return this.inner.supportsCatalogsInPrivilegeDefinitions();
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return this.inner.supportsCatalogsInProcedureCalls();
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return this.inner.supportsCatalogsInTableDefinitions();
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return this.inner.supportsColumnAliasing();
    }

    @Override
    public boolean supportsConvert(int n, int n2) throws SQLException {
        return this.inner.supportsConvert(n, n2);
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return this.inner.supportsConvert();
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return this.inner.supportsCoreSQLGrammar();
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return this.inner.supportsCorrelatedSubqueries();
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return this.inner.supportsDataDefinitionAndDataManipulationTransactions();
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return this.inner.supportsDataManipulationTransactionsOnly();
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return this.inner.supportsDifferentTableCorrelationNames();
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return this.inner.supportsExpressionsInOrderBy();
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return this.inner.supportsExtendedSQLGrammar();
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return this.inner.supportsFullOuterJoins();
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return this.inner.supportsGetGeneratedKeys();
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return this.inner.supportsGroupBy();
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return this.inner.supportsGroupByBeyondSelect();
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return this.inner.supportsGroupByUnrelated();
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return this.inner.supportsIntegrityEnhancementFacility();
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return this.inner.supportsLikeEscapeClause();
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return this.inner.supportsLimitedOuterJoins();
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return this.inner.supportsMinimumSQLGrammar();
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return this.inner.supportsMixedCaseIdentifiers();
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return this.inner.supportsMixedCaseQuotedIdentifiers();
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return this.inner.supportsMultipleOpenResults();
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return this.inner.supportsMultipleResultSets();
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return this.inner.supportsMultipleTransactions();
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return this.inner.supportsNamedParameters();
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return this.inner.supportsNonNullableColumns();
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return this.inner.supportsOpenCursorsAcrossCommit();
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return this.inner.supportsOpenCursorsAcrossRollback();
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return this.inner.supportsOpenStatementsAcrossCommit();
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return this.inner.supportsOpenStatementsAcrossRollback();
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return this.inner.supportsOrderByUnrelated();
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return this.inner.supportsOuterJoins();
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return this.inner.supportsPositionedDelete();
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return this.inner.supportsPositionedUpdate();
    }

    @Override
    public boolean supportsResultSetConcurrency(int n, int n2) throws SQLException {
        return this.inner.supportsResultSetConcurrency(n, n2);
    }

    @Override
    public boolean supportsResultSetHoldability(int n) throws SQLException {
        return this.inner.supportsResultSetHoldability(n);
    }

    @Override
    public boolean supportsResultSetType(int n) throws SQLException {
        return this.inner.supportsResultSetType(n);
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return this.inner.supportsSavepoints();
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return this.inner.supportsSchemasInDataManipulation();
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return this.inner.supportsSchemasInIndexDefinitions();
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return this.inner.supportsSchemasInPrivilegeDefinitions();
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return this.inner.supportsSchemasInProcedureCalls();
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return this.inner.supportsSchemasInTableDefinitions();
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return this.inner.supportsSelectForUpdate();
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return this.inner.supportsStatementPooling();
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return this.inner.supportsStoredFunctionsUsingCallSyntax();
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return this.inner.supportsStoredProcedures();
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return this.inner.supportsSubqueriesInComparisons();
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return this.inner.supportsSubqueriesInExists();
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return this.inner.supportsSubqueriesInIns();
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return this.inner.supportsSubqueriesInQuantifieds();
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return this.inner.supportsTableCorrelationNames();
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int n) throws SQLException {
        return this.inner.supportsTransactionIsolationLevel(n);
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return this.inner.supportsTransactions();
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return this.inner.supportsUnion();
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return this.inner.supportsUnionAll();
    }

    @Override
    public boolean updatesAreDetected(int n) throws SQLException {
        return this.inner.updatesAreDetected(n);
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return this.inner.usesLocalFilePerTable();
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return this.inner.usesLocalFiles();
    }

    @Override
    public String getURL() throws SQLException {
        return this.inner.getURL();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return this.inner.isReadOnly();
    }

    @Override
    public ResultSet getAttributes(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getAttributes(string, string2, string3, string4);
    }

    public boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

