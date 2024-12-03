/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Functions;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ExceptionPolicy {
    public <T> Function<Supplier<T>, Supplier<T>> handler();

    public static enum Policies implements ExceptionPolicy
    {
        IGNORE_EXCEPTIONS{

            @Override
            public <T> Function<Supplier<T>, Supplier<T>> handler() {
                return Functions.ignoreExceptions();
            }
        }
        ,
        THROW{

            @Override
            public <T> Function<Supplier<T>, Supplier<T>> handler() {
                return Function.identity();
            }
        };

    }
}

