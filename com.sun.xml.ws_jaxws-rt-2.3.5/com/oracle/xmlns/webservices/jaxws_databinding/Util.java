/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.sun.xml.ws.model.RuntimeModelerException;

class Util {
    Util() {
    }

    static String nullSafe(String value) {
        return value == null ? "" : value;
    }

    static <T> T nullSafe(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    static <T extends Enum> T nullSafe(Enum value, T defaultValue) {
        return (T)(value == null ? defaultValue : Enum.valueOf(defaultValue.getClass(), value.toString()));
    }

    public static Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeModelerException("runtime.modeler.external.metadata.generic", e);
        }
    }
}

