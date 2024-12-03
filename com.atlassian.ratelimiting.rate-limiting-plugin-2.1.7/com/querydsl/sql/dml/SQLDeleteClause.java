/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnegative
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.querydsl.sql.dml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.ValidatingVisitor;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.AbstractSQLClause;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLDeleteClause
extends AbstractSQLClause<SQLDeleteClause>
implements DeleteClause<SQLDeleteClause> {
    private static final Logger logger = LoggerFactory.getLogger(SQLDeleteClause.class);
    private static final ValidatingVisitor validatingVisitor = new ValidatingVisitor("Undeclared path '%s'. A delete operation can only reference a single table. Consider this alternative: DELETE ... WHERE EXISTS (subquery)");
    private final RelationalPath<?> entity;
    private final List<QueryMetadata> batches = new ArrayList<QueryMetadata>();
    private DefaultQueryMetadata metadata = new DefaultQueryMetadata();
    private transient String queryString;
    private transient List<Object> constants;

    public SQLDeleteClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
        this(connection, new Configuration(templates), entity);
    }

    public SQLDeleteClause(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
        this.metadata.setValidatingVisitor(validatingVisitor);
    }

    public SQLDeleteClause(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
        this.metadata.setValidatingVisitor(validatingVisitor);
    }

    public SQLDeleteClause addFlag(QueryFlag.Position position, String flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    public SQLDeleteClause addFlag(QueryFlag.Position position, Expression<?> flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    public SQLDeleteClause addBatch() {
        this.batches.add(this.metadata);
        this.metadata = new DefaultQueryMetadata();
        this.metadata.addJoin(JoinType.DEFAULT, this.entity);
        this.metadata.setValidatingVisitor(validatingVisitor);
        return this;
    }

    @Override
    public void clear() {
        this.batches.clear();
        this.metadata = new DefaultQueryMetadata();
        this.metadata.addJoin(JoinType.DEFAULT, this.entity);
        this.metadata.setValidatingVisitor(validatingVisitor);
    }

    private PreparedStatement createStatement() throws SQLException {
        this.listeners.preRender(this.context);
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeDelete(this.metadata, this.entity);
        this.queryString = serializer.toString();
        this.constants = serializer.getConstants();
        this.logQuery(logger, this.queryString, this.constants);
        this.context.addSQL(this.queryString);
        this.listeners.rendered(this.context);
        this.listeners.prePrepare(this.context);
        PreparedStatement stmt = this.connection().prepareStatement(this.queryString);
        this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
        this.context.addPreparedStatement(stmt);
        this.listeners.prepared(this.context);
        return stmt;
    }

    private Collection<PreparedStatement> createStatements() throws SQLException {
        boolean addBatches = !this.configuration.getUseLiterals();
        this.listeners.preRender(this.context);
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeDelete(this.batches.get(0), this.entity);
        this.queryString = serializer.toString();
        this.constants = serializer.getConstants();
        this.logQuery(logger, this.queryString, this.constants);
        this.context.addSQL(this.queryString);
        this.listeners.rendered(this.context);
        HashMap stmts = Maps.newHashMap();
        this.listeners.prePrepare(this.context);
        PreparedStatement stmt = this.connection().prepareStatement(this.queryString);
        this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
        if (addBatches) {
            stmt.addBatch();
        }
        stmts.put(this.queryString, stmt);
        this.context.addPreparedStatement(stmt);
        this.listeners.prepared(this.context);
        for (int i = 1; i < this.batches.size(); ++i) {
            this.listeners.preRender(this.context);
            serializer = this.createSerializer();
            serializer.serializeDelete(this.batches.get(i), this.entity);
            this.context.addSQL(serializer.toString());
            this.listeners.rendered(this.context);
            stmt = (PreparedStatement)stmts.get(serializer.toString());
            if (stmt == null) {
                this.listeners.prePrepare(this.context);
                stmt = this.connection().prepareStatement(serializer.toString());
                stmts.put(serializer.toString(), stmt);
                this.context.addPreparedStatement(stmt);
                this.listeners.prepared(this.context);
            }
            this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
            if (!addBatches) continue;
            stmt.addBatch();
        }
        return stmts.values();
    }

    @Override
    public long execute() {
        this.context = this.startContext(this.connection(), this.metadata, this.entity);
        PreparedStatement stmt = null;
        Collection<PreparedStatement> stmts = null;
        try {
            if (this.batches.isEmpty()) {
                stmt = this.createStatement();
                this.listeners.notifyDelete(this.entity, this.metadata);
                this.listeners.preExecute(this.context);
                int rc = stmt.executeUpdate();
                this.listeners.executed(this.context);
                long l = rc;
                return l;
            }
            stmts = this.createStatements();
            this.listeners.notifyDeletes(this.entity, this.batches);
            this.listeners.preExecute(this.context);
            long rc = this.executeBatch(stmts);
            this.listeners.executed(this.context);
            long l = rc;
            return l;
        }
        catch (SQLException e) {
            this.onException(this.context, e);
            throw this.configuration.translate(this.queryString, this.constants, e);
        }
        finally {
            if (stmt != null) {
                this.close(stmt);
            }
            if (stmts != null) {
                this.close(stmts);
            }
            this.reset();
            this.endContext(this.context);
        }
    }

    @Override
    public List<SQLBindings> getSQL() {
        if (this.batches.isEmpty()) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeDelete(this.metadata, this.entity);
            return ImmutableList.of((Object)this.createBindings(this.metadata, serializer));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (QueryMetadata metadata : this.batches) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeDelete(metadata, this.entity);
            builder.add((Object)this.createBindings(metadata, serializer));
        }
        return builder.build();
    }

    public SQLDeleteClause where(Predicate p) {
        this.metadata.addWhere(p);
        return this;
    }

    @Override
    public SQLDeleteClause where(Predicate ... o) {
        for (Predicate p : o) {
            this.metadata.addWhere(p);
        }
        return this;
    }

    public SQLDeleteClause limit(@Nonnegative long limit) {
        this.metadata.setModifiers(QueryModifiers.limit(limit));
        return this;
    }

    @Override
    public int getBatchCount() {
        return this.batches.size();
    }

    public String toString() {
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeDelete(this.metadata, this.entity);
        return serializer.toString();
    }
}

