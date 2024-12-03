/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.function.Supplier;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.SupplierException;

public class SupplierUtil {
    public static final Supplier<DefaultEdge> DEFAULT_EDGE_SUPPLIER = DefaultEdge::new;
    public static final Supplier<DefaultWeightedEdge> DEFAULT_WEIGHTED_EDGE_SUPPLIER = DefaultWeightedEdge::new;
    public static final Supplier<Object> OBJECT_SUPPLIER = Object::new;

    public static <T> Supplier<T> createSupplier(Class<? extends T> clazz) {
        if (clazz == DefaultEdge.class) {
            return DEFAULT_EDGE_SUPPLIER;
        }
        if (clazz == DefaultWeightedEdge.class) {
            return DEFAULT_WEIGHTED_EDGE_SUPPLIER;
        }
        if (clazz == Object.class) {
            return OBJECT_SUPPLIER;
        }
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(new Class[0]);
            if (!(Modifier.isPublic(constructor.getModifiers()) && Modifier.isPublic(constructor.getDeclaringClass().getModifiers()) || constructor.canAccess(null))) {
                constructor.setAccessible(true);
            }
            return new ConstructorSupplier<T>(constructor);
        }
        catch (ReflectiveOperationException e) {
            return SupplierUtil.getThrowingSupplier(e);
        }
    }

    private static <T> Supplier<T> getThrowingSupplier(Throwable e) {
        return (Supplier<Object> & Serializable)() -> {
            throw new SupplierException(e.getMessage(), e);
        };
    }

    public static Supplier<DefaultEdge> createDefaultEdgeSupplier() {
        return DEFAULT_EDGE_SUPPLIER;
    }

    public static Supplier<DefaultWeightedEdge> createDefaultWeightedEdgeSupplier() {
        return DEFAULT_WEIGHTED_EDGE_SUPPLIER;
    }

    public static Supplier<Integer> createIntegerSupplier() {
        return SupplierUtil.createIntegerSupplier(0);
    }

    public static Supplier<Integer> createIntegerSupplier(int start) {
        int[] modifiableInt = new int[]{start};
        return (Supplier<Integer> & Serializable)() -> {
            int n = modifiableInt[0];
            modifiableInt[0] = n + 1;
            return n;
        };
    }

    public static Supplier<Long> createLongSupplier() {
        return SupplierUtil.createLongSupplier(0L);
    }

    public static Supplier<Long> createLongSupplier(long start) {
        long[] modifiableLong = new long[]{start};
        return (Supplier<Long> & Serializable)() -> {
            long l = modifiableLong[0];
            modifiableLong[0] = l + 1L;
            return l;
        };
    }

    public static Supplier<String> createStringSupplier() {
        return SupplierUtil.createStringSupplier(0);
    }

    public static Supplier<String> createRandomUUIDStringSupplier() {
        return (Supplier<String> & Serializable)() -> UUID.randomUUID().toString();
    }

    public static Supplier<String> createStringSupplier(int start) {
        int[] container = new int[]{start};
        return (Supplier<String> & Serializable)() -> {
            int n = container[0];
            container[0] = n + 1;
            return String.valueOf(n);
        };
    }

    private static class ConstructorSupplier<T>
    implements Supplier<T>,
    Serializable {
        private final Constructor<? extends T> constructor;

        public ConstructorSupplier(Constructor<? extends T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T get() {
            try {
                return this.constructor.newInstance(new Object[0]);
            }
            catch (ReflectiveOperationException ex) {
                throw new SupplierException("Supplier failed", ex);
            }
        }

        Object writeReplace() throws ObjectStreamException {
            return new SerializedForm<T>(this.constructor.getDeclaringClass());
        }

        private static class SerializedForm<T>
        implements Serializable {
            private static final long serialVersionUID = -2385289829144892760L;
            private final Class<T> type;

            public SerializedForm(Class<T> type) {
                this.type = type;
            }

            Object readResolve() throws ObjectStreamException {
                try {
                    return new ConstructorSupplier<T>(this.type.getDeclaredConstructor(new Class[0]));
                }
                catch (ReflectiveOperationException e) {
                    InvalidObjectException ex = new InvalidObjectException("Failed to get no-args constructor from " + this.type);
                    ex.initCause(e);
                    throw ex;
                }
            }
        }
    }
}

