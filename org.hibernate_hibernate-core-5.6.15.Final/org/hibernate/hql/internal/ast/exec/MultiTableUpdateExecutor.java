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

public class MultiTableUpdateExecutor
implements StatementExecutor {
    private final MultiTableBulkIdStrategy.UpdateHandler updateHandler;

    public MultiTableUpdateExecutor(HqlSqlWalker walker) {
        MultiTableBulkIdStrategy strategy = walker.getSessionFactoryHelper().getFactory().getSessionFactoryOptions().getMultiTableBulkIdStrategy();
        this.updateHandler = strategy.buildUpdateHandler(walker.getSessionFactoryHelper().getFactory(), walker);
    }

    public MultiTableBulkIdStrategy.UpdateHandler getUpdateHandler() {
        return this.updateHandler;
    }

    @Override
    public String[] getSqlStatements() {
        return this.updateHandler.getSqlStatements();
    }

    @Override
    public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
        BulkOperationCleanupAction action = new BulkOperationCleanupAction(session, this.updateHandler.getTargetedQueryable());
        if (session.isEventSource()) {
            ((EventSource)session).getActionQueue().addAction(action);
        } else {
            action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion(true, session);
        }
        return this.updateHandler.execute(session, parameters);
    }
}

