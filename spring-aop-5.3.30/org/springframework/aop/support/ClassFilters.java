/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.aop.support;

import java.io.Serializable;
import java.util.Arrays;
import org.springframework.aop.ClassFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class ClassFilters {
    public static ClassFilter union(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull((Object)cf1, (String)"First ClassFilter must not be null");
        Assert.notNull((Object)cf2, (String)"Second ClassFilter must not be null");
        return new UnionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter union(ClassFilter[] classFilters) {
        Assert.notEmpty((Object[])classFilters, (String)"ClassFilter array must not be empty");
        return new UnionClassFilter(classFilters);
    }

    public static ClassFilter intersection(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull((Object)cf1, (String)"First ClassFilter must not be null");
        Assert.notNull((Object)cf2, (String)"Second ClassFilter must not be null");
        return new IntersectionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter intersection(ClassFilter[] classFilters) {
        Assert.notEmpty((Object[])classFilters, (String)"ClassFilter array must not be empty");
        return new IntersectionClassFilter(classFilters);
    }

    private static class IntersectionClassFilter
    implements ClassFilter,
    Serializable {
        private final ClassFilter[] filters;

        IntersectionClassFilter(ClassFilter[] filters) {
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

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof IntersectionClassFilter && ObjectUtils.nullSafeEquals((Object)this.filters, (Object)((IntersectionClassFilter)other).filters);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode((Object[])this.filters);
        }

        public String toString() {
            return this.getClass().getName() + ": " + Arrays.toString(this.filters);
        }
    }

    private static class UnionClassFilter
    implements ClassFilter,
    Serializable {
        private final ClassFilter[] filters;

        UnionClassFilter(ClassFilter[] filters) {
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

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof UnionClassFilter && ObjectUtils.nullSafeEquals((Object)this.filters, (Object)((UnionClassFilter)other).filters);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode((Object[])this.filters);
        }

        public String toString() {
            return this.getClass().getName() + ": " + Arrays.toString(this.filters);
        }
    }
}

