/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.AopProxyUtils
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.context.expression.MethodBasedEvaluationContext
 *  org.springframework.core.ParameterNameDiscoverer
 */
package org.springframework.security.access.expression.method;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.DefaultSecurityParameterNameDiscoverer;

class MethodSecurityEvaluationContext
extends MethodBasedEvaluationContext {
    MethodSecurityEvaluationContext(Authentication user, MethodInvocation mi) {
        this(user, mi, (ParameterNameDiscoverer)new DefaultSecurityParameterNameDiscoverer());
    }

    MethodSecurityEvaluationContext(Authentication user, MethodInvocation mi, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(mi.getThis(), MethodSecurityEvaluationContext.getSpecificMethod(mi), mi.getArguments(), parameterNameDiscoverer);
    }

    MethodSecurityEvaluationContext(MethodSecurityExpressionOperations root, MethodInvocation mi, ParameterNameDiscoverer parameterNameDiscoverer) {
        super((Object)root, MethodSecurityEvaluationContext.getSpecificMethod(mi), mi.getArguments(), parameterNameDiscoverer);
    }

    private static Method getSpecificMethod(MethodInvocation mi) {
        return AopUtils.getMostSpecificMethod((Method)mi.getMethod(), (Class)AopProxyUtils.ultimateTargetClass((Object)mi.getThis()));
    }
}

