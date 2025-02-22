/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;
import org.springframework.util.Assert;

public class RootClassFilter
implements ClassFilter,
Serializable {
    private final Class<?> clazz;

    public RootClassFilter(Class<?> clazz) {
        Assert.notNull(clazz, (String)"Class must not be null");
        this.clazz = clazz;
    }

    @Override
    public boolean matches(Class<?> candidate) {
        return this.clazz.isAssignableFrom(candidate);
    }

    public boolean equals(Object other) {
        return this == other || other instanceof RootClassFilter && this.clazz.equals(((RootClassFilter)other).clazz);
    }

    public int hashCode() {
        return this.clazz.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.clazz.getName();
    }
}

