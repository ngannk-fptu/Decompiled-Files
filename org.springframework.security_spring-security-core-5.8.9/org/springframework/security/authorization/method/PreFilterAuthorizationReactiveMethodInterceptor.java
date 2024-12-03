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
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.core.Ordered
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
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
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationMethodPointcuts;
import org.springframework.security.authorization.method.PreFilterExpressionAttributeRegistry;
import org.springframework.security.authorization.method.ReactiveAuthenticationUtils;
import org.springframework.security.authorization.method.ReactiveExpressionUtils;
import org.springframework.security.authorization.method.ReactiveMethodInvocationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.DefaultSecurityParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class PreFilterAuthorizationReactiveMethodInterceptor
implements Ordered,
MethodInterceptor,
PointcutAdvisor,
AopInfrastructureBean {
    private final PreFilterExpressionAttributeRegistry registry;
    private final Pointcut pointcut = AuthorizationMethodPointcuts.forAnnotations(PreFilter.class);
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultSecurityParameterNameDiscoverer();
    private int order = AuthorizationInterceptorsOrder.PRE_FILTER.getOrder();

    public PreFilterAuthorizationReactiveMethodInterceptor() {
        this(new DefaultMethodSecurityExpressionHandler());
    }

    public PreFilterAuthorizationReactiveMethodInterceptor(MethodSecurityExpressionHandler expressionHandler) {
        Assert.notNull((Object)expressionHandler, (String)"expressionHandler cannot be null");
        this.registry = new PreFilterExpressionAttributeRegistry(expressionHandler);
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        Assert.notNull((Object)parameterNameDiscoverer, (String)"parameterNameDiscoverer cannot be null");
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        PreFilterExpressionAttributeRegistry.PreFilterExpressionAttribute attribute = (PreFilterExpressionAttributeRegistry.PreFilterExpressionAttribute)this.registry.getAttribute(mi);
        if (attribute == PreFilterExpressionAttributeRegistry.PreFilterExpressionAttribute.NULL_ATTRIBUTE) {
            return ReactiveMethodInvocationUtils.proceed(mi);
        }
        FilterTarget filterTarget = this.findFilterTarget(attribute.getFilterTarget(), mi);
        Mono toInvoke = ReactiveAuthenticationUtils.getAuthentication().map(auth -> this.registry.getExpressionHandler().createEvaluationContext((Authentication)auth, mi));
        Method method = mi.getMethod();
        Class<?> type = filterTarget.value.getClass();
        Assert.state((boolean)Publisher.class.isAssignableFrom(type), () -> String.format("The parameter type %s on %s must be an instance of org.reactivestreams.Publisher (for example, a Mono or Flux) in order to support Reactor Context", type, method));
        ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(type);
        if (this.isMultiValue(type, adapter)) {
            Flux result = toInvoke.flatMapMany(ctx -> this.filterMultiValue((Publisher<?>)filterTarget.value, attribute.getExpression(), (EvaluationContext)ctx));
            mi.getArguments()[((FilterTarget)filterTarget).index] = adapter != null ? adapter.fromPublisher((Publisher)result) : result;
        } else {
            Mono result = toInvoke.flatMap(ctx -> this.filterSingleValue((Publisher<?>)filterTarget.value, attribute.getExpression(), (EvaluationContext)ctx));
            mi.getArguments()[((FilterTarget)filterTarget).index] = adapter != null ? adapter.fromPublisher((Publisher)result) : result;
        }
        return ReactiveMethodInvocationUtils.proceed(mi);
    }

    private FilterTarget findFilterTarget(String name, MethodInvocation mi) {
        Object value = null;
        int index = 0;
        if (StringUtils.hasText((String)name)) {
            Object target = mi.getThis();
            Class targetClass = target != null ? AopUtils.getTargetClass((Object)target) : null;
            Method specificMethod = AopUtils.getMostSpecificMethod((Method)mi.getMethod(), (Class)targetClass);
            String[] parameterNames = this.parameterNameDiscoverer.getParameterNames(specificMethod);
            if (parameterNames != null && parameterNames.length > 0) {
                Object[] arguments = mi.getArguments();
                for (index = 0; index < parameterNames.length; ++index) {
                    if (!name.equals(parameterNames[index])) continue;
                    value = arguments[index];
                    break;
                }
                Assert.notNull((Object)value, (String)("Filter target was null, or no argument with name '" + name + "' found in method."));
            }
        } else {
            Object[] arguments = mi.getArguments();
            Assert.state((arguments.length == 1 ? 1 : 0) != 0, (String)"Unable to determine the method argument for filtering. Specify the filter target.");
            value = arguments[0];
            Assert.notNull((Object)value, (String)"Filter target was null. Make sure you passing the correct value in the method argument.");
        }
        Assert.state((boolean)(value instanceof Publisher), (String)"Filter target must be an instance of Publisher.");
        return new FilterTarget((Publisher)value, index);
    }

    private boolean isMultiValue(Class<?> returnType, ReactiveAdapter adapter) {
        if (Flux.class.isAssignableFrom(returnType)) {
            return true;
        }
        return adapter == null || adapter.isMultiValue();
    }

    private Mono<?> filterSingleValue(Publisher<?> filterTarget, Expression filterExpression, EvaluationContext ctx) {
        MethodSecurityExpressionOperations rootObject = (MethodSecurityExpressionOperations)ctx.getRootObject().getValue();
        return Mono.from(filterTarget).filterWhen(filterObject -> {
            rootObject.setFilterObject(filterObject);
            return ReactiveExpressionUtils.evaluateAsBoolean(filterExpression, ctx);
        });
    }

    private Flux<?> filterMultiValue(Publisher<?> filterTarget, Expression filterExpression, EvaluationContext ctx) {
        MethodSecurityExpressionOperations rootObject = (MethodSecurityExpressionOperations)ctx.getRootObject().getValue();
        return Flux.from(filterTarget).filterWhen(filterObject -> {
            rootObject.setFilterObject(filterObject);
            return ReactiveExpressionUtils.evaluateAsBoolean(filterExpression, ctx);
        });
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

    private static final class FilterTarget {
        private final Publisher<?> value;
        private final int index;

        private FilterTarget(Publisher<?> value, int index) {
            this.value = value;
            this.index = index;
        }
    }
}

