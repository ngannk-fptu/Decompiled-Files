/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Publisher
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.core.ResolvableType
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ReflectionUtils
 *  reactor.core.publisher.Mono
 */
package org.springframework.data.mapping.callback;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.data.mapping.callback.EntityCallbackDiscoverer;
import org.springframework.data.mapping.callback.EntityCallbackInvoker;
import org.springframework.data.mapping.callback.ReactiveEntityCallbackInvoker;
import org.springframework.data.mapping.callback.ReactiveEntityCallbacks;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

class DefaultReactiveEntityCallbacks
implements ReactiveEntityCallbacks {
    private final Map<Class<?>, Method> callbackMethodCache = new ConcurrentReferenceHashMap(64);
    private final ReactiveEntityCallbackInvoker callbackInvoker = new DefaultReactiveEntityCallbackInvoker();
    private final EntityCallbackDiscoverer callbackDiscoverer;

    DefaultReactiveEntityCallbacks() {
        this.callbackDiscoverer = new EntityCallbackDiscoverer();
    }

    DefaultReactiveEntityCallbacks(BeanFactory beanFactory) {
        this.callbackDiscoverer = new EntityCallbackDiscoverer(beanFactory);
    }

    @Override
    public <T> Mono<T> callback(Class<? extends EntityCallback> callbackType, T entity, Object ... args) {
        Assert.notNull(entity, (String)"Entity must not be null!");
        Class entityType = entity != null ? ClassUtils.getUserClass(entity.getClass()) : this.callbackDiscoverer.resolveDeclaredEntityType(callbackType).getRawClass();
        Method callbackMethod = this.callbackMethodCache.computeIfAbsent(callbackType, it -> {
            Method method = EntityCallbackDiscoverer.lookupCallbackMethod(it, entityType, args);
            ReflectionUtils.makeAccessible((Method)method);
            return method;
        });
        Mono deferredCallbackChain = Mono.just(entity);
        for (EntityCallback callback : this.callbackDiscoverer.getEntityCallbacks(entityType, ResolvableType.forClass(callbackType))) {
            BiFunction callbackFunction = EntityCallbackDiscoverer.computeCallbackInvokerFunction(callback, callbackMethod, args);
            deferredCallbackChain = deferredCallbackChain.flatMap(it -> this.callbackInvoker.invokeCallback(callback, it, callbackFunction));
        }
        return deferredCallbackChain;
    }

    @Override
    public void addEntityCallback(EntityCallback<?> callback) {
        this.callbackDiscoverer.addEntityCallback(callback);
    }

    static class DefaultReactiveEntityCallbackInvoker
    implements ReactiveEntityCallbackInvoker {
        DefaultReactiveEntityCallbackInvoker() {
        }

        @Override
        public <T> Mono<T> invokeCallback(EntityCallback<T> callback, T entity, BiFunction<EntityCallback<T>, T, Object> callbackInvokerFunction) {
            try {
                Object value = callbackInvokerFunction.apply((EntityCallback<EntityCallback<T>>)callback, (EntityCallback<T>)entity);
                if (value != null) {
                    return value instanceof Publisher ? Mono.from((Publisher)((Publisher)value)) : Mono.just((Object)value);
                }
                throw new IllegalArgumentException(String.format("Callback invocation on %s returned null value for %s", callback.getClass(), entity));
            }
            catch (ClassCastException ex) {
                String msg = ex.getMessage();
                if (msg == null || EntityCallbackInvoker.matchesClassCastMessage(msg, entity.getClass())) {
                    Log logger = LogFactory.getLog(this.getClass());
                    if (logger.isDebugEnabled()) {
                        logger.debug((Object)("Non-matching callback type for entity callback: " + callback), (Throwable)ex);
                    }
                    return Mono.just(entity);
                }
                return Mono.error((Throwable)ex);
            }
        }
    }
}

