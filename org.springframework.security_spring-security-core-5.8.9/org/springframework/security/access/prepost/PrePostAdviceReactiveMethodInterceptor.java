/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.coroutines.Continuation
 *  kotlinx.coroutines.reactive.AwaitKt
 *  kotlinx.coroutines.reactive.ReactiveFlowKt
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.reactivestreams.Publisher
 *  org.springframework.core.CoroutinesUtils
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.util.Assert
 *  reactor.core.Exceptions
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.access.prepost;

import java.lang.reflect.Method;
import java.util.Collection;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.reactive.AwaitKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.reactivestreams.Publisher;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PostInvocationAuthorizationAdvice;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.Assert;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Deprecated
public class PrePostAdviceReactiveMethodInterceptor
implements MethodInterceptor {
    private Authentication anonymous = new AnonymousAuthenticationToken("key", (Object)"anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    private final MethodSecurityMetadataSource attributeSource;
    private final PreInvocationAuthorizationAdvice preInvocationAdvice;
    private final PostInvocationAuthorizationAdvice postAdvice;
    private static final String COROUTINES_FLOW_CLASS_NAME = "kotlinx.coroutines.flow.Flow";
    private static final int RETURN_TYPE_METHOD_PARAMETER_INDEX = -1;

    public PrePostAdviceReactiveMethodInterceptor(MethodSecurityMetadataSource attributeSource, PreInvocationAuthorizationAdvice preInvocationAdvice, PostInvocationAuthorizationAdvice postInvocationAdvice) {
        Assert.notNull((Object)attributeSource, (String)"attributeSource cannot be null");
        Assert.notNull((Object)preInvocationAdvice, (String)"preInvocationAdvice cannot be null");
        Assert.notNull((Object)postInvocationAdvice, (String)"postInvocationAdvice cannot be null");
        this.attributeSource = attributeSource;
        this.preInvocationAdvice = preInvocationAdvice;
        this.postAdvice = postInvocationAdvice;
    }

    public Object invoke(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Class<?> returnType = method.getReturnType();
        boolean isSuspendingFunction = KotlinDetector.isSuspendingFunction((Method)method);
        boolean hasFlowReturnType = COROUTINES_FLOW_CLASS_NAME.equals(new MethodParameter(method, -1).getParameterType().getName());
        boolean hasReactiveReturnType = Publisher.class.isAssignableFrom(returnType) || isSuspendingFunction || hasFlowReturnType;
        Assert.state((boolean)hasReactiveReturnType, () -> "The returnType " + returnType + " on " + method + " must return an instance of org.reactivestreams.Publisher (i.e. Mono / Flux) or the function must be a Kotlin coroutine function in order to support Reactor Context");
        Class<?> targetClass = invocation.getThis().getClass();
        Collection<ConfigAttribute> attributes = this.attributeSource.getAttributes(method, targetClass);
        PreInvocationAttribute preAttr = PrePostAdviceReactiveMethodInterceptor.findPreInvocationAttribute(attributes);
        Mono toInvoke = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).defaultIfEmpty((Object)this.anonymous).filter(auth -> this.preInvocationAdvice.before((Authentication)auth, invocation, preAttr)).switchIfEmpty(Mono.defer(() -> Mono.error((Throwable)new AccessDeniedException("Denied"))));
        PostInvocationAttribute attr = PrePostAdviceReactiveMethodInterceptor.findPostInvocationAttribute(attributes);
        if (Mono.class.isAssignableFrom(returnType)) {
            return toInvoke.flatMap(auth -> ((Mono)PrePostAdviceReactiveMethodInterceptor.proceed(invocation)).map(r -> attr != null ? this.postAdvice.after((Authentication)auth, invocation, attr, r) : r));
        }
        if (Flux.class.isAssignableFrom(returnType)) {
            return toInvoke.flatMapMany(auth -> ((Flux)PrePostAdviceReactiveMethodInterceptor.proceed(invocation)).map(r -> attr != null ? this.postAdvice.after((Authentication)auth, invocation, attr, r) : r));
        }
        if (hasFlowReturnType) {
            Flux response;
            if (isSuspendingFunction) {
                response = toInvoke.flatMapMany(auth -> Flux.from((Publisher)CoroutinesUtils.invokeSuspendingFunction((Method)invocation.getMethod(), (Object)invocation.getThis(), (Object[])invocation.getArguments())).map(r -> attr != null ? this.postAdvice.after((Authentication)auth, invocation, attr, r) : r));
            } else {
                ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(returnType);
                Assert.state((adapter != null ? 1 : 0) != 0, () -> "The returnType " + returnType + " on " + method + " must have a org.springframework.core.ReactiveAdapter registered");
                response = toInvoke.flatMapMany(auth -> Flux.from((Publisher)adapter.toPublisher(PrePostAdviceReactiveMethodInterceptor.flowProceed(invocation))).map(r -> attr != null ? this.postAdvice.after((Authentication)auth, invocation, attr, r) : r));
            }
            return KotlinDelegate.asFlow((Publisher)response);
        }
        if (isSuspendingFunction) {
            Mono response = toInvoke.flatMap(auth -> Mono.from((Publisher)CoroutinesUtils.invokeSuspendingFunction((Method)invocation.getMethod(), (Object)invocation.getThis(), (Object[])invocation.getArguments())).map(r -> attr != null ? this.postAdvice.after((Authentication)auth, invocation, attr, r) : r));
            return KotlinDelegate.awaitSingleOrNull((Publisher)response, invocation.getArguments()[invocation.getArguments().length - 1]);
        }
        return toInvoke.flatMapMany(auth -> Flux.from(PrePostAdviceReactiveMethodInterceptor.proceed(invocation)).map(r -> attr != null ? this.postAdvice.after((Authentication)auth, invocation, attr, r) : r));
    }

    private static <T extends Publisher<?>> T proceed(MethodInvocation invocation) {
        try {
            return (T)((Publisher)invocation.proceed());
        }
        catch (Throwable throwable) {
            throw Exceptions.propagate((Throwable)throwable);
        }
    }

    private static Object flowProceed(MethodInvocation invocation) {
        try {
            return invocation.proceed();
        }
        catch (Throwable throwable) {
            throw Exceptions.propagate((Throwable)throwable);
        }
    }

    private static PostInvocationAttribute findPostInvocationAttribute(Collection<ConfigAttribute> config) {
        for (ConfigAttribute attribute : config) {
            if (!(attribute instanceof PostInvocationAttribute)) continue;
            return (PostInvocationAttribute)attribute;
        }
        return null;
    }

    private static PreInvocationAttribute findPreInvocationAttribute(Collection<ConfigAttribute> config) {
        for (ConfigAttribute attribute : config) {
            if (!(attribute instanceof PreInvocationAttribute)) continue;
            return (PreInvocationAttribute)attribute;
        }
        return null;
    }

    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        private static Object asFlow(Publisher<?> publisher) {
            return ReactiveFlowKt.asFlow(publisher);
        }

        private static Object awaitSingleOrNull(Publisher<?> publisher, Object continuation) {
            return AwaitKt.awaitSingleOrNull(publisher, (Continuation)((Continuation)continuation));
        }
    }
}

