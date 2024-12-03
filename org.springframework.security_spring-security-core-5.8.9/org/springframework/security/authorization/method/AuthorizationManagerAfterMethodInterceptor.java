/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.PointcutAdvisor
 *  org.springframework.aop.framework.AopInfrastructureBean
 *  org.springframework.core.Ordered
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization.method;

import java.util.function.Supplier;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationMethodPointcuts;
import org.springframework.security.authorization.method.MethodInvocationResult;
import org.springframework.security.authorization.method.PostAuthorizeAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

public final class AuthorizationManagerAfterMethodInterceptor
implements Ordered,
MethodInterceptor,
PointcutAdvisor,
AopInfrastructureBean {
    private Supplier<SecurityContextHolderStrategy> securityContextHolderStrategy = SecurityContextHolder::getContextHolderStrategy;
    private final Log logger = LogFactory.getLog(this.getClass());
    private final Pointcut pointcut;
    private final AuthorizationManager<MethodInvocationResult> authorizationManager;
    private int order;
    private AuthorizationEventPublisher eventPublisher = AuthorizationManagerAfterMethodInterceptor::noPublish;

    public AuthorizationManagerAfterMethodInterceptor(Pointcut pointcut, AuthorizationManager<MethodInvocationResult> authorizationManager) {
        Assert.notNull((Object)pointcut, (String)"pointcut cannot be null");
        Assert.notNull(authorizationManager, (String)"authorizationManager cannot be null");
        this.pointcut = pointcut;
        this.authorizationManager = authorizationManager;
    }

    public static AuthorizationManagerAfterMethodInterceptor postAuthorize() {
        return AuthorizationManagerAfterMethodInterceptor.postAuthorize(new PostAuthorizeAuthorizationManager());
    }

    public static AuthorizationManagerAfterMethodInterceptor postAuthorize(PostAuthorizeAuthorizationManager authorizationManager) {
        AuthorizationManagerAfterMethodInterceptor interceptor = new AuthorizationManagerAfterMethodInterceptor(AuthorizationMethodPointcuts.forAnnotations(PostAuthorize.class), authorizationManager);
        interceptor.setOrder(500);
        return interceptor;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        Object result = mi.proceed();
        this.attemptAuthorization(mi, result);
        return result;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setAuthorizationEventPublisher(AuthorizationEventPublisher eventPublisher) {
        Assert.notNull((Object)eventPublisher, (String)"eventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
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

    private void attemptAuthorization(MethodInvocation mi, Object result) {
        this.logger.debug((Object)LogMessage.of(() -> "Authorizing method invocation " + mi));
        MethodInvocationResult object = new MethodInvocationResult(mi, result);
        AuthorizationDecision decision = this.authorizationManager.check(this::getAuthentication, object);
        this.eventPublisher.publishAuthorizationEvent(this::getAuthentication, object, decision);
        if (decision != null && !decision.isGranted()) {
            this.logger.debug((Object)LogMessage.of(() -> "Failed to authorize " + mi + " with authorization manager " + this.authorizationManager + " and decision " + decision));
            throw new AccessDeniedException("Access Denied");
        }
        this.logger.debug((Object)LogMessage.of(() -> "Authorized method invocation " + mi));
    }

    private Authentication getAuthentication() {
        Authentication authentication = this.securityContextHolderStrategy.get().getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("An Authentication object was not found in the SecurityContext");
        }
        return authentication;
    }

    private static <T> void noPublish(Supplier<Authentication> authentication, T object, AuthorizationDecision decision) {
    }
}

