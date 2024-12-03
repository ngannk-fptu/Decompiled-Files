/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;

public final class GetMethodFromPropertyName
implements PrivilegedAction<Method> {
    private final Class<?> clazz;
    private final String property;

    public static GetMethodFromPropertyName action(Class<?> clazz, String property) {
        return new GetMethodFromPropertyName(clazz, property);
    }

    private GetMethodFromPropertyName(Class<?> clazz, String property) {
        this.clazz = clazz;
        this.property = property;
    }

    @Override
    public Method run() {
        try {
            char[] string = this.property.toCharArray();
            string[0] = Character.toUpperCase(string[0]);
            String fullMethodName = new String(string);
            try {
                return this.clazz.getMethod("get" + fullMethodName, new Class[0]);
            }
            catch (NoSuchMethodException e) {
                return this.clazz.getMethod("is" + fullMethodName, new Class[0]);
            }
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
}

