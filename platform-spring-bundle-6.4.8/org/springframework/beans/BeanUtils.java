/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.jvm.JvmClassMappingKt
 *  kotlin.reflect.KCallable
 *  kotlin.reflect.KClass
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.KParameter
 *  kotlin.reflect.full.KClasses
 *  kotlin.reflect.jvm.KCallablesJvm
 *  kotlin.reflect.jvm.ReflectJvmMapping
 */
package org.springframework.beans;

import java.beans.ConstructorProperties;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KCallable;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.KCallablesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.GenericTypeAwarePropertyDescriptor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public abstract class BeanUtils {
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final Set<Class<?>> unknownEditorTypes = Collections.newSetFromMap(new ConcurrentReferenceHashMap(64));
    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES;

    @Deprecated
    public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return clazz.newInstance();
        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", (Throwable)ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", (Throwable)ex);
        }
    }

    public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return BeanUtils.instantiateClass(clazz.getDeclaredConstructor(new Class[0]), new Object[0]);
        }
        catch (NoSuchMethodException ex) {
            Constructor<T> ctor = BeanUtils.findPrimaryConstructor(clazz);
            if (ctor != null) {
                return BeanUtils.instantiateClass(ctor, new Object[0]);
            }
            throw new BeanInstantiationException(clazz, "No default constructor found", (Throwable)ex);
        }
        catch (LinkageError err) {
            throw new BeanInstantiationException(clazz, "Unresolvable class definition", (Throwable)err);
        }
    }

    public static <T> T instantiateClass(Class<?> clazz, Class<T> assignableTo) throws BeanInstantiationException {
        Assert.isAssignable(assignableTo, clazz);
        return (T)BeanUtils.instantiateClass(clazz);
    }

    public static <T> T instantiateClass(Constructor<T> ctor, Object ... args) throws BeanInstantiationException {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            ReflectionUtils.makeAccessible(ctor);
            if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(ctor.getDeclaringClass())) {
                return KotlinDelegate.instantiateClass(ctor, args);
            }
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Assert.isTrue(args.length <= parameterTypes.length, "Can't specify more arguments than constructor parameters");
            Object[] argsWithDefaultValues = new Object[args.length];
            for (int i2 = 0; i2 < args.length; ++i2) {
                Class<?> parameterType;
                argsWithDefaultValues[i2] = args[i2] == null ? ((parameterType = parameterTypes[i2]).isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null) : args[i2];
            }
            return ctor.newInstance(argsWithDefaultValues);
        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctor, "Is it an abstract class?", (Throwable)ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(ctor, "Is the constructor accessible?", (Throwable)ex);
        }
        catch (IllegalArgumentException ex) {
            throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", (Throwable)ex);
        }
        catch (InvocationTargetException ex) {
            throw new BeanInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
        }
    }

    public static <T> Constructor<T> getResolvableConstructor(Class<T> clazz) {
        Constructor<T> ctor = BeanUtils.findPrimaryConstructor(clazz);
        if (ctor != null) {
            return ctor;
        }
        Constructor<?>[] ctors = clazz.getConstructors();
        if (ctors.length == 1) {
            return ctors[0];
        }
        if (ctors.length == 0 && (ctors = clazz.getDeclaredConstructors()).length == 1) {
            return ctors[0];
        }
        try {
            return clazz.getDeclaredConstructor(new Class[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new IllegalStateException("No primary or single unique constructor found for " + clazz);
        }
    }

    @Nullable
    public static <T> Constructor<T> findPrimaryConstructor(Class<T> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(clazz)) {
            return KotlinDelegate.findPrimaryConstructor(clazz);
        }
        return null;
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String methodName, Class<?> ... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return BeanUtils.findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    @Nullable
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?> ... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return BeanUtils.findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    @Nullable
    public static Method findMethodWithMinimalParameters(Class<?> clazz, String methodName) throws IllegalArgumentException {
        Method targetMethod = BeanUtils.findMethodWithMinimalParameters(clazz.getMethods(), methodName);
        if (targetMethod == null) {
            targetMethod = BeanUtils.findDeclaredMethodWithMinimalParameters(clazz, methodName);
        }
        return targetMethod;
    }

    @Nullable
    public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName) throws IllegalArgumentException {
        Method targetMethod = BeanUtils.findMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null && clazz.getSuperclass() != null) {
            targetMethod = BeanUtils.findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
        }
        return targetMethod;
    }

    @Nullable
    public static Method findMethodWithMinimalParameters(Method[] methods, String methodName) throws IllegalArgumentException {
        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (Method method : methods) {
            if (!method.getName().equals(methodName)) continue;
            int numParams = method.getParameterCount();
            if (targetMethod == null || numParams < targetMethod.getParameterCount()) {
                targetMethod = method;
                numMethodsFoundWithCurrentMinimumArgs = 1;
                continue;
            }
            if (method.isBridge() || targetMethod.getParameterCount() != numParams) continue;
            if (targetMethod.isBridge()) {
                targetMethod = method;
                continue;
            }
            ++numMethodsFoundWithCurrentMinimumArgs;
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName + "' to a unique method. Attempted to resolve to overloaded method with the least number of parameters but there were " + numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }

    @Nullable
    public static Method resolveSignature(String signature, Class<?> clazz) {
        Assert.hasText(signature, "'signature' must not be empty");
        Assert.notNull(clazz, "Class must not be null");
        int startParen = signature.indexOf(40);
        int endParen = signature.indexOf(41);
        if (startParen > -1 && endParen == -1) {
            throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected closing ')' for args list");
        }
        if (startParen == -1 && endParen > -1) {
            throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected opening '(' for args list");
        }
        if (startParen == -1) {
            return BeanUtils.findMethodWithMinimalParameters(clazz, signature);
        }
        String methodName = signature.substring(0, startParen);
        String[] parameterTypeNames = StringUtils.commaDelimitedListToStringArray(signature.substring(startParen + 1, endParen));
        Class[] parameterTypes = new Class[parameterTypeNames.length];
        for (int i2 = 0; i2 < parameterTypeNames.length; ++i2) {
            String parameterTypeName = parameterTypeNames[i2].trim();
            try {
                parameterTypes[i2] = ClassUtils.forName(parameterTypeName, clazz.getClassLoader());
                continue;
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Invalid method signature: unable to resolve type [" + parameterTypeName + "] for argument " + i2 + ". Root cause: " + ex);
            }
        }
        return BeanUtils.findMethod(clazz, methodName, parameterTypes);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeansException {
        return CachedIntrospectionResults.forClass(clazz).getPropertyDescriptors();
    }

    @Nullable
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) throws BeansException {
        return CachedIntrospectionResults.forClass(clazz).getPropertyDescriptor(propertyName);
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(Method method) throws BeansException {
        return BeanUtils.findPropertyForMethod(method, method.getDeclaringClass());
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(Method method, Class<?> clazz) throws BeansException {
        PropertyDescriptor[] pds;
        Assert.notNull((Object)method, "Method must not be null");
        for (PropertyDescriptor pd : pds = BeanUtils.getPropertyDescriptors(clazz)) {
            if (!method.equals(pd.getReadMethod()) && !method.equals(pd.getWriteMethod())) continue;
            return pd;
        }
        return null;
    }

    @Nullable
    public static PropertyEditor findEditorByConvention(@Nullable Class<?> targetType) {
        if (targetType == null || targetType.isArray() || unknownEditorTypes.contains(targetType)) {
            return null;
        }
        ClassLoader cl = targetType.getClassLoader();
        if (cl == null) {
            try {
                cl = ClassLoader.getSystemClassLoader();
                if (cl == null) {
                    return null;
                }
            }
            catch (Throwable ex) {
                return null;
            }
        }
        String targetTypeName = targetType.getName();
        String editorName = targetTypeName + "Editor";
        try {
            Class<?> editorClass = cl.loadClass(editorName);
            if (editorClass != null) {
                if (!PropertyEditor.class.isAssignableFrom(editorClass)) {
                    unknownEditorTypes.add(targetType);
                    return null;
                }
                return (PropertyEditor)BeanUtils.instantiateClass(editorClass);
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        unknownEditorTypes.add(targetType);
        return null;
    }

    public static Class<?> findPropertyType(String propertyName, Class<?> ... beanClasses) {
        if (beanClasses != null) {
            for (Class<?> beanClass : beanClasses) {
                PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClass, propertyName);
                if (pd == null) continue;
                return pd.getPropertyType();
            }
        }
        return Object.class;
    }

    public static MethodParameter getWriteMethodParameter(PropertyDescriptor pd) {
        if (pd instanceof GenericTypeAwarePropertyDescriptor) {
            return new MethodParameter(((GenericTypeAwarePropertyDescriptor)pd).getWriteMethodParameter());
        }
        Method writeMethod = pd.getWriteMethod();
        Assert.state(writeMethod != null, "No write method available");
        return new MethodParameter(writeMethod, 0);
    }

    public static String[] getParameterNames(Constructor<?> ctor) {
        ConstructorProperties cp = ctor.getAnnotation(ConstructorProperties.class);
        String[] paramNames = cp != null ? cp.value() : parameterNameDiscoverer.getParameterNames(ctor);
        Assert.state(paramNames != null, () -> "Cannot resolve parameter names for constructor " + ctor);
        Assert.state(paramNames.length == ctor.getParameterCount(), () -> "Invalid number of parameter names: " + paramNames.length + " for constructor " + ctor);
        return paramNames;
    }

    public static boolean isSimpleProperty(Class<?> type) {
        Assert.notNull(type, "'type' must not be null");
        return BeanUtils.isSimpleValueType(type) || type.isArray() && BeanUtils.isSimpleValueType(type.getComponentType());
    }

    public static boolean isSimpleValueType(Class<?> type) {
        return Void.class != type && Void.TYPE != type && (ClassUtils.isPrimitiveOrWrapper(type) || Enum.class.isAssignableFrom(type) || CharSequence.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type) || URI.class == type || URL.class == type || Locale.class == type || Class.class == type);
    }

    public static void copyProperties(Object source, Object target) throws BeansException {
        BeanUtils.copyProperties(source, target, null, (String[])null);
    }

    public static void copyProperties(Object source, Object target, Class<?> editable) throws BeansException {
        BeanUtils.copyProperties(source, target, editable, (String[])null);
    }

    public static void copyProperties(Object source, Object target, String ... ignoreProperties) throws BeansException {
        BeanUtils.copyProperties(source, target, null, ignoreProperties);
    }

    private static void copyProperties(Object source, Object target, @Nullable Class<?> editable, String ... ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        HashSet<String> ignoredProps = ignoreProperties != null ? new HashSet<String>(Arrays.asList(ignoreProperties)) : null;
        CachedIntrospectionResults sourceResults = actualEditable != source.getClass() ? CachedIntrospectionResults.forClass(source.getClass()) : null;
        for (PropertyDescriptor targetPd : targetPds) {
            Method readMethod;
            PropertyDescriptor sourcePd;
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod == null || ignoredProps != null && ignoredProps.contains(targetPd.getName())) continue;
            PropertyDescriptor propertyDescriptor = sourcePd = sourceResults != null ? sourceResults.getPropertyDescriptor(targetPd.getName()) : targetPd;
            if (sourcePd == null || (readMethod = sourcePd.getReadMethod()) == null || !BeanUtils.isAssignable(writeMethod, readMethod)) continue;
            try {
                ReflectionUtils.makeAccessible(readMethod);
                Object value = readMethod.invoke(source, new Object[0]);
                ReflectionUtils.makeAccessible(writeMethod);
                writeMethod.invoke(target, value);
            }
            catch (Throwable ex) {
                throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
            }
        }
    }

    private static boolean isAssignable(Method writeMethod, Method readMethod) {
        Type paramType = writeMethod.getGenericParameterTypes()[0];
        if (paramType instanceof Class) {
            return ClassUtils.isAssignable((Class)paramType, readMethod.getReturnType());
        }
        if (paramType.equals(readMethod.getGenericReturnType())) {
            return true;
        }
        ResolvableType sourceType = ResolvableType.forMethodReturnType(readMethod);
        ResolvableType targetType = ResolvableType.forMethodParameter(writeMethod, 0);
        return sourceType.hasUnresolvableGenerics() || targetType.hasUnresolvableGenerics() ? ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType()) : targetType.isAssignableFrom(sourceType);
    }

    static {
        HashMap<Class<Comparable<Boolean>>, Comparable<Boolean>> values = new HashMap<Class<Comparable<Boolean>>, Comparable<Boolean>>();
        values.put(Boolean.TYPE, Boolean.valueOf(false));
        values.put(Byte.TYPE, Byte.valueOf((byte)0));
        values.put(Short.TYPE, Short.valueOf((short)0));
        values.put(Integer.TYPE, Integer.valueOf(0));
        values.put(Long.TYPE, Long.valueOf(0L));
        values.put(Float.TYPE, Float.valueOf(0.0f));
        values.put(Double.TYPE, Double.valueOf(0.0));
        values.put(Character.TYPE, Character.valueOf('\u0000'));
        DEFAULT_TYPE_VALUES = Collections.unmodifiableMap(values);
    }

    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        @Nullable
        public static <T> Constructor<T> findPrimaryConstructor(Class<T> clazz) {
            try {
                KFunction primaryCtor = KClasses.getPrimaryConstructor((KClass)JvmClassMappingKt.getKotlinClass(clazz));
                if (primaryCtor == null) {
                    return null;
                }
                Constructor constructor = ReflectJvmMapping.getJavaConstructor((KFunction)primaryCtor);
                if (constructor == null) {
                    throw new IllegalStateException("Failed to find Java constructor for Kotlin primary constructor: " + clazz.getName());
                }
                return constructor;
            }
            catch (UnsupportedOperationException ex) {
                return null;
            }
        }

        public static <T> T instantiateClass(Constructor<T> ctor, Object ... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            KFunction kotlinConstructor = ReflectJvmMapping.getKotlinFunction(ctor);
            if (kotlinConstructor == null) {
                return ctor.newInstance(args);
            }
            if (!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) {
                KCallablesJvm.setAccessible((KCallable)kotlinConstructor, (boolean)true);
            }
            List parameters = kotlinConstructor.getParameters();
            HashMap argParameters = CollectionUtils.newHashMap(parameters.size());
            Assert.isTrue(args.length <= parameters.size(), "Number of provided arguments should be less of equals than number of constructor parameters");
            for (int i2 = 0; i2 < args.length; ++i2) {
                if (((KParameter)parameters.get(i2)).isOptional() && args[i2] == null) continue;
                argParameters.put(parameters.get(i2), args[i2]);
            }
            return (T)kotlinConstructor.callBy(argParameters);
        }
    }
}

