/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.projection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

class PropertyAccessingMethodInterceptor
implements MethodInterceptor {
    private final BeanWrapper target;

    public PropertyAccessingMethodInterceptor(Object target) {
        Assert.notNull((Object)target, (String)"Proxy target must not be null!");
        this.target = new DirectFieldAccessFallbackBeanWrapper(target);
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (ReflectionUtils.isObjectMethod((Method)method)) {
            return invocation.proceed();
        }
        PropertyDescriptor descriptor = BeanUtils.findPropertyForMethod((Method)method);
        if (descriptor == null) {
            throw new IllegalStateException("Invoked method is not a property accessor!");
        }
        if (!PropertyAccessingMethodInterceptor.isSetterMethod(method, descriptor)) {
            return this.target.getPropertyValue(descriptor.getName());
        }
        if (invocation.getArguments().length != 1) {
            throw new IllegalStateException("Invoked setter method requires exactly one argument!");
        }
        this.target.setPropertyValue(descriptor.getName(), invocation.getArguments()[0]);
        return null;
    }

    private static boolean isSetterMethod(Method method, PropertyDescriptor descriptor) {
        return method.equals(descriptor.getWriteMethod());
    }
}

