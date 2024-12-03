/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopInfrastructureBean
 */
package org.springframework.security.access;

import java.util.Collection;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.security.core.Authentication;

public interface PermissionCacheOptimizer
extends AopInfrastructureBean {
    public void cachePermissionsFor(Authentication var1, Collection<?> var2);
}

