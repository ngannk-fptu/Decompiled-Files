/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 */
package com.atlassian.plugins.rest.common.interceptor.impl;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InterceptorChainBuilder {
    @TenantAware(value=TenancyScope.TENANTLESS)
    private final LinkedHashMap<Class<? extends ResourceInterceptor>, ResourceInterceptor> defaultResourceInterceptors = new LinkedHashMap();
    private final ContainerManagedPlugin plugin;

    public InterceptorChainBuilder(ContainerManagedPlugin plugin, ResourceInterceptor ... resourceInterceptors) {
        for (ResourceInterceptor resourceInterceptor : resourceInterceptors) {
            this.defaultResourceInterceptors.put(resourceInterceptor.getClass(), resourceInterceptor);
        }
        this.plugin = plugin;
    }

    public List<ResourceInterceptor> getResourceInterceptorsForMethod(Method m) {
        InterceptorChain chain = m.getAnnotation(InterceptorChain.class);
        if (chain == null && (chain = m.getDeclaringClass().getAnnotation(InterceptorChain.class)) == null) {
            chain = m.getDeclaringClass().getPackage().getAnnotation(InterceptorChain.class);
        }
        if (chain != null) {
            return this.buildFromClass(chain.value());
        }
        return new ArrayList<ResourceInterceptor>(this.defaultResourceInterceptors.values());
    }

    private List<ResourceInterceptor> buildFromClass(Class<? extends ResourceInterceptor>[] resourceInterceptorClasses) {
        ArrayList<ResourceInterceptor> resourceInterceptors = new ArrayList<ResourceInterceptor>();
        for (Class<? extends ResourceInterceptor> resourceInterceptorClass : resourceInterceptorClasses) {
            if (this.defaultResourceInterceptors.containsKey(resourceInterceptorClass)) {
                resourceInterceptors.add(this.defaultResourceInterceptors.get(resourceInterceptorClass));
                continue;
            }
            resourceInterceptors.add((ResourceInterceptor)this.plugin.getContainerAccessor().createBean(resourceInterceptorClass));
        }
        return resourceInterceptors;
    }
}

