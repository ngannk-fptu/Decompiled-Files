/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public abstract class SynchronizedFilterDatabaseMetaData
implements DatabaseMetaData {
    protected DatabaseMetaData inner;

    private void __setInner(DatabaseMetaData databaseMetaData) {
        this.inner = databaseMetaData;
    }

    public SynchronizedFilterDatabaseMetaData(DatabaseMetaData databaseMetaData) {
        this.__setInner(databaseMetaData);
    }

    public SynchronizedFilterDatabaseMetaData() {
    }

    public synchronized void setInner(DatabaseMetaData databaseMetaData) {
        this.__setInner(databaseMetaData);
    }

    public synchronized DatabaseMetaData getInner() {
        return this.inner;
    }

    @Override
    public synchronized boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return this.inner.autoCommitFailureClosesAllResultSets();
    }

    @Override
    public synchronized ResultSet getCatalogs() throws SQLException {
        return this.inner.getCatalogs();
    }

    @Override
    public synchronized boolean allProceduresAreCallable() throws SQLException {
        return this.inner.allProceduresAreCallable();
    }

    @Override
    public synchronized boolean allTablesAreSelectable() throws SQLException {
        return this.inner.allTablesAreSelectable();
    }

    @Override
    public synchronized boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return this.inner.dataDefinitionCausesTransactionCommit();
    }

    @Override
    public synchronized boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return this.inner.dataDefinitionIgnoredInTransactions();
    }

    @Override
    public synchronized boolean deletesAreDetected(int n) throws SQLException {
        return this.inner.deletesAreDetected(n);
    }

    @Override
    public synchronized boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return this.inner.doesMaxRowSizeIncludeBlobs();
    }

    @Override
    public synchronized boolean generatedKeyAlwaysReturned() throws SQLException {
        return this.inner.generatedKeyAlwaysReturned();
    }

    @Override
    public synchronized ResultSet getBestRowIdentifier(String string, String string2, String string3, int n, boolean bl) throws SQLException {
        return this.inner.getBestRowIdentifier(string, string2, string3, n, bl);
    }

    @Override
    public synchronized String getCatalogSeparator() throws SQLException {
        return this.inner.getCatalogSeparator();
    }

    @Override
    public synchronized String getCatalogTerm() throws SQLException {
        return this.inner.getCatalogTerm();
    }

    @Override
    public synchronized ResultSet getClientInfoProperties() throws SQLException {
        return this.inner.getClientInfoProperties();
    }

    @Override
    public synchronized ResultSet getColumnPrivileges(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getColumnPrivileges(string, string2, string3, string4);
    }

    @Override
    public synchronized ResultSet getColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getColumns(string, string2, string3, string4);
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public synchronized ResultSet getCrossReference(String string, String string2, String string3, String string4, String string5, String string6) throws SQLException {
        return this.inner.getCrossReference(string, string2, string3, string4, string5, string6);
    }

    @Override
    public synchronized int getDatabaseMajorVersion() throws SQLException {
        return this.inner.getDatabaseMajorVersion();
    }

    @Override
    public synchronized int getDatabaseMinorVersion() throws SQLException {
        return this.inner.getDatabaseMinorVersion();
    }

    @Override
    public synchronized String getDatabaseProductName() throws SQLException {
        return this.inner.getDatabaseProductName();
    }

    @Override
    public synchronized String getDatabaseProductVersion() throws SQLException {
        return this.inner.getDatabaseProductVersion();
    }

    @Override
    public synchronized int getDefaultTransactionIsolation() throws SQLException {
        return this.inner.getDefaultTransactionIsolation();
    }

    @Override
    public synchronized int getDriverMajorVersion() {
        return this.inner.getDriverMajorVersion();
    }

    @Override
    public synchronized int getDriverMinorVersion() {
        return this.inner.getDriverMinorVersion();
    }

    @Override
    public synchronized String getDriverName() throws SQLException {
        return this.inner.getDriverName();
    }

    @Override
    public synchronized String getDriverVersion() throws SQLException {
        return this.inner.getDriverVersion();
    }

    @Override
    public synchronized ResultSet getExportedKeys(String string, String string2, String string3) throws SQLException {
        return this.inner.getExportedKeys(string, string2, string3);
    }

    @Override
    public synchronized String getExtraNameCharacters() throws SQLException {
        return this.inner.getExtraNameCharacters();
    }

    @Override
    public synchronized ResultSet getFunctionColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getFunctionColumns(string, string2, string3, string4);
    }

    @Override
    public synchronized ResultSet getFunctions(String string, String string2, String string3) throws SQLException {
        return this.inner.getFunctions(string, string2, string3);
    }

    @Override
    public synchronized String getIdentifierQuoteString() throws SQLException {
        return this.inner.getIdentifierQuoteString();
    }

    @Override
    public synchronized ResultSet getImportedKeys(String string, String string2, String string3) throws SQLException {
        return this.inner.getImportedKeys(string, string2, string3);
    }

    @Override
    public synchronized ResultSet getIndexInfo(String string, String string2, String string3, boolean bl, boolean bl2) throws SQLException {
        return this.inner.getIndexInfo(string, string2, string3, bl, bl2);
    }

    @Override
    public synchronized int getJDBCMajorVersion() throws SQLException {
        return this.inner.getJDBCMajorVersion();
    }

    @Override
    public synchronized int getJDBCMinorVersion() throws SQLException {
        return this.inner.getJDBCMinorVersion();
    }

    @Override
    public synchronized int getMaxBinaryLiteralLength() throws SQLException {
        return this.inner.getMaxBinaryLiteralLength();
    }

    @Override
    public synchronized int getMaxCatalogNameLength() throws SQLException {
        return this.inner.getMaxCatalogNameLength();
    }

    @Override
    public synchronized int getMaxCharLiteralLength() throws SQLException {
        return this.inner.getMaxCharLiteralLength();
    }

    @Override
    public synchronized int getMaxColumnNameLength() throws SQLException {
        return this.inner.getMaxColumnNameLength();
    }

    @Override
    public synchronized int getMaxColumnsInGroupBy() throws SQLException {
        return this.inner.getMaxColumnsInGroupBy();
    }

    @Override
    public synchronized int getMaxColumnsInIndex() throws SQLException {
        return this.inner.getMaxColumnsInIndex();
    }

    @Override
    public synchronized int getMaxColumnsInOrderBy() throws SQLException {
        return this.inner.getMaxColumnsInOrderBy();
    }

    @Override
    public synchronized int getMaxColumnsInSelect() throws SQLException {
        return this.inner.getMaxColumnsInSelect();
    }

    @Override
    public synchronized int getMaxColumnsInTable() throws SQLException {
        return this.inner.getMaxColumnsInTable();
    }

    @Override
    public synchronized int getMaxConnections() throws SQLException {
        return this.inner.getMaxConnections();
    }

    @Override
    public synchronized int getMaxCursorNameLength() throws SQLException {
        return this.inner.getMaxCursorNameLength();
    }

    @Override
    public synchronized int getMaxIndexLength() throws SQLException {
        return this.inner.getMaxIndexLength();
    }

    @Override
    public synchronized int getMaxProcedureNameLength() throws SQLException {
        return this.inner.getMaxProcedureNameLength();
    }

    @Override
    public synchronized int getMaxRowSize() throws SQLException {
        return this.inner.getMaxRowSize();
    }

    @Override
    public synchronized int getMaxSchemaNameLength() throws SQLException {
        return this.inner.getMaxSchemaNameLength();
    }

    @Override
    public synchronized int getMaxStatementLength() throws SQLException {
        return this.inner.getMaxStatementLength();
    }

    @Override
    public synchronized int getMaxStatements() throws SQLException {
        return this.inner.getMaxStatements();
    }

    @Override
    public synchronized int getMaxTableNameLength() throws SQLException {
        return this.inner.getMaxTableNameLength();
    }

    @Override
    public synchronized int getMaxTablesInSelect() throws SQLException {
        return this.inner.getMaxTablesInSelect();
    }

    @Override
    public synchronized int getMaxUserNameLength() throws SQLException {
        return this.inner.getMaxUserNameLength();
    }

    @Override
    public synchronized String getNumericFunctions() throws SQLException {
        return this.inner.getNumericFunctions();
    }

    @Override
    public synchronized ResultSet getPrimaryKeys(String string, String string2, String string3) throws SQLException {
        return this.inner.getPrimaryKeys(string, string2, string3);
    }

    @Override
    public synchronized ResultSet getProcedureColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getProcedureColumns(string, string2, string3, string4);
    }

    @Override
    public synchronized String getProcedureTerm() throws SQLException {
        return this.inner.getProcedureTerm();
    }

    @Override
    public synchronized ResultSet getProcedures(String string, String string2, String string3) throws SQLException {
        return this.inner.getProcedures(string, string2, string3);
    }

    @Override
    public synchronized ResultSet getPseudoColumns(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getPseudoColumns(string, string2, string3, string4);
    }

    @Override
    public synchronized int getResultSetHoldability() throws SQLException {
        return this.inner.getResultSetHoldability();
    }

    @Override
    public synchronized RowIdLifetime getRowIdLifetime() throws SQLException {
        return this.inner.getRowIdLifetime();
    }

    @Override
    public synchronized String getSQLKeywords() throws SQLException {
        return this.inner.getSQLKeywords();
    }

    @Override
    public synchronized int getSQLStateType() throws SQLException {
        return this.inner.getSQLStateType();
    }

    @Override
    public synchronized String getSchemaTerm() throws SQLException {
        return this.inner.getSchemaTerm();
    }

    @Override
    public synchronized ResultSet getSchemas(String string, String string2) throws SQLException {
        return this.inner.getSchemas(string, string2);
    }

    @Override
    public synchronized ResultSet getSchemas() throws SQLException {
        return this.inner.getSchemas();
    }

    @Override
    public synchronized String getSearchStringEscape() throws SQLException {
        return this.inner.getSearchStringEscape();
    }

    @Override
    public synchronized String getStringFunctions() throws SQLException {
        return this.inner.getStringFunctions();
    }

    @Override
    public synchronized ResultSet getSuperTables(String string, String string2, String string3) throws SQLException {
        return this.inner.getSuperTables(string, string2, string3);
    }

    @Override
    public synchronized ResultSet getSuperTypes(String string, String string2, String string3) throws SQLException {
        return this.inner.getSuperTypes(string, string2, string3);
    }

    @Override
    public synchronized String getSystemFunctions() throws SQLException {
        return this.inner.getSystemFunctions();
    }

    @Override
    public synchronized ResultSet getTablePrivileges(String string, String string2, String string3) throws SQLException {
        return this.inner.getTablePrivileges(string, string2, string3);
    }

    @Override
    public synchronized ResultSet getTableTypes() throws SQLException {
        return this.inner.getTableTypes();
    }

    @Override
    public synchronized ResultSet getTables(String string, String string2, String string3, String[] stringArray) throws SQLException {
        return this.inner.getTables(string, string2, string3, stringArray);
    }

    @Override
    public synchronized String getTimeDateFunctions() throws SQLException {
        return this.inner.getTimeDateFunctions();
    }

    @Override
    public synchronized ResultSet getTypeInfo() throws SQLException {
        return this.inner.getTypeInfo();
    }

    @Override
    public synchronized ResultSet getUDTs(String string, String string2, String string3, int[] nArray) throws SQLException {
        return this.inner.getUDTs(string, string2, string3, nArray);
    }

    @Override
    public synchronized String getUserName() throws SQLException {
        return this.inner.getUserName();
    }

    @Override
    public synchronized ResultSet getVersionColumns(String string, String string2, String string3) throws SQLException {
        return this.inner.getVersionColumns(string, string2, string3);
    }

    @Override
    public synchronized boolean insertsAreDetected(int n) throws SQLException {
        return this.inner.insertsAreDetected(n);
    }

    @Override
    public synchronized boolean isCatalogAtStart() throws SQLException {
        return this.inner.isCatalogAtStart();
    }

    @Override
    public synchronized boolean locatorsUpdateCopy() throws SQLException {
        return this.inner.locatorsUpdateCopy();
    }

    @Override
    public synchronized boolean nullPlusNonNullIsNull() throws SQLException {
        return this.inner.nullPlusNonNullIsNull();
    }

    @Override
    public synchronized boolean nullsAreSortedAtEnd() throws SQLException {
        return this.inner.nullsAreSortedAtEnd();
    }

    @Override
    public synchronized boolean nullsAreSortedAtStart() throws SQLException {
        return this.inner.nullsAreSortedAtStart();
    }

    @Override
    public synchronized boolean nullsAreSortedHigh() throws SQLException {
        return this.inner.nullsAreSortedHigh();
    }

    @Override
    public synchronized boolean nullsAreSortedLow() throws SQLException {
        return this.inner.nullsAreSortedLow();
    }

    @Override
    public synchronized boolean othersDeletesAreVisible(int n) throws SQLException {
        return this.inner.othersDeletesAreVisible(n);
    }

    @Override
    public synchronized boolean othersInsertsAreVisible(int n) throws SQLException {
        return this.inner.othersInsertsAreVisible(n);
    }

    @Override
    public synchronized boolean othersUpdatesAreVisible(int n) throws SQLException {
        return this.inner.othersUpdatesAreVisible(n);
    }

    @Override
    public synchronized boolean ownDeletesAreVisible(int n) throws SQLException {
        return this.inner.ownDeletesAreVisible(n);
    }

    @Override
    public synchronized boolean ownInsertsAreVisible(int n) throws SQLException {
        return this.inner.ownInsertsAreVisible(n);
    }

    @Override
    public synchronized boolean ownUpdatesAreVisible(int n) throws SQLException {
        return this.inner.ownUpdatesAreVisible(n);
    }

    @Override
    public synchronized boolean storesLowerCaseIdentifiers() throws SQLException {
        return this.inner.storesLowerCaseIdentifiers();
    }

    @Override
    public synchronized boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return this.inner.storesLowerCaseQuotedIdentifiers();
    }

    @Override
    public synchronized boolean storesMixedCaseIdentifiers() throws SQLException {
        return this.inner.storesMixedCaseIdentifiers();
    }

    @Override
    public synchronized boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return this.inner.storesMixedCaseQuotedIdentifiers();
    }

    @Override
    public synchronized boolean storesUpperCaseIdentifiers() throws SQLException {
        return this.inner.storesUpperCaseIdentifiers();
    }

    @Override
    public synchronized boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return this.inner.storesUpperCaseQuotedIdentifiers();
    }

    @Override
    public synchronized boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return this.inner.supportsANSI92EntryLevelSQL();
    }

    @Override
    public synchronized boolean supportsANSI92FullSQL() throws SQLException {
        return this.inner.supportsANSI92FullSQL();
    }

    @Override
    public synchronized boolean supportsANSI92IntermediateSQL() throws SQLException {
        return this.inner.supportsANSI92IntermediateSQL();
    }

    @Override
    public synchronized boolean supportsAlterTableWithAddColumn() throws SQLException {
        return this.inner.supportsAlterTableWithAddColumn();
    }

    @Override
    public synchronized boolean supportsAlterTableWithDropColumn() throws SQLException {
        return this.inner.supportsAlterTableWithDropColumn();
    }

    @Override
    public synchronized boolean supportsBatchUpdates() throws SQLException {
        return this.inner.supportsBatchUpdates();
    }

    @Override
    public synchronized boolean supportsCatalogsInDataManipulation() throws SQLException {
        return this.inner.supportsCatalogsInDataManipulation();
    }

    @Override
    public synchronized boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return this.inner.supportsCatalogsInIndexDefinitions();
    }

    @Override
    public synchronized boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return this.inner.supportsCatalogsInPrivilegeDefinitions();
    }

    @Override
    public synchronized boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return this.inner.supportsCatalogsInProcedureCalls();
    }

    @Override
    public synchronized boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return this.inner.supportsCatalogsInTableDefinitions();
    }

    @Override
    public synchronized boolean supportsColumnAliasing() throws SQLException {
        return this.inner.supportsColumnAliasing();
    }

    @Override
    public synchronized boolean supportsConvert(int n, int n2) throws SQLException {
        return this.inner.supportsConvert(n, n2);
    }

    @Override
    public synchronized boolean supportsConvert() throws SQLException {
        return this.inner.supportsConvert();
    }

    @Override
    public synchronized boolean supportsCoreSQLGrammar() throws SQLException {
        return this.inner.supportsCoreSQLGrammar();
    }

    @Override
    public synchronized boolean supportsCorrelatedSubqueries() throws SQLException {
        return this.inner.supportsCorrelatedSubqueries();
    }

    @Override
    public synchronized boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return this.inner.supportsDataDefinitionAndDataManipulationTransactions();
    }

    @Override
    public synchronized boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return this.inner.supportsDataManipulationTransactionsOnly();
    }

    @Override
    public synchronized boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return this.inner.supportsDifferentTableCorrelationNames();
    }

    @Override
    public synchronized boolean supportsExpressionsInOrderBy() throws SQLException {
        return this.inner.supportsExpressionsInOrderBy();
    }

    @Override
    public synchronized boolean supportsExtendedSQLGrammar() throws SQLException {
        return this.inner.supportsExtendedSQLGrammar();
    }

    @Override
    public synchronized boolean supportsFullOuterJoins() throws SQLException {
        return this.inner.supportsFullOuterJoins();
    }

    @Override
    public synchronized boolean supportsGetGeneratedKeys() throws SQLException {
        return this.inner.supportsGetGeneratedKeys();
    }

    @Override
    public synchronized boolean supportsGroupBy() throws SQLException {
        return this.inner.supportsGroupBy();
    }

    @Override
    public synchronized boolean supportsGroupByBeyondSelect() throws SQLException {
        return this.inner.supportsGroupByBeyondSelect();
    }

    @Override
    public synchronized boolean supportsGroupByUnrelated() throws SQLException {
        return this.inner.supportsGroupByUnrelated();
    }

    @Override
    public synchronized boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return this.inner.supportsIntegrityEnhancementFacility();
    }

    @Override
    public synchronized boolean supportsLikeEscapeClause() throws SQLException {
        return this.inner.supportsLikeEscapeClause();
    }

    @Override
    public synchronized boolean supportsLimitedOuterJoins() throws SQLException {
        return this.inner.supportsLimitedOuterJoins();
    }

    @Override
    public synchronized boolean supportsMinimumSQLGrammar() throws SQLException {
        return this.inner.supportsMinimumSQLGrammar();
    }

    @Override
    public synchronized boolean supportsMixedCaseIdentifiers() throws SQLException {
        return this.inner.supportsMixedCaseIdentifiers();
    }

    @Override
    public synchronized boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return this.inner.supportsMixedCaseQuotedIdentifiers();
    }

    @Override
    public synchronized boolean supportsMultipleOpenResults() throws SQLException {
        return this.inner.supportsMultipleOpenResults();
    }

    @Override
    public synchronized boolean supportsMultipleResultSets() throws SQLException {
        return this.inner.supportsMultipleResultSets();
    }

    @Override
    public synchronized boolean supportsMultipleTransactions() throws SQLException {
        return this.inner.supportsMultipleTransactions();
    }

    @Override
    public synchronized boolean supportsNamedParameters() throws SQLException {
        return this.inner.supportsNamedParameters();
    }

    @Override
    public synchronized boolean supportsNonNullableColumns() throws SQLException {
        return this.inner.supportsNonNullableColumns();
    }

    @Override
    public synchronized boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return this.inner.supportsOpenCursorsAcrossCommit();
    }

    @Override
    public synchronized boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return this.inner.supportsOpenCursorsAcrossRollback();
    }

    @Override
    public synchronized boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return this.inner.supportsOpenStatementsAcrossCommit();
    }

    @Override
    public synchronized boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return this.inner.supportsOpenStatementsAcrossRollback();
    }

    @Override
    public synchronized boolean supportsOrderByUnrelated() throws SQLException {
        return this.inner.supportsOrderByUnrelated();
    }

    @Override
    public synchronized boolean supportsOuterJoins() throws SQLException {
        return this.inner.supportsOuterJoins();
    }

    @Override
    public synchronized boolean supportsPositionedDelete() throws SQLException {
        return this.inner.supportsPositionedDelete();
    }

    @Override
    public synchronized boolean supportsPositionedUpdate() throws SQLException {
        return this.inner.supportsPositionedUpdate();
    }

    @Override
    public synchronized boolean supportsResultSetConcurrency(int n, int n2) throws SQLException {
        return this.inner.supportsResultSetConcurrency(n, n2);
    }

    @Override
    public synchronized boolean supportsResultSetHoldability(int n) throws SQLException {
        return this.inner.supportsResultSetHoldability(n);
    }

    @Override
    public synchronized boolean supportsResultSetType(int n) throws SQLException {
        return this.inner.supportsResultSetType(n);
    }

    @Override
    public synchronized boolean supportsSavepoints() throws SQLException {
        return this.inner.supportsSavepoints();
    }

    @Override
    public synchronized boolean supportsSchemasInDataManipulation() throws SQLException {
        return this.inner.supportsSchemasInDataManipulation();
    }

    @Override
    public synchronized boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return this.inner.supportsSchemasInIndexDefinitions();
    }

    @Override
    public synchronized boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return this.inner.supportsSchemasInPrivilegeDefinitions();
    }

    @Override
    public synchronized boolean supportsSchemasInProcedureCalls() throws SQLException {
        return this.inner.supportsSchemasInProcedureCalls();
    }

    @Override
    public synchronized boolean supportsSchemasInTableDefinitions() throws SQLException {
        return this.inner.supportsSchemasInTableDefinitions();
    }

    @Override
    public synchronized boolean supportsSelectForUpdate() throws SQLException {
        return this.inner.supportsSelectForUpdate();
    }

    @Override
    public synchronized boolean supportsStatementPooling() throws SQLException {
        return this.inner.supportsStatementPooling();
    }

    @Override
    public synchronized boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return this.inner.supportsStoredFunctionsUsingCallSyntax();
    }

    @Override
    public synchronized boolean supportsStoredProcedures() throws SQLException {
        return this.inner.supportsStoredProcedures();
    }

    @Override
    public synchronized boolean supportsSubqueriesInComparisons() throws SQLException {
        return this.inner.supportsSubqueriesInComparisons();
    }

    @Override
    public synchronized boolean supportsSubqueriesInExists() throws SQLException {
        return this.inner.supportsSubqueriesInExists();
    }

    @Override
    public synchronized boolean supportsSubqueriesInIns() throws SQLException {
        return this.inner.supportsSubqueriesInIns();
    }

    @Override
    public synchronized boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return this.inner.supportsSubqueriesInQuantifieds();
    }

    @Override
    public synchronized boolean supportsTableCorrelationNames() throws SQLException {
        return this.inner.supportsTableCorrelationNames();
    }

    @Override
    public synchronized boolean supportsTransactionIsolationLevel(int n) throws SQLException {
        return this.inner.supportsTransactionIsolationLevel(n);
    }

    @Override
    public synchronized boolean supportsTransactions() throws SQLException {
        return this.inner.supportsTransactions();
    }

    @Override
    public synchronized boolean supportsUnion() throws SQLException {
        return this.inner.supportsUnion();
    }

    @Override
    public synchronized boolean supportsUnionAll() throws SQLException {
        return this.inner.supportsUnionAll();
    }

    @Override
    public synchronized boolean updatesAreDetected(int n) throws SQLException {
        return this.inner.updatesAreDetected(n);
    }

    @Override
    public synchronized boolean usesLocalFilePerTable() throws SQLException {
        return this.inner.usesLocalFilePerTable();
    }

    @Override
    public synchronized boolean usesLocalFiles() throws SQLException {
        return this.inner.usesLocalFiles();
    }

    @Override
    public synchronized String getURL() throws SQLException {
        return this.inner.getURL();
    }

    @Override
    public synchronized boolean isReadOnly() throws SQLException {
        return this.inner.isReadOnly();
    }

    @Override
    public synchronized ResultSet getAttributes(String string, String string2, String string3, String string4) throws SQLException {
        return this.inner.getAttributes(string, string2, string3, string4);
    }

    public synchronized boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public synchronized Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

