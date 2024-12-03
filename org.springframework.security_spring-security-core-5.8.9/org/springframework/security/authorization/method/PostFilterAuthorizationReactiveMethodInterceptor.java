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
 *  org.springframework.expression.EvaluationContext
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
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationMethodPointcuts;
import org.springframework.security.authorization.method.ExpressionAttribute;
import org.springframework.security.authorization.method.PostFilterExpressionAttributeRegistry;
import org.springframework.security.authorization.method.ReactiveAuthenticationUtils;
import org.springframework.security.authorization.method.ReactiveExpressionUtils;
import org.springframework.security.authorization.method.ReactiveMethodInvocationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class PostFilterAuthorizationReactiveMethodInterceptor
implements Ordered,
MethodInterceptor,
PointcutAdvisor,
AopInfrastructureBean {
    private final PostFilterExpressionAttributeRegistry registry;
    private final Pointcut pointcut = AuthorizationMethodPointcuts.forAnnotations(PostFilter.class);
    private int order = AuthorizationInterceptorsOrder.POST_FILTER.getOrder();

    public PostFilterAuthorizationReactiveMethodInterceptor() {
        this(new DefaultMethodSecurityExpressionHandler());
    }

    public PostFilterAuthorizationReactiveMethodInterceptor(MethodSecurityExpressionHandler expressionHandler) {
        Assert.notNull((Object)expressionHandler, (String)"expressionHandler cannot be null");
        this.registry = new PostFilterExpressionAttributeRegistry(expressionHandler);
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        Object attribute = this.registry.getAttribute(mi);
        if (attribute == ExpressionAttribute.NULL_ATTRIBUTE) {
            return ReactiveMethodInvocationUtils.proceed(mi);
        }
        Mono toInvoke = ReactiveAuthenticationUtils.getAuthentication().map(auth -> this.registry.getExpressionHandler().createEvaluationContext((Authentication)auth, mi));
        Method method = mi.getMethod();
        Class<?> type = method.getReturnType();
        Assert.state((boolean)Publisher.class.isAssignableFrom(type), () -> String.format("The parameter type %s on %s must be an instance of org.reactivestreams.Publisher (for example, a Mono or Flux) in order to support Reactor Context", type, method));
        ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(type);
        if (this.isMultiValue(type, adapter)) {
            Flux publisher = Flux.defer(() -> (Publisher)ReactiveMethodInvocationUtils.proceed(mi));
            Flux flux = toInvoke.flatMapMany(arg_0 -> this.lambda$invoke$3((Publisher)publisher, attribute, arg_0));
            return adapter != null ? adapter.fromPublisher((Publisher)flux) : flux;
        }
        Mono publisher = Mono.defer(() -> (Mono)ReactiveMethodInvocationUtils.proceed(mi));
        Mono mono = toInvoke.flatMap(arg_0 -> this.lambda$invoke$5((Publisher)publisher, attribute, arg_0));
        return adapter != null ? adapter.fromPublisher((Publisher)mono) : mono;
    }

    private boolean isMultiValue(Class<?> returnType, ReactiveAdapter adapter) {
        if (Flux.class.isAssignableFrom(returnType)) {
            return true;
        }
        return adapter == null || adapter.isMultiValue();
    }

    private Mono<?> filterSingleValue(Publisher<?> publisher, EvaluationContext ctx, ExpressionAttribute attribute) {
        return Mono.from(publisher).doOnNext(result -> this.setFilterObject(ctx, result)).flatMap(result -> this.postFilter(ctx, result, attribute));
    }

    private Flux<?> filterMultiValue(Publisher<?> publisher, EvaluationContext ctx, ExpressionAttribute attribute) {
        return Flux.from(publisher).doOnNext(result -> this.setFilterObject(ctx, result)).flatMap(result -> this.postFilter(ctx, result, attribute));
    }

    private void setFilterObject(EvaluationContext ctx, Object result) {
        ((MethodSecurityExpressionOperations)ctx.getRootObject().getValue()).setFilterObject(result);
    }

    private Mono<?> postFilter(EvaluationContext ctx, Object result, ExpressionAttribute attribute) {
        return ReactiveExpressionUtils.evaluateAsBoolean(attribute.getExpression(), ctx).flatMap(granted -> granted != false ? Mono.just((Object)result) : Mono.empty());
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

    private /* synthetic */ Mono lambda$invoke$5(Publisher publisher, ExpressionAttribute attribute, EvaluationContext ctx) {
        return this.filterSingleValue(publisher, ctx, attribute);
    }

    private /* synthetic */ Publisher lambda$invoke$3(Publisher publisher, ExpressionAttribute attribute, EvaluationContext ctx) {
        return this.filterMultiValue(publisher, ctx, attribute);
    }
}

