/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.inline.InlineIdsSubSelectValuesListDeleteHandlerImpl;
import org.hibernate.hql.spi.id.inline.InlineIdsSubSelectValuesListUpdateHandlerImpl;

public class InlineIdsSubSelectValueListBulkIdStrategy
implements MultiTableBulkIdStrategy {
    public static final InlineIdsSubSelectValueListBulkIdStrategy INSTANCE = new InlineIdsSubSelectValueListBulkIdStrategy();

    @Override
    public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess jdbcConnectionAccess, MetadataImplementor metadataImplementor, SessionFactoryOptions sessionFactoryOptions, SqlStringGenerationContext sqlStringGenerationContext) {
    }

    @Override
    public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
    }

    @Override
    public MultiTableBulkIdStrategy.UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        return new InlineIdsSubSelectValuesListUpdateHandlerImpl(factory, walker);
    }

    @Override
    public MultiTableBulkIdStrategy.DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        return new InlineIdsSubSelectValuesListDeleteHandlerImpl(factory, walker);
    }
}

