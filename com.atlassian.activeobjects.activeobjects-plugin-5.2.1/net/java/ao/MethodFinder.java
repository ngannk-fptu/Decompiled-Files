/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.java.ao;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import net.java.ao.schema.FieldNameConverter;

final class MethodFinder {
    private static final Supplier<MethodFinder> INSTANCE_SUPPLIER = Suppliers.synchronizedSupplier((Supplier)Suppliers.memoize((Supplier)new Supplier<MethodFinder>(){

        public MethodFinder get() {
            return new MethodFinder();
        }
    }));

    MethodFinder() {
    }

    public Iterable<Method> findAnnotatedMethods(Class<? extends Annotation> annotation, Class<?> type) {
        return new AnnotatedMethods(annotation, type).getAnnotatedMethods();
    }

    public Method findCounterPartMethod(FieldNameConverter converter, Method method) {
        return new CounterPartMethod(converter, method).getCounterPartMethod();
    }

    public static MethodFinder getInstance() {
        return (MethodFinder)INSTANCE_SUPPLIER.get();
    }

    private static final class CounterPartMethod {
        private final FieldNameConverter converter;
        private final Method method;

        CounterPartMethod(FieldNameConverter converter, Method method) {
            this.converter = converter;
            this.method = method;
        }

        Method getCounterPartMethod() {
            String name = this.converter.getName(this.method);
            Class<?> clazz = this.method.getDeclaringClass();
            for (Method other : clazz.getMethods()) {
                String otherName = this.converter.getName(other);
                if (other.equals(this.method) || otherName == null || !otherName.equals(name)) continue;
                return other;
            }
            return null;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CounterPartMethod that = (CounterPartMethod)o;
            if (this.converter != null ? !this.converter.equals(that.converter) : that.converter != null) {
                return false;
            }
            return !(this.method != null ? !this.method.equals(that.method) : that.method != null);
        }

        public int hashCode() {
            int result = this.converter != null ? this.converter.hashCode() : 0;
            result = 31 * result + (this.method != null ? this.method.hashCode() : 0);
            return result;
        }
    }

    private static final class AnnotatedMethods {
        private final Class<? extends Annotation> annotation;
        private final Class<?> type;

        AnnotatedMethods(Class<? extends Annotation> annotation, Class<?> type) {
            this.annotation = Objects.requireNonNull(annotation, "annotation can't be null");
            this.type = Objects.requireNonNull(type, "type can't be null");
        }

        Iterable<Method> getAnnotatedMethods() {
            ImmutableList.Builder annotatedMethods = ImmutableList.builder();
            for (Method m : this.type.getMethods()) {
                if (!m.isAnnotationPresent(this.annotation)) continue;
                annotatedMethods.add((Object)m);
            }
            return annotatedMethods.build();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AnnotatedMethods that = (AnnotatedMethods)o;
            if (this.annotation != null ? !this.annotation.equals(that.annotation) : that.annotation != null) {
                return false;
            }
            return !(this.type != null ? !this.type.equals(that.type) : that.type != null);
        }

        public int hashCode() {
            int result = this.annotation != null ? this.annotation.hashCode() : 0;
            result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
            return result;
        }
    }
}

