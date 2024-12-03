/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.rdbms.TransactionalExecutor
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnectionConverter;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.pocketknife.internal.querydsl.schema.DatabaseSchemaCreation;
import com.atlassian.sal.api.rdbms.TransactionalExecutor;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import io.atlassian.fugue.Option;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseAccessorImpl
implements DatabaseAccessor {
    private static final Logger log = LoggerFactory.getLogger(DatabaseAccessorImpl.class);
    private final DatabaseConnectionConverter databaseConnectionConverter;
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private final DatabaseSchemaCreation databaseSchemaCreation;

    @Autowired
    public DatabaseAccessorImpl(DatabaseConnectionConverter databaseConnectionConverter, TransactionalExecutorFactory transactionalExecutorFactory, DatabaseSchemaCreation databaseSchemaCreation) {
        this.databaseConnectionConverter = databaseConnectionConverter;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.databaseSchemaCreation = databaseSchemaCreation;
    }

    @Override
    public <T> T runInNewTransaction(Function<DatabaseConnection, T> function, OnRollback onRollback) {
        return this.execute(function, false, true, onRollback);
    }

    @Override
    public <T> T runInTransaction(Function<DatabaseConnection, T> function, OnRollback onRollback) {
        return this.execute(function, false, false, onRollback);
    }

    private <T> T execute(Function<DatabaseConnection, T> function, boolean readOnly, boolean requiresNew, OnRollback onRollback) {
        this.databaseSchemaCreation.prime();
        TransactionalExecutor executor = this.transactionalExecutorFactory.createExecutor(readOnly, requiresNew);
        try {
            return (T)executor.execute(jdbcConnection -> {
                DatabaseConnection connection = this.databaseConnectionConverter.convertExternallyManaged(jdbcConnection);
                return function.apply(connection);
            });
        }
        catch (RuntimeException re) {
            try {
                Option.option((Object)onRollback).forEach(OnRollback::execute);
            }
            catch (Throwable ignored) {
                log.debug("Error throw from onRollback execution, will be logged, but ignored to ensure rethrown original exception", ignored);
            }
            throw re;
        }
    }
}

