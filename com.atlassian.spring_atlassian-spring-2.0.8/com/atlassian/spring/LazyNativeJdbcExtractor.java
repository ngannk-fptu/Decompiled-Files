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
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

public class LazyNativeJdbcExtractor
implements NativeJdbcExtractor {
    private NativeJdbcExtractor delegatedExtractor;
    private Class extractorClass;

    public void setExtractorClass(Class extractorClass) {
        this.extractorClass = extractorClass;
    }

    private synchronized NativeJdbcExtractor getDelegatedExtractor() {
        try {
            if (this.delegatedExtractor == null) {
                this.delegatedExtractor = (NativeJdbcExtractor)this.extractorClass.newInstance();
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Error occurred trying to instantiate a native extractor of type: " + this.extractorClass, e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Error occurred trying to instantiate a native extractor of type: " + this.extractorClass, e);
        }
        if (this.delegatedExtractor != null) {
            return this.delegatedExtractor;
        }
        throw new RuntimeException("Error occurred trying to instantiate a native extractor of type: " + this.extractorClass);
    }

    public boolean isNativeConnectionNecessaryForNativeStatements() {
        return this.getDelegatedExtractor().isNativeConnectionNecessaryForNativeStatements();
    }

    public boolean isNativeConnectionNecessaryForNativePreparedStatements() {
        return this.getDelegatedExtractor().isNativeConnectionNecessaryForNativePreparedStatements();
    }

    public boolean isNativeConnectionNecessaryForNativeCallableStatements() {
        return this.getDelegatedExtractor().isNativeConnectionNecessaryForNativeCallableStatements();
    }

    public Connection getNativeConnection(Connection con) throws SQLException {
        return this.getDelegatedExtractor().getNativeConnection(con);
    }

    public Connection getNativeConnectionFromStatement(Statement stmt) throws SQLException {
        return this.getDelegatedExtractor().getNativeConnectionFromStatement(stmt);
    }

    public Statement getNativeStatement(Statement stmt) throws SQLException {
        return this.getDelegatedExtractor().getNativeStatement(stmt);
    }

    public PreparedStatement getNativePreparedStatement(PreparedStatement ps) throws SQLException {
        return this.getDelegatedExtractor().getNativePreparedStatement(ps);
    }

    public CallableStatement getNativeCallableStatement(CallableStatement cs) throws SQLException {
        return this.getDelegatedExtractor().getNativeCallableStatement(cs);
    }

    public ResultSet getNativeResultSet(ResultSet rs) throws SQLException {
        return this.getDelegatedExtractor().getNativeResultSet(rs);
    }
}

