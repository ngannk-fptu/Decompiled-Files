/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public abstract class ClassUtils {
    private ClassUtils() {
    }

    public static boolean hasProperty(Class<?> type, String property) {
        if (null != ReflectionUtils.findMethod(type, (String)("get" + property))) {
            return true;
        }
        return null != ReflectionUtils.findField(type, (String)StringUtils.uncapitalize((String)property));
    }

    public static void ifPresent(String className, @Nullable ClassLoader classLoader, Consumer<Class<?>> action) {
        try {
            Class theClass = org.springframework.util.ClassUtils.forName((String)className, (ClassLoader)classLoader);
            action.accept(theClass);
        }
        catch (IllegalAccessError err) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(), err);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static boolean isGenericRepositoryInterface(Class<?> interfaze) {
        return Repository.class.equals(interfaze);
    }

    public static boolean isGenericRepositoryInterface(@Nullable String interfaceName) {
        return Repository.class.getName().equals(interfaceName);
    }

    public static int getNumberOfOccurences(Method method, Class<?> type) {
        int result = 0;
        for (Class<?> clazz : method.getParameterTypes()) {
            if (!type.equals(clazz)) continue;
            ++result;
        }
        return result;
    }

    public static void assertReturnTypeAssignable(Method method, Class<?> ... types) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        Assert.notEmpty((Object[])types, (String)"Types must not be null or empty!");
        TypeInformation<?> returnType = ClassUtils.getEffectivelyReturnedTypeFrom(method);
        Arrays.stream(types).filter(it -> it.isAssignableFrom(returnType.getType())).findAny().orElseThrow(() -> new IllegalStateException("Method has to have one of the following return types! " + Arrays.toString(types)));
    }

    public static boolean isOfType(@Nullable Object object, Collection<Class<?>> types) {
        if (object == null) {
            return false;
        }
        return types.stream().anyMatch(it -> it.isAssignableFrom(object.getClass()));
    }

    public static boolean hasParameterOfType(Method method, Class<?> type) {
        return Arrays.asList(method.getParameterTypes()).contains(type);
    }

    public static void unwrapReflectionException(Exception ex) throws Throwable {
        if (ex instanceof InvocationTargetException) {
            throw ((InvocationTargetException)ex).getTargetException();
        }
        throw ex;
    }

    private static TypeInformation<?> getEffectivelyReturnedTypeFrom(Method method) {
        TypeInformation returnType = ClassTypeInformation.fromReturnTypeOf(method);
        return QueryExecutionConverters.supports(returnType.getType()) || ReactiveWrapperConverters.supports(returnType.getType()) ? returnType.getRequiredComponentType() : returnType;
    }
}

