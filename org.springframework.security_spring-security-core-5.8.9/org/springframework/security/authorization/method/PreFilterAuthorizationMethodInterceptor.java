/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.PointcutAdvisor
 *  org.springframework.aop.framework.AopInfrastructureBean
 *  org.springframework.core.Ordered
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.authorization.method;

import java.util.function.Supplier;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.core.Ordered;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationMethodPointcuts;
import org.springframework.security.authorization.method.PreFilterExpressionAttributeRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public final class PreFilterAuthorizationMethodInterceptor
implements Ordered,
MethodInterceptor,
PointcutAdvisor,
AopInfrastructureBean {
    private Supplier<SecurityContextHolderStrategy> securityContextHolderStrategy = SecurityContextHolder::getContextHolderStrategy;
    private PreFilterExpressionAttributeRegistry registry = new PreFilterExpressionAttributeRegistry();
    private int order = AuthorizationInterceptorsOrder.PRE_FILTER.getOrder();
    private final Pointcut pointcut = AuthorizationMethodPointcuts.forAnnotations(PreFilter.class);

    public void setExpressionHandler(MethodSecurityExpressionHandler expressionHandler) {
        this.registry = new PreFilterExpressionAttributeRegistry(expressionHandler);
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public Advice getAdvice() {
        return this;
    }

    public boolean isPerInstance() {
        return true;
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy strategy) {
        this.securityContextHolderStrategy = () -> strategy;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        PreFilterExpressionAttributeRegistry.PreFilterExpressionAttribute attribute = (PreFilterExpressionAttributeRegistry.PreFilterExpressionAttribute)this.registry.getAttribute(mi);
        if (attribute == PreFilterExpressionAttributeRegistry.PreFilterExpressionAttribute.NULL_ATTRIBUTE) {
            return mi.proceed();
        }
        MethodSecurityExpressionHandler expressionHandler = this.registry.getExpressionHandler();
        EvaluationContext ctx = expressionHandler.createEvaluationContext(this::getAuthentication, mi);
        Object filterTarget = this.findFilterTarget(attribute.getFilterTarget(), ctx, mi);
        expressionHandler.filter(filterTarget, attribute.getExpression(), ctx);
        return mi.proceed();
    }

    private Object findFilterTarget(String filterTargetName, EvaluationContext ctx, MethodInvocation methodInvocation) {
        Object filterTarget;
        if (StringUtils.hasText((String)filterTargetName)) {
            filterTarget = ctx.lookupVariable(filterTargetName);
            Assert.notNull((Object)filterTarget, () -> "Filter target was null, or no argument with name '" + filterTargetName + "' found in method.");
        } else {
            Object[] arguments = methodInvocation.getArguments();
            Assert.state((arguments.length == 1 ? 1 : 0) != 0, (String)"Unable to determine the method argument for filtering. Specify the filter target.");
            filterTarget = arguments[0];
            Assert.notNull((Object)filterTarget, (String)"Filter target was null. Make sure you passing the correct value in the method argument.");
        }
        Assert.state((!filterTarget.getClass().isArray() ? 1 : 0) != 0, (String)"Pre-filtering on array types is not supported. Using a Collection will solve this problem.");
        return filterTarget;
    }

    private Authentication getAuthentication() {
        Authentication authentication = this.securityContextHolderStrategy.get().getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("An Authentication object was not found in the SecurityContext");
        }
        return authentication;
    }
}

