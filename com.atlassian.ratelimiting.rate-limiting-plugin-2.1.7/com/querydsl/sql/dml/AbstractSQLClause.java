/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.MDC
 */
package com.querydsl.sql.dml;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.dml.DMLClause;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Path;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLListenerContextImpl;
import com.querydsl.sql.SQLListeners;
import com.querydsl.sql.SQLSerializer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.MDC;

public abstract class AbstractSQLClause<C extends AbstractSQLClause<C>>
implements DMLClause<C> {
    protected final Configuration configuration;
    protected final SQLListeners listeners;
    protected boolean useLiterals;
    protected SQLListenerContextImpl context;
    @Nullable
    private Provider<Connection> connProvider;
    @Nullable
    private Connection conn;

    public AbstractSQLClause(Configuration configuration) {
        this.configuration = configuration;
        this.listeners = new SQLListeners(configuration.getListeners());
        this.useLiterals = configuration.getUseLiterals();
    }

    public AbstractSQLClause(Configuration configuration, Provider<Connection> connProvider) {
        this(configuration);
        this.connProvider = connProvider;
    }

    public AbstractSQLClause(Configuration configuration, Connection conn) {
        this(configuration);
        this.conn = conn;
    }

    public void addListener(SQLListener listener) {
        this.listeners.add(listener);
    }

    public abstract void clear();

    protected SQLListenerContextImpl startContext(Connection connection, QueryMetadata metadata, RelationalPath<?> entity) {
        SQLListenerContextImpl context = new SQLListenerContextImpl(metadata, connection, entity);
        this.listeners.start(context);
        return context;
    }

    protected void onException(SQLListenerContextImpl context, Exception e) {
        context.setException(e);
        this.listeners.exception(context);
    }

    protected void endContext(SQLListenerContextImpl context) {
        this.listeners.end(context);
        this.context = null;
    }

    protected SQLBindings createBindings(QueryMetadata metadata, SQLSerializer serializer) {
        String queryString = serializer.toString();
        ImmutableList.Builder args = ImmutableList.builder();
        Map<ParamExpression<?>, Object> params = metadata.getParams();
        for (Object o : serializer.getConstants()) {
            if (o instanceof ParamExpression) {
                if (!params.containsKey(o)) {
                    throw new ParamNotSetException((ParamExpression)o);
                }
                o = metadata.getParams().get(o);
            }
            args.add(o);
        }
        return new SQLBindings(queryString, (ImmutableList<Object>)args.build());
    }

    protected SQLSerializer createSerializer() {
        SQLSerializer serializer = new SQLSerializer(this.configuration, true);
        serializer.setUseLiterals(this.useLiterals);
        return serializer;
    }

    public abstract List<SQLBindings> getSQL();

    protected void setParameters(PreparedStatement stmt, List<?> objects, List<Path<?>> constantPaths, Map<ParamExpression<?>, ?> params) {
        if (objects.size() != constantPaths.size()) {
            throw new IllegalArgumentException("Expected " + objects.size() + " paths, " + "but got " + constantPaths.size());
        }
        for (int i = 0; i < objects.size(); ++i) {
            Object o = objects.get(i);
            try {
                if (o instanceof ParamExpression) {
                    if (!params.containsKey(o)) {
                        throw new ParamNotSetException((ParamExpression)o);
                    }
                    o = params.get(o);
                }
                this.configuration.set(stmt, constantPaths.get(i), i + 1, o);
                continue;
            }
            catch (SQLException e) {
                throw this.configuration.translate(e);
            }
        }
    }

    private long executeBatch(PreparedStatement stmt) throws SQLException {
        if (this.configuration.getUseLiterals()) {
            return stmt.executeUpdate();
        }
        if (this.configuration.getTemplates().isBatchCountViaGetUpdateCount()) {
            stmt.executeBatch();
            return stmt.getUpdateCount();
        }
        long rv = 0L;
        for (int i : stmt.executeBatch()) {
            rv += (long)i;
        }
        return rv;
    }

    protected long executeBatch(Collection<PreparedStatement> stmts) throws SQLException {
        long rv = 0L;
        for (PreparedStatement stmt : stmts) {
            rv += this.executeBatch(stmt);
        }
        return rv;
    }

    protected void close(Statement stmt) {
        try {
            stmt.close();
        }
        catch (SQLException e) {
            throw this.configuration.translate(e);
        }
    }

    protected void close(Collection<? extends Statement> stmts) {
        for (Statement statement : stmts) {
            this.close(statement);
        }
    }

    protected void close(ResultSet rs) {
        try {
            rs.close();
        }
        catch (SQLException e) {
            throw this.configuration.translate(e);
        }
    }

    protected void logQuery(Logger logger, String queryString, Collection<Object> parameters) {
        if (logger.isDebugEnabled()) {
            String normalizedQuery = queryString.replace('\n', ' ');
            MDC.put((String)"querydsl.query", (String)normalizedQuery);
            MDC.put((String)"querydsl.parameters", (String)String.valueOf(parameters));
            logger.debug(normalizedQuery);
        }
    }

    protected void cleanupMDC() {
        MDC.remove((String)"querydsl.query");
        MDC.remove((String)"querydsl.parameters");
    }

    protected void reset() {
        this.cleanupMDC();
    }

    protected Connection connection() {
        if (this.conn == null) {
            if (this.connProvider != null) {
                this.conn = this.connProvider.get();
            } else {
                throw new IllegalStateException("No connection provided");
            }
        }
        return this.conn;
    }

    public void setUseLiterals(boolean useLiterals) {
        this.useLiterals = useLiterals;
    }

    public abstract int getBatchCount();
}

