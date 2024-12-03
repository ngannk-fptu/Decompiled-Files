/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.querydsl.sql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.querydsl.core.QueryMetadata;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLListenerContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SQLListenerContextImpl
implements SQLListenerContext {
    private final Map<String, Object> contextMap = Maps.newHashMap();
    private final QueryMetadata md;
    private final List<String> sqlStatements;
    private final List<PreparedStatement> preparedStatements = Lists.newArrayList();
    private RelationalPath<?> entity;
    private Connection connection;
    private Exception exception;

    public SQLListenerContextImpl(QueryMetadata metadata, Connection connection, RelationalPath<?> entity) {
        this.sqlStatements = Lists.newArrayList();
        this.md = metadata;
        this.connection = connection;
        this.entity = entity;
    }

    public SQLListenerContextImpl(QueryMetadata metadata, Connection connection) {
        this(metadata, connection, null);
    }

    public SQLListenerContextImpl(QueryMetadata metadata) {
        this(metadata, null, null);
    }

    public void addSQL(String sql) {
        this.sqlStatements.add(sql);
    }

    public void setEntity(RelationalPath<?> entity) {
        this.entity = entity;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void addPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatements.add(preparedStatement);
    }

    @Override
    public QueryMetadata getMetadata() {
        return this.md;
    }

    @Override
    public RelationalPath<?> getEntity() {
        return this.entity;
    }

    @Override
    public String getSQL() {
        return this.sqlStatements.isEmpty() ? null : this.sqlStatements.get(0);
    }

    @Override
    public Collection<String> getSQLStatements() {
        return this.sqlStatements;
    }

    @Override
    public Exception getException() {
        return this.exception;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public Collection<PreparedStatement> getPreparedStatements() {
        return this.preparedStatements;
    }

    @Override
    public PreparedStatement getPreparedStatement() {
        return this.preparedStatements.isEmpty() ? null : this.preparedStatements.get(0);
    }

    @Override
    public Object getData(String dataKey) {
        return this.contextMap.get(dataKey);
    }

    @Override
    public void setData(String dataKey, Object value) {
        this.contextMap.put(dataKey, value);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append(" sql:").append(this.nicerSql(this.getSQL())).append(" connection:").append(this.connection == null ? "not connected" : "connected").append(" entity:").append(this.entity).append(" exception:").append(this.exception);
        for (Map.Entry<String, Object> entry : this.contextMap.entrySet()) {
            sb.append(" [").append(entry.getKey()).append(":").append(entry.getValue()).append("]");
        }
        return sb.toString();
    }

    private String nicerSql(String sql) {
        return "'" + (sql == null ? null : sql.replace('\n', ' ')) + "'";
    }
}

