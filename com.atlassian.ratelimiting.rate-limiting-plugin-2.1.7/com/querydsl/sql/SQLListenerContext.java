/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryMetadata;
import com.querydsl.sql.RelationalPath;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;

public interface SQLListenerContext {
    public Object getData(String var1);

    public void setData(String var1, Object var2);

    public QueryMetadata getMetadata();

    public String getSQL();

    public Collection<String> getSQLStatements();

    public RelationalPath<?> getEntity();

    public Connection getConnection();

    public Exception getException();

    public PreparedStatement getPreparedStatement();

    public Collection<PreparedStatement> getPreparedStatements();
}

