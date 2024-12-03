/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.exec;

import org.hibernate.HibernateException;
import org.hibernate.action.internal.BulkOperationCleanupAction;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.exec.StatementExecutor;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;

public class MultiTableDeleteExecutor
implements StatementExecutor {
    private final MultiTableBulkIdStrategy.DeleteHandler deleteHandler;

    public MultiTableDeleteExecutor(HqlSqlWalker walker) {
        MultiTableBulkIdStrategy strategy = walker.getSessionFactoryHelper().getFactory().getSessionFactoryOptions().getMultiTableBulkIdStrategy();
        this.deleteHandler = strategy.buildDeleteHandler(walker.getSessionFactoryHelper().getFactory(), walker);
    }

    public MultiTableBulkIdStrategy.DeleteHandler getDeleteHandler() {
        return this.deleteHandler;
    }

    @Override
    public String[] getSqlStatements() {
        return this.deleteHandler.getSqlStatements();
    }

    @Override
    public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
        BulkOperationCleanupAction action = new BulkOperationCleanupAction(session, this.deleteHandler.getTargetedQueryable());
        if (session.isEventSource()) {
            ((EventSource)session).getActionQueue().addAction(action);
        } else {
            action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion(true, session);
        }
        return this.deleteHandler.execute(session, parameters);
    }
}

