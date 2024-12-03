/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v7;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.util.ProxyGenerator;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.reflection.stdclasses.CachedSAMClass;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.transform.trait.Traits;
import org.codehaus.groovy.vmplugin.v7.TypeHelper;

public class TypeTransformers {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodHandle TO_STRING;
    private static final MethodHandle TO_BYTE;
    private static final MethodHandle TO_INT;
    private static final MethodHandle TO_LONG;
    private static final MethodHandle TO_SHORT;
    private static final MethodHandle TO_FLOAT;
    private static final MethodHandle TO_DOUBLE;
    private static final MethodHandle TO_BIG_INT;
    private static final MethodHandle AS_ARRAY;
    private static final MethodHandle TO_REFLECTIVE_PROXY;
    private static final MethodHandle TO_GENERATED_PROXY;
    private static final MethodHandle TO_SAMTRAIT_PROXY;
    private static final MethodHandle DOUBLE_TO_BIG_DEC;
    private static final MethodHandle DOUBLE_TO_BIG_DEC_WITH_CONVERSION;
    private static final MethodHandle LONG_TO_BIG_DEC;
    private static final MethodHandle BIG_INT_TO_BIG_DEC;

    protected static MethodHandle addTransformer(MethodHandle handle, int pos, Object arg, Class parameter) {
        MethodHandle transformer = null;
        if (arg instanceof GString) {
            transformer = TO_STRING;
        } else if (arg instanceof Closure) {
            transformer = TypeTransformers.createSAMTransform(arg, parameter);
        } else if (Number.class.isAssignableFrom(parameter)) {
            transformer = TypeTransformers.selectNumberTransformer(parameter, arg);
        } else if (parameter.isArray()) {
            transformer = MethodHandles.insertArguments(AS_ARRAY, 1, parameter);
        }
        if (transformer == null) {
            throw new GroovyBugError("Unknown transformation for argument " + arg + " at position " + pos + " with " + arg.getClass() + " for parameter of type " + parameter);
        }
        return TypeTransformers.applyUnsharpFilter(handle, pos, transformer);
    }

    private static MethodHandle createSAMTransform(Object arg, Class parameter) {
        Method method = CachedSAMClass.getSAMMethod(parameter);
        if (method == null) {
            return null;
        }
        if (parameter.isInterface()) {
            if (Traits.isTrait(parameter)) {
                MethodHandle ret = TO_SAMTRAIT_PROXY;
                ret = MethodHandles.insertArguments(ret, 2, ProxyGenerator.INSTANCE, Collections.singletonList(parameter));
                ret = MethodHandles.insertArguments(ret, 0, method.getName());
                return ret;
            }
            MethodHandle ret = TO_REFLECTIVE_PROXY;
            ret = MethodHandles.insertArguments(ret, 1, method.getName(), arg.getClass().getClassLoader(), new Class[]{parameter});
            return ret;
        }
        MethodHandle ret = TO_GENERATED_PROXY;
        ret = MethodHandles.insertArguments(ret, 2, ProxyGenerator.INSTANCE, parameter);
        ret = MethodHandles.insertArguments(ret, 0, method.getName());
        return ret;
    }

    public static MethodHandle applyUnsharpFilter(MethodHandle handle, int pos, MethodHandle transformer) {
        MethodType type = transformer.type();
        TypeDescriptor.OfField given = handle.type().parameterType(pos);
        if (type.returnType() != given || type.parameterType(0) != given) {
            transformer = transformer.asType(MethodType.methodType(given, type.parameterType(0)));
        }
        return MethodHandles.filterArguments(handle, pos, transformer);
    }

    private static MethodHandle selectNumberTransformer(Class param, Object arg) {
        if ((param = TypeHelper.getWrapperClass(param)) == Byte.class) {
            return TO_BYTE;
        }
        if (param == Character.class || param == Integer.class) {
            return TO_INT;
        }
        if (param == Long.class) {
            return TO_LONG;
        }
        if (param == Float.class) {
            return TO_FLOAT;
        }
        if (param == Double.class) {
            return TO_DOUBLE;
        }
        if (param == BigInteger.class) {
            return TO_BIG_INT;
        }
        if (param == BigDecimal.class) {
            if (arg instanceof Double) {
                return DOUBLE_TO_BIG_DEC;
            }
            if (arg instanceof Long) {
                return LONG_TO_BIG_DEC;
            }
            if (arg instanceof BigInteger) {
                return BIG_INT_TO_BIG_DEC;
            }
            return DOUBLE_TO_BIG_DEC_WITH_CONVERSION;
        }
        if (param == Short.class) {
            return TO_SHORT;
        }
        return null;
    }

    static {
        try {
            TO_STRING = LOOKUP.findVirtual(Object.class, "toString", MethodType.methodType(String.class));
            TO_BYTE = LOOKUP.findVirtual(Number.class, "byteValue", MethodType.methodType(Byte.TYPE));
            TO_SHORT = LOOKUP.findVirtual(Number.class, "shortValue", MethodType.methodType(Short.TYPE));
            TO_INT = LOOKUP.findVirtual(Number.class, "intValue", MethodType.methodType(Integer.TYPE));
            TO_LONG = LOOKUP.findVirtual(Number.class, "longValue", MethodType.methodType(Long.TYPE));
            TO_FLOAT = LOOKUP.findVirtual(Number.class, "floatValue", MethodType.methodType(Float.TYPE));
            TO_DOUBLE = LOOKUP.findVirtual(Number.class, "doubleValue", MethodType.methodType(Double.TYPE));
            DOUBLE_TO_BIG_DEC = LOOKUP.findConstructor(BigDecimal.class, MethodType.methodType(Void.TYPE, Double.TYPE));
            DOUBLE_TO_BIG_DEC_WITH_CONVERSION = MethodHandles.filterReturnValue(TO_DOUBLE, DOUBLE_TO_BIG_DEC);
            LONG_TO_BIG_DEC = LOOKUP.findConstructor(BigDecimal.class, MethodType.methodType(Void.TYPE, Long.TYPE));
            BIG_INT_TO_BIG_DEC = LOOKUP.findConstructor(BigDecimal.class, MethodType.methodType(Void.TYPE, BigInteger.class));
            MethodHandle tmp = LOOKUP.findConstructor(BigInteger.class, MethodType.methodType(Void.TYPE, String.class));
            TO_BIG_INT = MethodHandles.filterReturnValue(TO_STRING, tmp);
            AS_ARRAY = LOOKUP.findStatic(DefaultTypeTransformation.class, "asArray", MethodType.methodType(Object.class, Object.class, Class.class));
            MethodHandle newProxyInstance = LOOKUP.findStatic(Proxy.class, "newProxyInstance", MethodType.methodType(Object.class, ClassLoader.class, Class[].class, InvocationHandler.class));
            MethodHandle newConvertedClosure = LOOKUP.findConstructor(ConvertedClosure.class, MethodType.methodType(Void.TYPE, Closure.class, String.class));
            MethodType newOrder = newProxyInstance.type().dropParameterTypes(2, 3);
            newOrder = newOrder.insertParameterTypes(0, InvocationHandler.class, Closure.class, String.class);
            tmp = MethodHandles.permuteArguments(newProxyInstance, newOrder, 3, 4, 0);
            TO_REFLECTIVE_PROXY = MethodHandles.foldArguments(tmp, newConvertedClosure.asType(newConvertedClosure.type().changeReturnType(InvocationHandler.class)));
            MethodHandle map = LOOKUP.findStatic(Collections.class, "singletonMap", MethodType.methodType(Map.class, Object.class, Object.class));
            newProxyInstance = LOOKUP.findVirtual(ProxyGenerator.class, "instantiateAggregateFromBaseClass", MethodType.methodType(GroovyObject.class, Map.class, Class.class));
            newOrder = newProxyInstance.type().dropParameterTypes(1, 2);
            newOrder = newOrder.insertParameterTypes(0, Map.class, Object.class, Object.class);
            tmp = MethodHandles.permuteArguments(newProxyInstance, newOrder, 3, 0, 4);
            TO_GENERATED_PROXY = tmp = MethodHandles.foldArguments(tmp, map);
            map = LOOKUP.findStatic(Collections.class, "singletonMap", MethodType.methodType(Map.class, Object.class, Object.class));
            newProxyInstance = LOOKUP.findVirtual(ProxyGenerator.class, "instantiateAggregate", MethodType.methodType(GroovyObject.class, Map.class, List.class));
            newOrder = newProxyInstance.type().dropParameterTypes(1, 2);
            newOrder = newOrder.insertParameterTypes(0, Map.class, Object.class, Object.class);
            tmp = MethodHandles.permuteArguments(newProxyInstance, newOrder, 3, 0, 4);
            TO_SAMTRAIT_PROXY = tmp = MethodHandles.foldArguments(tmp, map);
        }
        catch (Exception e) {
            throw new GroovyBugError(e);
        }
    }
}

