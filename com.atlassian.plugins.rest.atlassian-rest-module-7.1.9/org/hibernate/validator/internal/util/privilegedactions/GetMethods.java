/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;

public final class GetMethods
implements PrivilegedAction<Method[]> {
    private final Class<?> clazz;

    public static GetMethods action(Class<?> clazz) {
        return new GetMethods(clazz);
    }

    private GetMethods(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Method[] run() {
        return this.clazz.getMethods();
    }
}

