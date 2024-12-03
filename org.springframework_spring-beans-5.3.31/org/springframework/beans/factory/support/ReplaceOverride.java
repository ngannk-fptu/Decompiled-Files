/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ReplaceOverride
extends MethodOverride {
    private final String methodReplacerBeanName;
    private final List<String> typeIdentifiers = new ArrayList<String>();

    public ReplaceOverride(String methodName, String methodReplacerBeanName) {
        super(methodName);
        Assert.notNull((Object)methodReplacerBeanName, (String)"Method replacer bean name must not be null");
        this.methodReplacerBeanName = methodReplacerBeanName;
    }

    public String getMethodReplacerBeanName() {
        return this.methodReplacerBeanName;
    }

    public void addTypeIdentifier(String identifier) {
        this.typeIdentifiers.add(identifier);
    }

    @Override
    public boolean matches(Method method) {
        if (!method.getName().equals(this.getMethodName())) {
            return false;
        }
        if (!this.isOverloaded()) {
            return true;
        }
        if (this.typeIdentifiers.size() != method.getParameterCount()) {
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < this.typeIdentifiers.size(); ++i) {
            String identifier = this.typeIdentifiers.get(i);
            if (parameterTypes[i].getName().contains(identifier)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof ReplaceOverride) || !super.equals(other)) {
            return false;
        }
        ReplaceOverride that = (ReplaceOverride)other;
        return ObjectUtils.nullSafeEquals((Object)this.methodReplacerBeanName, (Object)that.methodReplacerBeanName) && ObjectUtils.nullSafeEquals(this.typeIdentifiers, that.typeIdentifiers);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object)this.methodReplacerBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.typeIdentifiers);
        return hashCode;
    }

    public String toString() {
        return "Replace override for method '" + this.getMethodName() + "'";
    }
}

