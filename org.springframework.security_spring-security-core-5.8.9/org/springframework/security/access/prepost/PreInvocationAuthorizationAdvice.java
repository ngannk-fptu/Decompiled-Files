/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.AopInfrastructureBean
 */
package org.springframework.security.access.prepost;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.core.Authentication;

@Deprecated
public interface PreInvocationAuthorizationAdvice
extends AopInfrastructureBean {
    public boolean before(Authentication var1, MethodInvocation var2, PreInvocationAttribute var3);
}

