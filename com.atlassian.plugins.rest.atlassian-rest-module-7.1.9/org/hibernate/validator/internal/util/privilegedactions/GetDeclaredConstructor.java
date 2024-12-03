/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Constructor;
import java.security.PrivilegedAction;

public final class GetDeclaredConstructor<T>
implements PrivilegedAction<Constructor<T>> {
    private final Class<T> clazz;
    private final Class<?>[] params;

    public static <T> GetDeclaredConstructor<T> action(Class<T> clazz, Class<?> ... params) {
        return new GetDeclaredConstructor<T>(clazz, params);
    }

    private GetDeclaredConstructor(Class<T> clazz, Class<?> ... params) {
        this.clazz = clazz;
        this.params = params;
    }

    @Override
    public Constructor<T> run() {
        try {
            return this.clazz.getDeclaredConstructor(this.params);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
}

