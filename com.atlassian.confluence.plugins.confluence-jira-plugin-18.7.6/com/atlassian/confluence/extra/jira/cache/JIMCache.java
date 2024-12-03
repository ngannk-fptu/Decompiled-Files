/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 */
package com.atlassian.confluence.extra.jira.cache;

import com.atlassian.cache.Cache;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface JIMCache<V> {
    public CompletionStage<Optional<V>> get(String var1);

    public CompletionStage<V> get(String var1, Supplier<V> var2);

    public CompletionStage<Void> remove(String ... var1);

    public CompletionStage<?> putIfAbsent(String var1, V var2);

    public static <T, R> R fold(CompletionStage<T> stage, BiFunction<T, Throwable, R> fn) {
        return stage.handle(fn).toCompletableFuture().join();
    }

    public static <T, R> R fold(CompletionStage<T> stage, Function<T, R> success, Function<Throwable, R> failure) {
        return (R)stage.handle((val, err) -> err != null ? failure.apply((Throwable)err) : success.apply(val)).toCompletableFuture().join();
    }

    public static class AtlassianCacheImpl<V>
    implements JIMCache<V> {
        private final Cache<String, V> delegate;

        public AtlassianCacheImpl(Cache<String, V> delegate) {
            this.delegate = delegate;
        }

        @Override
        public CompletionStage<Optional<V>> get(String key) {
            return AtlassianCacheImpl.perform(() -> Optional.ofNullable(this.delegate.get((Object)key)));
        }

        @Override
        public CompletionStage<V> get(String key, Supplier<V> supplier) {
            return AtlassianCacheImpl.perform(() -> this.delegate.get((Object)key, ((Supplier)supplier)::get));
        }

        @Override
        public CompletionStage<Void> remove(String ... keys) {
            return AtlassianCacheImpl.perform(() -> {
                Arrays.stream(keys).forEach(arg_0 -> this.delegate.remove(arg_0));
                return null;
            });
        }

        @Override
        public CompletionStage<?> putIfAbsent(String key, V value) {
            return AtlassianCacheImpl.perform(() -> {
                this.delegate.putIfAbsent((Object)key, value);
                return null;
            });
        }

        private static <T> CompletableFuture<T> perform(Callable<T> task) {
            try {
                T result = task.call();
                return CompletableFuture.completedFuture(result);
            }
            catch (Exception ex) {
                CompletableFuture result = new CompletableFuture();
                result.completeExceptionally(ex);
                return result;
            }
        }
    }
}

