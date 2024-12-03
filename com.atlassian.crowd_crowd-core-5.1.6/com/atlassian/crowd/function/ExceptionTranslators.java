/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package com.atlassian.crowd.function;

import com.google.common.base.Throwables;
import java.util.function.Function;

public class ExceptionTranslators {
    public static <F, T, E extends Exception> Function<F, T> toRuntimeException(FunctionWithException<F, T, E> function, Function<Exception, ? extends RuntimeException> exceptionTranslator) {
        return f -> {
            try {
                return function.get(f);
            }
            catch (Exception e) {
                Throwables.throwIfUnchecked((Throwable)e);
                throw (RuntimeException)exceptionTranslator.apply(e);
            }
        };
    }

    public static <F, T> Function<F, T> toRuntimeException(FunctionWithException<F, T, RuntimeException> function) {
        return ExceptionTranslators.toRuntimeException(function, RuntimeException::new);
    }

    public static interface BiFunctionWithException<F, U, T, E extends Exception> {
        public T get(F var1, U var2) throws E;
    }

    public static interface FunctionWithException<F, T, E extends Exception> {
        public T get(F var1) throws E;
    }
}

