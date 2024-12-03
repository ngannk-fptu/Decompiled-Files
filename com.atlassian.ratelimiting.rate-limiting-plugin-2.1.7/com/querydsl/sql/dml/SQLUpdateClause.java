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
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.AbstractSQLClause;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.Mapper;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateBatch;
import com.querydsl.sql.types.Null;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnegative;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLUpdateClause
extends AbstractSQLClause<SQLUpdateClause>
implements UpdateClause<SQLUpdateClause> {
    private static final Logger logger = LoggerFactory.getLogger(SQLInsertClause.class);
    private final RelationalPath<?> entity;
    private final List<SQLUpdateBatch> batches = new ArrayList<SQLUpdateBatch>();
    private Map<Path<?>, Expression<?>> updates = Maps.newLinkedHashMap();
    private QueryMetadata metadata = new DefaultQueryMetadata();
    private transient String queryString;
    private transient List<Object> constants;

    public SQLUpdateClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
        this(connection, new Configuration(templates), entity);
    }

    public SQLUpdateClause(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
    }

    public SQLUpdateClause(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
    }

    public SQLUpdateClause addFlag(QueryFlag.Position position, String flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    public SQLUpdateClause addFlag(QueryFlag.Position position, Expression<?> flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    public SQLUpdateClause addBatch() {
        this.batches.add(new SQLUpdateBatch(this.metadata, this.updates));
        this.updates = Maps.newLinkedHashMap();
        this.metadata = new DefaultQueryMetadata();
        this.metadata.addJoin(JoinType.DEFAULT, this.entity);
        return this;
    }

    @Override
    public void clear() {
        this.batches.clear();
        this.updates = Maps.newLinkedHashMap();
        this.metadata = new DefaultQueryMetadata();
        this.metadata.addJoin(JoinType.DEFAULT, this.entity);
    }

    private PreparedStatement createStatement() throws SQLException {
        this.listeners.preRender(this.context);
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeUpdate(this.metadata, this.entity, this.updates);
        this.queryString = serializer.toString();
        this.constants = serializer.getConstants();
        this.logQuery(logger, this.queryString, this.constants);
        this.context.addSQL(this.queryString);
        this.listeners.prepared(this.context);
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
        serializer.serializeUpdate(this.batches.get(0).getMetadata(), this.entity, this.batches.get(0).getUpdates());
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
        stmts.put(serializer.toString(), stmt);
        this.context.addPreparedStatement(stmt);
        this.listeners.prepared(this.context);
        for (int i = 1; i < this.batches.size(); ++i) {
            this.listeners.preRender(this.context);
            serializer = this.createSerializer();
            serializer.serializeUpdate(this.batches.get(i).getMetadata(), this.entity, this.batches.get(i).getUpdates());
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
                this.listeners.notifyUpdate(this.entity, this.metadata, this.updates);
                this.listeners.preExecute(this.context);
                int rc = stmt.executeUpdate();
                this.listeners.executed(this.context);
                long l = rc;
                return l;
            }
            stmts = this.createStatements();
            this.listeners.notifyUpdates(this.entity, this.batches);
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
            serializer.serializeUpdate(this.metadata, this.entity, this.updates);
            return ImmutableList.of((Object)this.createBindings(this.metadata, serializer));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (SQLUpdateBatch batch : this.batches) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeUpdate(batch.getMetadata(), this.entity, batch.getUpdates());
            builder.add((Object)this.createBindings(this.metadata, serializer));
        }
        return builder.build();
    }

    @Override
    public <T> SQLUpdateClause set(Path<T> path, T value) {
        if (value instanceof Expression) {
            this.updates.put(path, (Expression)value);
        } else if (value != null) {
            this.updates.put(path, ConstantImpl.create(value));
        } else {
            this.setNull((Path)path);
        }
        return this;
    }

    @Override
    public <T> SQLUpdateClause set(Path<T> path, Expression<? extends T> expression) {
        if (expression != null) {
            this.updates.put(path, expression);
        } else {
            this.setNull((Path)path);
        }
        return this;
    }

    @Override
    public <T> SQLUpdateClause setNull(Path<T> path) {
        this.updates.put(path, Null.CONSTANT);
        return this;
    }

    @Override
    public SQLUpdateClause set(List<? extends Path<?>> paths, List<?> values) {
        for (int i = 0; i < paths.size(); ++i) {
            if (values.get(i) instanceof Expression) {
                this.updates.put(paths.get(i), (Expression)values.get(i));
                continue;
            }
            if (values.get(i) != null) {
                this.updates.put(paths.get(i), ConstantImpl.create(values.get(i)));
                continue;
            }
            this.updates.put(paths.get(i), Null.CONSTANT);
        }
        return this;
    }

    public SQLUpdateClause where(Predicate p) {
        this.metadata.addWhere(p);
        return this;
    }

    @Override
    public SQLUpdateClause where(Predicate ... o) {
        for (Predicate p : o) {
            this.metadata.addWhere(p);
        }
        return this;
    }

    public SQLUpdateClause limit(@Nonnegative long limit) {
        this.metadata.setModifiers(QueryModifiers.limit(limit));
        return this;
    }

    public String toString() {
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeUpdate(this.metadata, this.entity, this.updates);
        return serializer.toString();
    }

    public SQLUpdateClause populate(Object bean) {
        return this.populate(bean, DefaultMapper.DEFAULT);
    }

    public <T> SQLUpdateClause populate(T obj, Mapper<T> mapper) {
        List primaryKeyColumns = this.entity.getPrimaryKey() != null ? this.entity.getPrimaryKey().getLocalColumns() : Collections.emptyList();
        Map<Path<?>, Object> values = mapper.createMap(this.entity, obj);
        for (Map.Entry<Path<?>, Object> entry : values.entrySet()) {
            if (primaryKeyColumns.contains(entry.getKey())) continue;
            this.set((Path)entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.updates.isEmpty() && this.batches.isEmpty();
    }

    @Override
    public int getBatchCount() {
        return this.batches.size();
    }
}

