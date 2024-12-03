/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.beans.factory.support;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

abstract class AutowireUtils {
    public static final Comparator<Executable> EXECUTABLE_COMPARATOR = (e1, e2) -> {
        int result = Boolean.compare(Modifier.isPublic(e2.getModifiers()), Modifier.isPublic(e1.getModifiers()));
        return result != 0 ? result : Integer.compare(e2.getParameterCount(), e1.getParameterCount());
    };

    AutowireUtils() {
    }

    public static void sortConstructors(Constructor<?>[] constructors) {
        Arrays.sort(constructors, EXECUTABLE_COMPARATOR);
    }

    public static void sortFactoryMethods(Method[] factoryMethods) {
        Arrays.sort(factoryMethods, EXECUTABLE_COMPARATOR);
    }

    public static boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
        Method wm = pd.getWriteMethod();
        if (wm == null) {
            return false;
        }
        if (!wm.getDeclaringClass().getName().contains("$$")) {
            return false;
        }
        Class<?> superclass = wm.getDeclaringClass().getSuperclass();
        return !ClassUtils.hasMethod(superclass, (Method)wm);
    }

    public static boolean isSetterDefinedInInterface(PropertyDescriptor pd, Set<Class<?>> interfaces) {
        Method setter = pd.getWriteMethod();
        if (setter != null) {
            Class<?> targetClass = setter.getDeclaringClass();
            for (Class<?> ifc : interfaces) {
                if (!ifc.isAssignableFrom(targetClass) || !ClassUtils.hasMethod(ifc, (Method)setter)) continue;
                return true;
            }
        }
        return false;
    }

    public static Object resolveAutowiringValue(Object autowiringValue, Class<?> requiredType) {
        if (autowiringValue instanceof ObjectFactory && !requiredType.isInstance(autowiringValue)) {
            ObjectFactory factory = (ObjectFactory)autowiringValue;
            if (autowiringValue instanceof Serializable && requiredType.isInterface()) {
                autowiringValue = Proxy.newProxyInstance(requiredType.getClassLoader(), new Class[]{requiredType}, (InvocationHandler)new ObjectFactoryDelegatingInvocationHandler(factory));
            } else {
                return factory.getObject();
            }
        }
        return autowiringValue;
    }

    public static Class<?> resolveReturnTypeForFactoryMethod(Method method, Object[] args, @Nullable ClassLoader classLoader) {
        Assert.notNull((Object)method, (String)"Method must not be null");
        Assert.notNull((Object)args, (String)"Argument array must not be null");
        TypeVariable<Method>[] declaredTypeVariables = method.getTypeParameters();
        Type genericReturnType = method.getGenericReturnType();
        Type[] methodParameterTypes = method.getGenericParameterTypes();
        Assert.isTrue((args.length == methodParameterTypes.length ? 1 : 0) != 0, (String)"Argument array does not match parameter count");
        boolean locallyDeclaredTypeVariableMatchesReturnType = false;
        for (TypeVariable<Method> currentTypeVariable : declaredTypeVariables) {
            if (!currentTypeVariable.equals(genericReturnType)) continue;
            locallyDeclaredTypeVariableMatchesReturnType = true;
            break;
        }
        if (locallyDeclaredTypeVariableMatchesReturnType) {
            for (int i = 0; i < methodParameterTypes.length; ++i) {
                Type[] actualTypeArguments;
                Type methodParameterType = methodParameterTypes[i];
                Object arg = args[i];
                if (methodParameterType.equals(genericReturnType)) {
                    block18: {
                        if (arg instanceof TypedStringValue) {
                            TypedStringValue typedValue = (TypedStringValue)arg;
                            if (typedValue.hasTargetType()) {
                                return typedValue.getTargetType();
                            }
                            try {
                                Class<?> resolvedType = typedValue.resolveTargetType(classLoader);
                                if (resolvedType != null) {
                                    return resolvedType;
                                }
                                break block18;
                            }
                            catch (ClassNotFoundException ex) {
                                throw new IllegalStateException("Failed to resolve value type [" + typedValue.getTargetTypeName() + "] for factory method argument", ex);
                            }
                        }
                        if (arg != null && !(arg instanceof BeanMetadataElement)) {
                            return arg.getClass();
                        }
                    }
                    return method.getReturnType();
                }
                if (!(methodParameterType instanceof ParameterizedType)) continue;
                ParameterizedType parameterizedType = (ParameterizedType)methodParameterType;
                for (Type typeArg : actualTypeArguments = parameterizedType.getActualTypeArguments()) {
                    TypedStringValue typedValue;
                    String targetTypeName;
                    if (!typeArg.equals(genericReturnType)) continue;
                    if (arg instanceof Class) {
                        return (Class)arg;
                    }
                    String className = null;
                    if (arg instanceof String) {
                        className = (String)arg;
                    } else if (arg instanceof TypedStringValue && ((targetTypeName = (typedValue = (TypedStringValue)arg).getTargetTypeName()) == null || Class.class.getName().equals(targetTypeName))) {
                        className = typedValue.getValue();
                    }
                    if (className != null) {
                        try {
                            return ClassUtils.forName((String)className, (ClassLoader)classLoader);
                        }
                        catch (ClassNotFoundException ex) {
                            throw new IllegalStateException("Could not resolve class name [" + arg + "] for factory method argument", ex);
                        }
                    }
                    return method.getReturnType();
                }
            }
        }
        return method.getReturnType();
    }

    private static class ObjectFactoryDelegatingInvocationHandler
    implements InvocationHandler,
    Serializable {
        private final ObjectFactory<?> objectFactory;

        ObjectFactoryDelegatingInvocationHandler(ObjectFactory<?> objectFactory) {
            this.objectFactory = objectFactory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "toString": {
                    return this.objectFactory.toString();
                }
            }
            try {
                return method.invoke(this.objectFactory.getObject(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}

