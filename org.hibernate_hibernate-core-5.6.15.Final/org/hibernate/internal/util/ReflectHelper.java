/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Transient
 */
package org.hibernate.internal.util;

import java.beans.Introspector;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.persistence.Transient;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyMixedImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;

public final class ReflectHelper {
    private static final Pattern JAVA_CONSTANT_PATTERN;
    public static final Class[] NO_PARAM_SIGNATURE;
    public static final Object[] NO_PARAMS;
    public static final Class[] SINGLE_OBJECT_PARAM_SIGNATURE;
    private static final Method OBJECT_EQUALS;
    private static final Method OBJECT_HASHCODE;

    private ReflectHelper() {
    }

    public static Method extractEqualsMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getMethod("equals", SINGLE_OBJECT_PARAM_SIGNATURE);
    }

    public static Method extractHashCodeMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getMethod("hashCode", NO_PARAM_SIGNATURE);
    }

    public static boolean overridesEquals(Class clazz) {
        Method equals;
        try {
            equals = ReflectHelper.extractEqualsMethod(clazz);
        }
        catch (NoSuchMethodException nsme) {
            return false;
        }
        return !OBJECT_EQUALS.equals(equals);
    }

    public static boolean overridesHashCode(Class clazz) {
        Method hashCode;
        try {
            hashCode = ReflectHelper.extractHashCodeMethod(clazz);
        }
        catch (NoSuchMethodException nsme) {
            return false;
        }
        return !OBJECT_HASHCODE.equals(hashCode);
    }

    public static boolean implementsInterface(Class clazz, Class intf) {
        assert (intf.isInterface()) : "Interface to check was not an interface";
        return intf.isAssignableFrom(clazz);
    }

    public static Class classForName(String name, Class caller) throws ClassNotFoundException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return classLoader.loadClass(name);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return Class.forName(name, true, caller.getClassLoader());
    }

    @Deprecated
    public static Class classForName(String name) throws ClassNotFoundException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return classLoader.loadClass(name);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return Class.forName(name);
    }

    public static boolean isPublic(Class clazz, Member member) {
        return Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(clazz.getModifiers());
    }

    public static Class reflectedPropertyClass(String className, String name, ClassLoaderService classLoaderService) throws MappingException {
        try {
            Class clazz = classLoaderService.classForName(className);
            return ReflectHelper.getter(clazz, name).getReturnType();
        }
        catch (ClassLoadingException e) {
            throw new MappingException("class " + className + " not found while looking for property: " + name, (Throwable)((Object)e));
        }
    }

    public static Class reflectedPropertyClass(Class clazz, String name) throws MappingException {
        return ReflectHelper.getter(clazz, name).getReturnType();
    }

    private static Getter getter(Class clazz, String name) throws MappingException {
        return PropertyAccessStrategyMixedImpl.INSTANCE.buildPropertyAccess(clazz, name).getGetter();
    }

    public static Object getConstantValue(String name, SessionFactoryImplementor factory) {
        Class clazz;
        boolean conventionalJavaConstants = factory.getSessionFactoryOptions().isConventionalJavaConstants();
        try {
            if (conventionalJavaConstants && !JAVA_CONSTANT_PATTERN.matcher(name).find()) {
                return null;
            }
            ClassLoaderService classLoaderService = factory.getServiceRegistry().getService(ClassLoaderService.class);
            clazz = classLoaderService.classForName(StringHelper.qualifier(name));
        }
        catch (Throwable t) {
            return null;
        }
        try {
            return clazz.getField(StringHelper.unqualify(name)).get(null);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) throws PropertyNotFoundException {
        if (ReflectHelper.isAbstractClass(clazz)) {
            return null;
        }
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(NO_PARAM_SIGNATURE);
            ReflectHelper.ensureAccessibility(constructor);
            return constructor;
        }
        catch (NoSuchMethodException nme) {
            throw new PropertyNotFoundException("Object class [" + clazz.getName() + "] must declare a default (no-argument) constructor");
        }
    }

    public static boolean isAbstractClass(Class clazz) {
        int modifier = clazz.getModifiers();
        return Modifier.isAbstract(modifier) || Modifier.isInterface(modifier);
    }

    public static boolean isFinalClass(Class clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }

    public static Constructor getConstructor(Class clazz, Type[] types) throws PropertyNotFoundException {
        Constructor<?>[] candidates = clazz.getConstructors();
        Constructor<?> constructor = null;
        int numberOfMatchingConstructors = 0;
        for (Constructor<?> candidate : candidates) {
            Class<?>[] params = candidate.getParameterTypes();
            if (params.length != types.length) continue;
            boolean found = true;
            for (int j = 0; j < params.length; ++j) {
                boolean ok;
                boolean bl = ok = types[j] == null || params[j].isAssignableFrom(types[j].getReturnedClass()) || types[j] instanceof PrimitiveType && params[j] == ((PrimitiveType)((Object)types[j])).getPrimitiveClass();
                if (ok) continue;
                found = false;
                break;
            }
            if (!found) continue;
            ++numberOfMatchingConstructors;
            ReflectHelper.ensureAccessibility(candidate);
            constructor = candidate;
        }
        if (numberOfMatchingConstructors == 1) {
            return constructor;
        }
        throw new PropertyNotFoundException("no appropriate constructor in class: " + clazz.getName());
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class ... constructorArgs) {
        Constructor<T> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor(constructorArgs);
            try {
                ReflectHelper.ensureAccessibility(constructor);
            }
            catch (SecurityException e) {
                constructor = null;
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        return constructor;
    }

    public static Method getMethod(Class clazz, Method method) {
        try {
            return clazz.getMethod(method.getName(), method.getParameterTypes());
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Method getMethod(Class clazz, String methodName, Class ... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Field findField(Class containerClass, String propertyName) {
        if (containerClass == null) {
            throw new IllegalArgumentException("Class on which to find field [" + propertyName + "] cannot be null");
        }
        if (containerClass == Object.class) {
            throw new IllegalArgumentException("Illegal attempt to locate field [" + propertyName + "] on Object.class");
        }
        Field field = ReflectHelper.locateField(containerClass, propertyName);
        if (field == null) {
            throw new PropertyNotFoundException(String.format(Locale.ROOT, "Could not locate field name [%s] on class [%s]", propertyName, containerClass.getName()));
        }
        ReflectHelper.ensureAccessibility(field);
        return field;
    }

    public static void ensureAccessibility(AccessibleObject accessibleObject) {
        if (accessibleObject.isAccessible()) {
            return;
        }
        accessibleObject.setAccessible(true);
    }

    private static Field locateField(Class clazz, String propertyName) {
        if (clazz == null || Object.class.equals((Object)clazz)) {
            return null;
        }
        try {
            Field field = clazz.getDeclaredField(propertyName);
            if (!ReflectHelper.isStaticField(field)) {
                return field;
            }
            return ReflectHelper.locateField(clazz.getSuperclass(), propertyName);
        }
        catch (NoSuchFieldException nsfe) {
            return ReflectHelper.locateField(clazz.getSuperclass(), propertyName);
        }
    }

    public static boolean isStaticField(Field field) {
        return field != null && (field.getModifiers() & 8) == 8;
    }

    public static Method findGetterMethod(Class containerClass, String propertyName) {
        Method getter = null;
        for (Class checkClass = containerClass; getter == null && checkClass != null && !checkClass.equals(Object.class); checkClass = checkClass.getSuperclass()) {
            getter = ReflectHelper.getGetterOrNull(checkClass, propertyName);
            if (getter != null) continue;
            getter = ReflectHelper.getGetterOrNull(checkClass.getInterfaces(), propertyName);
        }
        if (getter == null) {
            throw new PropertyNotFoundException(String.format(Locale.ROOT, "Could not locate getter method for property [%s#%s]", containerClass.getName(), propertyName));
        }
        ReflectHelper.ensureAccessibility(getter);
        return getter;
    }

    private static Method getGetterOrNull(Class[] interfaces, String propertyName) {
        Method getter = null;
        for (int i = 0; getter == null && i < interfaces.length; ++i) {
            Class anInterface = interfaces[i];
            getter = ReflectHelper.getGetterOrNull(anInterface, propertyName);
            if (getter != null) continue;
            getter = ReflectHelper.getGetterOrNull(anInterface.getInterfaces(), propertyName);
        }
        return getter;
    }

    private static Method getGetterOrNull(Class containerClass, String propertyName) {
        for (Method method : containerClass.getDeclaredMethods()) {
            String decapitalizedStemName;
            String stemName;
            if (method.getParameterCount() != 0 || method.isBridge() || method.getAnnotation(Transient.class) != null || Modifier.isStatic(method.getModifiers())) continue;
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                stemName = methodName.substring(3);
                decapitalizedStemName = Introspector.decapitalize(stemName);
                if (stemName.equals(propertyName) || decapitalizedStemName.equals(propertyName)) {
                    ReflectHelper.verifyNoIsVariantExists(containerClass, propertyName, method, stemName);
                    return method;
                }
            }
            if (!methodName.startsWith("is")) continue;
            stemName = methodName.substring(2);
            decapitalizedStemName = Introspector.decapitalize(stemName);
            if (!stemName.equals(propertyName) && !decapitalizedStemName.equals(propertyName)) continue;
            ReflectHelper.verifyNoGetVariantExists(containerClass, propertyName, method, stemName);
            return method;
        }
        return null;
    }

    private static void verifyNoIsVariantExists(Class containerClass, String propertyName, Method getMethod, String stemName) {
        try {
            Method isMethod = containerClass.getDeclaredMethod("is" + stemName, new Class[0]);
            if (!Modifier.isStatic(isMethod.getModifiers()) && isMethod.getAnnotation(Transient.class) == null) {
                ReflectHelper.checkGetAndIsVariants(containerClass, propertyName, getMethod, isMethod);
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
    }

    private static void checkGetAndIsVariants(Class containerClass, String propertyName, Method getMethod, Method isMethod) {
        if (!isMethod.getReturnType().equals(getMethod.getReturnType())) {
            throw new MappingException(String.format(Locale.ROOT, "In trying to locate getter for property [%s], Class [%s] defined both a `get` [%s] and `is` [%s] variant", propertyName, containerClass.getName(), getMethod.toString(), isMethod.toString()));
        }
    }

    private static void verifyNoGetVariantExists(Class containerClass, String propertyName, Method isMethod, String stemName) {
        try {
            Method getMethod = containerClass.getDeclaredMethod("get" + stemName, new Class[0]);
            if (!Modifier.isStatic(getMethod.getModifiers()) && getMethod.getAnnotation(Transient.class) == null) {
                ReflectHelper.checkGetAndIsVariants(containerClass, propertyName, getMethod, isMethod);
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
    }

    public static Method getterMethodOrNull(Class containerJavaType, String propertyName) {
        try {
            return ReflectHelper.findGetterMethod(containerJavaType, propertyName);
        }
        catch (PropertyNotFoundException e) {
            return null;
        }
    }

    public static Method setterMethodOrNull(Class containerClass, String propertyName, Class propertyType) {
        Method setter = null;
        for (Class checkClass = containerClass; setter == null && checkClass != null && !checkClass.equals(Object.class); checkClass = checkClass.getSuperclass()) {
            setter = ReflectHelper.setterOrNull(checkClass, propertyName, propertyType);
            if (setter == null) {
                setter = ReflectHelper.setterOrNull(checkClass.getInterfaces(), propertyName, propertyType);
                continue;
            }
            ReflectHelper.ensureAccessibility(setter);
        }
        return setter;
    }

    public static Method findSetterMethod(Class containerClass, String propertyName, Class propertyType) {
        Method setter = ReflectHelper.setterMethodOrNull(containerClass, propertyName, propertyType);
        if (setter == null) {
            throw new PropertyNotFoundException(String.format(Locale.ROOT, "Could not locate setter method for property [%s#%s]", containerClass.getName(), propertyName));
        }
        return setter;
    }

    private static Method setterOrNull(Class[] interfaces, String propertyName, Class propertyType) {
        Method setter = null;
        for (int i = 0; setter == null && i < interfaces.length; ++i) {
            Class anInterface = interfaces[i];
            setter = ReflectHelper.setterOrNull(anInterface, propertyName, propertyType);
            if (setter != null) continue;
            setter = ReflectHelper.setterOrNull(anInterface.getInterfaces(), propertyName, propertyType);
        }
        return setter;
    }

    private static Method setterOrNull(Class theClass, String propertyName, Class propertyType) {
        Method potentialSetter = null;
        for (Method method : theClass.getDeclaredMethods()) {
            String testOldMethod;
            String testStdMethod;
            String methodName = method.getName();
            if (method.getParameterCount() != 1 || !methodName.startsWith("set") || !(testStdMethod = Introspector.decapitalize(testOldMethod = methodName.substring(3))).equals(propertyName) && !testOldMethod.equals(propertyName)) continue;
            potentialSetter = method;
            if (propertyType == null || method.getParameterTypes()[0].equals(propertyType)) break;
        }
        return potentialSetter;
    }

    public static Method findGetterMethodForFieldAccess(Field field, String propertyName) {
        for (Method method : field.getDeclaringClass().getDeclaredMethods()) {
            String decapitalizedStemName;
            String stemName;
            if (method.getParameterCount() != 0 || Modifier.isStatic(method.getModifiers()) || !method.getReturnType().isAssignableFrom(field.getType())) continue;
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                stemName = methodName.substring(3);
                decapitalizedStemName = Introspector.decapitalize(stemName);
                if (stemName.equals(propertyName) || decapitalizedStemName.equals(propertyName)) {
                    return method;
                }
            }
            if (!methodName.startsWith("is")) continue;
            stemName = methodName.substring(2);
            decapitalizedStemName = Introspector.decapitalize(stemName);
            if (!stemName.equals(propertyName) && !decapitalizedStemName.equals(propertyName)) continue;
            return method;
        }
        return null;
    }

    static {
        Method hash;
        Method eq;
        JAVA_CONSTANT_PATTERN = Pattern.compile("[a-z\\d]+\\.([A-Z]+[a-z\\d]+)+\\$?([A-Z]{1}[a-z\\d]+)*\\.[A-Z_\\$]+", 256);
        NO_PARAM_SIGNATURE = new Class[0];
        NO_PARAMS = new Object[0];
        SINGLE_OBJECT_PARAM_SIGNATURE = new Class[]{Object.class};
        try {
            eq = ReflectHelper.extractEqualsMethod(Object.class);
            hash = ReflectHelper.extractHashCodeMethod(Object.class);
        }
        catch (Exception e) {
            throw new AssertionFailure("Could not find Object.equals() or Object.hashCode()", e);
        }
        OBJECT_EQUALS = eq;
        OBJECT_HASHCODE = hash;
    }
}

