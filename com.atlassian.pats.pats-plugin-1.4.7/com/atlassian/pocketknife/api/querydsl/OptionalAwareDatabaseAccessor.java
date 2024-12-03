/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.api.querydsl;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import java.util.Optional;
import java.util.function.Function;

public interface OptionalAwareDatabaseAccessor {
    @Deprecated
    default public <T> Optional<T> runInNewOptionalAwareTransaction(Function<DatabaseConnection, Optional<T>> callback) {
        return this.runInNewOptionalAwareTransaction(callback, OnRollback.NOOP);
    }

    public <T> Optional<T> runInNewOptionalAwareTransaction(Function<DatabaseConnection, Optional<T>> var1, OnRollback var2);

    @Deprecated
    default public <T> Optional<T> runInOptionalAwareTransaction(Function<DatabaseConnection, Optional<T>> callback) {
        return this.runInOptionalAwareTransaction(callback, OnRollback.NOOP);
    }

    public <T> Optional<T> runInOptionalAwareTransaction(Function<DatabaseConnection, Optional<T>> var1, OnRollback var2);

    @Deprecated
    default public <T> Optional<T> runOptionalAware(Function<DatabaseConnection, Optional<T>> callback) {
        return this.runInOptionalAwareTransaction(callback);
    }

    default public <T> Optional<T> runOptionalAware(Function<DatabaseConnection, Optional<T>> callback, OnRollback onRollback) {
        return this.runInOptionalAwareTransaction(callback, onRollback);
    }
}

