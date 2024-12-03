/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Constants
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.core.Constants;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class IsolationLevelDataSourceAdapter
extends UserCredentialsDataSourceAdapter {
    private static final Constants constants = new Constants(TransactionDefinition.class);
    @Nullable
    private Integer isolationLevel;

    public final void setIsolationLevelName(String constantName) throws IllegalArgumentException {
        if (!constantName.startsWith("ISOLATION_")) {
            throw new IllegalArgumentException("Only isolation constants allowed");
        }
        this.setIsolationLevel(constants.asNumber(constantName).intValue());
    }

    public void setIsolationLevel(int isolationLevel) {
        if (!constants.getValues("ISOLATION_").contains(isolationLevel)) {
            throw new IllegalArgumentException("Only values of isolation constants allowed");
        }
        this.isolationLevel = isolationLevel != -1 ? Integer.valueOf(isolationLevel) : null;
    }

    @Nullable
    protected Integer getIsolationLevel() {
        return this.isolationLevel;
    }

    @Override
    protected Connection doGetConnection(@Nullable String username, @Nullable String password) throws SQLException {
        Integer isolationLevelToUse;
        Connection con = super.doGetConnection(username, password);
        Boolean readOnlyToUse = this.getCurrentReadOnlyFlag();
        if (readOnlyToUse != null) {
            con.setReadOnly(readOnlyToUse);
        }
        if ((isolationLevelToUse = this.getCurrentIsolationLevel()) != null) {
            con.setTransactionIsolation(isolationLevelToUse);
        }
        return con;
    }

    @Nullable
    protected Integer getCurrentIsolationLevel() {
        Integer isolationLevelToUse = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
        if (isolationLevelToUse == null) {
            isolationLevelToUse = this.getIsolationLevel();
        }
        return isolationLevelToUse;
    }

    @Nullable
    protected Boolean getCurrentReadOnlyFlag() {
        boolean txReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        return txReadOnly ? Boolean.TRUE : null;
    }
}

