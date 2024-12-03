/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 */
package com.querydsl.sql.teradata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.AbstractSQLClause;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;

public class SetQueryBandClause
extends AbstractSQLClause<SetQueryBandClause> {
    private boolean forSession = true;
    private final Map<String, String> values = Maps.newLinkedHashMap();
    private transient String queryString;
    private transient String parameter;

    public SetQueryBandClause(Connection connection, SQLTemplates templates) {
        this(connection, new Configuration(templates));
    }

    public SetQueryBandClause(Connection connection, Configuration configuration) {
        super(configuration, connection);
    }

    public SetQueryBandClause(Provider<Connection> connection, Configuration configuration) {
        super(configuration, connection);
    }

    public SetQueryBandClause forSession() {
        this.queryString = null;
        this.forSession = true;
        return this;
    }

    public SetQueryBandClause forTransaction() {
        this.queryString = null;
        this.forSession = false;
        return this;
    }

    public SetQueryBandClause set(String key, String value) {
        this.queryString = null;
        this.values.put(key, value);
        return this;
    }

    public SetQueryBandClause set(Map<String, String> values) {
        this.queryString = null;
        this.values.putAll(values);
        return this;
    }

    @Override
    public void clear() {
        this.values.clear();
    }

    @Override
    public long execute() {
        PreparedStatement stmt = null;
        try {
            stmt = this.connection().prepareStatement(this.toString());
            if (this.parameter != null) {
                stmt.setString(1, this.parameter);
            }
            long l = 1L;
            return l;
        }
        catch (SQLException e) {
            ImmutableList bindings = this.parameter != null ? ImmutableList.of((Object)this.parameter) : ImmutableList.of();
            throw this.configuration.translate(this.queryString, (List<Object>)bindings, e);
        }
        finally {
            if (stmt != null) {
                this.close(stmt);
            }
        }
    }

    @Override
    public List<SQLBindings> getSQL() {
        SQLBindings bindings = this.configuration.getUseLiterals() || this.forSession ? new SQLBindings(this.toString(), (ImmutableList<Object>)ImmutableList.of()) : new SQLBindings(this.toString(), (ImmutableList<Object>)ImmutableList.of((Object)this.parameter));
        return ImmutableList.of((Object)bindings);
    }

    public String toString() {
        if (this.queryString == null) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : this.values.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue());
                builder.append(";");
            }
            if (this.configuration.getUseLiterals() || this.forSession) {
                this.queryString = "set query_band='" + this.configuration.getTemplates().escapeLiteral(builder.toString()) + (this.forSession ? "' for session" : "' for transaction");
                this.parameter = null;
            } else {
                this.queryString = "set query_band=? for transaction";
                this.parameter = builder.toString();
            }
        }
        return this.queryString;
    }

    @Override
    public int getBatchCount() {
        return 0;
    }
}

