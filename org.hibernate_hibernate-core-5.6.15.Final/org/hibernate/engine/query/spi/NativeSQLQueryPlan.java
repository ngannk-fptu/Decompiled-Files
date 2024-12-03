/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.BulkOperationCleanupAction;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.param.ParameterBinder;

public class NativeSQLQueryPlan
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(NativeSQLQueryPlan.class);
    private final String sourceQuery;
    private final CustomQuery customQuery;

    public NativeSQLQueryPlan(String sourceQuery, CustomQuery customQuery) {
        this.sourceQuery = sourceQuery;
        this.customQuery = customQuery;
    }

    public String getSourceQuery() {
        return this.sourceQuery;
    }

    public CustomQuery getCustomQuery() {
        return this.customQuery;
    }

    protected void coordinateSharedCacheCleanup(SharedSessionContractImplementor session) {
        BulkOperationCleanupAction action = new BulkOperationCleanupAction(session, this.getCustomQuery().getQuerySpaces());
        if (session.isEventSource()) {
            ((EventSource)session).getActionQueue().addAction(action);
        } else {
            action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion(true, session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int performExecuteUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
        this.coordinateSharedCacheCleanup(session);
        if (queryParameters.isCallable()) {
            throw new IllegalArgumentException("callable not yet supported for native queries");
        }
        int result = 0;
        RowSelection selection = queryParameters.getRowSelection();
        try {
            queryParameters.processFilters(this.customQuery.getSQL(), session);
            String sql = session.getJdbcServices().getDialect().addSqlHintOrComment(queryParameters.getFilteredSQL(), queryParameters, session.getFactory().getSessionFactoryOptions().isCommentsEnabled());
            PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, false);
            try {
                int col = 1;
                for (ParameterBinder binder : this.customQuery.getParameterValueBinders()) {
                    col += binder.bind(ps, queryParameters, session, col);
                }
                if (selection != null && selection.getTimeout() != null) {
                    ps.setQueryTimeout(selection.getTimeout());
                }
                result = session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
            }
            finally {
                if (ps != null) {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
            }
        }
        catch (SQLException sqle) {
            throw session.getFactory().getSQLExceptionHelper().convert(sqle, "could not execute native bulk manipulation query", this.sourceQuery);
        }
        return result;
    }
}

