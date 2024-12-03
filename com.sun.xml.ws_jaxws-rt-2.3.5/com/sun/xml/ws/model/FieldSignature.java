/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

final class FieldSignature {
    FieldSignature() {
    }

    static String vms(Type t) {
        if (t instanceof Class && ((Class)t).isPrimitive()) {
            Class c = (Class)t;
            if (c == Integer.TYPE) {
                return "I";
            }
            if (c == Void.TYPE) {
                return "V";
            }
            if (c == Boolean.TYPE) {
                return "Z";
            }
            if (c == Byte.TYPE) {
                return "B";
            }
            if (c == Character.TYPE) {
                return "C";
            }
            if (c == Short.TYPE) {
                return "S";
            }
            if (c == Double.TYPE) {
                return "D";
            }
            if (c == Float.TYPE) {
                return "F";
            }
            if (c == Long.TYPE) {
                return "J";
            }
        } else {
            if (t instanceof Class && ((Class)t).isArray()) {
                return "[" + FieldSignature.vms(((Class)t).getComponentType());
            }
            if (t instanceof Class || t instanceof ParameterizedType) {
                return "L" + FieldSignature.fqcn(t) + ";";
            }
            if (t instanceof GenericArrayType) {
                return "[" + FieldSignature.vms(((GenericArrayType)t).getGenericComponentType());
            }
            if (t instanceof TypeVariable) {
                return "Ljava/lang/Object;";
            }
            if (t instanceof WildcardType) {
                WildcardType w = (WildcardType)t;
                if (w.getLowerBounds().length > 0) {
                    return "-" + FieldSignature.vms(w.getLowerBounds()[0]);
                }
                if (w.getUpperBounds().length > 0) {
                    Type wt = w.getUpperBounds()[0];
                    if (wt.equals(Object.class)) {
                        return "*";
                    }
                    return "+" + FieldSignature.vms(wt);
                }
            }
        }
        throw new IllegalArgumentException("Illegal vms arg " + t);
    }

    private static String fqcn(Type t) {
        if (t instanceof Class) {
            Class c = (Class)t;
            if (c.getDeclaringClass() == null) {
                return c.getName().replace('.', '/');
            }
            return FieldSignature.fqcn(c.getDeclaringClass()) + "$" + c.getSimpleName();
        }
        if (t instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)t;
            if (p.getOwnerType() == null) {
                return FieldSignature.fqcn(p.getRawType()) + FieldSignature.args(p);
            }
            assert (p.getRawType() instanceof Class);
            return FieldSignature.fqcn(p.getOwnerType()) + "." + ((Class)p.getRawType()).getSimpleName() + FieldSignature.args(p);
        }
        throw new IllegalArgumentException("Illegal fqcn arg = " + t);
    }

    private static String args(ParameterizedType p) {
        StringBuilder sig = new StringBuilder("<");
        for (Type t : p.getActualTypeArguments()) {
            sig.append(FieldSignature.vms(t));
        }
        return sig.append(">").toString();
    }
}

