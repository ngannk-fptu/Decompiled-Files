/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopInfrastructureBean
 */
package org.springframework.security.access;

import java.io.Serializable;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.security.core.Authentication;

public interface PermissionEvaluator
extends AopInfrastructureBean {
    public boolean hasPermission(Authentication var1, Object var2, Object var3);

    public boolean hasPermission(Authentication var1, Serializable var2, String var3, Object var4);
}

