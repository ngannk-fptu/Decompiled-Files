/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.core.BridgeMethodResolver
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.Order
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.concurrent.ListenableFuture
 */
package org.springframework.context.event;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletionStage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventExpressionEvaluator;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

public class ApplicationListenerMethodAdapter
implements GenericApplicationListener {
    private static final boolean reactiveStreamsPresent = ClassUtils.isPresent((String)"org.reactivestreams.Publisher", (ClassLoader)ApplicationListenerMethodAdapter.class.getClassLoader());
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final String beanName;
    private final Method method;
    private final Method targetMethod;
    private final AnnotatedElementKey methodKey;
    private final List<ResolvableType> declaredEventTypes;
    @Nullable
    private final String condition;
    private final int order;
    @Nullable
    private volatile String listenerId;
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private EventExpressionEvaluator evaluator;

    public ApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        this.beanName = beanName;
        this.method = BridgeMethodResolver.findBridgedMethod((Method)method);
        this.targetMethod = !Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod((Method)method, targetClass) : this.method;
        this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
        EventListener ann = (EventListener)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)this.targetMethod, EventListener.class);
        this.declaredEventTypes = ApplicationListenerMethodAdapter.resolveDeclaredEventTypes(method, ann);
        this.condition = ann != null ? ann.condition() : null;
        this.order = ApplicationListenerMethodAdapter.resolveOrder(this.targetMethod);
        String id = ann != null ? ann.id() : "";
        this.listenerId = !id.isEmpty() ? id : null;
    }

    private static List<ResolvableType> resolveDeclaredEventTypes(Method method, @Nullable EventListener ann) {
        Class<?>[] classes;
        int count = method.getParameterCount();
        if (count > 1) {
            throw new IllegalStateException("Maximum one parameter is allowed for event listener method: " + method);
        }
        if (ann != null && (classes = ann.classes()).length > 0) {
            ArrayList<ResolvableType> types = new ArrayList<ResolvableType>(classes.length);
            for (Class<?> eventType : classes) {
                types.add(ResolvableType.forClass(eventType));
            }
            return types;
        }
        if (count == 0) {
            throw new IllegalStateException("Event parameter is mandatory for event listener method: " + method);
        }
        return Collections.singletonList(ResolvableType.forMethodParameter((Method)method, (int)0));
    }

    private static int resolveOrder(Method method) {
        Order ann = (Order)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)method, Order.class);
        return ann != null ? ann.value() : Integer.MAX_VALUE;
    }

    void init(ApplicationContext applicationContext, @Nullable EventExpressionEvaluator evaluator) {
        this.applicationContext = applicationContext;
        this.evaluator = evaluator;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        this.processEvent(event);
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        for (ResolvableType declaredEventType : this.declaredEventTypes) {
            ResolvableType payloadType;
            if (declaredEventType.isAssignableFrom(eventType)) {
                return true;
            }
            if (!PayloadApplicationEvent.class.isAssignableFrom(eventType.toClass()) || !declaredEventType.isAssignableFrom(payloadType = eventType.as(PayloadApplicationEvent.class).getGeneric(new int[0]))) continue;
            return true;
        }
        return eventType.hasUnresolvableGenerics();
    }

    @Override
    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return true;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public String getListenerId() {
        String id = this.listenerId;
        if (id == null) {
            this.listenerId = id = this.getDefaultListenerId();
        }
        return id;
    }

    protected String getDefaultListenerId() {
        Method method = this.getTargetMethod();
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (Class<?> paramType : method.getParameterTypes()) {
            sj.add(paramType.getName());
        }
        return ClassUtils.getQualifiedMethodName((Method)method) + sj.toString();
    }

    public void processEvent(ApplicationEvent event) {
        Object[] args = this.resolveArguments(event);
        if (this.shouldHandle(event, args)) {
            Object result = this.doInvoke(args);
            if (result != null) {
                this.handleResult(result);
            } else {
                this.logger.trace((Object)"No result object given - no result to handle");
            }
        }
    }

    @Nullable
    protected Object[] resolveArguments(ApplicationEvent event) {
        Object payload;
        ResolvableType declaredEventType = this.getResolvableType(event);
        if (declaredEventType == null) {
            return null;
        }
        if (this.method.getParameterCount() == 0) {
            return new Object[0];
        }
        Class declaredEventClass = declaredEventType.toClass();
        if (!ApplicationEvent.class.isAssignableFrom(declaredEventClass) && event instanceof PayloadApplicationEvent && declaredEventClass.isInstance(payload = ((PayloadApplicationEvent)event).getPayload())) {
            return new Object[]{payload};
        }
        return new Object[]{event};
    }

    protected void handleResult(Object result) {
        if (reactiveStreamsPresent && new ReactiveResultHandler().subscribeToPublisher(result)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Adapted to reactive result: " + result));
            }
        } else if (result instanceof CompletionStage) {
            ((CompletionStage)result).whenComplete((event, ex) -> {
                if (ex != null) {
                    this.handleAsyncError((Throwable)ex);
                } else if (event != null) {
                    this.publishEvents(event);
                }
            });
        } else if (result instanceof ListenableFuture) {
            ((ListenableFuture)result).addCallback(this::publishEvents, this::handleAsyncError);
        } else {
            this.publishEvents(result);
        }
    }

    private void publishEvents(Object result) {
        if (result.getClass().isArray()) {
            Object[] events;
            for (Object event : events = ObjectUtils.toObjectArray((Object)result)) {
                this.publishEvent(event);
            }
        } else if (result instanceof Collection) {
            Collection events = (Collection)result;
            for (Object event : events) {
                this.publishEvent(event);
            }
        } else {
            this.publishEvent(result);
        }
    }

    private void publishEvent(@Nullable Object event) {
        if (event != null) {
            Assert.notNull((Object)this.applicationContext, (String)"ApplicationContext must not be null");
            this.applicationContext.publishEvent(event);
        }
    }

    protected void handleAsyncError(Throwable t) {
        this.logger.error((Object)"Unexpected error occurred in asynchronous listener", t);
    }

    private boolean shouldHandle(ApplicationEvent event, @Nullable Object[] args) {
        if (args == null) {
            return false;
        }
        String condition = this.getCondition();
        if (StringUtils.hasText((String)condition)) {
            Assert.notNull((Object)this.evaluator, (String)"EventExpressionEvaluator must not be null");
            return this.evaluator.condition(condition, event, this.targetMethod, this.methodKey, args, (BeanFactory)this.applicationContext);
        }
        return true;
    }

    @Nullable
    protected Object doInvoke(Object ... args) {
        Object bean2 = this.getTargetBean();
        if (bean2.equals(null)) {
            return null;
        }
        ReflectionUtils.makeAccessible((Method)this.method);
        try {
            return this.method.invoke(bean2, args);
        }
        catch (IllegalArgumentException ex) {
            this.assertTargetBean(this.method, bean2, args);
            throw new IllegalStateException(this.getInvocationErrorMessage(bean2, ex.getMessage(), args), ex);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(this.getInvocationErrorMessage(bean2, ex.getMessage(), args), ex);
        }
        catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            }
            String msg = this.getInvocationErrorMessage(bean2, "Failed to invoke event listener method", args);
            throw new UndeclaredThrowableException(targetException, msg);
        }
    }

    protected Object getTargetBean() {
        Assert.notNull((Object)this.applicationContext, (String)"ApplicationContext must not be null");
        return this.applicationContext.getBean(this.beanName);
    }

    protected Method getTargetMethod() {
        return this.targetMethod;
    }

    @Nullable
    protected String getCondition() {
        return this.condition;
    }

    protected String getDetailedErrorMessage(Object bean2, String message) {
        StringBuilder sb = new StringBuilder(message).append('\n');
        sb.append("HandlerMethod details: \n");
        sb.append("Bean [").append(bean2.getClass().getName()).append("]\n");
        sb.append("Method [").append(this.method.toGenericString()).append("]\n");
        return sb.toString();
    }

    private void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> targetBeanClass;
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass = targetBean.getClass())) {
            String msg = "The event listener method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual bean class '" + targetBeanClass.getName() + "'. If the bean requires proxying (e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(this.getInvocationErrorMessage(targetBean, msg, args));
        }
    }

    private String getInvocationErrorMessage(Object bean2, String message, Object[] resolvedArgs) {
        StringBuilder sb = new StringBuilder(this.getDetailedErrorMessage(bean2, message));
        sb.append("Resolved arguments: \n");
        for (int i = 0; i < resolvedArgs.length; ++i) {
            sb.append('[').append(i).append("] ");
            if (resolvedArgs[i] == null) {
                sb.append("[null] \n");
                continue;
            }
            sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
            sb.append("[value=").append(resolvedArgs[i]).append("]\n");
        }
        return sb.toString();
    }

    @Nullable
    private ResolvableType getResolvableType(ApplicationEvent event) {
        PayloadApplicationEvent payloadEvent;
        ResolvableType eventType;
        ResolvableType payloadType = null;
        if (event instanceof PayloadApplicationEvent && (eventType = (payloadEvent = (PayloadApplicationEvent)event).getResolvableType()) != null) {
            payloadType = eventType.as(PayloadApplicationEvent.class).getGeneric(new int[0]);
        }
        for (ResolvableType declaredEventType : this.declaredEventTypes) {
            Class eventClass = declaredEventType.toClass();
            if (!ApplicationEvent.class.isAssignableFrom(eventClass) && payloadType != null && declaredEventType.isAssignableFrom(payloadType)) {
                return declaredEventType;
            }
            if (!eventClass.isInstance(event)) continue;
            return declaredEventType;
        }
        return null;
    }

    public String toString() {
        return this.method.toGenericString();
    }

    private class EventPublicationSubscriber
    implements Subscriber<Object> {
        private EventPublicationSubscriber() {
        }

        public void onSubscribe(Subscription s) {
            s.request(Integer.MAX_VALUE);
        }

        public void onNext(Object o) {
            ApplicationListenerMethodAdapter.this.publishEvents(o);
        }

        public void onError(Throwable t) {
            ApplicationListenerMethodAdapter.this.handleAsyncError(t);
        }

        public void onComplete() {
        }
    }

    private class ReactiveResultHandler {
        private ReactiveResultHandler() {
        }

        public boolean subscribeToPublisher(Object result) {
            ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(result.getClass());
            if (adapter != null) {
                adapter.toPublisher(result).subscribe((Subscriber)new EventPublicationSubscriber());
                return true;
            }
            return false;
        }
    }
}

