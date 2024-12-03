/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Constructor;
import java.security.PrivilegedAction;

public final class GetDeclaredConstructors
implements PrivilegedAction<Constructor<?>[]> {
    private final Class<?> clazz;

    public static GetDeclaredConstructors action(Class<?> clazz) {
        return new GetDeclaredConstructors(clazz);
    }

    private GetDeclaredConstructors(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Constructor<?>[] run() {
        return this.clazz.getDeclaredConstructors();
    }
}

