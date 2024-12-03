/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class UnsatisfiedDependencyException
extends BeanCreationException {
    @Nullable
    private final InjectionPoint injectionPoint;

    public UnsatisfiedDependencyException(@Nullable String resourceDescription, @Nullable String beanName, String propertyName, String msg) {
        super(resourceDescription, beanName, "Unsatisfied dependency expressed through bean property '" + propertyName + "'" + (StringUtils.hasLength(msg) ? ": " + msg : ""));
        this.injectionPoint = null;
    }

    public UnsatisfiedDependencyException(@Nullable String resourceDescription, @Nullable String beanName, String propertyName, BeansException ex) {
        this(resourceDescription, beanName, propertyName, "");
        this.initCause(ex);
    }

    public UnsatisfiedDependencyException(@Nullable String resourceDescription, @Nullable String beanName, @Nullable InjectionPoint injectionPoint, String msg) {
        super(resourceDescription, beanName, "Unsatisfied dependency expressed through " + injectionPoint + (StringUtils.hasLength(msg) ? ": " + msg : ""));
        this.injectionPoint = injectionPoint;
    }

    public UnsatisfiedDependencyException(@Nullable String resourceDescription, @Nullable String beanName, @Nullable InjectionPoint injectionPoint, BeansException ex) {
        this(resourceDescription, beanName, injectionPoint, "");
        this.initCause(ex);
    }

    @Nullable
    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }
}

