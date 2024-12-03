/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.OracleTypesHelper;
import org.hibernate.sql.ANSIJoinFragment;
import org.hibernate.sql.JoinFragment;

public class Oracle10gDialect
extends Oracle9iDialect {
    @Override
    public JoinFragment createOuterJoinFragment() {
        return new ANSIJoinFragment();
    }

    @Override
    public String getCrossJoinSeparator() {
        return " cross join ";
    }

    @Override
    public String getWriteLockString(int timeout) {
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString();
        }
        return super.getWriteLockString(timeout);
    }

    @Override
    public String getWriteLockString(String aliases, int timeout) {
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString(aliases);
        }
        return super.getWriteLockString(aliases, timeout);
    }

    @Override
    public String getForUpdateSkipLockedString() {
        return " for update skip locked";
    }

    @Override
    public String getForUpdateSkipLockedString(String aliases) {
        return this.getForUpdateString() + " of " + aliases + " skip locked";
    }

    @Override
    public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
        return (ResultSet)statement.getObject(position);
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
        statement.registerOutParameter(name, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType());
        return 1;
    }

    @Override
    public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
        return (ResultSet)statement.getObject(name);
    }

    @Override
    public boolean supportsSkipLocked() {
        return true;
    }
}

