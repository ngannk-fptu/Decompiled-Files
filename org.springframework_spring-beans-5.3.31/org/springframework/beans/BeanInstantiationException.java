/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;

public class BeanInstantiationException
extends FatalBeanException {
    private final Class<?> beanClass;
    @Nullable
    private final Constructor<?> constructor;
    @Nullable
    private final Method constructingMethod;

    public BeanInstantiationException(Class<?> beanClass, String msg) {
        this(beanClass, msg, null);
    }

    public BeanInstantiationException(Class<?> beanClass, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
        this.constructor = null;
        this.constructingMethod = null;
    }

    public BeanInstantiationException(Constructor<?> constructor, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + constructor.getDeclaringClass().getName() + "]: " + msg, cause);
        this.beanClass = constructor.getDeclaringClass();
        this.constructor = constructor;
        this.constructingMethod = null;
    }

    public BeanInstantiationException(Method constructingMethod, String msg, @Nullable Throwable cause) {
        super("Failed to instantiate [" + constructingMethod.getReturnType().getName() + "]: " + msg, cause);
        this.beanClass = constructingMethod.getReturnType();
        this.constructor = null;
        this.constructingMethod = constructingMethod;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Nullable
    public Constructor<?> getConstructor() {
        return this.constructor;
    }

    @Nullable
    public Method getConstructingMethod() {
        return this.constructingMethod;
    }
}

