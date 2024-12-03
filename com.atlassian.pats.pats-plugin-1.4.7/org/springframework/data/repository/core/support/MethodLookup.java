/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@FunctionalInterface
public interface MethodLookup {
    public List<MethodPredicate> getLookups();

    default public MethodLookup and(MethodLookup other) {
        Assert.notNull((Object)other, (String)"Other method lookup must not be null!");
        return () -> Stream.concat(this.getLookups().stream(), other.getLookups().stream()).collect(Collectors.toList());
    }

    public static final class InvokedMethod {
        private final Method method;

        private InvokedMethod(Method method) {
            this.method = method;
        }

        public static InvokedMethod of(Method method) {
            return new InvokedMethod(method);
        }

        public Class<?> getDeclaringClass() {
            return this.method.getDeclaringClass();
        }

        public String getName() {
            return this.method.getName();
        }

        public Class<?>[] getParameterTypes() {
            return this.method.getParameterTypes();
        }

        public int getParameterCount() {
            return this.method.getParameterCount();
        }

        public Method getMethod() {
            return this.method;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof InvokedMethod)) {
                return false;
            }
            InvokedMethod that = (InvokedMethod)o;
            return ObjectUtils.nullSafeEquals((Object)this.method, (Object)that.method);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode((Object)this.method);
        }

        public String toString() {
            return "MethodLookup.InvokedMethod(method=" + this.getMethod() + ")";
        }
    }

    @FunctionalInterface
    public static interface MethodPredicate
    extends BiPredicate<InvokedMethod, Method> {
        @Override
        public boolean test(InvokedMethod var1, Method var2);
    }
}

