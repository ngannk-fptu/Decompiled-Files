/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
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
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.util.ResultSetAdapter;
import com.querydsl.sql.AbstractSQLQuery;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.AbstractSQLClause;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.Mapper;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.types.Null;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLInsertClause
extends AbstractSQLClause<SQLInsertClause>
implements InsertClause<SQLInsertClause> {
    private static final Logger logger = LoggerFactory.getLogger(SQLInsertClause.class);
    private final RelationalPath<?> entity;
    private final QueryMetadata metadata = new DefaultQueryMetadata();
    @Nullable
    private SubQueryExpression<?> subQuery;
    @Nullable
    private SQLQuery<?> subQueryBuilder;
    private final List<SQLInsertBatch> batches = new ArrayList<SQLInsertBatch>();
    private final List<Path<?>> columns = new ArrayList();
    private final List<Expression<?>> values = new ArrayList();
    private transient String queryString;
    private transient List<Object> constants;
    private transient boolean batchToBulk;

    public SQLInsertClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
        this(connection, new Configuration(templates), entity);
    }

    public SQLInsertClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity, SQLQuery<?> subQuery) {
        this(connection, new Configuration(templates), entity);
        this.subQueryBuilder = subQuery;
    }

    public SQLInsertClause(Connection connection, Configuration configuration, RelationalPath<?> entity, SQLQuery<?> subQuery) {
        this(connection, configuration, entity);
        this.subQueryBuilder = subQuery;
    }

    public SQLInsertClause(Connection connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
    }

    public SQLInsertClause(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity, SQLQuery<?> subQuery) {
        this(connection, configuration, entity);
        this.subQueryBuilder = subQuery;
    }

    public SQLInsertClause(Provider<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
        super(configuration, connection);
        this.entity = entity;
        this.metadata.addJoin(JoinType.DEFAULT, entity);
    }

    public SQLInsertClause addFlag(QueryFlag.Position position, String flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    public SQLInsertClause addFlag(QueryFlag.Position position, Expression<?> flag) {
        this.metadata.addFlag(new QueryFlag(position, flag));
        return this;
    }

    public SQLInsertClause addBatch() {
        if (this.subQueryBuilder != null) {
            this.subQuery = ((AbstractSQLQuery)this.subQueryBuilder.select(this.values.toArray(new Expression[this.values.size()]))).clone();
            this.values.clear();
        }
        this.batches.add(new SQLInsertBatch(this.columns, this.values, this.subQuery));
        this.columns.clear();
        this.values.clear();
        this.subQuery = null;
        return this;
    }

    public void setBatchToBulk(boolean b) {
        this.batchToBulk = b && this.configuration.getTemplates().isBatchToBulkSupported();
    }

    @Override
    public void clear() {
        this.batches.clear();
        this.columns.clear();
        this.values.clear();
        this.subQuery = null;
    }

    @Override
    public SQLInsertClause columns(Path<?> ... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    @Nullable
    public <T> T executeWithKey(Path<T> path) {
        return this.executeWithKey(path.getType(), path);
    }

    public <T> T executeWithKey(Class<T> type) {
        return this.executeWithKey(type, null);
    }

    private <T> T executeWithKey(Class<T> type, @Nullable Path<T> path) {
        ResultSet rs = null;
        try {
            rs = this.executeWithKeys();
            if (rs.next()) {
                T t = this.configuration.get(rs, path, 1, type);
                return t;
            }
            T t = null;
            return t;
        }
        catch (SQLException e) {
            throw this.configuration.translate(e);
        }
        finally {
            if (rs != null) {
                this.close(rs);
            }
            this.reset();
        }
    }

    public <T> List<T> executeWithKeys(Path<T> path) {
        return this.executeWithKeys(path.getType(), path);
    }

    public <T> List<T> executeWithKeys(Class<T> type) {
        return this.executeWithKeys(type, null);
    }

    private <T> List<T> executeWithKeys(Class<T> type, @Nullable Path<T> path) {
        ResultSet rs = null;
        try {
            rs = this.executeWithKeys();
            ArrayList<T> rv = new ArrayList<T>();
            while (rs.next()) {
                rv.add(this.configuration.get(rs, path, 1, type));
            }
            ArrayList<T> arrayList = rv;
            return arrayList;
        }
        catch (SQLException e) {
            throw this.configuration.translate(e);
        }
        finally {
            if (rs != null) {
                this.close(rs);
            }
            this.reset();
        }
    }

    private PreparedStatement createStatement(boolean withKeys) throws SQLException {
        this.listeners.preRender(this.context);
        SQLSerializer serializer = this.createSerializer();
        if (this.subQueryBuilder != null) {
            this.subQuery = ((AbstractSQLQuery)this.subQueryBuilder.select(this.values.toArray(new Expression[this.values.size()]))).clone();
            this.values.clear();
        }
        if (!this.batches.isEmpty() && this.batchToBulk) {
            serializer.serializeInsert(this.metadata, this.entity, this.batches);
        } else {
            serializer.serializeInsert(this.metadata, this.entity, this.columns, this.values, this.subQuery);
        }
        this.context.addSQL(serializer.toString());
        this.listeners.rendered(this.context);
        return this.prepareStatementAndSetParameters(serializer, withKeys);
    }

    private Collection<PreparedStatement> createStatements(boolean withKeys) throws SQLException {
        boolean addBatches = !this.configuration.getUseLiterals();
        this.listeners.preRender(this.context);
        if (this.subQueryBuilder != null) {
            this.subQuery = ((AbstractSQLQuery)this.subQueryBuilder.select(this.values.toArray(new Expression[this.values.size()]))).clone();
            this.values.clear();
        }
        HashMap stmts = Maps.newHashMap();
        SQLSerializer serializer = this.createSerializer();
        serializer.serializeInsert(this.metadata, this.entity, this.batches.get(0).getColumns(), this.batches.get(0).getValues(), this.batches.get(0).getSubQuery());
        PreparedStatement stmt = this.prepareStatementAndSetParameters(serializer, withKeys);
        if (addBatches) {
            stmt.addBatch();
        }
        stmts.put(serializer.toString(), stmt);
        this.context.addSQL(serializer.toString());
        this.listeners.rendered(this.context);
        for (int i = 1; i < this.batches.size(); ++i) {
            SQLInsertBatch batch = this.batches.get(i);
            this.listeners.preRender(this.context);
            serializer = this.createSerializer();
            serializer.serializeInsert(this.metadata, this.entity, batch.getColumns(), batch.getValues(), batch.getSubQuery());
            this.context.addSQL(serializer.toString());
            this.listeners.rendered(this.context);
            stmt = (PreparedStatement)stmts.get(serializer.toString());
            if (stmt == null) {
                stmt = this.prepareStatementAndSetParameters(serializer, withKeys);
                stmts.put(serializer.toString(), stmt);
            } else {
                this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
            }
            if (!addBatches) continue;
            stmt.addBatch();
        }
        return stmts.values();
    }

    private PreparedStatement prepareStatementAndSetParameters(SQLSerializer serializer, boolean withKeys) throws SQLException {
        PreparedStatement stmt;
        this.listeners.prePrepare(this.context);
        this.queryString = serializer.toString();
        this.constants = serializer.getConstants();
        this.logQuery(logger, this.queryString, this.constants);
        if (withKeys) {
            if (this.entity.getPrimaryKey() != null) {
                String[] target = new String[this.entity.getPrimaryKey().getLocalColumns().size()];
                for (int i = 0; i < target.length; ++i) {
                    String column;
                    Path<?> path = this.entity.getPrimaryKey().getLocalColumns().get(i);
                    target[i] = column = ColumnMetadata.getName(path);
                }
                stmt = this.connection().prepareStatement(this.queryString, target);
            } else {
                stmt = this.connection().prepareStatement(this.queryString, 1);
            }
        } else {
            stmt = this.connection().prepareStatement(this.queryString);
        }
        this.setParameters(stmt, serializer.getConstants(), serializer.getConstantPaths(), this.metadata.getParams());
        this.context.addPreparedStatement(stmt);
        this.listeners.prepared(this.context);
        return stmt;
    }

    public ResultSet executeWithKeys() {
        this.context = this.startContext(this.connection(), this.metadata, this.entity);
        try {
            PreparedStatement stmt = null;
            if (this.batches.isEmpty()) {
                stmt = this.createStatement(true);
                this.listeners.notifyInsert(this.entity, this.metadata, this.columns, this.values, this.subQuery);
                this.listeners.preExecute(this.context);
                stmt.executeUpdate();
                this.listeners.executed(this.context);
            } else if (this.batchToBulk) {
                stmt = this.createStatement(true);
                this.listeners.notifyInserts(this.entity, this.metadata, this.batches);
                this.listeners.preExecute(this.context);
                stmt.executeUpdate();
                this.listeners.executed(this.context);
            } else {
                Collection<PreparedStatement> stmts = this.createStatements(true);
                if (stmts != null && stmts.size() > 1) {
                    throw new IllegalStateException("executeWithKeys called with batch statement and multiple SQL strings");
                }
                stmt = stmts.iterator().next();
                this.listeners.notifyInserts(this.entity, this.metadata, this.batches);
                this.listeners.preExecute(this.context);
                stmt.executeBatch();
                this.listeners.executed(this.context);
            }
            final PreparedStatement stmt2 = stmt;
            ResultSet rs = stmt.getGeneratedKeys();
            return new ResultSetAdapter(rs){

                @Override
                public void close() throws SQLException {
                    try {
                        super.close();
                    }
                    finally {
                        stmt2.close();
                        SQLInsertClause.this.reset();
                        SQLInsertClause.this.endContext(SQLInsertClause.this.context);
                    }
                }
            };
        }
        catch (SQLException e) {
            this.onException(this.context, e);
            this.reset();
            this.endContext(this.context);
            throw this.configuration.translate(this.queryString, this.constants, e);
        }
    }

    @Override
    public long execute() {
        long l;
        Collection<PreparedStatement> stmts;
        block15: {
            PreparedStatement stmt;
            block13: {
                long l2;
                block14: {
                    block11: {
                        long l3;
                        block12: {
                            this.context = this.startContext(this.connection(), this.metadata, this.entity);
                            stmt = null;
                            stmts = null;
                            if (!this.batches.isEmpty()) break block11;
                            stmt = this.createStatement(false);
                            this.listeners.notifyInsert(this.entity, this.metadata, this.columns, this.values, this.subQuery);
                            this.listeners.preExecute(this.context);
                            int rc = stmt.executeUpdate();
                            this.listeners.executed(this.context);
                            l3 = rc;
                            if (stmt == null) break block12;
                            this.close(stmt);
                        }
                        if (stmts != null) {
                            this.close(stmts);
                        }
                        this.reset();
                        this.endContext(this.context);
                        return l3;
                    }
                    if (!this.batchToBulk) break block13;
                    stmt = this.createStatement(false);
                    this.listeners.notifyInserts(this.entity, this.metadata, this.batches);
                    this.listeners.preExecute(this.context);
                    int rc = stmt.executeUpdate();
                    this.listeners.executed(this.context);
                    l2 = rc;
                    if (stmt == null) break block14;
                    this.close(stmt);
                }
                if (stmts != null) {
                    this.close(stmts);
                }
                this.reset();
                this.endContext(this.context);
                return l2;
            }
            try {
                stmts = this.createStatements(false);
                this.listeners.notifyInserts(this.entity, this.metadata, this.batches);
                this.listeners.preExecute(this.context);
                long rc = this.executeBatch(stmts);
                this.listeners.executed(this.context);
                l = rc;
                if (stmt == null) break block15;
            }
            catch (SQLException e) {
                try {
                    this.onException(this.context, e);
                    throw this.configuration.translate(this.queryString, this.constants, e);
                }
                catch (Throwable throwable) {
                    if (stmt != null) {
                        this.close(stmt);
                    }
                    if (stmts != null) {
                        this.close(stmts);
                    }
                    this.reset();
                    this.endContext(this.context);
                    throw throwable;
                }
            }
            this.close(stmt);
        }
        if (stmts != null) {
            this.close(stmts);
        }
        this.reset();
        this.endContext(this.context);
        return l;
    }

    @Override
    public List<SQLBindings> getSQL() {
        if (this.batches.isEmpty()) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeInsert(this.metadata, this.entity, this.columns, this.values, this.subQuery);
            return ImmutableList.of((Object)this.createBindings(this.metadata, serializer));
        }
        if (this.batchToBulk) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeInsert(this.metadata, this.entity, this.batches);
            return ImmutableList.of((Object)this.createBindings(this.metadata, serializer));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (SQLInsertBatch batch : this.batches) {
            SQLSerializer serializer = this.createSerializer();
            serializer.serializeInsert(this.metadata, this.entity, batch.getColumns(), batch.getValues(), batch.getSubQuery());
            builder.add((Object)this.createBindings(this.metadata, serializer));
        }
        return builder.build();
    }

    @Override
    public SQLInsertClause select(SubQueryExpression<?> sq) {
        this.subQuery = sq;
        for (Map.Entry<ParamExpression<?>, Object> entry : sq.getMetadata().getParams().entrySet()) {
            this.metadata.setParam(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public <T> SQLInsertClause set(Path<T> path, T value) {
        this.columns.add(path);
        if (value instanceof Expression) {
            this.values.add((Expression)value);
        } else if (value != null) {
            this.values.add(ConstantImpl.create(value));
        } else {
            this.values.add(Null.CONSTANT);
        }
        return this;
    }

    @Override
    public <T> SQLInsertClause set(Path<T> path, Expression<? extends T> expression) {
        this.columns.add(path);
        this.values.add(expression);
        return this;
    }

    @Override
    public <T> SQLInsertClause setNull(Path<T> path) {
        this.columns.add(path);
        this.values.add(Null.CONSTANT);
        return this;
    }

    @Override
    public SQLInsertClause values(Object ... v) {
        for (Object value : v) {
            if (value instanceof Expression) {
                this.values.add((Expression)value);
                continue;
            }
            if (value != null) {
                this.values.add(ConstantImpl.create(value));
                continue;
            }
            this.values.add(Null.CONSTANT);
        }
        return this;
    }

    public String toString() {
        SQLSerializer serializer = this.createSerializer();
        if (!this.batches.isEmpty() && this.batchToBulk) {
            serializer.serializeInsert(this.metadata, this.entity, this.batches);
        } else {
            serializer.serializeInsert(this.metadata, this.entity, this.columns, this.values, this.subQuery);
        }
        return serializer.toString();
    }

    public SQLInsertClause populate(Object bean) {
        return this.populate(bean, DefaultMapper.DEFAULT);
    }

    public <T> SQLInsertClause populate(T obj, Mapper<T> mapper) {
        Map<Path<?>, Object> values = mapper.createMap(this.entity, obj);
        for (Map.Entry<Path<?>, Object> entry : values.entrySet()) {
            this.set((Path)entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty() && this.batches.isEmpty();
    }

    @Override
    public int getBatchCount() {
        return this.batches.size();
    }
}

