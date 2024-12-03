/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceDatabaseService
implements DatabaseService {
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private final SystemInformationService systemInformationService;

    @Autowired
    public ConfluenceDatabaseService(TransactionalExecutorFactory transactionalExecutorFactory, SystemInformationService systemInformationService) {
        this.transactionalExecutorFactory = Objects.requireNonNull(transactionalExecutorFactory);
        this.systemInformationService = Objects.requireNonNull(systemInformationService);
    }

    @Override
    public <R> R runInConnection(@Nonnull Function<Connection, R> callback) {
        try {
            TransactionalExecutor executor = this.transactionalExecutorFactory.create();
            return (R)executor.execute(callback::apply);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDialect() {
        return this.systemInformationService.getDatabaseInfo().getDialect();
    }
}

