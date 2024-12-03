/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.framework.AopProxyUtils
 */
package org.springframework.security.access.method;

import java.util.Collection;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

@Deprecated
public abstract class AbstractMethodSecurityMetadataSource
implements MethodSecurityMetadataSource {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public final Collection<ConfigAttribute> getAttributes(Object object) {
        if (object instanceof MethodInvocation) {
            Collection<ConfigAttribute> attrs;
            MethodInvocation mi = (MethodInvocation)object;
            Object target = mi.getThis();
            Class targetClass = null;
            if (target != null) {
                Class clazz = targetClass = target instanceof Class ? (Class)target : AopProxyUtils.ultimateTargetClass((Object)target);
            }
            if ((attrs = this.getAttributes(mi.getMethod(), targetClass)) != null && !attrs.isEmpty()) {
                return attrs;
            }
            if (target != null && !(target instanceof Class)) {
                attrs = this.getAttributes(mi.getMethod(), target.getClass());
            }
            return attrs;
        }
        throw new IllegalArgumentException("Object must be a non-null MethodInvocation");
    }

    @Override
    public final boolean supports(Class<?> clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }
}

