/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.querydsl;

import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLSupplier;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDslHelper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.dml.SQLDeleteClause;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="transactionalQueryDSLSupplier")
public class TransactionalQueryDSLSupplier
implements QueryDSLSupplier {
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private final SystemInformationService systemInformationService;

    @Autowired
    public TransactionalQueryDSLSupplier(@ComponentImport TransactionalExecutorFactory transactionalExecutorFactory, @ComponentImport SystemInformationService systemInformationService) {
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.systemInformationService = systemInformationService;
    }

    @Override
    public <A> A executeSQLQuery(@Nonnull QueryDSLSupplier.QueryCallback<A> queryCallback) {
        return (A)this.transactionalExecutorFactory.create().execute(connection -> {
            SQLQuery<Void> query = new SQLQuery<Void>(connection, QueryDslHelper.getConfiguration(this.systemInformationService.getDatabaseInfo().getDialect()));
            return queryCallback.execute(query);
        });
    }

    @Override
    public <A> A executeDeleteSQLClause(RelationalPath<?> relationalPath, @Nonnull QueryDSLSupplier.DeleteQueryCallback<A> queryCallback) {
        return (A)this.transactionalExecutorFactory.create().execute(connection -> {
            SQLDeleteClause deleteClause = new SQLDeleteClause(connection, QueryDslHelper.getConfiguration(this.systemInformationService.getDatabaseInfo().getDialect()), relationalPath);
            return queryCallback.execute(deleteClause);
        });
    }
}

