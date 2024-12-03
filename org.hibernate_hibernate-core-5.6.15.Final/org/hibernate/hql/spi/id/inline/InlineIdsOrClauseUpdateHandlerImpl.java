/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.List;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.inline.AbstractInlineIdsUpdateHandlerImpl;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.hql.spi.id.inline.InlineIdsOrClauseBuilder;
import org.hibernate.sql.Update;

public class InlineIdsOrClauseUpdateHandlerImpl
extends AbstractInlineIdsUpdateHandlerImpl
implements MultiTableBulkIdStrategy.UpdateHandler {
    public InlineIdsOrClauseUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        super(factory, walker);
    }

    @Override
    protected IdsClauseBuilder newIdsClauseBuilder(List<Object[]> ids) {
        return new InlineIdsOrClauseBuilder(this.dialect(), this.getTargetedQueryable().getIdentifierType(), this.factory().getTypeResolver(), this.getTargetedQueryable().getIdentifierColumnNames(), ids);
    }

    @Override
    protected Update generateUpdate(String tableName, String[] columnNames, String idSubselect, String comment) {
        Update update = new Update(this.factory().getServiceRegistry().getService(JdbcServices.class).getDialect()).setTableName(tableName).setWhere(idSubselect);
        if (this.factory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment(comment);
        }
        return update;
    }
}

