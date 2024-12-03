/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;

final class TrueClassFilter
implements ClassFilter,
Serializable {
    public static final TrueClassFilter INSTANCE = new TrueClassFilter();

    private TrueClassFilter() {
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return true;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public String toString() {
        return "ClassFilter.TRUE";
    }
}

