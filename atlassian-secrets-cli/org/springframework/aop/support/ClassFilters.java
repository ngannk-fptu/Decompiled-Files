/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class ClassFilters {
    public static ClassFilter union(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull((Object)cf1, "First ClassFilter must not be null");
        Assert.notNull((Object)cf2, "Second ClassFilter must not be null");
        return new UnionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter union(ClassFilter[] classFilters) {
        Assert.notEmpty((Object[])classFilters, "ClassFilter array must not be empty");
        return new UnionClassFilter(classFilters);
    }

    public static ClassFilter intersection(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull((Object)cf1, "First ClassFilter must not be null");
        Assert.notNull((Object)cf2, "Second ClassFilter must not be null");
        return new IntersectionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter intersection(ClassFilter[] classFilters) {
        Assert.notEmpty((Object[])classFilters, "ClassFilter array must not be empty");
        return new IntersectionClassFilter(classFilters);
    }

    private static class IntersectionClassFilter
    implements ClassFilter,
    Serializable {
        private ClassFilter[] filters;

        public IntersectionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override
        public boolean matches(Class<?> clazz) {
            for (ClassFilter filter : this.filters) {
                if (filter.matches(clazz)) continue;
                return false;
            }
            return true;
        }

        public boolean equals(Object other) {
            return this == other || other instanceof IntersectionClassFilter && ObjectUtils.nullSafeEquals(this.filters, ((IntersectionClassFilter)other).filters);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.filters);
        }
    }

    private static class UnionClassFilter
    implements ClassFilter,
    Serializable {
        private ClassFilter[] filters;

        public UnionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override
        public boolean matches(Class<?> clazz) {
            for (ClassFilter filter : this.filters) {
                if (!filter.matches(clazz)) continue;
                return true;
            }
            return false;
        }

        public boolean equals(Object other) {
            return this == other || other instanceof UnionClassFilter && ObjectUtils.nullSafeEquals(this.filters, ((UnionClassFilter)other).filters);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.filters);
        }
    }
}

