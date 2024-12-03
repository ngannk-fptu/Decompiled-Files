/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.OptionalAwareDatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OptionalAwareDatabaseAccessorImpl
implements OptionalAwareDatabaseAccessor {
    private static final Logger log = LoggerFactory.getLogger(OptionalAwareDatabaseAccessorImpl.class);
    private final DatabaseAccessor databaseAccessor;

    @Autowired
    public OptionalAwareDatabaseAccessorImpl(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
    }

    @Override
    public <T> Optional<T> runInNewOptionalAwareTransaction(Function<DatabaseConnection, Optional<T>> function, OnRollback onRollback) {
        try {
            return this.databaseAccessor.runInNewTransaction(databaseConnection -> {
                Optional optional = (Optional)Objects.requireNonNull(function.apply((DatabaseConnection)databaseConnection), "Callback result must not be null");
                if (!optional.isPresent()) {
                    throw new OptionalAwareDatabaseAccessorTriggerRollbackException();
                }
                return optional;
            }, onRollback);
        }
        catch (OptionalAwareDatabaseAccessorTriggerRollbackException e) {
            if (log.isDebugEnabled()) {
                log.debug("Rollback was requested due to empty being returned from optional aware transaction");
            }
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> runInOptionalAwareTransaction(Function<DatabaseConnection, Optional<T>> function, OnRollback onRollback) {
        try {
            return this.databaseAccessor.runInTransaction(databaseConnection -> {
                Optional optional = (Optional)Objects.requireNonNull(function.apply((DatabaseConnection)databaseConnection), "Callback result must not be null");
                if (!optional.isPresent()) {
                    throw new OptionalAwareDatabaseAccessorTriggerRollbackException();
                }
                return optional;
            }, onRollback);
        }
        catch (OptionalAwareDatabaseAccessorTriggerRollbackException e) {
            if (log.isDebugEnabled()) {
                log.debug("Rollback was requested due to empty being returned from optional aware transaction");
            }
            return Optional.empty();
        }
    }

    private static class OptionalAwareDatabaseAccessorTriggerRollbackException
    extends RuntimeException {
        OptionalAwareDatabaseAccessorTriggerRollbackException() {
            super("RuntimeException to trigger a transaction rollback");
        }
    }
}

