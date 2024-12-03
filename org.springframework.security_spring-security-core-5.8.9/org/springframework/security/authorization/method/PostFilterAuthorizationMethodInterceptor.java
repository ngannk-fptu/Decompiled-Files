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
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationMethodPointcuts;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.security.authorization.method.PostFilterExpressionAttributeRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

public final class PostFilterAuthorizationMethodInterceptor
implements Ordered,
MethodInterceptor,
PointcutAdvisor,
AopInfrastructureBean {
    private Supplier<SecurityContextHolderStrategy> securityContextHolderStrategy = SecurityContextHolder::getContextHolderStrategy;
    private PostFilterExpressionAttributeRegistry registry = new PostFilterExpressionAttributeRegistry();
    private int order = AuthorizationInterceptorsOrder.POST_FILTER.getOrder();
    private final Pointcut pointcut = AuthorizationMethodPointcuts.forAnnotations(PostFilter.class);

    public void setExpressionHandler(MethodSecurityExpressionHandler expressionHandler) {
        this.registry = new PostFilterExpressionAttributeRegistry(expressionHandler);
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
        Object returnedObject = mi.proceed();
        Object attribute = this.registry.getAttribute(mi);
        if (attribute == ExpressionAttribute.NULL_ATTRIBUTE) {
            return returnedObject;
        }
        MethodSecurityExpressionHandler expressionHandler = this.registry.getExpressionHandler();
        EvaluationContext ctx = expressionHandler.createEvaluationContext(this::getAuthentication, mi);
        return expressionHandler.filter(returnedObject, ((ExpressionAttribute)attribute).getExpression(), ctx);
    }

    private Authentication getAuthentication() {
        Authentication authentication = this.securityContextHolderStrategy.get().getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("An Authentication object was not found in the SecurityContext");
        }
        return authentication;
    }
}

