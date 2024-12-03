/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.exec;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.BulkOperationCleanupAction;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.ast.exec.StatementExecutor;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;

public abstract class BasicExecutor
implements StatementExecutor {
    abstract Queryable getPersister();

    abstract String getSql();

    abstract List<ParameterSpecification> getParameterSpecifications();

    @Override
    public String[] getSqlStatements() {
        return new String[]{this.getSql()};
    }

    @Override
    public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
        BulkOperationCleanupAction action = new BulkOperationCleanupAction(session, this.getPersister());
        if (session.isEventSource()) {
            ((EventSource)session).getActionQueue().addAction(action);
        } else {
            action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion(true, session);
        }
        return this.doExecute(session.getJdbcServices().getDialect().addSqlHintOrComment(this.getSql(), parameters, session.getFactory().getSessionFactoryOptions().isCommentsEnabled()), parameters, this.getParameterSpecifications(), session);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int doExecute(String sql, QueryParameters parameters, List<ParameterSpecification> parameterSpecifications, SharedSessionContractImplementor session) throws HibernateException {
        PreparedStatement st = null;
        try {
            st = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(sql, false);
            int pos = 1;
            for (ParameterSpecification parameter : parameterSpecifications) {
                pos += parameter.bind(st, parameters, session, pos);
            }
            RowSelection selection = parameters.getRowSelection();
            if (selection != null && selection.getTimeout() != null) {
                st.setQueryTimeout(selection.getTimeout());
            }
            int n = session.getJdbcCoordinator().getResultSetReturn().executeUpdate(st);
            if (st != null) {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                session.getJdbcCoordinator().afterStatementExecution();
            }
            return n;
        }
        catch (Throwable throwable) {
            try {
                if (st != null) {
                    session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
                    session.getJdbcCoordinator().afterStatementExecution();
                }
                throw throwable;
            }
            catch (SQLException sqle) {
                throw session.getJdbcServices().getSqlExceptionHelper().convert(sqle, "could not execute update query", sql);
            }
        }
    }
}

