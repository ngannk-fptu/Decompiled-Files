/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.util.AnnotationDetectionMethodCallback;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

public class EventPublishingRepositoryProxyPostProcessor
implements RepositoryProxyPostProcessor {
    private final ApplicationEventPublisher publisher;

    public EventPublishingRepositoryProxyPostProcessor(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        EventPublishingMethod method = EventPublishingMethod.of(repositoryInformation.getDomainType());
        if (method == null) {
            return;
        }
        factory.addAdvice((Advice)new EventPublishingMethodInterceptor(method, this.publisher));
    }

    private static boolean isEventPublishingMethod(Method method) {
        return method.getParameterCount() == 1 && (EventPublishingRepositoryProxyPostProcessor.isSaveMethod(method.getName()) || EventPublishingRepositoryProxyPostProcessor.isDeleteMethod(method.getName()));
    }

    private static boolean isSaveMethod(String methodName) {
        return methodName.startsWith("save");
    }

    private static boolean isDeleteMethod(String methodName) {
        return methodName.equals("delete") || methodName.equals("deleteAll");
    }

    static class EventPublishingMethod {
        private static Map<Class<?>, EventPublishingMethod> cache = new ConcurrentReferenceHashMap();
        private static EventPublishingMethod NONE = new EventPublishingMethod(null, null);
        private final Method publishingMethod;
        @Nullable
        private final Method clearingMethod;

        EventPublishingMethod(Method publishingMethod, Method clearingMethod) {
            this.publishingMethod = publishingMethod;
            this.clearingMethod = clearingMethod;
        }

        @Nullable
        public static EventPublishingMethod of(Class<?> type) {
            Assert.notNull(type, (String)"Type must not be null!");
            EventPublishingMethod eventPublishingMethod = cache.get(type);
            if (eventPublishingMethod != null) {
                return eventPublishingMethod.orNull();
            }
            EventPublishingMethod result = EventPublishingMethod.from(EventPublishingMethod.getDetector(type, DomainEvents.class), () -> EventPublishingMethod.getDetector(type, AfterDomainEventPublication.class));
            cache.put(type, result);
            return result.orNull();
        }

        public void publishEventsFrom(@Nullable Object object, ApplicationEventPublisher publisher) {
            if (object == null) {
                return;
            }
            for (Object aggregateRoot : EventPublishingMethod.asCollection(object)) {
                for (Object event : EventPublishingMethod.asCollection(ReflectionUtils.invokeMethod((Method)this.publishingMethod, (Object)aggregateRoot))) {
                    publisher.publishEvent(event);
                }
                if (this.clearingMethod == null) continue;
                ReflectionUtils.invokeMethod((Method)this.clearingMethod, (Object)aggregateRoot);
            }
        }

        @Nullable
        private EventPublishingMethod orNull() {
            return this == NONE ? null : this;
        }

        private static <T extends Annotation> AnnotationDetectionMethodCallback<T> getDetector(Class<?> type, Class<T> annotation) {
            AnnotationDetectionMethodCallback<T> callback = new AnnotationDetectionMethodCallback<T>(annotation);
            ReflectionUtils.doWithMethods(type, callback);
            return callback;
        }

        private static EventPublishingMethod from(AnnotationDetectionMethodCallback<?> publishing, Supplier<AnnotationDetectionMethodCallback<?>> clearing) {
            if (!publishing.hasFoundAnnotation()) {
                return NONE;
            }
            Method eventMethod = publishing.getRequiredMethod();
            ReflectionUtils.makeAccessible((Method)eventMethod);
            return new EventPublishingMethod(eventMethod, EventPublishingMethod.getClearingMethod(clearing.get()));
        }

        @Nullable
        private static Method getClearingMethod(AnnotationDetectionMethodCallback<?> clearing) {
            if (!clearing.hasFoundAnnotation()) {
                return null;
            }
            Method method = clearing.getRequiredMethod();
            ReflectionUtils.makeAccessible((Method)method);
            return method;
        }

        private static Collection<Object> asCollection(@Nullable Object source) {
            if (source == null) {
                return Collections.emptyList();
            }
            if (Collection.class.isInstance(source)) {
                return (Collection)source;
            }
            return Collections.singletonList(source);
        }
    }

    static class EventPublishingMethodInterceptor
    implements MethodInterceptor {
        private final EventPublishingMethod eventMethod;
        private final ApplicationEventPublisher publisher;

        private EventPublishingMethodInterceptor(EventPublishingMethod eventMethod, ApplicationEventPublisher publisher) {
            this.eventMethod = eventMethod;
            this.publisher = publisher;
        }

        public static EventPublishingMethodInterceptor of(EventPublishingMethod eventMethod, ApplicationEventPublisher publisher) {
            return new EventPublishingMethodInterceptor(eventMethod, publisher);
        }

        @Nullable
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Object result = invocation.proceed();
            if (!EventPublishingRepositoryProxyPostProcessor.isEventPublishingMethod(invocation.getMethod())) {
                return result;
            }
            Object[] arguments = invocation.getArguments();
            this.eventMethod.publishEventsFrom(arguments[0], this.publisher);
            return result;
        }
    }
}

