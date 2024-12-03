/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.inline.AbstractInlineIdsUpdateHandlerImpl;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.hql.spi.id.inline.InlineIdsSubSelectValuesListBuilder;

public class InlineIdsSubSelectValuesListUpdateHandlerImpl
extends AbstractInlineIdsUpdateHandlerImpl
implements MultiTableBulkIdStrategy.DeleteHandler {
    public InlineIdsSubSelectValuesListUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        super(factory, walker);
        Dialect dialect = this.factory().getServiceRegistry().getService(JdbcServices.class).getDialect();
        if (!dialect.supportsRowValueConstructorSyntaxInInList()) {
            throw new UnsupportedOperationException("The " + this.getClass().getSimpleName() + " can only be used with Dialects that support IN clause row-value expressions (for composite identifiers)!");
        }
        if (!dialect.supportsValuesList()) {
            throw new UnsupportedOperationException("The " + this.getClass().getSimpleName() + " can only be used with Dialects that support VALUES lists!");
        }
    }

    @Override
    protected IdsClauseBuilder newIdsClauseBuilder(List<Object[]> ids) {
        return new InlineIdsSubSelectValuesListBuilder(this.dialect(), this.getTargetedQueryable().getIdentifierType(), this.factory().getTypeResolver(), this.getTargetedQueryable().getIdentifierColumnNames(), ids);
    }
}

