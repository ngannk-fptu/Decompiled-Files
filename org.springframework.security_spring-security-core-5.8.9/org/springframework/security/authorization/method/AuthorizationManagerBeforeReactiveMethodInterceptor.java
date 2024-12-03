/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.reactivestreams.Publisher
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.PointcutAdvisor
 *  org.springframework.aop.framework.AopInfrastructureBean
 *  org.springframework.core.Ordered
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization.method;

import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.reactivestreams.Publisher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.core.Ordered;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationMethodPointcuts;
import org.springframework.security.authorization.method.PreAuthorizeReactiveAuthorizationManager;
import org.springframework.security.authorization.method.ReactiveAuthenticationUtils;
import org.springframework.security.authorization.method.ReactiveMethodInvocationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class AuthorizationManagerBeforeReactiveMethodInterceptor
implements Ordered,
MethodInterceptor,
PointcutAdvisor,
AopInfrastructureBean {
    private final Pointcut pointcut;
    private final ReactiveAuthorizationManager<MethodInvocation> authorizationManager;
    private int order = AuthorizationInterceptorsOrder.FIRST.getOrder();

    public static AuthorizationManagerBeforeReactiveMethodInterceptor preAuthorize() {
        return AuthorizationManagerBeforeReactiveMethodInterceptor.preAuthorize(new PreAuthorizeReactiveAuthorizationManager());
    }

    public static AuthorizationManagerBeforeReactiveMethodInterceptor preAuthorize(ReactiveAuthorizationManager<MethodInvocation> authorizationManager) {
        AuthorizationManagerBeforeReactiveMethodInterceptor interceptor = new AuthorizationManagerBeforeReactiveMethodInterceptor(AuthorizationMethodPointcuts.forAnnotations(PreAuthorize.class), authorizationManager);
        interceptor.setOrder(AuthorizationInterceptorsOrder.PRE_AUTHORIZE.getOrder());
        return interceptor;
    }

    public AuthorizationManagerBeforeReactiveMethodInterceptor(Pointcut pointcut, ReactiveAuthorizationManager<MethodInvocation> authorizationManager) {
        Assert.notNull((Object)pointcut, (String)"pointcut cannot be null");
        Assert.notNull(authorizationManager, (String)"authorizationManager cannot be null");
        this.pointcut = pointcut;
        this.authorizationManager = authorizationManager;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        Method method = mi.getMethod();
        Class<?> type = method.getReturnType();
        Assert.state((boolean)Publisher.class.isAssignableFrom(type), () -> String.format("The returnType %s on %s must return an instance of org.reactivestreams.Publisher (for example, a Mono or Flux) in order to support Reactor Context", type, method));
        Mono<Authentication> authentication = ReactiveAuthenticationUtils.getAuthentication();
        ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(type);
        Mono<Void> preAuthorize = this.authorizationManager.verify(authentication, mi);
        if (this.isMultiValue(type, adapter)) {
            Flux publisher = Flux.defer(() -> (Publisher)ReactiveMethodInvocationUtils.proceed(mi));
            Flux result = preAuthorize.thenMany((Publisher)publisher);
            return adapter != null ? adapter.fromPublisher((Publisher)result) : result;
        }
        Mono publisher = Mono.defer(() -> (Mono)ReactiveMethodInvocationUtils.proceed(mi));
        Mono result = preAuthorize.then(publisher);
        return adapter != null ? adapter.fromPublisher((Publisher)result) : result;
    }

    private boolean isMultiValue(Class<?> returnType, ReactiveAdapter adapter) {
        if (Flux.class.isAssignableFrom(returnType)) {
            return true;
        }
        return adapter == null || adapter.isMultiValue();
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

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

