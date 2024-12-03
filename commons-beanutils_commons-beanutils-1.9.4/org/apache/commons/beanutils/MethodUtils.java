/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.beanutils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MethodUtils {
    private static boolean loggedAccessibleWarning = false;
    private static boolean CACHE_METHODS = true;
    private static final Class<?>[] EMPTY_CLASS_PARAMETERS = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Map<MethodDescriptor, Reference<Method>> cache = Collections.synchronizedMap(new WeakHashMap());

    public static synchronized void setCacheMethods(boolean cacheMethods) {
        CACHE_METHODS = cacheMethods;
        if (!CACHE_METHODS) {
            MethodUtils.clearCache();
        }
    }

    public static synchronized int clearCache() {
        int size = cache.size();
        cache.clear();
        return size;
    }

    public static Object invokeMethod(Object object, String methodName, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args = MethodUtils.toArray(arg);
        return MethodUtils.invokeMethod(object, methodName, args);
    }

    public static Object invokeMethod(Object object, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        int arguments = args.length;
        Class[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return MethodUtils.invokeMethod(object, methodName, args, parameterTypes);
    }

    public static Object invokeMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method;
        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        if ((method = MethodUtils.getMatchingAccessibleMethod(object.getClass(), methodName, parameterTypes)) == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
        }
        return method.invoke(object, args);
    }

    public static Object invokeExactMethod(Object object, String methodName, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args = MethodUtils.toArray(arg);
        return MethodUtils.invokeExactMethod(object, methodName, args);
    }

    public static Object invokeExactMethod(Object object, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        int arguments = args.length;
        Class[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return MethodUtils.invokeExactMethod(object, methodName, args, parameterTypes);
    }

    public static Object invokeExactMethod(Object object, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method;
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if ((method = MethodUtils.getAccessibleMethod(object.getClass(), methodName, parameterTypes)) == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + object.getClass().getName());
        }
        return method.invoke(object, args);
    }

    public static Object invokeExactStaticMethod(Class<?> objectClass, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method;
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if ((method = MethodUtils.getAccessibleMethod(objectClass, methodName, parameterTypes)) == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + objectClass.getName());
        }
        return method.invoke(null, args);
    }

    public static Object invokeStaticMethod(Class<?> objectClass, String methodName, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args = MethodUtils.toArray(arg);
        return MethodUtils.invokeStaticMethod(objectClass, methodName, args);
    }

    public static Object invokeStaticMethod(Class<?> objectClass, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        int arguments = args.length;
        Class[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return MethodUtils.invokeStaticMethod(objectClass, methodName, args, parameterTypes);
    }

    public static Object invokeStaticMethod(Class<?> objectClass, String methodName, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method;
        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        if ((method = MethodUtils.getMatchingAccessibleMethod(objectClass, methodName, parameterTypes)) == null) {
            throw new NoSuchMethodException("No such accessible method: " + methodName + "() on class: " + objectClass.getName());
        }
        return method.invoke(null, args);
    }

    public static Object invokeExactStaticMethod(Class<?> objectClass, String methodName, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args = MethodUtils.toArray(arg);
        return MethodUtils.invokeExactStaticMethod(objectClass, methodName, args);
    }

    public static Object invokeExactStaticMethod(Class<?> objectClass, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }
        int arguments = args.length;
        Class[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return MethodUtils.invokeExactStaticMethod(objectClass, methodName, args, parameterTypes);
    }

    private static Object[] toArray(Object arg) {
        Object[] args = null;
        if (arg != null) {
            args = new Object[]{arg};
        }
        return args;
    }

    public static Method getAccessibleMethod(Class<?> clazz, String methodName, Class<?> parameterType) {
        Class[] parameterTypes = new Class[]{parameterType};
        return MethodUtils.getAccessibleMethod(clazz, methodName, parameterTypes);
    }

    public static Method getAccessibleMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        try {
            MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, true);
            Method method = MethodUtils.getCachedMethod(md);
            if (method != null) {
                return method;
            }
            method = MethodUtils.getAccessibleMethod(clazz, clazz.getMethod(methodName, parameterTypes));
            MethodUtils.cacheMethod(md, method);
            return method;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getAccessibleMethod(Method method) {
        if (method == null) {
            return null;
        }
        return MethodUtils.getAccessibleMethod(method.getDeclaringClass(), method);
    }

    public static Method getAccessibleMethod(Class<?> clazz, Method method) {
        Class<?>[] parameterTypes;
        if (method == null) {
            return null;
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        boolean sameClass = true;
        if (clazz == null) {
            clazz = method.getDeclaringClass();
        } else {
            sameClass = clazz.equals(method.getDeclaringClass());
            if (!method.getDeclaringClass().isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(clazz.getName() + " is not assignable from " + method.getDeclaringClass().getName());
            }
        }
        if (Modifier.isPublic(clazz.getModifiers())) {
            if (!sameClass && !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                MethodUtils.setMethodAccessible(method);
            }
            return method;
        }
        String methodName = method.getName();
        if ((method = MethodUtils.getAccessibleMethodFromInterfaceNest(clazz, methodName, parameterTypes = method.getParameterTypes())) == null) {
            method = MethodUtils.getAccessibleMethodFromSuperclass(clazz, methodName, parameterTypes);
        }
        return method;
    }

    private static Method getAccessibleMethodFromSuperclass(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        for (Class<?> parentClazz = clazz.getSuperclass(); parentClazz != null; parentClazz = parentClazz.getSuperclass()) {
            if (!Modifier.isPublic(parentClazz.getModifiers())) continue;
            try {
                return parentClazz.getMethod(methodName, parameterTypes);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
        }
        return null;
    }

    private static Method getAccessibleMethodFromInterfaceNest(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Method method = null;
        while (clazz != null) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                if (!Modifier.isPublic(interfaces[i].getModifiers())) continue;
                try {
                    method = interfaces[i].getDeclaredMethod(methodName, parameterTypes);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
                if (method != null) {
                    return method;
                }
                method = MethodUtils.getAccessibleMethodFromInterfaceNest(interfaces[i], methodName, parameterTypes);
                if (method == null) continue;
                return method;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Method getMatchingAccessibleMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Log log = LogFactory.getLog(MethodUtils.class);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Matching name=" + methodName + " on " + clazz));
        }
        MethodDescriptor md = new MethodDescriptor(clazz, methodName, parameterTypes, false);
        try {
            Method method = MethodUtils.getCachedMethod(md);
            if (method != null) {
                return method;
            }
            method = clazz.getMethod(methodName, parameterTypes);
            if (log.isTraceEnabled()) {
                log.trace((Object)("Found straight match: " + method));
                log.trace((Object)("isPublic:" + Modifier.isPublic(method.getModifiers())));
            }
            MethodUtils.setMethodAccessible(method);
            MethodUtils.cacheMethod(md, method);
            return method;
        }
        catch (NoSuchMethodException method) {
            int paramSize = parameterTypes.length;
            Method bestMatch = null;
            Method[] methods = clazz.getMethods();
            float bestMatchCost = Float.MAX_VALUE;
            float myCost = Float.MAX_VALUE;
            for (Method method2 : methods) {
                Class<?>[] methodsParams;
                int methodParamSize;
                if (!method2.getName().equals(methodName)) continue;
                if (log.isTraceEnabled()) {
                    log.trace((Object)"Found matching name:");
                    log.trace((Object)method2);
                }
                if ((methodParamSize = (methodsParams = method2.getParameterTypes()).length) != paramSize) continue;
                boolean match = true;
                for (int n = 0; n < methodParamSize; ++n) {
                    if (log.isTraceEnabled()) {
                        log.trace((Object)("Param=" + parameterTypes[n].getName()));
                        log.trace((Object)("Method=" + methodsParams[n].getName()));
                    }
                    if (MethodUtils.isAssignmentCompatible(methodsParams[n], parameterTypes[n])) continue;
                    if (log.isTraceEnabled()) {
                        log.trace((Object)(methodsParams[n] + " is not assignable from " + parameterTypes[n]));
                    }
                    match = false;
                    break;
                }
                if (!match) continue;
                Method method3 = MethodUtils.getAccessibleMethod(clazz, method2);
                if (method3 != null) {
                    if (log.isTraceEnabled()) {
                        log.trace((Object)(method3 + " accessible version of " + method2));
                    }
                    MethodUtils.setMethodAccessible(method3);
                    myCost = MethodUtils.getTotalTransformationCost(parameterTypes, method3.getParameterTypes());
                    if (myCost < bestMatchCost) {
                        bestMatch = method3;
                        bestMatchCost = myCost;
                    }
                }
                log.trace((Object)"Couldn't find accessible method.");
            }
            if (bestMatch != null) {
                MethodUtils.cacheMethod(md, bestMatch);
            } else {
                log.trace((Object)"No match found.");
            }
            return bestMatch;
        }
    }

    private static void setMethodAccessible(Method method) {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        }
        catch (SecurityException se) {
            Log log = LogFactory.getLog(MethodUtils.class);
            if (!loggedAccessibleWarning) {
                boolean vulnerableJVM = false;
                try {
                    String specVersion = System.getProperty("java.specification.version");
                    if (specVersion.charAt(0) == '1' && (specVersion.charAt(2) == '0' || specVersion.charAt(2) == '1' || specVersion.charAt(2) == '2' || specVersion.charAt(2) == '3')) {
                        vulnerableJVM = true;
                    }
                }
                catch (SecurityException e) {
                    vulnerableJVM = true;
                }
                if (vulnerableJVM) {
                    log.warn((Object)"Current Security Manager restricts use of workarounds for reflection bugs  in pre-1.4 JVMs.");
                }
                loggedAccessibleWarning = true;
            }
            log.debug((Object)"Cannot setAccessible on method. Therefore cannot use jvm access bug workaround.", (Throwable)se);
        }
    }

    private static float getTotalTransformationCost(Class<?>[] srcArgs, Class<?>[] destArgs) {
        float totalCost = 0.0f;
        for (int i = 0; i < srcArgs.length; ++i) {
            Class<?> srcClass = srcArgs[i];
            Class<?> destClass = destArgs[i];
            totalCost += MethodUtils.getObjectTransformationCost(srcClass, destClass);
        }
        return totalCost;
    }

    private static float getObjectTransformationCost(Class<?> srcClass, Class<?> destClass) {
        float cost = 0.0f;
        while (srcClass != null && !destClass.equals(srcClass)) {
            Class<?> destClassWrapperClazz;
            if (destClass.isPrimitive() && (destClassWrapperClazz = MethodUtils.getPrimitiveWrapper(destClass)) != null && destClassWrapperClazz.equals(srcClass)) {
                cost += 0.25f;
                break;
            }
            if (destClass.isInterface() && MethodUtils.isAssignmentCompatible(destClass, srcClass)) {
                cost += 0.25f;
                break;
            }
            cost += 1.0f;
            srcClass = srcClass.getSuperclass();
        }
        if (srcClass == null) {
            cost += 1.5f;
        }
        return cost;
    }

    public static final boolean isAssignmentCompatible(Class<?> parameterType, Class<?> parameterization) {
        Class<?> parameterWrapperClazz;
        if (parameterType.isAssignableFrom(parameterization)) {
            return true;
        }
        if (parameterType.isPrimitive() && (parameterWrapperClazz = MethodUtils.getPrimitiveWrapper(parameterType)) != null) {
            return parameterWrapperClazz.equals(parameterization);
        }
        return false;
    }

    public static Class<?> getPrimitiveWrapper(Class<?> primitiveType) {
        if (Boolean.TYPE.equals(primitiveType)) {
            return Boolean.class;
        }
        if (Float.TYPE.equals(primitiveType)) {
            return Float.class;
        }
        if (Long.TYPE.equals(primitiveType)) {
            return Long.class;
        }
        if (Integer.TYPE.equals(primitiveType)) {
            return Integer.class;
        }
        if (Short.TYPE.equals(primitiveType)) {
            return Short.class;
        }
        if (Byte.TYPE.equals(primitiveType)) {
            return Byte.class;
        }
        if (Double.TYPE.equals(primitiveType)) {
            return Double.class;
        }
        if (Character.TYPE.equals(primitiveType)) {
            return Character.class;
        }
        return null;
    }

    public static Class<?> getPrimitiveType(Class<?> wrapperType) {
        if (Boolean.class.equals(wrapperType)) {
            return Boolean.TYPE;
        }
        if (Float.class.equals(wrapperType)) {
            return Float.TYPE;
        }
        if (Long.class.equals(wrapperType)) {
            return Long.TYPE;
        }
        if (Integer.class.equals(wrapperType)) {
            return Integer.TYPE;
        }
        if (Short.class.equals(wrapperType)) {
            return Short.TYPE;
        }
        if (Byte.class.equals(wrapperType)) {
            return Byte.TYPE;
        }
        if (Double.class.equals(wrapperType)) {
            return Double.TYPE;
        }
        if (Character.class.equals(wrapperType)) {
            return Character.TYPE;
        }
        Log log = LogFactory.getLog(MethodUtils.class);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Not a known primitive wrapper class: " + wrapperType));
        }
        return null;
    }

    public static Class<?> toNonPrimitiveClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            Class<?> primitiveClazz = MethodUtils.getPrimitiveWrapper(clazz);
            if (primitiveClazz != null) {
                return primitiveClazz;
            }
            return clazz;
        }
        return clazz;
    }

    private static Method getCachedMethod(MethodDescriptor md) {
        Reference<Method> methodRef;
        if (CACHE_METHODS && (methodRef = cache.get(md)) != null) {
            return methodRef.get();
        }
        return null;
    }

    private static void cacheMethod(MethodDescriptor md, Method method) {
        if (CACHE_METHODS && method != null) {
            cache.put(md, new WeakReference<Method>(method));
        }
    }

    private static class MethodDescriptor {
        private final Class<?> cls;
        private final String methodName;
        private final Class<?>[] paramTypes;
        private final boolean exact;
        private final int hashCode;

        public MethodDescriptor(Class<?> cls, String methodName, Class<?>[] paramTypes, boolean exact) {
            if (cls == null) {
                throw new IllegalArgumentException("Class cannot be null");
            }
            if (methodName == null) {
                throw new IllegalArgumentException("Method Name cannot be null");
            }
            if (paramTypes == null) {
                paramTypes = EMPTY_CLASS_PARAMETERS;
            }
            this.cls = cls;
            this.methodName = methodName;
            this.paramTypes = paramTypes;
            this.exact = exact;
            this.hashCode = methodName.length();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MethodDescriptor)) {
                return false;
            }
            MethodDescriptor md = (MethodDescriptor)obj;
            return this.exact == md.exact && this.methodName.equals(md.methodName) && this.cls.equals(md.cls) && Arrays.equals(this.paramTypes, md.paramTypes);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}

