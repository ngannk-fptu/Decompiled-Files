/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.api.message;

import com.oracle.webservices.api.message.BasePropertySet;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
final class AccessorFactory {
    private AccessorFactory() {
    }

    static MethodHandles.Lookup createPrivateLookup(Class c, MethodHandles.Lookup lookup) {
        return null;
    }

    static BasePropertySet.Accessor createAccessor(Field f, String name, MethodHandles.Lookup lookup) throws IllegalAccessException {
        return new BasePropertySet.FieldAccessor(f, name);
    }

    static BasePropertySet.Accessor createAccessor(Method getter, Method setter, String value, MethodHandles.Lookup lookup) throws IllegalAccessException {
        return new BasePropertySet.MethodAccessor(getter, setter, value);
    }
}

