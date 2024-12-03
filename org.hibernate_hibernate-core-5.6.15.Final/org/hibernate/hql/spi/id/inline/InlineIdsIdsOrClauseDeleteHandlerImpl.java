/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.List;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.inline.AbstractInlineIdsDeleteHandlerImpl;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.hql.spi.id.inline.InlineIdsOrClauseBuilder;
import org.hibernate.sql.Delete;

public class InlineIdsIdsOrClauseDeleteHandlerImpl
extends AbstractInlineIdsDeleteHandlerImpl
implements MultiTableBulkIdStrategy.DeleteHandler {
    public InlineIdsIdsOrClauseDeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        super(factory, walker);
    }

    @Override
    protected IdsClauseBuilder newIdsClauseBuilder(List<Object[]> ids) {
        return new InlineIdsOrClauseBuilder(this.dialect(), this.getTargetedQueryable().getIdentifierType(), this.factory().getTypeResolver(), this.getTargetedQueryable().getIdentifierColumnNames(), ids);
    }

    @Override
    protected Delete generateDelete(String tableName, String[] columnNames, String idSubselect, String comment) {
        Delete delete = new Delete().setTableName(tableName).setWhere(idSubselect);
        if (this.factory().getSessionFactoryOptions().isCommentsEnabled()) {
            delete.setComment(comment);
        }
        return delete;
    }
}

