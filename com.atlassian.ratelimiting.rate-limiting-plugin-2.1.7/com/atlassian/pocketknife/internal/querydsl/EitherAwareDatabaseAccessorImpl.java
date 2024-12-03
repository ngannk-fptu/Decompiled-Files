/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.pocketknife.internal.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.EitherAwareDatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import io.atlassian.fugue.Either;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EitherAwareDatabaseAccessorImpl
implements EitherAwareDatabaseAccessor {
    private static final Logger log = LoggerFactory.getLogger(EitherAwareDatabaseAccessorImpl.class);
    private final DatabaseAccessor databaseAccessor;

    @Autowired
    public EitherAwareDatabaseAccessorImpl(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
    }

    @Override
    public <L, R> Either<L, R> runInNewEitherAwareTransaction(Function<DatabaseConnection, Either<L, R>> function, OnRollback onRollback) {
        AtomicReference leftReference = new AtomicReference();
        try {
            return this.databaseAccessor.runInNewTransaction(databaseConnection -> {
                Either either = (Either)Objects.requireNonNull(function.apply((DatabaseConnection)databaseConnection), "Callback result must not be null");
                either.left().forEach(l -> {
                    leftReference.set(l);
                    throw new EitherAwareDatabaseAccessorTriggerRollbackException();
                });
                return either;
            }, onRollback);
        }
        catch (EitherAwareDatabaseAccessorTriggerRollbackException e) {
            if (log.isDebugEnabled()) {
                log.debug("Rollback was requested due to left '{}' being returned from either aware transaction", leftReference.get());
            }
            return Either.left(leftReference.get());
        }
    }

    @Override
    public <L, R> Either<L, R> runInEitherAwareTransaction(Function<DatabaseConnection, Either<L, R>> function, OnRollback onRollback) {
        AtomicReference leftReference = new AtomicReference();
        try {
            return this.databaseAccessor.runInTransaction(databaseConnection -> {
                Either either = (Either)Objects.requireNonNull(function.apply((DatabaseConnection)databaseConnection), "Callback result must not be null");
                either.left().forEach(l -> {
                    leftReference.set(l);
                    throw new EitherAwareDatabaseAccessorTriggerRollbackException();
                });
                return either;
            }, onRollback);
        }
        catch (EitherAwareDatabaseAccessorTriggerRollbackException e) {
            if (log.isDebugEnabled()) {
                log.debug("Rollback was requested due to left '{}' being returned from either aware transaction", leftReference.get());
            }
            return Either.left(leftReference.get());
        }
    }

    private static class EitherAwareDatabaseAccessorTriggerRollbackException
    extends RuntimeException {
        EitherAwareDatabaseAccessorTriggerRollbackException() {
            super("RuntimeException to trigger a transaction rollback");
        }
    }
}

