/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 */
package com.atlassian.pocketknife.api.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import io.atlassian.fugue.Either;
import java.util.function.Function;

public interface EitherAwareDatabaseAccessor {
    @Deprecated
    default public <L, R> Either<L, R> runInNewEitherAwareTransaction(Function<DatabaseConnection, Either<L, R>> callback) {
        return this.runInNewEitherAwareTransaction(callback, OnRollback.NOOP);
    }

    public <L, R> Either<L, R> runInNewEitherAwareTransaction(Function<DatabaseConnection, Either<L, R>> var1, OnRollback var2);

    @Deprecated
    default public <L, R> Either<L, R> runInEitherAwareTransaction(Function<DatabaseConnection, Either<L, R>> callback) {
        return this.runInEitherAwareTransaction(callback, OnRollback.NOOP);
    }

    public <L, R> Either<L, R> runInEitherAwareTransaction(Function<DatabaseConnection, Either<L, R>> var1, OnRollback var2);

    @Deprecated
    default public <L, R> Either<L, R> runEitherAware(Function<DatabaseConnection, Either<L, R>> callback) {
        return this.runInEitherAwareTransaction(callback);
    }

    default public <L, R> Either<L, R> runEitherAware(Function<DatabaseConnection, Either<L, R>> callback, OnRollback onRollback) {
        return this.runInEitherAwareTransaction(callback, onRollback);
    }
}

