/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.common.util;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    public R apply(T var1) throws Exception;

    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> lambda) {
        return ThrowingFunction.unchecked(lambda, RuntimeException::new);
    }

    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> lambda, Function<Exception, ? extends RuntimeException> exceptionMapper) {
        return input -> {
            try {
                return lambda.apply(input);
            }
            catch (Exception ex) {
                throw (RuntimeException)exceptionMapper.apply(ex);
            }
        };
    }
}

