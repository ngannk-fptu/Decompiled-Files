/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 */
package com.atlassian.fugue;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Deprecated
public final class Throwables {
    @Deprecated
    public static <R extends RuntimeException> R propagate(Throwable throwable, Function<Throwable, R> function) {
        com.google.common.base.Throwables.propagateIfPossible((Throwable)((Throwable)Preconditions.checkNotNull((Object)throwable)));
        throw (RuntimeException)function.apply((Object)throwable);
    }

    @Deprecated
    public static <R extends RuntimeException> R propagate(Throwable throwable, Class<R> runtimeType) {
        return Throwables.propagate(throwable, new ExceptionFunction((Class)Preconditions.checkNotNull(runtimeType)));
    }

    private static final class ExceptionFunction<E extends Exception>
    implements Function<Throwable, E> {
        private final Class<E> type;

        private ExceptionFunction(Class<E> type) {
            this.type = (Class)Preconditions.checkNotNull(type);
        }

        public E apply(Throwable throwable) {
            return (E)((Exception)ExceptionFunction.newInstance(ExceptionFunction.getConstructor(this.type, Throwable.class), throwable));
        }

        private static <T> Constructor<T> getConstructor(Class<T> type, Class<?> ... argTypes) {
            try {
                return type.getConstructor(argTypes);
            }
            catch (NoSuchMethodException e) {
                throw com.google.common.base.Throwables.propagate((Throwable)e);
            }
        }

        private static <T> T newInstance(Constructor<T> constructor, Object ... args) {
            try {
                return constructor.newInstance(args);
            }
            catch (InstantiationException e) {
                throw com.google.common.base.Throwables.propagate((Throwable)e);
            }
            catch (IllegalAccessException e) {
                throw com.google.common.base.Throwables.propagate((Throwable)e);
            }
            catch (InvocationTargetException e) {
                throw com.google.common.base.Throwables.propagate((Throwable)e);
            }
        }
    }
}

