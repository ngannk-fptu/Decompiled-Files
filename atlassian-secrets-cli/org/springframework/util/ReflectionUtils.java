/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public abstract class ReflectionUtils {
    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";
    private static final Method[] NO_METHODS = new Method[0];
    private static final Field[] NO_FIELDS = new Field[0];
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap(256);
    public static final FieldFilter COPYABLE_FIELDS = field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
    public static final MethodFilter NON_BRIDGED_METHODS = method -> !method.isBridge();
    public static final MethodFilter USER_DECLARED_METHODS = method -> !method.isBridge() && !method.isSynthetic() && method.getDeclaringClass() != Object.class;

    @Nullable
    public static Field findField(Class<?> clazz, String name) {
        return ReflectionUtils.findField(clazz, name, null);
    }

    @Nullable
    public static Field findField(Class<?> clazz, @Nullable String name, @Nullable Class<?> type) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
        for (Class<?> searchType = clazz; Object.class != searchType && searchType != null; searchType = searchType.getSuperclass()) {
            Field[] fields;
            for (Field field : fields = ReflectionUtils.getDeclaredFields(searchType)) {
                if (name != null && !name.equals(field.getName()) || type != null && !type.equals(field.getType())) continue;
                return field;
            }
        }
        return null;
    }

    public static void setField(Field field, @Nullable Object target, @Nullable Object value) {
        try {
            field.set(target, value);
        }
        catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    @Nullable
    public static Object getField(Field field, @Nullable Object target) {
        try {
            return field.get(target);
        }
        catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String name) {
        return ReflectionUtils.findMethod(clazz, name, new Class[0]);
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String name, Class<?> ... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull((Object)name, "Method name must not be null");
        for (Class<?> searchType = clazz; searchType != null; searchType = searchType.getSuperclass()) {
            Method[] methods;
            for (Method method : methods = searchType.isInterface() ? searchType.getMethods() : ReflectionUtils.getDeclaredMethods(searchType)) {
                if (!name.equals(method.getName()) || paramTypes != null && !Arrays.equals(paramTypes, method.getParameterTypes())) continue;
                return method;
            }
        }
        return null;
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target) {
        return ReflectionUtils.invokeMethod(method, target, new Object[0]);
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target, Object ... args) {
        try {
            return method.invoke(target, args);
        }
        catch (Exception ex) {
            ReflectionUtils.handleReflectionException(ex);
            throw new IllegalStateException("Should never get here");
        }
    }

    @Nullable
    public static Object invokeJdbcMethod(Method method, @Nullable Object target) throws SQLException {
        return ReflectionUtils.invokeJdbcMethod(method, target, new Object[0]);
    }

    @Nullable
    public static Object invokeJdbcMethod(Method method, @Nullable Object target, Object ... args) throws SQLException {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
        }
        catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SQLException) {
                throw (SQLException)ex.getTargetException();
            }
            ReflectionUtils.handleInvocationTargetException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            ReflectionUtils.handleInvocationTargetException((InvocationTargetException)ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException)ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
    }

    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException)ex;
        }
        if (ex instanceof Error) {
            throw (Error)ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void rethrowException(Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception)ex;
        }
        if (ex instanceof Error) {
            throw (Error)ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static boolean declaresException(Method method, Class<?> exceptionType) {
        Class<?>[] declaredExceptions;
        Assert.notNull((Object)method, "Method must not be null");
        for (Class<?> declaredException : declaredExceptions = method.getExceptionTypes()) {
            if (!declaredException.isAssignableFrom(exceptionType)) continue;
            return true;
        }
        return false;
    }

    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    public static boolean isEqualsMethod(@Nullable Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return paramTypes.length == 1 && paramTypes[0] == Object.class;
    }

    public static boolean isHashCodeMethod(@Nullable Method method) {
        return method != null && method.getName().equals("hashCode") && method.getParameterCount() == 0;
    }

    public static boolean isToStringMethod(@Nullable Method method) {
        return method != null && method.getName().equals("toString") && method.getParameterCount() == 0;
    }

    public static boolean isObjectMethod(@Nullable Method method) {
        if (method == null) {
            return false;
        }
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static boolean isCglibRenamedMethod(Method renamedMethod) {
        String name = renamedMethod.getName();
        if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
            int i;
            for (i = name.length() - 1; i >= 0 && Character.isDigit(name.charAt(i)); --i) {
            }
            return i > CGLIB_RENAMED_METHOD_PREFIX.length() && i < name.length() - 1 && name.charAt(i) == '$';
        }
        return false;
    }

    public static void makeAccessible(Field field) {
        if (!(Modifier.isPublic(field.getModifiers()) && Modifier.isPublic(field.getDeclaringClass().getModifiers()) && !Modifier.isFinal(field.getModifiers()) || field.isAccessible())) {
            field.setAccessible(true);
        }
    }

    public static void makeAccessible(Method method) {
        if (!(Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(method.getDeclaringClass().getModifiers()) || method.isAccessible())) {
            method.setAccessible(true);
        }
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if (!(Modifier.isPublic(ctor.getModifiers()) && Modifier.isPublic(ctor.getDeclaringClass().getModifiers()) || ctor.isAccessible())) {
            ctor.setAccessible(true);
        }
    }

    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?> ... parameterTypes) throws NoSuchMethodException {
        Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
        ReflectionUtils.makeAccessible(ctor);
        return ctor;
    }

    public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
        Method[] methods;
        for (Method method : methods = ReflectionUtils.getDeclaredMethods(clazz)) {
            try {
                mc.doWith(method);
            }
            catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
        ReflectionUtils.doWithMethods(clazz, mc, null);
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) {
        Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) continue;
            try {
                mc.doWith(method);
            }
            catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
        if (clazz.getSuperclass() != null) {
            ReflectionUtils.doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            for (GenericDeclaration genericDeclaration : clazz.getInterfaces()) {
                ReflectionUtils.doWithMethods(genericDeclaration, mc, mf);
            }
        }
    }

    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        ArrayList methods = new ArrayList(32);
        ReflectionUtils.doWithMethods(leafClass, methods::add);
        return methods.toArray(new Method[0]);
    }

    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
        ArrayList methods = new ArrayList(32);
        ReflectionUtils.doWithMethods(leafClass, method -> {
            boolean knownSignature = false;
            Method methodBeingOverriddenWithCovariantReturnType = null;
            for (Method existingMethod : methods) {
                if (!method.getName().equals(existingMethod.getName()) || !Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) continue;
                if (existingMethod.getReturnType() != method.getReturnType() && existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                    methodBeingOverriddenWithCovariantReturnType = existingMethod;
                    break;
                }
                knownSignature = true;
                break;
            }
            if (methodBeingOverriddenWithCovariantReturnType != null) {
                methods.remove(methodBeingOverriddenWithCovariantReturnType);
            }
            if (!knownSignature && !ReflectionUtils.isCglibRenamedMethod(method)) {
                methods.add(method);
            }
        });
        return methods.toArray(new Method[0]);
    }

    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = ReflectionUtils.findConcreteMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    Iterator<Method> iterator = defaultMethods.iterator();
                    while (iterator.hasNext()) {
                        Method defaultMethod;
                        result[index] = defaultMethod = iterator.next();
                        ++index;
                    }
                } else {
                    result = declaredMethods;
                }
                declaredMethodsCache.put(clazz, result.length == 0 ? NO_METHODS : result);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    @Nullable
    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        LinkedList<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (Modifier.isAbstract(ifcMethod.getModifiers())) continue;
                if (result == null) {
                    result = new LinkedList<Method>();
                }
                result.add(ifcMethod);
            }
        }
        return result;
    }

    public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
        for (Field field : ReflectionUtils.getDeclaredFields(clazz)) {
            try {
                fc.doWith(field);
            }
            catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc) {
        ReflectionUtils.doWithFields(clazz, fc, null);
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc, @Nullable FieldFilter ff) {
        Class<?> targetClass = clazz;
        do {
            Field[] fields;
            for (Field field : fields = ReflectionUtils.getDeclaredFields(targetClass)) {
                if (ff != null && !ff.matches(field)) continue;
                try {
                    fc.doWith(field);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }
        } while ((targetClass = targetClass.getSuperclass()) != null && targetClass != Object.class);
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            try {
                result = clazz.getDeclaredFields();
                declaredFieldsCache.put(clazz, result.length == 0 ? NO_FIELDS : result);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    public static void shallowCopyFieldState(Object src, Object dest) {
        Assert.notNull(src, "Source for field copy cannot be null");
        Assert.notNull(dest, "Destination for field copy cannot be null");
        if (!src.getClass().isAssignableFrom(dest.getClass())) {
            throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        }
        ReflectionUtils.doWithFields(src.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            Object srcValue = field.get(src);
            field.set(dest, srcValue);
        }, COPYABLE_FIELDS);
    }

    public static void clearCache() {
        declaredMethodsCache.clear();
        declaredFieldsCache.clear();
    }

    @FunctionalInterface
    public static interface FieldFilter {
        public boolean matches(Field var1);
    }

    @FunctionalInterface
    public static interface FieldCallback {
        public void doWith(Field var1) throws IllegalArgumentException, IllegalAccessException;
    }

    @FunctionalInterface
    public static interface MethodFilter {
        public boolean matches(Method var1);
    }

    @FunctionalInterface
    public static interface MethodCallback {
        public void doWith(Method var1) throws IllegalArgumentException, IllegalAccessException;
    }
}

