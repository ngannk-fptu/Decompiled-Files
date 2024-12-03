/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import java.beans.Introspector;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ParameterTypes;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.util.FastArray;

public class MetaClassHelper {
    public static final Object[] EMPTY_ARRAY = new Object[0];
    public static final Class[] EMPTY_TYPE_ARRAY = new Class[0];
    public static final Object[] ARRAY_WITH_NULL = new Object[]{null};
    protected static final Logger LOG = Logger.getLogger(MetaClassHelper.class.getName());
    private static final int MAX_ARG_LEN = 12;
    private static final int OBJECT_SHIFT = 23;
    private static final int INTERFACE_SHIFT = 0;
    private static final int PRIMITIVE_SHIFT = 21;
    private static final int VARGS_SHIFT = 44;
    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Class[] PRIMITIVES = new Class[]{Boolean.TYPE, Boolean.class, Byte.TYPE, Byte.class, Short.TYPE, Short.class, Character.TYPE, Character.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, BigInteger.class, Float.TYPE, Float.class, Double.TYPE, Double.class, BigDecimal.class, Number.class, Object.class};
    private static final int[][] PRIMITIVE_DISTANCE_TABLE = new int[][]{{0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 2}, {1, 0, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 2}, {18, 19, 0, 1, 2, 3, 16, 17, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, {18, 19, 1, 0, 2, 3, 16, 17, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, {18, 19, 14, 15, 0, 1, 16, 17, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13}, {18, 19, 14, 15, 1, 0, 16, 17, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13}, {18, 19, 16, 17, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13}, {18, 19, 16, 17, 14, 15, 1, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13}, {18, 19, 14, 15, 12, 13, 16, 17, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, {18, 19, 14, 15, 12, 13, 16, 17, 1, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 1, 0, 2, 3, 4, 5, 6, 7, 8, 9}, {18, 19, 9, 10, 7, 8, 16, 17, 5, 6, 3, 4, 0, 14, 15, 12, 13, 11, 1, 2}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 8, 9, 7, 0, 1, 2, 3, 4, 5, 6}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 8, 9, 7, 1, 0, 2, 3, 4, 5, 6}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 8, 9, 7, 5, 6, 0, 1, 2, 3, 4}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 8, 9, 7, 5, 6, 1, 0, 2, 3, 4}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 8, 9, 7, 5, 6, 3, 4, 0, 1, 2}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 8, 9, 7, 5, 6, 3, 4, 2, 0, 1}, {18, 19, 14, 15, 12, 13, 16, 17, 10, 11, 8, 9, 7, 5, 6, 3, 4, 2, 1, 0}};

    public static boolean accessibleToConstructor(Class at, Constructor constructor) {
        boolean accessible = false;
        int modifiers = constructor.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            accessible = true;
        } else if (Modifier.isPrivate(modifiers)) {
            accessible = at.getName().equals(constructor.getName());
        } else if (Modifier.isProtected(modifiers)) {
            Boolean isAccessible = MetaClassHelper.checkCompatiblePackages(at, constructor);
            if (isAccessible != null) {
                accessible = isAccessible;
            } else {
                boolean flag = false;
                for (Class clazz = at; !flag && clazz != null; clazz = clazz.getSuperclass()) {
                    if (clazz.equals(constructor.getDeclaringClass())) {
                        flag = true;
                        break;
                    }
                    if (clazz.equals(Object.class)) break;
                }
                accessible = flag;
            }
        } else {
            Boolean isAccessible = MetaClassHelper.checkCompatiblePackages(at, constructor);
            if (isAccessible != null) {
                accessible = isAccessible;
            }
        }
        return accessible;
    }

    private static Boolean checkCompatiblePackages(Class at, Constructor constructor) {
        if (at.getPackage() == null && constructor.getDeclaringClass().getPackage() == null) {
            return Boolean.TRUE;
        }
        if (at.getPackage() == null && constructor.getDeclaringClass().getPackage() != null) {
            return Boolean.FALSE;
        }
        if (at.getPackage() != null && constructor.getDeclaringClass().getPackage() == null) {
            return Boolean.FALSE;
        }
        if (at.getPackage().equals(constructor.getDeclaringClass().getPackage())) {
            return Boolean.TRUE;
        }
        return null;
    }

    public static Object[] asWrapperArray(Object parameters, Class componentType) {
        Object[] ret;
        block9: {
            block15: {
                block14: {
                    block13: {
                        block12: {
                            block11: {
                                block10: {
                                    block8: {
                                        ret = null;
                                        if (componentType != Boolean.TYPE) break block8;
                                        boolean[] array = (boolean[])parameters;
                                        ret = new Object[array.length];
                                        for (int i = 0; i < array.length; ++i) {
                                            ret[i] = array[i];
                                        }
                                        break block9;
                                    }
                                    if (componentType != Character.TYPE) break block10;
                                    char[] array = (char[])parameters;
                                    ret = new Object[array.length];
                                    for (int i = 0; i < array.length; ++i) {
                                        ret[i] = Character.valueOf(array[i]);
                                    }
                                    break block9;
                                }
                                if (componentType != Byte.TYPE) break block11;
                                byte[] array = (byte[])parameters;
                                ret = new Object[array.length];
                                for (int i = 0; i < array.length; ++i) {
                                    ret[i] = array[i];
                                }
                                break block9;
                            }
                            if (componentType != Integer.TYPE) break block12;
                            int[] array = (int[])parameters;
                            ret = new Object[array.length];
                            for (int i = 0; i < array.length; ++i) {
                                ret[i] = array[i];
                            }
                            break block9;
                        }
                        if (componentType != Short.TYPE) break block13;
                        short[] array = (short[])parameters;
                        ret = new Object[array.length];
                        for (int i = 0; i < array.length; ++i) {
                            ret[i] = array[i];
                        }
                        break block9;
                    }
                    if (componentType != Long.TYPE) break block14;
                    long[] array = (long[])parameters;
                    ret = new Object[array.length];
                    for (int i = 0; i < array.length; ++i) {
                        ret[i] = array[i];
                    }
                    break block9;
                }
                if (componentType != Double.TYPE) break block15;
                double[] array = (double[])parameters;
                ret = new Object[array.length];
                for (int i = 0; i < array.length; ++i) {
                    ret[i] = array[i];
                }
                break block9;
            }
            if (componentType != Float.TYPE) break block9;
            float[] array = (float[])parameters;
            ret = new Object[array.length];
            for (int i = 0; i < array.length; ++i) {
                ret[i] = Float.valueOf(array[i]);
            }
        }
        return ret;
    }

    public static Object asPrimitiveArray(List list, Class parameterType) {
        Class<?> arrayType = parameterType.getComponentType();
        Object objArray = Array.newInstance(arrayType, list.size());
        for (int i = 0; i < list.size(); ++i) {
            Object obj = list.get(i);
            if (arrayType.isPrimitive()) {
                if (obj instanceof Integer) {
                    Array.setInt(objArray, i, (Integer)obj);
                    continue;
                }
                if (obj instanceof Double) {
                    Array.setDouble(objArray, i, (Double)obj);
                    continue;
                }
                if (obj instanceof Boolean) {
                    Array.setBoolean(objArray, i, (Boolean)obj);
                    continue;
                }
                if (obj instanceof Long) {
                    Array.setLong(objArray, i, (Long)obj);
                    continue;
                }
                if (obj instanceof Float) {
                    Array.setFloat(objArray, i, ((Float)obj).floatValue());
                    continue;
                }
                if (obj instanceof Character) {
                    Array.setChar(objArray, i, ((Character)obj).charValue());
                    continue;
                }
                if (obj instanceof Byte) {
                    Array.setByte(objArray, i, (Byte)obj);
                    continue;
                }
                if (!(obj instanceof Short)) continue;
                Array.setShort(objArray, i, (Short)obj);
                continue;
            }
            Array.set(objArray, i, obj);
        }
        return objArray;
    }

    private static int getPrimitiveIndex(Class c) {
        for (int i = 0; i < PRIMITIVES.length; i = (int)((byte)(i + 1))) {
            if (PRIMITIVES[i] != c) continue;
            return i;
        }
        return -1;
    }

    private static int getPrimitiveDistance(Class from, Class to) {
        int fromIndex = MetaClassHelper.getPrimitiveIndex(from);
        int toIndex = MetaClassHelper.getPrimitiveIndex(to);
        if (fromIndex == -1 || toIndex == -1) {
            return -1;
        }
        return PRIMITIVE_DISTANCE_TABLE[toIndex][fromIndex];
    }

    private static int getMaximumInterfaceDistance(Class c, Class interfaceClass) {
        if (c == null) {
            return -1;
        }
        if (c == interfaceClass) {
            return 0;
        }
        Class<?>[] interfaces = c.getInterfaces();
        int max = -1;
        for (Class<?> anInterface : interfaces) {
            int sub = MetaClassHelper.getMaximumInterfaceDistance(anInterface, interfaceClass);
            if (sub != -1) {
                ++sub;
            }
            max = Math.max(max, sub);
        }
        int superClassMax = MetaClassHelper.getMaximumInterfaceDistance(c.getSuperclass(), interfaceClass);
        if (superClassMax != -1) {
            ++superClassMax;
        }
        return Math.max(max, superClassMax);
    }

    private static long calculateParameterDistance(Class argument, CachedClass parameter) {
        int dist;
        if (parameter.getTheClass() == argument) {
            return 0L;
        }
        if (parameter.isInterface() && ((dist = MetaClassHelper.getMaximumInterfaceDistance(argument, parameter.getTheClass()) << 0) > -1 || argument == null || !Closure.class.isAssignableFrom(argument))) {
            return dist;
        }
        long objectDistance = 0L;
        if (argument != null) {
            long pd = MetaClassHelper.getPrimitiveDistance(parameter.getTheClass(), argument);
            if (pd != -1L) {
                return pd << 21;
            }
            objectDistance += (long)(PRIMITIVES.length + 1);
            if (argument.isArray() && !parameter.isArray) {
                objectDistance += 4L;
            }
            Class clazz = ReflectionCache.autoboxType(argument);
            while (clazz != null && clazz != parameter.getTheClass()) {
                if (clazz == GString.class && parameter.getTheClass() == String.class) {
                    objectDistance += 2L;
                    break;
                }
                clazz = clazz.getSuperclass();
                objectDistance += 3L;
            }
        } else {
            Class clazz = parameter.getTheClass();
            if (clazz.isPrimitive()) {
                objectDistance += 2L;
            } else {
                while (clazz != Object.class && clazz != null) {
                    clazz = clazz.getSuperclass();
                    objectDistance += 2L;
                }
            }
        }
        return objectDistance << 23;
    }

    public static long calculateParameterDistance(Class[] arguments, ParameterTypes pt) {
        CachedClass[] parameters = pt.getParameterTypes();
        if (parameters.length == 0) {
            return 0L;
        }
        long ret = 0L;
        int noVargsLength = parameters.length - 1;
        for (int i = 0; i < noVargsLength; ++i) {
            ret += MetaClassHelper.calculateParameterDistance(arguments[i], parameters[i]);
        }
        if (arguments.length == parameters.length) {
            CachedClass baseType = parameters[noVargsLength];
            if (!parameters[noVargsLength].isAssignableFrom(arguments[noVargsLength])) {
                baseType = ReflectionCache.getCachedClass(baseType.getTheClass().getComponentType());
                ret += 0x200000000000L;
            }
            ret += MetaClassHelper.calculateParameterDistance(arguments[noVargsLength], baseType);
        } else if (arguments.length > parameters.length) {
            ret += 2L + (long)arguments.length - (long)parameters.length << 44;
            CachedClass vargsType = ReflectionCache.getCachedClass(parameters[noVargsLength].getTheClass().getComponentType());
            for (int i = noVargsLength; i < arguments.length; ++i) {
                ret += MetaClassHelper.calculateParameterDistance(arguments[i], vargsType);
            }
        } else {
            ret += 0x100000000000L;
        }
        return ret;
    }

    public static String capitalize(String property) {
        String rest = property.substring(1);
        if (Character.isLowerCase(property.charAt(0)) && rest.length() > 0 && Character.isUpperCase(rest.charAt(0))) {
            return property;
        }
        return property.substring(0, 1).toUpperCase() + rest;
    }

    public static Object chooseEmptyMethodParams(FastArray methods) {
        Object vargsMethod = null;
        int len = methods.size();
        Object[] data = methods.getArray();
        for (int i = 0; i != len; ++i) {
            Object method = data[i];
            ParameterTypes pt = (ParameterTypes)method;
            CachedClass[] paramTypes = pt.getParameterTypes();
            int paramLength = paramTypes.length;
            if (paramLength == 0) {
                return method;
            }
            if (paramLength != 1 || !pt.isVargsMethod(EMPTY_ARRAY)) continue;
            vargsMethod = method;
        }
        return vargsMethod;
    }

    @Deprecated
    public static Object chooseMostGeneralMethodWith1NullParam(FastArray methods) {
        CachedClass closestClass = null;
        CachedClass closestVargsClass = null;
        Object answer = null;
        int closestDist = -1;
        int len = methods.size();
        for (int i = 0; i != len; ++i) {
            int newDist;
            Object[] data = methods.getArray();
            Object method = data[i];
            ParameterTypes pt = (ParameterTypes)method;
            CachedClass[] paramTypes = pt.getParameterTypes();
            int paramLength = paramTypes.length;
            if (paramLength == 0 || paramLength > 2) continue;
            CachedClass theType = paramTypes[0];
            if (theType.isPrimitive) continue;
            if (paramLength == 2) {
                if (!pt.isVargsMethod(ARRAY_WITH_NULL)) continue;
                if (closestClass == null) {
                    closestVargsClass = paramTypes[1];
                    closestClass = theType;
                    answer = method;
                    continue;
                }
                if (closestClass.getTheClass() == theType.getTheClass()) {
                    CachedClass newVargsClass;
                    if (closestVargsClass == null || !MetaClassHelper.isAssignableFrom((newVargsClass = paramTypes[1]).getTheClass(), closestVargsClass.getTheClass())) continue;
                    closestVargsClass = newVargsClass;
                    answer = method;
                    continue;
                }
                if (!MetaClassHelper.isAssignableFrom(theType.getTheClass(), closestClass.getTheClass())) continue;
                closestVargsClass = paramTypes[1];
                closestClass = theType;
                answer = method;
                continue;
            }
            if (closestClass == null || MetaClassHelper.isAssignableFrom(theType.getTheClass(), closestClass.getTheClass())) {
                closestVargsClass = null;
                closestClass = theType;
                answer = method;
                closestDist = -1;
                continue;
            }
            if (closestDist == -1) {
                closestDist = closestClass.getSuperClassDistance();
            }
            if ((newDist = theType.getSuperClassDistance()) >= closestDist) continue;
            closestDist = newDist;
            closestVargsClass = null;
            closestClass = theType;
            answer = method;
        }
        return answer;
    }

    public static boolean containsMatchingMethod(List list, MetaMethod method) {
        for (Object aList : list) {
            CachedClass[] params2;
            MetaMethod aMethod = (MetaMethod)aList;
            CachedClass[] params1 = aMethod.getParameterTypes();
            if (params1.length != (params2 = method.getParameterTypes()).length) continue;
            boolean matches = true;
            for (int i = 0; i < params1.length; ++i) {
                if (params1[i] == params2[i]) continue;
                matches = false;
                break;
            }
            if (!matches) continue;
            return true;
        }
        return false;
    }

    public static Class[] convertToTypeArray(Object[] args) {
        if (args == null) {
            return null;
        }
        int s = args.length;
        Class[] ans = new Class[s];
        for (int i = 0; i < s; ++i) {
            Object o = args[i];
            ans[i] = MetaClassHelper.getClassWithNullAndWrapper(o);
        }
        return ans;
    }

    public static Object makeCommonArray(Object[] arguments, int offset, Class fallback) {
        Class<?> baseClass = null;
        for (int i = offset; i < arguments.length; ++i) {
            if (arguments[i] == null) continue;
            Class<?> argClass = arguments[i].getClass();
            if (baseClass == null) {
                baseClass = argClass;
                continue;
            }
            while (baseClass != Object.class && !baseClass.isAssignableFrom(argClass)) {
                baseClass = baseClass.getSuperclass();
            }
        }
        if (baseClass == null) {
            baseClass = fallback;
        }
        if (baseClass == Object.class && fallback.isInterface()) {
            int tmpCount = 0;
            for (int i = offset; i < arguments.length; ++i) {
                if (arguments[i] == null) continue;
                HashSet intfs = new HashSet();
                for (Class<?> tmpClass = arguments[i].getClass(); tmpClass != Object.class; tmpClass = tmpClass.getSuperclass()) {
                    intfs.addAll(Arrays.asList(tmpClass.getInterfaces()));
                }
                if (!intfs.contains(fallback)) continue;
                ++tmpCount;
            }
            if (tmpCount == arguments.length - offset) {
                baseClass = fallback;
            }
        }
        Object result = MetaClassHelper.makeArray(null, baseClass, arguments.length - offset);
        System.arraycopy(arguments, offset, result, 0, arguments.length - offset);
        return result;
    }

    public static Object makeArray(Object obj, Class secondary, int length) {
        Class<?> baseClass = secondary;
        if (obj != null) {
            baseClass = obj.getClass();
        }
        return Array.newInstance(baseClass, length);
    }

    public static GroovyRuntimeException createExceptionText(String init, MetaMethod method, Object object, Object[] args, Throwable reason, boolean setReason) {
        return new GroovyRuntimeException(init + method + " on: " + object + " with arguments: " + InvokerHelper.toString(args) + " reason: " + reason, setReason ? reason : null);
    }

    protected static String getClassName(Object object) {
        if (object == null) {
            return null;
        }
        return object instanceof Class ? ((Class)object).getName() : object.getClass().getName();
    }

    public static Closure getMethodPointer(Object object, String methodName) {
        return new MethodClosure(object, methodName);
    }

    public static boolean isAssignableFrom(Class classToTransformTo, Class classToTransformFrom) {
        if (classToTransformTo == classToTransformFrom || classToTransformFrom == null || classToTransformTo == Object.class) {
            return true;
        }
        if ((classToTransformTo = ReflectionCache.autoboxType(classToTransformTo)) == (classToTransformFrom = ReflectionCache.autoboxType(classToTransformFrom))) {
            return true;
        }
        if (classToTransformTo == Integer.class ? classToTransformFrom == Short.class || classToTransformFrom == Byte.class || classToTransformFrom == BigInteger.class : (classToTransformTo == Double.class ? classToTransformFrom == Integer.class || classToTransformFrom == Long.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class || classToTransformFrom == Float.class || classToTransformFrom == BigDecimal.class || classToTransformFrom == BigInteger.class : (classToTransformTo == BigDecimal.class ? classToTransformFrom == Double.class || classToTransformFrom == Integer.class || classToTransformFrom == Long.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class || classToTransformFrom == Float.class || classToTransformFrom == BigInteger.class : (classToTransformTo == BigInteger.class ? classToTransformFrom == Integer.class || classToTransformFrom == Long.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class : (classToTransformTo == Long.class ? classToTransformFrom == Integer.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class : (classToTransformTo == Float.class ? classToTransformFrom == Integer.class || classToTransformFrom == Long.class || classToTransformFrom == Short.class || classToTransformFrom == Byte.class : (classToTransformTo == Short.class ? classToTransformFrom == Byte.class : classToTransformTo == String.class && GString.class.isAssignableFrom(classToTransformFrom)))))))) {
            return true;
        }
        return ReflectionCache.isAssignableFrom(classToTransformTo, classToTransformFrom);
    }

    public static boolean isGenericSetMethod(MetaMethod method) {
        return method.getName().equals("set") && method.getParameterTypes().length == 2;
    }

    protected static boolean isSuperclass(Class clazz, Class superclass) {
        while (clazz != null) {
            if (clazz == superclass) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    public static boolean parametersAreCompatible(Class[] arguments, Class[] parameters) {
        if (arguments.length != parameters.length) {
            return false;
        }
        for (int i = 0; i < arguments.length; ++i) {
            if (MetaClassHelper.isAssignableFrom(parameters[i], arguments[i])) continue;
            return false;
        }
        return true;
    }

    public static void logMethodCall(Object object, String methodName, Object[] arguments) {
        String className = MetaClassHelper.getClassName(object);
        String logname = "methodCalls." + className + "." + methodName;
        Logger objLog = Logger.getLogger(logname);
        if (!objLog.isLoggable(Level.FINER)) {
            return;
        }
        StringBuilder msg = new StringBuilder(methodName);
        msg.append("(");
        if (arguments != null) {
            int i = 0;
            while (i < arguments.length) {
                msg.append(MetaClassHelper.normalizedValue(arguments[i]));
                if (++i >= arguments.length) continue;
                msg.append(",");
            }
        }
        msg.append(")");
        objLog.logp(Level.FINER, className, msg.toString(), "called from MetaClass.invokeMethod");
    }

    protected static String normalizedValue(Object argument) {
        String value;
        try {
            value = argument.toString();
            if (value.length() > 12) {
                value = value.substring(0, 10) + "..";
            }
            if (argument instanceof String) {
                value = "'" + value + "'";
            }
        }
        catch (Exception e) {
            value = MetaClassHelper.shortName(argument);
        }
        return value;
    }

    protected static String shortName(Object object) {
        if (object == null || object.getClass() == null) {
            return "unknownClass";
        }
        String name = MetaClassHelper.getClassName(object);
        if (name == null) {
            return "unknownClassName";
        }
        int lastDotPos = name.lastIndexOf(46);
        if (lastDotPos < 0 || lastDotPos >= name.length() - 1) {
            return name;
        }
        return name.substring(lastDotPos + 1);
    }

    public static Class[] wrap(Class[] classes) {
        Class[] wrappedArguments = new Class[classes.length];
        for (int i = 0; i < wrappedArguments.length; ++i) {
            Class c = classes[i];
            if (c == null) continue;
            if (c.isPrimitive()) {
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
                }
            } else if (MetaClassHelper.isSuperclass(c, GString.class)) {
                c = String.class;
            }
            wrappedArguments[i] = c;
        }
        return wrappedArguments;
    }

    public static boolean sameClasses(Class[] params, Object[] arguments, boolean weakNullCheck) {
        if (params.length != arguments.length) {
            return false;
        }
        for (int i = params.length - 1; i >= 0; --i) {
            Object arg = arguments[i];
            Class compareClass = MetaClassHelper.getClassWithNullAndWrapper(arg);
            if (params[i] == compareClass) continue;
            return false;
        }
        return true;
    }

    private static Class getClassWithNullAndWrapper(Object arg) {
        if (arg == null) {
            return null;
        }
        if (arg instanceof Wrapper) {
            Wrapper w = (Wrapper)arg;
            return w.getType();
        }
        return arg.getClass();
    }

    public static boolean sameClasses(Class[] params, Object[] arguments) {
        if (params.length != arguments.length) {
            return false;
        }
        for (int i = params.length - 1; i >= 0; --i) {
            Object arg = arguments[i];
            if (!(arg == null ? params[i] != null : params[i] != MetaClassHelper.getClassWithNullAndWrapper(arg))) continue;
            return false;
        }
        return true;
    }

    public static boolean sameClasses(Class[] params) {
        return params.length == 0;
    }

    public static boolean sameClasses(Class[] params, Object arg1) {
        if (params.length != 1) {
            return false;
        }
        return params[0] == MetaClassHelper.getClassWithNullAndWrapper(arg1);
    }

    public static boolean sameClasses(Class[] params, Object arg1, Object arg2) {
        if (params.length != 2) {
            return false;
        }
        if (params[0] != MetaClassHelper.getClassWithNullAndWrapper(arg1)) {
            return false;
        }
        return params[1] == MetaClassHelper.getClassWithNullAndWrapper(arg2);
    }

    public static boolean sameClasses(Class[] params, Object arg1, Object arg2, Object arg3) {
        if (params.length != 3) {
            return false;
        }
        if (params[0] != MetaClassHelper.getClassWithNullAndWrapper(arg1)) {
            return false;
        }
        if (params[1] != MetaClassHelper.getClassWithNullAndWrapper(arg2)) {
            return false;
        }
        return params[2] == MetaClassHelper.getClassWithNullAndWrapper(arg3);
    }

    public static boolean sameClasses(Class[] params, Object arg1, Object arg2, Object arg3, Object arg4) {
        if (params.length != 4) {
            return false;
        }
        if (params[0] != MetaClassHelper.getClassWithNullAndWrapper(arg1)) {
            return false;
        }
        if (params[1] != MetaClassHelper.getClassWithNullAndWrapper(arg2)) {
            return false;
        }
        if (params[2] != MetaClassHelper.getClassWithNullAndWrapper(arg3)) {
            return false;
        }
        return params[3] == MetaClassHelper.getClassWithNullAndWrapper(arg4);
    }

    public static boolean sameClass(Class[] params, Object arg) {
        return params[0] == MetaClassHelper.getClassWithNullAndWrapper(arg);
    }

    public static Class[] castArgumentsToClassArray(Object[] argTypes) {
        if (argTypes == null) {
            return EMPTY_CLASS_ARRAY;
        }
        Class[] classes = new Class[argTypes.length];
        for (int i = 0; i < argTypes.length; ++i) {
            Object argType = argTypes[i];
            classes[i] = argType instanceof Class ? (Class)argType : (argType == null ? null : argType.getClass());
        }
        return classes;
    }

    public static void unwrap(Object[] arguments) {
        for (int i = 0; i != arguments.length; ++i) {
            if (!(arguments[i] instanceof Wrapper)) continue;
            arguments[i] = ((Wrapper)arguments[i]).unwrap();
        }
    }

    public static void doSetMetaClass(Object self, MetaClass mc) {
        if (self instanceof GroovyObject) {
            DefaultGroovyMethods.setMetaClass((GroovyObject)self, mc);
        } else {
            DefaultGroovyMethods.setMetaClass(self, mc);
        }
    }

    public static String convertPropertyName(String prop) {
        if (Character.isDigit(prop.charAt(0))) {
            return prop;
        }
        return Introspector.decapitalize(prop);
    }
}

