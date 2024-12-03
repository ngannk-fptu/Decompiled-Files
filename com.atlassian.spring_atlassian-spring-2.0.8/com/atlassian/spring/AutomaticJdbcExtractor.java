/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor
 */
package com.atlassian.spring;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

public class AutomaticJdbcExtractor
implements NativeJdbcExtractor {
    private Map extractors;
    private NativeJdbcExtractor defaultJdbcExtractor;
    private NativeJdbcExtractor jdbcExtractor;

    public boolean isNativeConnectionNecessaryForNativeStatements() {
        return true;
    }

    public boolean isNativeConnectionNecessaryForNativePreparedStatements() {
        return true;
    }

    public boolean isNativeConnectionNecessaryForNativeCallableStatements() {
        return true;
    }

    public Connection getNativeConnection(Connection con) throws SQLException {
        return this.getJdbcExtractor(con).getNativeConnection(con);
    }

    private synchronized NativeJdbcExtractor getJdbcExtractor(Object o) {
        if (this.jdbcExtractor == null) {
            String objClass = o.getClass().getName();
            for (String classPrefix : this.extractors.keySet()) {
                if (objClass.indexOf(classPrefix) == -1) continue;
                this.jdbcExtractor = (NativeJdbcExtractor)this.extractors.get(classPrefix);
            }
            if (this.jdbcExtractor == null) {
                this.jdbcExtractor = this.defaultJdbcExtractor;
            }
        }
        return this.jdbcExtractor;
    }

    public Connection getNativeConnectionFromStatement(Statement stmt) throws SQLException {
        return this.getJdbcExtractor(stmt).getNativeConnectionFromStatement(stmt);
    }

    public Statement getNativeStatement(Statement stmt) throws SQLException {
        return this.getJdbcExtractor(stmt).getNativeStatement(stmt);
    }

    public PreparedStatement getNativePreparedStatement(PreparedStatement ps) throws SQLException {
        return this.getJdbcExtractor(ps).getNativePreparedStatement(ps);
    }

    public CallableStatement getNativeCallableStatement(CallableStatement cs) throws SQLException {
        return this.getJdbcExtractor(cs).getNativeCallableStatement(cs);
    }

    public ResultSet getNativeResultSet(ResultSet rs) throws SQLException {
        return this.getJdbcExtractor(rs).getNativeResultSet(rs);
    }

    public Map getExtractors() {
        return this.extractors;
    }

    public void setExtractors(Map extractors) {
        this.extractors = extractors;
    }

    public NativeJdbcExtractor getDefaultJdbcExtractor() {
        return this.defaultJdbcExtractor;
    }

    public void setDefaultJdbcExtractor(NativeJdbcExtractor defaultJdbcExtractor) {
        this.defaultJdbcExtractor = defaultJdbcExtractor;
    }
}

