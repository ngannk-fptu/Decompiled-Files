/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;

public class RootClassFilter
implements ClassFilter,
Serializable {
    private Class<?> clazz;

    public RootClassFilter(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean matches(Class<?> candidate) {
        return this.clazz.isAssignableFrom(candidate);
    }
}

