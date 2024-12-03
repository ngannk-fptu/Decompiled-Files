/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v7;

import groovy.lang.MetaMethod;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.vmplugin.v7.IndyInterface;
import org.codehaus.groovy.vmplugin.v7.Selector;
import org.codehaus.groovy.vmplugin.v7.TypeHelper;

public class IndyMath {
    private static final MethodType IV = MethodType.methodType(Void.TYPE, Integer.TYPE);
    private static final MethodType II = MethodType.methodType(Integer.TYPE, Integer.TYPE);
    private static final MethodType IIV = MethodType.methodType(Void.TYPE, Integer.TYPE, Integer.TYPE);
    private static final MethodType III = MethodType.methodType(Integer.TYPE, Integer.TYPE, Integer.TYPE);
    private static final MethodType LV = MethodType.methodType(Void.TYPE, Long.TYPE);
    private static final MethodType LL = MethodType.methodType(Long.TYPE, Long.TYPE);
    private static final MethodType LLV = MethodType.methodType(Void.TYPE, Long.TYPE, Long.TYPE);
    private static final MethodType LLL = MethodType.methodType(Long.TYPE, Long.TYPE, Long.TYPE);
    private static final MethodType DV = MethodType.methodType(Void.TYPE, Double.TYPE);
    private static final MethodType DD = MethodType.methodType(Double.TYPE, Double.TYPE);
    private static final MethodType DDV = MethodType.methodType(Void.TYPE, Double.TYPE, Double.TYPE);
    private static final MethodType DDD = MethodType.methodType(Double.TYPE, Double.TYPE, Double.TYPE);
    private static final MethodType GV = MethodType.methodType(Void.TYPE, BigDecimal.class);
    private static final MethodType GGV = MethodType.methodType(Void.TYPE, BigDecimal.class, BigDecimal.class);
    private static final MethodType OOV = MethodType.methodType(Void.TYPE, Object.class, Object.class);
    private static Map<String, Map<MethodType, MethodHandle>> methods = new HashMap<String, Map<MethodType, MethodHandle>>();

    private static void makeMapEntry(String method, MethodType[] keys, MethodType[] values) throws NoSuchMethodException, IllegalAccessException {
        HashMap<MethodType, MethodHandle> xMap = new HashMap<MethodType, MethodHandle>();
        methods.put(method, xMap);
        for (int i = 0; i < keys.length; ++i) {
            xMap.put(keys[i], IndyInterface.LOOKUP.findStatic(IndyMath.class, method, values[i]));
        }
    }

    public static boolean chooseMathMethod(Selector info, MetaMethod metaMethod) {
        Map<MethodType, MethodHandle> xmap = methods.get(info.name);
        if (xmap == null) {
            return false;
        }
        MethodType type = TypeHelper.replaceWithMoreSpecificType(info.args, info.targetType);
        MethodHandle handle = xmap.get(type = IndyMath.widenOperators(type));
        if (handle == null) {
            return false;
        }
        info.handle = handle;
        return true;
    }

    private static MethodType widenOperators(MethodType mt) {
        if (mt.parameterCount() == 2) {
            TypeDescriptor.OfField leftType = mt.parameterType(0);
            TypeDescriptor.OfField rightType = mt.parameterType(1);
            if (TypeHelper.isIntCategory((Class)leftType) && TypeHelper.isIntCategory((Class)rightType)) {
                return IIV;
            }
            if (TypeHelper.isLongCategory((Class)leftType) && TypeHelper.isLongCategory((Class)rightType)) {
                return LLV;
            }
            if (TypeHelper.isBigDecCategory((Class)leftType) && TypeHelper.isBigDecCategory((Class)rightType)) {
                return GGV;
            }
            if (TypeHelper.isDoubleCategory((Class)leftType) && TypeHelper.isDoubleCategory((Class)rightType)) {
                return DDV;
            }
            return OOV;
        }
        if (mt.parameterCount() == 1) {
            TypeDescriptor.OfField leftType = mt.parameterType(0);
            if (TypeHelper.isIntCategory((Class)leftType)) {
                return IV;
            }
            if (TypeHelper.isLongCategory((Class)leftType)) {
                return LV;
            }
            if (TypeHelper.isBigDecCategory((Class)leftType)) {
                return GV;
            }
            if (TypeHelper.isDoubleCategory((Class)leftType)) {
                return DV;
            }
        }
        return mt;
    }

    public static int plus(int a, int b) {
        return a + b;
    }

    public static int minus(int a, int b) {
        return a - b;
    }

    public static int multiply(int a, int b) {
        return a * b;
    }

    public static int mod(int a, int b) {
        return a % b;
    }

    public static int or(int a, int b) {
        return a | b;
    }

    public static int xor(int a, int b) {
        return a ^ b;
    }

    public static int and(int a, int b) {
        return a & b;
    }

    public static int leftShift(int a, int b) {
        return a << b;
    }

    public static int rightShift(int a, int b) {
        return a >> b;
    }

    public static long plus(long a, long b) {
        return a + b;
    }

    public static long minus(long a, long b) {
        return a - b;
    }

    public static long multiply(long a, long b) {
        return a * b;
    }

    public static long mod(long a, long b) {
        return a % b;
    }

    public static long or(long a, long b) {
        return a | b;
    }

    public static long xor(long a, long b) {
        return a ^ b;
    }

    public static long and(long a, long b) {
        return a & b;
    }

    public static long leftShift(long a, long b) {
        return a << (int)b;
    }

    public static long rightShift(long a, long b) {
        return a >> (int)b;
    }

    public static double plus(double a, double b) {
        return a + b;
    }

    public static double minus(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double div(double a, double b) {
        return a / b;
    }

    public static int next(int i) {
        return i + 1;
    }

    public static long next(long l) {
        return l + 1L;
    }

    public static double next(double d) {
        return d + 1.0;
    }

    public static int previous(int i) {
        return i - 1;
    }

    public static long previous(long l) {
        return l - 1L;
    }

    public static double previous(double d) {
        return d - 1.0;
    }

    static {
        try {
            MethodType[] keys = new MethodType[]{IIV, LLV, DDV};
            MethodType[] values = new MethodType[]{III, LLL, DDD};
            IndyMath.makeMapEntry("minus", keys, values);
            IndyMath.makeMapEntry("plus", keys, values);
            IndyMath.makeMapEntry("multiply", keys, values);
            keys = new MethodType[]{DDV};
            values = new MethodType[]{DDD};
            IndyMath.makeMapEntry("div", keys, values);
            keys = new MethodType[]{IV, LV, DV};
            values = new MethodType[]{II, LL, DD};
            IndyMath.makeMapEntry("next", keys, values);
            IndyMath.makeMapEntry("previous", keys, values);
            keys = new MethodType[]{IIV, LLV};
            values = new MethodType[]{III, LLL};
            IndyMath.makeMapEntry("mod", keys, values);
            IndyMath.makeMapEntry("or", keys, values);
            IndyMath.makeMapEntry("xor", keys, values);
            IndyMath.makeMapEntry("and", keys, values);
            IndyMath.makeMapEntry("leftShift", keys, values);
            IndyMath.makeMapEntry("rightShift", keys, values);
        }
        catch (Exception e) {
            throw new GroovyBugError(e);
        }
    }
}

