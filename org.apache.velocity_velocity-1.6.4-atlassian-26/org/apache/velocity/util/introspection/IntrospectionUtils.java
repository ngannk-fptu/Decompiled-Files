/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

public class IntrospectionUtils {
    public static boolean isMethodInvocationConvertible(Class formal, Class actual, boolean possibleVarArg) {
        if (actual == null && !formal.isPrimitive()) {
            return true;
        }
        if (actual != null && formal.isAssignableFrom(actual)) {
            return true;
        }
        if (formal.isPrimitive()) {
            if (formal == Boolean.TYPE && actual == Boolean.class) {
                return true;
            }
            if (formal == Character.TYPE && actual == Character.class) {
                return true;
            }
            if (formal == Byte.TYPE && actual == Byte.class) {
                return true;
            }
            if (formal == Short.TYPE && (actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Integer.TYPE && (actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Long.TYPE && (actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Float.TYPE && (actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Double.TYPE && (actual == Double.class || actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
        }
        if (possibleVarArg && formal.isArray()) {
            if (actual.isArray()) {
                actual = actual.getComponentType();
            }
            return IntrospectionUtils.isMethodInvocationConvertible(formal.getComponentType(), actual, false);
        }
        return false;
    }

    public static boolean isStrictMethodInvocationConvertible(Class formal, Class actual, boolean possibleVarArg) {
        if (actual == null && !formal.isPrimitive()) {
            return true;
        }
        if (formal.isAssignableFrom(actual)) {
            return true;
        }
        if (formal.isPrimitive()) {
            if (formal == Short.TYPE && actual == Byte.TYPE) {
                return true;
            }
            if (formal == Integer.TYPE && (actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
            if (formal == Long.TYPE && (actual == Integer.TYPE || actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
            if (formal == Float.TYPE && (actual == Long.TYPE || actual == Integer.TYPE || actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
            if (formal == Double.TYPE && (actual == Float.TYPE || actual == Long.TYPE || actual == Integer.TYPE || actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
        }
        if (possibleVarArg && formal.isArray()) {
            if (actual.isArray()) {
                actual = actual.getComponentType();
            }
            return IntrospectionUtils.isStrictMethodInvocationConvertible(formal.getComponentType(), actual, false);
        }
        return false;
    }
}

