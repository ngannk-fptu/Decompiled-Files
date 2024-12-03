/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.cte;

import java.util.Arrays;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.AbstractIdsBulkIdHandler;
import org.hibernate.hql.spi.id.cte.CteValuesListBuilder;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.Queryable;

public abstract class AbstractCteValuesListBulkIdHandler
extends AbstractIdsBulkIdHandler {
    private final String catalog;
    private final String schema;
    private final JdbcEnvironment jdbcEnvironment;

    public AbstractCteValuesListBulkIdHandler(SessionFactoryImplementor sessionFactory, HqlSqlWalker walker, String catalog, String schema) {
        super(sessionFactory, walker);
        Dialect dialect = sessionFactory.getServiceRegistry().getService(JdbcServices.class).getDialect();
        if (!dialect.supportsNonQueryWithCTE()) {
            throw new UnsupportedOperationException("The " + this.getClass().getSimpleName() + " can only be used with Dialects that support CTE that can take UPDATE or DELETE statements as well!");
        }
        if (!dialect.supportsValuesList()) {
            throw new UnsupportedOperationException("The " + this.getClass().getSimpleName() + " can only be used with Dialects that support VALUES lists!");
        }
        if (!dialect.supportsRowValueConstructorSyntaxInInList()) {
            throw new UnsupportedOperationException("The " + this.getClass().getSimpleName() + " can only be used with Dialects that support IN clause row-value expressions (for composite identifiers)!");
        }
        this.jdbcEnvironment = sessionFactory.getServiceRegistry().getService(JdbcServices.class).getJdbcEnvironment();
        this.catalog = catalog;
        this.schema = schema;
    }

    protected String determineIdTableName(Queryable persister) {
        String qualifiedTableName = this.jdbcEnvironment.getIdentifierHelper().applyGlobalQuoting("HT_" + StringHelper.unquote(persister.getTableName(), this.jdbcEnvironment.getDialect())).render();
        return persister.getFactory().getSqlStringGenerationContext().formatWithoutDefaults(new QualifiedTableName(Identifier.toIdentifier(this.catalog), Identifier.toIdentifier(this.schema), Identifier.toIdentifier(qualifiedTableName)));
    }

    protected String generateIdSubselect(Queryable persister) {
        return "select " + String.join((CharSequence)", ", persister.getIdentifierColumnNames()) + " from " + this.determineIdTableName(persister) + " tmp2";
    }

    protected String generateIdSubselect(String idSubselect, Queryable persister, AbstractCollectionPersister cPersister) {
        Object[] columnNames = AbstractCteValuesListBulkIdHandler.getKeyColumnNames(persister, cPersister);
        if (Arrays.equals(this.getTargetedQueryable().getIdentifierColumnNames(), columnNames)) {
            return idSubselect;
        }
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append("select ");
        AbstractCteValuesListBulkIdHandler.appendJoined(", ", (String[])columnNames, selectBuilder);
        selectBuilder.append(" from ").append(this.getTargetedQueryable().getTableName());
        selectBuilder.append(" tmp where (");
        AbstractCteValuesListBulkIdHandler.appendJoined(", ", this.getTargetedQueryable().getIdentifierColumnNames(), selectBuilder);
        selectBuilder.append(") in (select ");
        AbstractCteValuesListBulkIdHandler.appendJoined(", ", this.getTargetedQueryable().getIdentifierColumnNames(), selectBuilder);
        selectBuilder.append(" from ").append(this.determineIdTableName(persister)).append(" tmp2)");
        return selectBuilder.toString();
    }

    protected CteValuesListBuilder prepareCteStatement(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        return new CteValuesListBuilder(this.determineIdTableName(this.getTargetedQueryable()), this.getTargetedQueryable().getIdentifierColumnNames(), this.selectIds(session, queryParameters));
    }
}

