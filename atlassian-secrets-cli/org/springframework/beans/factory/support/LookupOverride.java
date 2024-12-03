/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class LookupOverride
extends MethodOverride {
    @Nullable
    private final String beanName;
    @Nullable
    private Method method;

    public LookupOverride(String methodName, @Nullable String beanName) {
        super(methodName);
        this.beanName = beanName;
    }

    public LookupOverride(Method method, @Nullable String beanName) {
        super(method.getName());
        this.method = method;
        this.beanName = beanName;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public boolean matches(Method method) {
        if (this.method != null) {
            return method.equals(this.method);
        }
        return method.getName().equals(this.getMethodName()) && (!this.isOverloaded() || Modifier.isAbstract(method.getModifiers()) || method.getParameterCount() == 0);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof LookupOverride) || !super.equals(other)) {
            return false;
        }
        LookupOverride that = (LookupOverride)other;
        return ObjectUtils.nullSafeEquals(this.method, that.method) && ObjectUtils.nullSafeEquals(this.beanName, that.beanName);
    }

    @Override
    public int hashCode() {
        return 29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.beanName);
    }

    public String toString() {
        return "LookupOverride for method '" + this.getMethodName() + "'";
    }
}

