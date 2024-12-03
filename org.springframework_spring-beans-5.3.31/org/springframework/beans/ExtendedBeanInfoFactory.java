/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.beans.ExtendedBeanInfo;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

public class ExtendedBeanInfoFactory
implements BeanInfoFactory,
Ordered {
    @Override
    @Nullable
    public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        return this.supports(beanClass) ? new ExtendedBeanInfo(Introspector.getBeanInfo(beanClass)) : null;
    }

    private boolean supports(Class<?> beanClass) {
        for (Method method : beanClass.getMethods()) {
            if (!ExtendedBeanInfo.isCandidateWriteMethod(method)) continue;
            return true;
        }
        return false;
    }

    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

