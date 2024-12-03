/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyDescriptorUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

final class GenericTypeAwarePropertyDescriptor
extends PropertyDescriptor {
    private final Class<?> beanClass;
    @Nullable
    private final Method readMethod;
    @Nullable
    private final Method writeMethod;
    @Nullable
    private volatile Set<Method> ambiguousWriteMethods;
    @Nullable
    private MethodParameter writeMethodParameter;
    @Nullable
    private Class<?> propertyType;
    @Nullable
    private final Class<?> propertyEditorClass;

    public GenericTypeAwarePropertyDescriptor(Class<?> beanClass, String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod, @Nullable Class<?> propertyEditorClass) throws IntrospectionException {
        super(propertyName, null, null);
        Method candidate;
        Method writeMethodToUse;
        this.beanClass = beanClass;
        Method readMethodToUse = readMethod != null ? BridgeMethodResolver.findBridgedMethod(readMethod) : null;
        Method method = writeMethodToUse = writeMethod != null ? BridgeMethodResolver.findBridgedMethod(writeMethod) : null;
        if (writeMethodToUse == null && readMethodToUse != null && (candidate = ClassUtils.getMethodIfAvailable(this.beanClass, "set" + StringUtils.capitalize(this.getName()), null)) != null && candidate.getParameterCount() == 1) {
            writeMethodToUse = candidate;
        }
        this.readMethod = readMethodToUse;
        this.writeMethod = writeMethodToUse;
        if (this.writeMethod != null) {
            if (this.readMethod == null) {
                HashSet<Method> ambiguousCandidates = new HashSet<Method>();
                for (Method method2 : beanClass.getMethods()) {
                    if (!method2.getName().equals(writeMethodToUse.getName()) || method2.equals(writeMethodToUse) || method2.isBridge() || method2.getParameterCount() != writeMethodToUse.getParameterCount()) continue;
                    ambiguousCandidates.add(method2);
                }
                if (!ambiguousCandidates.isEmpty()) {
                    this.ambiguousWriteMethods = ambiguousCandidates;
                }
            }
            this.writeMethodParameter = new MethodParameter(this.writeMethod, 0).withContainingClass(this.beanClass);
        }
        if (this.readMethod != null) {
            this.propertyType = GenericTypeResolver.resolveReturnType(this.readMethod, this.beanClass);
        } else if (this.writeMethodParameter != null) {
            this.propertyType = this.writeMethodParameter.getParameterType();
        }
        this.propertyEditorClass = propertyEditorClass;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    @Nullable
    public Method getReadMethod() {
        return this.readMethod;
    }

    @Override
    @Nullable
    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public Method getWriteMethodForActualAccess() {
        Assert.state(this.writeMethod != null, "No write method available");
        Set<Method> ambiguousCandidates = this.ambiguousWriteMethods;
        if (ambiguousCandidates != null) {
            this.ambiguousWriteMethods = null;
            LogFactory.getLog(GenericTypeAwarePropertyDescriptor.class).debug("Non-unique JavaBean property '" + this.getName() + "' being accessed! Ambiguous write methods found next to actually used [" + this.writeMethod + "]: " + ambiguousCandidates);
        }
        return this.writeMethod;
    }

    public MethodParameter getWriteMethodParameter() {
        Assert.state(this.writeMethodParameter != null, "No write method available");
        return this.writeMethodParameter;
    }

    @Override
    @Nullable
    public Class<?> getPropertyType() {
        return this.propertyType;
    }

    @Override
    @Nullable
    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GenericTypeAwarePropertyDescriptor)) {
            return false;
        }
        GenericTypeAwarePropertyDescriptor otherPd = (GenericTypeAwarePropertyDescriptor)other;
        return this.getBeanClass().equals(otherPd.getBeanClass()) && PropertyDescriptorUtils.equals(this, otherPd);
    }

    @Override
    public int hashCode() {
        int hashCode = this.getBeanClass().hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getReadMethod());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getWriteMethod());
        return hashCode;
    }
}

