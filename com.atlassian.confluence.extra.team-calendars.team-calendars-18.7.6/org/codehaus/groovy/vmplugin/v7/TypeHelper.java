/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v7;

import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;

public class TypeHelper {
    protected static Class getWrapperClass(Class c) {
        if (c == Integer.TYPE) {
            c = Integer.class;
        } else if (c == Byte.TYPE) {
            c = Byte.class;
        } else if (c == Long.TYPE) {
            c = Long.class;
        } else if (c == Double.TYPE) {
            c = Double.class;
        } else if (c == Float.TYPE) {
            c = Float.class;
        } else if (c == Boolean.TYPE) {
            c = Boolean.class;
        } else if (c == Character.TYPE) {
            c = Character.class;
        } else if (c == Short.TYPE) {
            c = Short.class;
        }
        return c;
    }

    protected static boolean argumentClassIsParameterClass(Class argumentClass, Class parameterClass) {
        if (argumentClass == parameterClass) {
            return true;
        }
        return TypeHelper.getWrapperClass(parameterClass) == argumentClass;
    }

    protected static MethodType replaceWithMoreSpecificType(Object[] args, MethodType callSiteType) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null || ((Class)callSiteType.parameterType(i)).isPrimitive()) continue;
            Class<?> argClass = args[i].getClass();
            callSiteType = callSiteType.changeParameterType(i, argClass);
        }
        return callSiteType;
    }

    protected static boolean isIntCategory(Class x) {
        return x == Integer.class || x == Integer.TYPE || x == Byte.class || x == Byte.TYPE || x == Short.class || x == Short.TYPE;
    }

    protected static boolean isLongCategory(Class x) {
        return x == Long.class || x == Long.TYPE || TypeHelper.isIntCategory(x);
    }

    private static boolean isBigIntCategory(Class x) {
        return x == BigInteger.class || TypeHelper.isLongCategory(x);
    }

    protected static boolean isBigDecCategory(Class x) {
        return x == BigDecimal.class || TypeHelper.isBigIntCategory(x);
    }

    protected static boolean isDoubleCategory(Class x) {
        return x == Float.class || x == Float.TYPE || x == Double.class || x == Double.TYPE || TypeHelper.isBigDecCategory(x);
    }
}

