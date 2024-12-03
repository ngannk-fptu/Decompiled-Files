/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.privilegedactions;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

public final class GetDeclaredField
implements PrivilegedAction<Field> {
    private final Class<?> clazz;
    private final String fieldName;
    private final boolean makeAccessible;

    public static GetDeclaredField action(Class<?> clazz, String fieldName) {
        return new GetDeclaredField(clazz, fieldName, false);
    }

    public static GetDeclaredField andMakeAccessible(Class<?> clazz, String fieldName) {
        return new GetDeclaredField(clazz, fieldName, true);
    }

    private GetDeclaredField(Class<?> clazz, String fieldName, boolean makeAccessible) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        this.makeAccessible = makeAccessible;
    }

    @Override
    public Field run() {
        try {
            Field field = this.clazz.getDeclaredField(this.fieldName);
            if (this.makeAccessible) {
                field.setAccessible(true);
            }
            return field;
        }
        catch (NoSuchFieldException e) {
            return null;
        }
    }
}

