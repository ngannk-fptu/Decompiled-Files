/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.api.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import java.util.function.Function;

public interface DatabaseAccessor {
    @Deprecated
    default public <T> T runInNewTransaction(Function<DatabaseConnection, T> callback) {
        return this.runInNewTransaction(callback, OnRollback.NOOP);
    }

    public <T> T runInNewTransaction(Function<DatabaseConnection, T> var1, OnRollback var2);

    @Deprecated
    default public <T> T runInTransaction(Function<DatabaseConnection, T> callback) {
        return this.runInTransaction(callback, OnRollback.NOOP);
    }

    public <T> T runInTransaction(Function<DatabaseConnection, T> var1, OnRollback var2);

    @Deprecated
    default public <T> T run(Function<DatabaseConnection, T> callback) {
        return this.runInTransaction(callback);
    }

    default public <T> T run(Function<DatabaseConnection, T> callback, OnRollback onRollback) {
        return this.runInTransaction(callback, onRollback);
    }
}

