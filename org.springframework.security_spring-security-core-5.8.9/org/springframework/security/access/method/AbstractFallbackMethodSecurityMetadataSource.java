/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.AopUtils
 */
package org.springframework.security.access.method;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import org.springframework.aop.support.AopUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;

@Deprecated
public abstract class AbstractFallbackMethodSecurityMetadataSource
extends AbstractMethodSecurityMetadataSource {
    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        Method specificMethod = AopUtils.getMostSpecificMethod((Method)method, targetClass);
        Collection<ConfigAttribute> attr = this.findAttributes(specificMethod, targetClass);
        if (attr != null) {
            return attr;
        }
        attr = this.findAttributes(specificMethod.getDeclaringClass());
        if (attr != null) {
            return attr;
        }
        if (specificMethod != method || targetClass == null) {
            attr = this.findAttributes(method, method.getDeclaringClass());
            if (attr != null) {
                return attr;
            }
            return this.findAttributes(method.getDeclaringClass());
        }
        return Collections.emptyList();
    }

    protected abstract Collection<ConfigAttribute> findAttributes(Method var1, Class<?> var2);

    protected abstract Collection<ConfigAttribute> findAttributes(Class<?> var1);
}

