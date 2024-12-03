/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.List;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.AbstractIdsBulkIdHandler;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;

public abstract class AbstractInlineIdsBulkIdHandler
extends AbstractIdsBulkIdHandler {
    public AbstractInlineIdsBulkIdHandler(SessionFactoryImplementor sessionFactory, HqlSqlWalker walker) {
        super(sessionFactory, walker);
    }

    protected IdsClauseBuilder prepareInlineStatement(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        return this.newIdsClauseBuilder(this.selectIds(session, queryParameters));
    }

    protected abstract IdsClauseBuilder newIdsClauseBuilder(List<Object[]> var1);
}

