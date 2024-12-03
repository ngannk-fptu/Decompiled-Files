/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.internal;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.ScrollMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.dialect.pagination.NoopLimitHandler;
import org.hibernate.engine.jdbc.ColumnNameCache;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.resource.jdbc.ResourceRegistry;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

public abstract class AbstractLoadPlanBasedLoader {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(AbstractLoadPlanBasedLoader.class);
    private final SessionFactoryImplementor factory;
    private ColumnNameCache columnNameCache;

    public AbstractLoadPlanBasedLoader(SessionFactoryImplementor factory) {
        this.factory = factory;
    }

    protected SessionFactoryImplementor getFactory() {
        return this.factory;
    }

    protected abstract LoadQueryDetails getStaticLoadQuery();

    protected abstract int[] getNamedParameterLocs(String var1);

    protected abstract void autoDiscoverTypes(ResultSet var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected List executeLoad(SharedSessionContractImplementor session, QueryParameters queryParameters, LoadQueryDetails loadQueryDetails, boolean returnProxies, ResultTransformer forcedResultTransformer) throws SQLException {
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
        if (queryParameters.isReadOnlyInitialized()) {
            persistenceContext.setDefaultReadOnly(queryParameters.isReadOnly());
        } else {
            queryParameters.setReadOnly(persistenceContext.isDefaultReadOnly());
        }
        persistenceContext.beforeLoad();
        try {
            List results;
            String sql = loadQueryDetails.getSqlStatement();
            SqlStatementWrapper wrapper = null;
            try {
                wrapper = this.executeQueryStatement(sql, queryParameters, false, session);
                results = loadQueryDetails.getResultSetProcessor().extractResults(wrapper.getResultSet(), session, queryParameters, new NamedParameterContext(){

                    @Override
                    public int[] getNamedParameterLocations(String name) {
                        return AbstractLoadPlanBasedLoader.this.getNamedParameterLocs(name);
                    }
                }, returnProxies, queryParameters.isReadOnly(), forcedResultTransformer, Collections.EMPTY_LIST);
            }
            finally {
                if (wrapper != null) {
                    JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
                    ResourceRegistry resourceRegistry = jdbcCoordinator.getResourceRegistry();
                    resourceRegistry.release(wrapper.getStatement());
                    jdbcCoordinator.afterStatementExecution();
                }
                persistenceContext.afterLoad();
            }
            persistenceContext.initializeNonLazyCollections();
            List list = results;
            return list;
        }
        finally {
            persistenceContext.setDefaultReadOnly(defaultReadOnlyOrig);
        }
    }

    protected SqlStatementWrapper executeQueryStatement(String sqlStatement, QueryParameters queryParameters, boolean scroll, SharedSessionContractImplementor session) throws SQLException {
        queryParameters.processFilters(sqlStatement, session);
        LimitHandler limitHandler = this.getLimitHandler(queryParameters.getRowSelection());
        String sql = limitHandler.processSql(queryParameters.getFilteredSQL(), queryParameters);
        sql = session.getJdbcServices().getJdbcEnvironment().getDialect().addSqlHintOrComment(sql, queryParameters, session.getFactory().getSessionFactoryOptions().isCommentsEnabled());
        PreparedStatement st = this.prepareQueryStatement(sql, queryParameters, limitHandler, scroll, session);
        return new SqlStatementWrapper(st, this.getResultSet(st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session));
    }

    protected LimitHandler getLimitHandler(RowSelection selection) {
        LimitHandler limitHandler = this.getFactory().getDialect().getLimitHandler();
        return LimitHelper.useLimit(limitHandler, selection) ? limitHandler : NoopLimitHandler.INSTANCE;
    }

    protected final PreparedStatement prepareQueryStatement(String sql, QueryParameters queryParameters, LimitHandler limitHandler, boolean scroll, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Dialect dialect = session.getJdbcServices().getJdbcEnvironment().getDialect();
        RowSelection selection = queryParameters.getRowSelection();
        boolean useLimit = LimitHelper.useLimit(limitHandler, selection);
        boolean hasFirstRow = LimitHelper.hasFirstRow(selection);
        boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
        boolean callable = queryParameters.isCallable();
        ScrollMode scrollMode = this.getScrollMode(scroll, hasFirstRow, useLimitOffset, queryParameters);
        PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(sql, callable, scrollMode);
        try {
            LockOptions lockOptions;
            int col = 1;
            col += limitHandler.bindLimitParametersAtStartOfQuery(selection, st, col);
            if (callable) {
                col = dialect.registerResultSetOutParameter((CallableStatement)st, col);
            }
            col += this.bindParameterValues(st, queryParameters, col, session);
            col += limitHandler.bindLimitParametersAtEndOfQuery(selection, st, col);
            limitHandler.setMaxRows(selection, st);
            if (selection != null) {
                if (selection.getTimeout() != null) {
                    st.setQueryTimeout(selection.getTimeout());
                }
                if (selection.getFetchSize() != null) {
                    st.setFetchSize(selection.getFetchSize());
                }
            }
            if ((lockOptions = queryParameters.getLockOptions()) != null && lockOptions.getTimeOut() != -1) {
                if (!dialect.supportsLockTimeouts()) {
                    if (log.isDebugEnabled()) {
                        log.debugf("Lock timeout [%s] requested but dialect reported to not support lock timeouts", lockOptions.getTimeOut());
                    }
                } else if (dialect.isLockTimeoutParameterized()) {
                    st.setInt(col++, lockOptions.getTimeOut());
                }
            }
            if (log.isTraceEnabled()) {
                log.tracev("Bound [{0}] parameters total", col);
            }
        }
        catch (SQLException sqle) {
            session.getJdbcCoordinator().getResourceRegistry().release(st);
            session.getJdbcCoordinator().afterStatementExecution();
            throw sqle;
        }
        catch (HibernateException he) {
            session.getJdbcCoordinator().getResourceRegistry().release(st);
            session.getJdbcCoordinator().afterStatementExecution();
            throw he;
        }
        return st;
    }

    protected ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
        boolean canScroll = this.getFactory().getSettings().isScrollableResultSetsEnabled();
        if (canScroll) {
            if (scroll) {
                return queryParameters.getScrollMode();
            }
            if (hasFirstRow && !useLimitOffSet) {
                return ScrollMode.SCROLL_INSENSITIVE;
            }
        }
        return null;
    }

    protected int bindParameterValues(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException {
        int span = 0;
        span += this.bindPositionalParameters(statement, queryParameters, startIndex, session);
        span += this.bindNamedParameters(statement, queryParameters.getNamedParameters(), startIndex + span, session);
        return span;
    }

    protected int bindPositionalParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        Object[] values = queryParameters.getFilteredPositionalParameterValues();
        Type[] types = queryParameters.getFilteredPositionalParameterTypes();
        int span = 0;
        for (int i = 0; i < values.length; ++i) {
            types[i].nullSafeSet(statement, values[i], startIndex + span, session);
            span += types[i].getColumnSpan(this.getFactory());
        }
        return span;
    }

    protected int bindNamedParameters(PreparedStatement statement, Map namedParams, int startIndex, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        if (namedParams != null) {
            Iterator itr = namedParams.entrySet().iterator();
            int result = 0;
            while (itr.hasNext()) {
                int[] locs;
                Map.Entry e = itr.next();
                String name = (String)e.getKey();
                TypedValue typedval = (TypedValue)e.getValue();
                for (int loc : locs = this.getNamedParameterLocs(name)) {
                    if (log.isDebugEnabled()) {
                        log.debugf("bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, loc + startIndex);
                    }
                    typedval.getType().nullSafeSet(statement, typedval.getValue(), loc + startIndex, session);
                }
                result += locs.length;
            }
            return result;
        }
        return 0;
    }

    protected final ResultSet getResultSet(PreparedStatement st, RowSelection selection, LimitHandler limitHandler, boolean autodiscovertypes, SharedSessionContractImplementor session) throws SQLException, HibernateException {
        try {
            ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
            rs = this.wrapResultSetIfEnabled(rs, session);
            if (!limitHandler.supportsLimitOffset() || !LimitHelper.useLimit(limitHandler, selection)) {
                this.advance(rs, selection);
            }
            if (autodiscovertypes) {
                this.autoDiscoverTypes(rs);
            }
            return rs;
        }
        catch (SQLException | HibernateException ex) {
            session.getJdbcCoordinator().getResourceRegistry().release(st);
            session.getJdbcCoordinator().afterStatementExecution();
            throw ex;
        }
    }

    protected void advance(ResultSet rs, RowSelection selection) throws SQLException {
        int firstRow = LimitHelper.getFirstRow(selection);
        if (firstRow != 0) {
            if (this.getFactory().getSettings().isScrollableResultSetsEnabled()) {
                rs.absolute(firstRow);
            } else {
                for (int m = 0; m < firstRow; ++m) {
                    rs.next();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ResultSet wrapResultSetIfEnabled(ResultSet rs, SharedSessionContractImplementor session) {
        if (session.getFactory().getSessionFactoryOptions().isWrapResultSetsEnabled()) {
            try {
                if (log.isDebugEnabled()) {
                    log.debugf("Wrapping result set [%s]", rs);
                }
                ResultSetWrapper wrapper = session.getFactory().getServiceRegistry().getService(JdbcServices.class).getResultSetWrapper();
                AbstractLoadPlanBasedLoader abstractLoadPlanBasedLoader = this;
                synchronized (abstractLoadPlanBasedLoader) {
                    return wrapper.wrap(rs, this.retreiveColumnNameToIndexCache(rs));
                }
            }
            catch (SQLException e) {
                log.unableToWrapResultSet(e);
                return rs;
            }
        }
        return rs;
    }

    private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
        if (this.columnNameCache == null) {
            log.trace("Building columnName->columnIndex cache");
            this.columnNameCache = new ColumnNameCache(rs.getMetaData().getColumnCount());
        }
        return this.columnNameCache;
    }

    protected static class SqlStatementWrapper {
        private final Statement statement;
        private final ResultSet resultSet;

        private SqlStatementWrapper(Statement statement, ResultSet resultSet) {
            this.resultSet = resultSet;
            this.statement = statement;
        }

        public ResultSet getResultSet() {
            return this.resultSet;
        }

        public Statement getStatement() {
            return this.statement;
        }
    }
}

