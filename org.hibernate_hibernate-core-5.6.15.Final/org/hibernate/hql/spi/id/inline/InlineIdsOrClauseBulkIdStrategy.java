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
import org.hibernate.hql.spi.id.inline.InlineIdsIdsOrClauseDeleteHandlerImpl;
import org.hibernate.hql.spi.id.inline.InlineIdsOrClauseUpdateHandlerImpl;

public class InlineIdsOrClauseBulkIdStrategy
implements MultiTableBulkIdStrategy {
    public static final InlineIdsOrClauseBulkIdStrategy INSTANCE = new InlineIdsOrClauseBulkIdStrategy();

    @Override
    public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess jdbcConnectionAccess, MetadataImplementor metadataImplementor, SessionFactoryOptions sessionFactoryOptions, SqlStringGenerationContext sqlStringGenerationContext) {
    }

    @Override
    public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
    }

    @Override
    public MultiTableBulkIdStrategy.UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        return new InlineIdsOrClauseUpdateHandlerImpl(factory, walker);
    }

    @Override
    public MultiTableBulkIdStrategy.DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        return new InlineIdsIdsOrClauseDeleteHandlerImpl(factory, walker);
    }
}

