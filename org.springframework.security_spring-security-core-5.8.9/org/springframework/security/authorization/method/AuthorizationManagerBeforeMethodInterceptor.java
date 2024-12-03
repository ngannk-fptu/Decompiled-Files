/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.security.DenyAll
 *  javax.annotation.security.PermitAll
 *  javax.annotation.security.RolesAllowed
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
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationMethodPointcuts;
import org.springframework.security.authorization.method.Jsr250AuthorizationManager;
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager;
import org.springframework.security.authorization.method.SecuredAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

public final class AuthorizationManagerBeforeMethodInterceptor
implements Ordered,
MethodInterceptor,
PointcutAdvisor,
AopInfrastructureBean {
    private Supplier<SecurityContextHolderStrategy> securityContextHolderStrategy = SecurityContextHolder::getContextHolderStrategy;
    private final Log logger = LogFactory.getLog(this.getClass());
    private final Pointcut pointcut;
    private final AuthorizationManager<MethodInvocation> authorizationManager;
    private int order = AuthorizationInterceptorsOrder.FIRST.getOrder();
    private AuthorizationEventPublisher eventPublisher = AuthorizationManagerBeforeMethodInterceptor::noPublish;

    public AuthorizationManagerBeforeMethodInterceptor(Pointcut pointcut, AuthorizationManager<MethodInvocation> authorizationManager) {
        Assert.notNull((Object)pointcut, (String)"pointcut cannot be null");
        Assert.notNull(authorizationManager, (String)"authorizationManager cannot be null");
        this.pointcut = pointcut;
        this.authorizationManager = authorizationManager;
    }

    public static AuthorizationManagerBeforeMethodInterceptor preAuthorize() {
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(new PreAuthorizeAuthorizationManager());
    }

    public static AuthorizationManagerBeforeMethodInterceptor preAuthorize(PreAuthorizeAuthorizationManager authorizationManager) {
        AuthorizationManagerBeforeMethodInterceptor interceptor = new AuthorizationManagerBeforeMethodInterceptor(AuthorizationMethodPointcuts.forAnnotations(PreAuthorize.class), authorizationManager);
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder());
        return interceptor;
    }

    public static AuthorizationManagerBeforeMethodInterceptor secured() {
        return AuthorizationManagerBeforeMethodInterceptor.secured(new SecuredAuthorizationManager());
    }

    public static AuthorizationManagerBeforeMethodInterceptor secured(SecuredAuthorizationManager authorizationManager) {
        AuthorizationManagerBeforeMethodInterceptor interceptor = new AuthorizationManagerBeforeMethodInterceptor(AuthorizationMethodPointcuts.forAnnotations(Secured.class), authorizationManager);
        interceptor.setOrder(AuthorizationInterceptorsOrder.SECURED.getOrder());
        return interceptor;
    }

    public static AuthorizationManagerBeforeMethodInterceptor jsr250() {
        return AuthorizationManagerBeforeMethodInterceptor.jsr250(new Jsr250AuthorizationManager());
    }

    public static AuthorizationManagerBeforeMethodInterceptor jsr250(Jsr250AuthorizationManager authorizationManager) {
        AuthorizationManagerBeforeMethodInterceptor interceptor = new AuthorizationManagerBeforeMethodInterceptor(AuthorizationMethodPointcuts.forAnnotations(RolesAllowed.class, DenyAll.class, PermitAll.class), authorizationManager);
        interceptor.setOrder(AuthorizationInterceptorsOrder.JSR250.getOrder());
        return interceptor;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        this.attemptAuthorization(mi);
        return mi.proceed();
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

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = () -> securityContextHolderStrategy;
    }

    private void attemptAuthorization(MethodInvocation mi) {
        this.logger.debug((Object)LogMessage.of(() -> "Authorizing method invocation " + mi));
        AuthorizationDecision decision = this.authorizationManager.check(this::getAuthentication, mi);
        this.eventPublisher.publishAuthorizationEvent(this::getAuthentication, mi, decision);
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

