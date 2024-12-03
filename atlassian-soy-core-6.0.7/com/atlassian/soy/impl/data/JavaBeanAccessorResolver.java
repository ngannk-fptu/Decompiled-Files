/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 */
package com.atlassian.soy.impl.data;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import java.lang.reflect.Method;
import java.util.Map;

@TenantAware(value=TenancyScope.TENANTLESS, comment="Caches acsessors of the classes, same for all tenants.")
public interface JavaBeanAccessorResolver {
    public void clearCaches();

    public Map<String, Method> resolveAccessors(Class<?> var1);
}

