/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.inject.Provider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryException;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.core.util.ResultSetAdapter;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.ProjectableSQLQuery;
import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLListenerContext;
import com.querydsl.sql.SQLListenerContextImpl;
import com.querydsl.sql.SQLListeners;
import com.querydsl.sql.SQLResultIterator;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.StatementOptions;
import java.lang.reflect.InvocationTargetException;
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
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class AbstractSQLQuery<T, Q extends AbstractSQLQuery<T, Q>>
extends ProjectableSQLQuery<T, Q> {
    protected static final String PARENT_CONTEXT = AbstractSQLQuery.class.getName() + "#PARENT_CONTEXT";
    private static final Logger logger = LoggerFactory.getLogger(AbstractSQLQuery.class);
    private static final QueryFlag rowCountFlag = new QueryFlag(QueryFlag.Position.AFTER_PROJECTION, ", count(*) over() ");
    @Nullable
    private Provider<Connection> connProvider;
    @Nullable
    private Connection conn;
    protected SQLListeners listeners;
    protected boolean useLiterals;
    private boolean getLastCell;
    private Object lastCell;
    private SQLListenerContext parentContext;
    private StatementOptions statementOptions = StatementOptions.DEFAULT;

    public AbstractSQLQuery(@Nullable Connection conn, Configuration configuration) {
        this(conn, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public AbstractSQLQuery(@Nullable Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(new QueryMixin(metadata, false), configuration);
        this.conn = conn;
        this.listeners = new SQLListeners(configuration.getListeners());
        this.useLiterals = configuration.getUseLiterals();
    }

    public AbstractSQLQuery(Provider<Connection> connProvider, Configuration configuration) {
        this(connProvider, configuration, (QueryMetadata)new DefaultQueryMetadata());
    }

    public AbstractSQLQuery(Provider<Connection> connProvider, Configuration configuration, QueryMetadata metadata) {
        super(new QueryMixin(metadata, false), configuration);
        this.connProvider = connProvider;
        this.listeners = new SQLListeners(configuration.getListeners());
        this.useLiterals = configuration.getUseLiterals();
    }

    public SimpleExpression<T> as(String alias) {
        return Expressions.as(this, alias);
    }

    public SimpleExpression<T> as(Path<?> alias) {
        return Expressions.as(this, alias);
    }

    public void addListener(SQLListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public long fetchCount() {
        try {
            return this.unsafeCount();
        }
        catch (SQLException e) {
            String error = "Caught " + e.getClass().getName();
            logger.error(error, (Throwable)e);
            throw this.configuration.translate(e);
        }
    }

    public Q forUpdate() {
        QueryFlag forUpdateFlag = this.configuration.getTemplates().getForUpdateFlag();
        return (Q)((AbstractSQLQuery)this.addFlag(forUpdateFlag));
    }

    public Q forShare() {
        return this.forShare(false);
    }

    public Q forShare(boolean fallbackToForUpdate) {
        SQLTemplates sqlTemplates = this.configuration.getTemplates();
        if (sqlTemplates.isForShareSupported()) {
            QueryFlag forShareFlag = sqlTemplates.getForShareFlag();
            return (Q)((AbstractSQLQuery)this.addFlag(forShareFlag));
        }
        if (fallbackToForUpdate) {
            return this.forUpdate();
        }
        throw new QueryException("Using forShare() is not supported");
    }

    @Override
    protected SQLSerializer createSerializer() {
        SQLSerializer serializer = new SQLSerializer(this.configuration);
        serializer.setUseLiterals(this.useLiterals);
        return serializer;
    }

    @Nullable
    private <U> U get(ResultSet rs, Expression<?> expr, int i, Class<U> type) throws SQLException {
        return this.configuration.get(rs, expr instanceof Path ? (Path)expr : null, i, type);
    }

    private void set(PreparedStatement stmt, Path<?> path, int i, Object value) throws SQLException {
        this.configuration.set(stmt, path, i, value);
    }

    protected SQLListenerContextImpl startContext(Connection connection, QueryMetadata metadata) {
        SQLListenerContextImpl context = new SQLListenerContextImpl(metadata, connection);
        if (this.parentContext != null) {
            context.setData(PARENT_CONTEXT, this.parentContext);
        }
        this.listeners.start(context);
        return context;
    }

    protected void onException(SQLListenerContextImpl context, Exception e) {
        context.setException(e);
        this.listeners.exception(context);
    }

    protected void endContext(SQLListenerContext context) {
        this.listeners.end(context);
    }

    @Deprecated
    public ResultSet getResults(Expression<?> ... exprs) {
        if (exprs.length > 0) {
            this.queryMixin.setProjection(exprs);
        }
        return this.getResults();
    }

    public ResultSet getResults() {
        final SQLListenerContextImpl context = this.startContext(this.connection(), this.queryMixin.getMetadata());
        String queryString = null;
        Object constants = ImmutableList.of();
        try {
            this.listeners.preRender(context);
            SQLSerializer serializer = this.serialize(false);
            queryString = serializer.toString();
            this.logQuery(queryString, serializer.getConstants());
            context.addSQL(queryString);
            this.listeners.rendered(context);
            this.listeners.notifyQuery(this.queryMixin.getMetadata());
            constants = serializer.getConstants();
            this.listeners.prePrepare(context);
            final PreparedStatement stmt = this.getPreparedStatement(queryString);
            this.setParameters(stmt, (List<?>)constants, serializer.getConstantPaths(), this.getMetadata().getParams());
            context.addPreparedStatement(stmt);
            this.listeners.prepared(context);
            this.listeners.preExecute(context);
            ResultSet rs = stmt.executeQuery();
            this.listeners.executed(context);
            return new ResultSetAdapter(rs){

                @Override
                public void close() throws SQLException {
                    try {
                        super.close();
                    }
                    finally {
                        stmt.close();
                        AbstractSQLQuery.this.reset();
                        AbstractSQLQuery.this.endContext(context);
                    }
                }
            };
        }
        catch (SQLException e) {
            this.onException(context, e);
            this.reset();
            this.endContext(context);
            throw this.configuration.translate(queryString, (List<Object>)constants, e);
        }
    }

    private PreparedStatement getPreparedStatement(String queryString) throws SQLException {
        PreparedStatement statement = this.connection().prepareStatement(queryString);
        if (this.statementOptions.getFetchSize() != null) {
            statement.setFetchSize(this.statementOptions.getFetchSize());
        }
        if (this.statementOptions.getMaxFieldSize() != null) {
            statement.setMaxFieldSize(this.statementOptions.getMaxFieldSize());
        }
        if (this.statementOptions.getQueryTimeout() != null) {
            statement.setQueryTimeout(this.statementOptions.getQueryTimeout());
        }
        if (this.statementOptions.getMaxRows() != null) {
            statement.setMaxRows(this.statementOptions.getMaxRows());
        }
        return statement;
    }

    protected Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public CloseableIterator<T> iterate() {
        Expression<?> expr = this.queryMixin.getMetadata().getProjection();
        return this.iterateSingle(this.queryMixin.getMetadata(), expr);
    }

    private CloseableIterator<T> iterateSingle(QueryMetadata metadata, final @Nullable Expression<T> expr) {
        SQLListenerContextImpl context = this.startContext(this.connection(), this.queryMixin.getMetadata());
        String queryString = null;
        Object constants = ImmutableList.of();
        try {
            this.listeners.preRender(context);
            SQLSerializer serializer = this.serialize(false);
            queryString = serializer.toString();
            this.logQuery(queryString, serializer.getConstants());
            context.addSQL(queryString);
            this.listeners.rendered(context);
            this.listeners.notifyQuery(this.queryMixin.getMetadata());
            constants = serializer.getConstants();
            this.listeners.prePrepare(context);
            PreparedStatement stmt = this.getPreparedStatement(queryString);
            this.setParameters(stmt, (List<?>)constants, serializer.getConstantPaths(), metadata.getParams());
            context.addPreparedStatement(stmt);
            this.listeners.prepared(context);
            this.listeners.preExecute(context);
            ResultSet rs = stmt.executeQuery();
            this.listeners.executed(context);
            if (expr == null) {
                SQLResultIterator sQLResultIterator = new SQLResultIterator<T>(this.configuration, stmt, rs, this.listeners, context){

                    @Override
                    public T produceNext(ResultSet rs) throws Exception {
                        return rs.getObject(1);
                    }
                };
                return sQLResultIterator;
            }
            if (expr instanceof FactoryExpression) {
                SQLResultIterator sQLResultIterator = new SQLResultIterator<T>(this.configuration, stmt, rs, this.listeners, context){

                    @Override
                    public T produceNext(ResultSet rs) throws Exception {
                        return AbstractSQLQuery.this.newInstance((FactoryExpression)expr, rs, 0);
                    }
                };
                return sQLResultIterator;
            }
            if (expr.equals(Wildcard.all)) {
                SQLResultIterator sQLResultIterator = new SQLResultIterator<T>(this.configuration, stmt, rs, this.listeners, context){

                    @Override
                    public T produceNext(ResultSet rs) throws Exception {
                        Object[] rv = new Object[rs.getMetaData().getColumnCount()];
                        for (int i = 0; i < rv.length; ++i) {
                            rv[i] = rs.getObject(i + 1);
                        }
                        return rv;
                    }
                };
                return sQLResultIterator;
            }
            SQLResultIterator sQLResultIterator = new SQLResultIterator<T>(this.configuration, stmt, rs, this.listeners, context){

                @Override
                public T produceNext(ResultSet rs) throws Exception {
                    return AbstractSQLQuery.this.get(rs, expr, 1, expr.getType());
                }
            };
            return sQLResultIterator;
        }
        catch (SQLException e) {
            this.onException(context, e);
            this.endContext(context);
            throw this.configuration.translate(queryString, (List<Object>)constants, e);
        }
        catch (RuntimeException e) {
            logger.error("Caught " + e.getClass().getName() + " for " + queryString);
            throw e;
        }
        finally {
            this.reset();
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public List<T> fetch() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [7[CATCHBLOCK]], but top level block is 5[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public QueryResults<T> fetchResults() {
        this.parentContext = this.startContext(this.connection(), this.queryMixin.getMetadata());
        Expression<?> expr = this.queryMixin.getMetadata().getProjection();
        QueryModifiers originalModifiers = this.queryMixin.getMetadata().getModifiers();
        try {
            if (this.configuration.getTemplates().isCountViaAnalytics() && this.queryMixin.getMetadata().getGroupBy().isEmpty()) {
                long total;
                List<T> results;
                try {
                    this.queryMixin.addFlag(rowCountFlag);
                    this.getLastCell = true;
                    results = this.fetch();
                }
                finally {
                    this.queryMixin.removeFlag(rowCountFlag);
                }
                if (!results.isEmpty()) {
                    if (!(this.lastCell instanceof Number)) throw new IllegalStateException("Unsupported lastCell instance " + this.lastCell);
                    total = ((Number)this.lastCell).longValue();
                } else {
                    total = this.fetchCount();
                }
                QueryResults<T> queryResults = new QueryResults<T>(results, originalModifiers, total);
                return queryResults;
            }
            this.queryMixin.setProjection(expr);
            long total = this.fetchCount();
            if (total > 0L) {
                QueryResults<T> queryResults = new QueryResults<T>(this.fetch(), originalModifiers, total);
                return queryResults;
            }
            QueryResults queryResults = QueryResults.emptyResults();
            return queryResults;
        }
        finally {
            this.endContext(this.parentContext);
            this.reset();
            this.getLastCell = false;
            this.parentContext = null;
        }
    }

    private <RT> RT newInstance(FactoryExpression<RT> c, ResultSet rs, int offset) throws InstantiationException, IllegalAccessException, InvocationTargetException, SQLException {
        Object[] args = new Object[c.getArgs().size()];
        for (int i = 0; i < args.length; ++i) {
            args[i] = this.get(rs, c.getArgs().get(i), offset + i + 1, c.getArgs().get(i).getType());
        }
        return c.newInstance(args);
    }

    private void reset() {
        this.cleanupMDC();
    }

    protected void setParameters(PreparedStatement stmt, List<?> objects, List<Path<?>> constantPaths, Map<ParamExpression<?>, ?> params) {
        if (objects.size() != constantPaths.size()) {
            throw new IllegalArgumentException("Expected " + objects.size() + " paths, but got " + constantPaths.size());
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
                this.set(stmt, constantPaths.get(i), i + 1, o);
                continue;
            }
            catch (SQLException e) {
                throw this.configuration.translate(e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long unsafeCount() throws SQLException {
        SQLListenerContextImpl context = this.startContext(this.connection(), this.getMetadata());
        String queryString = null;
        Object constants = ImmutableList.of();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.listeners.preRender(context);
            SQLSerializer serializer = this.serialize(true);
            queryString = serializer.toString();
            this.logQuery(queryString, serializer.getConstants());
            context.addSQL(queryString);
            this.listeners.rendered(context);
            constants = serializer.getConstants();
            this.listeners.prePrepare(context);
            stmt = this.getPreparedStatement(queryString);
            this.setParameters((PreparedStatement)stmt, (List<?>)constants, serializer.getConstantPaths(), this.getMetadata().getParams());
            context.addPreparedStatement((PreparedStatement)stmt);
            this.listeners.prepared(context);
            this.listeners.preExecute(context);
            rs = stmt.executeQuery();
            boolean hasResult = rs.next();
            this.listeners.executed(context);
            if (hasResult) {
                long l = rs.getLong(1);
                return l;
            }
            long l = 0L;
            return l;
        }
        catch (SQLException e) {
            this.onException(context, e);
            throw this.configuration.translate(queryString, (List<Object>)constants, e);
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
            this.endContext(context);
            this.cleanupMDC();
        }
    }

    protected void logQuery(String queryString, Collection<Object> parameters) {
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

    private Connection connection() {
        if (this.conn == null) {
            if (this.connProvider != null) {
                this.conn = (Connection)this.connProvider.get();
            } else {
                throw new IllegalStateException("No connection provided");
            }
        }
        return this.conn;
    }

    public void setUseLiterals(boolean useLiterals) {
        this.useLiterals = useLiterals;
    }

    @Override
    protected void clone(Q query) {
        super.clone(query);
        this.useLiterals = ((AbstractSQLQuery)query).useLiterals;
        this.listeners = new SQLListeners(((AbstractSQLQuery)query).listeners);
    }

    @Override
    public Q clone() {
        return this.clone(this.conn);
    }

    public abstract Q clone(Connection var1);

    public void setStatementOptions(StatementOptions statementOptions) {
        this.statementOptions = statementOptions;
    }
}

