/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id;

import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.persister.entity.Queryable;

public interface MultiTableBulkIdStrategy {
    @Deprecated
    default public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata, SessionFactoryOptions sessionFactoryOptions) {
        throw new IllegalStateException("prepare() was not implemented!");
    }

    default public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata, SessionFactoryOptions sessionFactoryOptions, SqlStringGenerationContext sqlStringGenerationContext) {
        this.prepare(jdbcServices, connectionAccess, metadata, sessionFactoryOptions);
    }

    public void release(JdbcServices var1, JdbcConnectionAccess var2);

    public UpdateHandler buildUpdateHandler(SessionFactoryImplementor var1, HqlSqlWalker var2);

    public DeleteHandler buildDeleteHandler(SessionFactoryImplementor var1, HqlSqlWalker var2);

    public static interface DeleteHandler {
        public Queryable getTargetedQueryable();

        public String[] getSqlStatements();

        public int execute(SharedSessionContractImplementor var1, QueryParameters var2);
    }

    public static interface UpdateHandler {
        public Queryable getTargetedQueryable();

        public String[] getSqlStatements();

        public int execute(SharedSessionContractImplementor var1, QueryParameters var2);
    }
}

