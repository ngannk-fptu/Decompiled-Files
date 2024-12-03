/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package io.atlassian.fugue.deprecated;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

@Deprecated
public final class Throwables {
    @Deprecated
    public static <R extends RuntimeException> R propagate(Throwable throwable, Function<Throwable, R> function) {
        com.google.common.base.Throwables.propagateIfPossible((Throwable)Objects.requireNonNull(throwable));
        throw (RuntimeException)function.apply(throwable);
    }

    @Deprecated
    public static <R extends RuntimeException> R propagate(Throwable throwable, Class<R> runtimeType) {
        return Throwables.propagate(throwable, new ExceptionFunction(Objects.requireNonNull(runtimeType)));
    }

    private static final class ExceptionFunction<E extends Exception>
    implements Function<Throwable, E> {
        private final Class<E> type;

        private ExceptionFunction(Class<E> type) {
            this.type = Objects.requireNonNull(type);
        }

        @Override
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
            catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw com.google.common.base.Throwables.propagate((Throwable)e);
            }
        }
    }
}

