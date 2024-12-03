/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import com.mchange.v2.c3p0.impl.NewProxyResultSet;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

public final class NewProxyDatabaseMetaData
implements DatabaseMetaData {
    protected DatabaseMetaData inner;
    private static final MLogger logger = MLog.getLogger((String)"com.mchange.v2.c3p0.impl.NewProxyDatabaseMetaData");
    volatile NewPooledConnection parentPooledConnection;
    ConnectionEventListener cel = new ConnectionEventListener(){

        @Override
        public void connectionErrorOccurred(ConnectionEvent evt) {
        }

        @Override
        public void connectionClosed(ConnectionEvent evt) {
            NewProxyDatabaseMetaData.this.detach();
        }
    };
    NewProxyConnection proxyCon;

    private void __setInner(DatabaseMetaData inner) {
        this.inner = inner;
    }

    NewProxyDatabaseMetaData(DatabaseMetaData inner) {
        this.__setInner(inner);
    }

    @Override
    public final String getURL() throws SQLException {
        try {
            return this.inner.getURL();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getAttributes(String a, String b, String c, String d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getAttributes(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean isReadOnly() throws SQLException {
        try {
            return this.inner.isReadOnly();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final Connection getConnection() throws SQLException {
        try {
            return this.proxyCon;
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean allProceduresAreCallable() throws SQLException {
        try {
            return this.inner.allProceduresAreCallable();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean allTablesAreSelectable() throws SQLException {
        try {
            return this.inner.allTablesAreSelectable();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getUserName() throws SQLException {
        try {
            return this.inner.getUserName();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean nullsAreSortedHigh() throws SQLException {
        try {
            return this.inner.nullsAreSortedHigh();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean nullsAreSortedLow() throws SQLException {
        try {
            return this.inner.nullsAreSortedLow();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean nullsAreSortedAtStart() throws SQLException {
        try {
            return this.inner.nullsAreSortedAtStart();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean nullsAreSortedAtEnd() throws SQLException {
        try {
            return this.inner.nullsAreSortedAtEnd();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getDatabaseProductName() throws SQLException {
        try {
            return this.inner.getDatabaseProductName();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getDatabaseProductVersion() throws SQLException {
        try {
            return this.inner.getDatabaseProductVersion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getDriverName() throws SQLException {
        try {
            return this.inner.getDriverName();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getDriverVersion() throws SQLException {
        try {
            return this.inner.getDriverVersion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getDriverMajorVersion() {
        return this.inner.getDriverMajorVersion();
    }

    @Override
    public final int getDriverMinorVersion() {
        return this.inner.getDriverMinorVersion();
    }

    @Override
    public final boolean usesLocalFiles() throws SQLException {
        try {
            return this.inner.usesLocalFiles();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean usesLocalFilePerTable() throws SQLException {
        try {
            return this.inner.usesLocalFilePerTable();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsMixedCaseIdentifiers() throws SQLException {
        try {
            return this.inner.supportsMixedCaseIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean storesUpperCaseIdentifiers() throws SQLException {
        try {
            return this.inner.storesUpperCaseIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean storesLowerCaseIdentifiers() throws SQLException {
        try {
            return this.inner.storesLowerCaseIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean storesMixedCaseIdentifiers() throws SQLException {
        try {
            return this.inner.storesMixedCaseIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.inner.supportsMixedCaseQuotedIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.inner.storesUpperCaseQuotedIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.inner.storesLowerCaseQuotedIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.inner.storesMixedCaseQuotedIdentifiers();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getIdentifierQuoteString() throws SQLException {
        try {
            return this.inner.getIdentifierQuoteString();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getSQLKeywords() throws SQLException {
        try {
            return this.inner.getSQLKeywords();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getNumericFunctions() throws SQLException {
        try {
            return this.inner.getNumericFunctions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getStringFunctions() throws SQLException {
        try {
            return this.inner.getStringFunctions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getSystemFunctions() throws SQLException {
        try {
            return this.inner.getSystemFunctions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getTimeDateFunctions() throws SQLException {
        try {
            return this.inner.getTimeDateFunctions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getSearchStringEscape() throws SQLException {
        try {
            return this.inner.getSearchStringEscape();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getExtraNameCharacters() throws SQLException {
        try {
            return this.inner.getExtraNameCharacters();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsAlterTableWithAddColumn() throws SQLException {
        try {
            return this.inner.supportsAlterTableWithAddColumn();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsAlterTableWithDropColumn() throws SQLException {
        try {
            return this.inner.supportsAlterTableWithDropColumn();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsColumnAliasing() throws SQLException {
        try {
            return this.inner.supportsColumnAliasing();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean nullPlusNonNullIsNull() throws SQLException {
        try {
            return this.inner.nullPlusNonNullIsNull();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsConvert(int a, int b) throws SQLException {
        try {
            return this.inner.supportsConvert(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsConvert() throws SQLException {
        try {
            return this.inner.supportsConvert();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsTableCorrelationNames() throws SQLException {
        try {
            return this.inner.supportsTableCorrelationNames();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsDifferentTableCorrelationNames() throws SQLException {
        try {
            return this.inner.supportsDifferentTableCorrelationNames();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsExpressionsInOrderBy() throws SQLException {
        try {
            return this.inner.supportsExpressionsInOrderBy();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsOrderByUnrelated() throws SQLException {
        try {
            return this.inner.supportsOrderByUnrelated();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsGroupBy() throws SQLException {
        try {
            return this.inner.supportsGroupBy();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsGroupByUnrelated() throws SQLException {
        try {
            return this.inner.supportsGroupByUnrelated();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsGroupByBeyondSelect() throws SQLException {
        try {
            return this.inner.supportsGroupByBeyondSelect();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsLikeEscapeClause() throws SQLException {
        try {
            return this.inner.supportsLikeEscapeClause();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsMultipleResultSets() throws SQLException {
        try {
            return this.inner.supportsMultipleResultSets();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsMultipleTransactions() throws SQLException {
        try {
            return this.inner.supportsMultipleTransactions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsNonNullableColumns() throws SQLException {
        try {
            return this.inner.supportsNonNullableColumns();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsMinimumSQLGrammar() throws SQLException {
        try {
            return this.inner.supportsMinimumSQLGrammar();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsCoreSQLGrammar() throws SQLException {
        try {
            return this.inner.supportsCoreSQLGrammar();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsExtendedSQLGrammar() throws SQLException {
        try {
            return this.inner.supportsExtendedSQLGrammar();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsANSI92EntryLevelSQL() throws SQLException {
        try {
            return this.inner.supportsANSI92EntryLevelSQL();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsANSI92IntermediateSQL() throws SQLException {
        try {
            return this.inner.supportsANSI92IntermediateSQL();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsANSI92FullSQL() throws SQLException {
        try {
            return this.inner.supportsANSI92FullSQL();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsIntegrityEnhancementFacility() throws SQLException {
        try {
            return this.inner.supportsIntegrityEnhancementFacility();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsOuterJoins() throws SQLException {
        try {
            return this.inner.supportsOuterJoins();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsFullOuterJoins() throws SQLException {
        try {
            return this.inner.supportsFullOuterJoins();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsLimitedOuterJoins() throws SQLException {
        try {
            return this.inner.supportsLimitedOuterJoins();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getSchemaTerm() throws SQLException {
        try {
            return this.inner.getSchemaTerm();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getProcedureTerm() throws SQLException {
        try {
            return this.inner.getProcedureTerm();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getCatalogTerm() throws SQLException {
        try {
            return this.inner.getCatalogTerm();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean isCatalogAtStart() throws SQLException {
        try {
            return this.inner.isCatalogAtStart();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final String getCatalogSeparator() throws SQLException {
        try {
            return this.inner.getCatalogSeparator();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSchemasInDataManipulation() throws SQLException {
        try {
            return this.inner.supportsSchemasInDataManipulation();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSchemasInProcedureCalls() throws SQLException {
        try {
            return this.inner.supportsSchemasInProcedureCalls();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSchemasInTableDefinitions() throws SQLException {
        try {
            return this.inner.supportsSchemasInTableDefinitions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSchemasInIndexDefinitions() throws SQLException {
        try {
            return this.inner.supportsSchemasInIndexDefinitions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        try {
            return this.inner.supportsSchemasInPrivilegeDefinitions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsCatalogsInDataManipulation() throws SQLException {
        try {
            return this.inner.supportsCatalogsInDataManipulation();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsCatalogsInProcedureCalls() throws SQLException {
        try {
            return this.inner.supportsCatalogsInProcedureCalls();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsCatalogsInTableDefinitions() throws SQLException {
        try {
            return this.inner.supportsCatalogsInTableDefinitions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        try {
            return this.inner.supportsCatalogsInIndexDefinitions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        try {
            return this.inner.supportsCatalogsInPrivilegeDefinitions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsPositionedDelete() throws SQLException {
        try {
            return this.inner.supportsPositionedDelete();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsPositionedUpdate() throws SQLException {
        try {
            return this.inner.supportsPositionedUpdate();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSelectForUpdate() throws SQLException {
        try {
            return this.inner.supportsSelectForUpdate();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsStoredProcedures() throws SQLException {
        try {
            return this.inner.supportsStoredProcedures();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSubqueriesInComparisons() throws SQLException {
        try {
            return this.inner.supportsSubqueriesInComparisons();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSubqueriesInExists() throws SQLException {
        try {
            return this.inner.supportsSubqueriesInExists();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSubqueriesInIns() throws SQLException {
        try {
            return this.inner.supportsSubqueriesInIns();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSubqueriesInQuantifieds() throws SQLException {
        try {
            return this.inner.supportsSubqueriesInQuantifieds();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsCorrelatedSubqueries() throws SQLException {
        try {
            return this.inner.supportsCorrelatedSubqueries();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsUnion() throws SQLException {
        try {
            return this.inner.supportsUnion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsUnionAll() throws SQLException {
        try {
            return this.inner.supportsUnionAll();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        try {
            return this.inner.supportsOpenCursorsAcrossCommit();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        try {
            return this.inner.supportsOpenCursorsAcrossRollback();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        try {
            return this.inner.supportsOpenStatementsAcrossCommit();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        try {
            return this.inner.supportsOpenStatementsAcrossRollback();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxBinaryLiteralLength() throws SQLException {
        try {
            return this.inner.getMaxBinaryLiteralLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxCharLiteralLength() throws SQLException {
        try {
            return this.inner.getMaxCharLiteralLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxColumnNameLength() throws SQLException {
        try {
            return this.inner.getMaxColumnNameLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxColumnsInGroupBy() throws SQLException {
        try {
            return this.inner.getMaxColumnsInGroupBy();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxColumnsInIndex() throws SQLException {
        try {
            return this.inner.getMaxColumnsInIndex();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxColumnsInOrderBy() throws SQLException {
        try {
            return this.inner.getMaxColumnsInOrderBy();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxColumnsInSelect() throws SQLException {
        try {
            return this.inner.getMaxColumnsInSelect();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxColumnsInTable() throws SQLException {
        try {
            return this.inner.getMaxColumnsInTable();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxConnections() throws SQLException {
        try {
            return this.inner.getMaxConnections();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxCursorNameLength() throws SQLException {
        try {
            return this.inner.getMaxCursorNameLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxIndexLength() throws SQLException {
        try {
            return this.inner.getMaxIndexLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxSchemaNameLength() throws SQLException {
        try {
            return this.inner.getMaxSchemaNameLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxProcedureNameLength() throws SQLException {
        try {
            return this.inner.getMaxProcedureNameLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxCatalogNameLength() throws SQLException {
        try {
            return this.inner.getMaxCatalogNameLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxRowSize() throws SQLException {
        try {
            return this.inner.getMaxRowSize();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        try {
            return this.inner.doesMaxRowSizeIncludeBlobs();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxStatementLength() throws SQLException {
        try {
            return this.inner.getMaxStatementLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxStatements() throws SQLException {
        try {
            return this.inner.getMaxStatements();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxTableNameLength() throws SQLException {
        try {
            return this.inner.getMaxTableNameLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxTablesInSelect() throws SQLException {
        try {
            return this.inner.getMaxTablesInSelect();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getMaxUserNameLength() throws SQLException {
        try {
            return this.inner.getMaxUserNameLength();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getDefaultTransactionIsolation() throws SQLException {
        try {
            return this.inner.getDefaultTransactionIsolation();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsTransactions() throws SQLException {
        try {
            return this.inner.supportsTransactions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsTransactionIsolationLevel(int a) throws SQLException {
        try {
            return this.inner.supportsTransactionIsolationLevel(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        try {
            return this.inner.supportsDataDefinitionAndDataManipulationTransactions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        try {
            return this.inner.supportsDataManipulationTransactionsOnly();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        try {
            return this.inner.dataDefinitionCausesTransactionCommit();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        try {
            return this.inner.dataDefinitionIgnoredInTransactions();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getProcedures(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getProcedures(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getProcedureColumns(String a, String b, String c, String d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getProcedureColumns(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getTables(String a, String b, String c, String[] d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getTables(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getSchemas(String a, String b) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getSchemas(a, b);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getSchemas() throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getSchemas();
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getCatalogs() throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getCatalogs();
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getTableTypes() throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getTableTypes();
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getColumns(String a, String b, String c, String d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getColumns(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getColumnPrivileges(String a, String b, String c, String d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getColumnPrivileges(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getTablePrivileges(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getTablePrivileges(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getBestRowIdentifier(String a, String b, String c, int d, boolean e) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getBestRowIdentifier(a, b, c, d, e);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getVersionColumns(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getVersionColumns(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getPrimaryKeys(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getPrimaryKeys(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getImportedKeys(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getImportedKeys(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getExportedKeys(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getExportedKeys(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getCrossReference(String a, String b, String c, String d, String e, String f) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getCrossReference(a, b, c, d, e, f);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getTypeInfo() throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getTypeInfo();
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getIndexInfo(String a, String b, String c, boolean d, boolean e) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getIndexInfo(a, b, c, d, e);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsResultSetType(int a) throws SQLException {
        try {
            return this.inner.supportsResultSetType(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsResultSetConcurrency(int a, int b) throws SQLException {
        try {
            return this.inner.supportsResultSetConcurrency(a, b);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean ownUpdatesAreVisible(int a) throws SQLException {
        try {
            return this.inner.ownUpdatesAreVisible(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean ownDeletesAreVisible(int a) throws SQLException {
        try {
            return this.inner.ownDeletesAreVisible(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean ownInsertsAreVisible(int a) throws SQLException {
        try {
            return this.inner.ownInsertsAreVisible(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean othersUpdatesAreVisible(int a) throws SQLException {
        try {
            return this.inner.othersUpdatesAreVisible(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean othersDeletesAreVisible(int a) throws SQLException {
        try {
            return this.inner.othersDeletesAreVisible(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean othersInsertsAreVisible(int a) throws SQLException {
        try {
            return this.inner.othersInsertsAreVisible(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean updatesAreDetected(int a) throws SQLException {
        try {
            return this.inner.updatesAreDetected(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean deletesAreDetected(int a) throws SQLException {
        try {
            return this.inner.deletesAreDetected(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean insertsAreDetected(int a) throws SQLException {
        try {
            return this.inner.insertsAreDetected(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsBatchUpdates() throws SQLException {
        try {
            return this.inner.supportsBatchUpdates();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getUDTs(String a, String b, String c, int[] d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getUDTs(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsSavepoints() throws SQLException {
        try {
            return this.inner.supportsSavepoints();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsNamedParameters() throws SQLException {
        try {
            return this.inner.supportsNamedParameters();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsMultipleOpenResults() throws SQLException {
        try {
            return this.inner.supportsMultipleOpenResults();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsGetGeneratedKeys() throws SQLException {
        try {
            return this.inner.supportsGetGeneratedKeys();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getSuperTypes(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getSuperTypes(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getSuperTables(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getSuperTables(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsResultSetHoldability(int a) throws SQLException {
        try {
            return this.inner.supportsResultSetHoldability(a);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getDatabaseMajorVersion() throws SQLException {
        try {
            return this.inner.getDatabaseMajorVersion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getDatabaseMinorVersion() throws SQLException {
        try {
            return this.inner.getDatabaseMinorVersion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getJDBCMajorVersion() throws SQLException {
        try {
            return this.inner.getJDBCMajorVersion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getJDBCMinorVersion() throws SQLException {
        try {
            return this.inner.getJDBCMinorVersion();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getSQLStateType() throws SQLException {
        try {
            return this.inner.getSQLStateType();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean locatorsUpdateCopy() throws SQLException {
        try {
            return this.inner.locatorsUpdateCopy();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsStatementPooling() throws SQLException {
        try {
            return this.inner.supportsStatementPooling();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final RowIdLifetime getRowIdLifetime() throws SQLException {
        try {
            return this.inner.getRowIdLifetime();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        try {
            return this.inner.supportsStoredFunctionsUsingCallSyntax();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        try {
            return this.inner.autoCommitFailureClosesAllResultSets();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getClientInfoProperties() throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getClientInfoProperties();
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getFunctions(String a, String b, String c) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getFunctions(a, b, c);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getFunctionColumns(String a, String b, String c, String d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getFunctionColumns(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final ResultSet getPseudoColumns(String a, String b, String c, String d) throws SQLException {
        try {
            ResultSet innerResultSet = this.inner.getPseudoColumns(a, b, c, d);
            if (innerResultSet == null) {
                return null;
            }
            return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean generatedKeyAlwaysReturned() throws SQLException {
        try {
            return this.inner.generatedKeyAlwaysReturned();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final long getMaxLogicalLobSize() throws SQLException {
        try {
            return this.inner.getMaxLogicalLobSize();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final boolean supportsRefCursors() throws SQLException {
        try {
            return this.inner.supportsRefCursors();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    @Override
    public final int getResultSetHoldability() throws SQLException {
        try {
            return this.inner.getResultSetHoldability();
        }
        catch (NullPointerException exc) {
            if (this.isDetached()) {
                throw SqlUtils.toSQLException((String)"You can't operate on a closed DatabaseMetaData!!!", (Throwable)exc);
            }
            throw exc;
        }
        catch (Exception exc) {
            if (!this.isDetached()) {
                throw this.parentPooledConnection.handleThrowable(exc);
            }
            throw SqlUtils.toSQLException((Throwable)exc);
        }
    }

    public final Object unwrap(Class a) throws SQLException {
        if (this.isWrapperForInner(a)) {
            return this.inner.unwrap(a);
        }
        if (this.isWrapperForThis(a)) {
            return this;
        }
        throw new SQLException(this + " is not a wrapper for or implementation of " + a.getName());
    }

    public final boolean isWrapperFor(Class a) throws SQLException {
        return this.isWrapperForInner(a) || this.isWrapperForThis(a);
    }

    void attach(NewPooledConnection parentPooledConnection) {
        this.parentPooledConnection = parentPooledConnection;
        parentPooledConnection.addConnectionEventListener(this.cel);
    }

    private void detach() {
        this.parentPooledConnection.removeConnectionEventListener(this.cel);
        this.parentPooledConnection = null;
    }

    NewProxyDatabaseMetaData(DatabaseMetaData inner, NewPooledConnection parentPooledConnection) {
        this(inner);
        this.attach(parentPooledConnection);
    }

    boolean isDetached() {
        return this.parentPooledConnection == null;
    }

    public String toString() {
        return super.toString() + " [wrapping: " + this.inner + "]";
    }

    private boolean isWrapperForInner(Class intfcl) {
        return DatabaseMetaData.class == intfcl || intfcl.isAssignableFrom(this.inner.getClass());
    }

    private boolean isWrapperForThis(Class intfcl) {
        return intfcl.isAssignableFrom(this.getClass());
    }

    NewProxyDatabaseMetaData(DatabaseMetaData inner, NewPooledConnection parentPooledConnection, NewProxyConnection proxyCon) {
        this(inner, parentPooledConnection);
        this.proxyCon = proxyCon;
    }
}

