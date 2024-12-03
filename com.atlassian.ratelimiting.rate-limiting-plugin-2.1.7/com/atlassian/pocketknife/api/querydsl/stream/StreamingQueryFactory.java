/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.pocketknife.api.querydsl.stream;

import com.atlassian.annotations.PublicApi;
import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.stream.ClosePromise;
import com.atlassian.pocketknife.api.querydsl.stream.CloseableIterable;
import com.querydsl.sql.SQLQuery;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@PublicApi
public interface StreamingQueryFactory {
    public <Q> CloseableIterable<Q> stream(DatabaseConnection var1, Supplier<SQLQuery<Q>> var2);

    public <Q> CloseableIterable<Q> stream(DatabaseConnection var1, ClosePromise var2, Supplier<SQLQuery<Q>> var3);

    public <Q, T> List<T> streamyMap(DatabaseConnection var1, StreamyMapClosure<Q, T> var2);

    public <Q, T> T streamyFold(DatabaseConnection var1, T var2, StreamyFoldClosure<Q, T> var3);

    public static interface StreamyMapClosure<Q, T> {
        public Function<DatabaseConnection, SQLQuery<Q>> getQuery();

        public Function<Q, T> getMapFunction();
    }

    public static interface StreamyFoldClosure<Q, T> {
        public Function<DatabaseConnection, SQLQuery<Q>> getQuery();

        public BiFunction<T, Q, T> getFoldFunction();
    }
}

