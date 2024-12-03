/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.ExperimentalApi;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;

@ExperimentalApi
public class VCacheUtils {
    @Deprecated
    public static <V> V join(CompletionStage<V> stage) {
        return stage.toCompletableFuture().join();
    }

    public static <V> V unsafeJoin(CompletionStage<V> stage) {
        return stage.toCompletableFuture().join();
    }

    public static <T, R> R fold(CompletionStage<T> stage, Function<T, R> success, Function<Throwable, R> failure) {
        return (R)VCacheUtils.unsafeJoin(stage.handle((val, err) -> err != null ? failure.apply((Throwable)err) : success.apply(val)));
    }

    public static <T, R> R fold(CompletionStage<T> stage, BiFunction<T, Throwable, R> fn) {
        return VCacheUtils.unsafeJoin(stage.handle(fn));
    }
}

