/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventExpressionEvaluator;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class ApplicationListenerMethodAdapter
implements GenericApplicationListener {
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
    private ApplicationContext applicationContext;
    @Nullable
    private EventExpressionEvaluator evaluator;

    public ApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        this.beanName = beanName;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.targetMethod = !Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : this.method;
        this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
        EventListener ann = AnnotatedElementUtils.findMergedAnnotation(this.targetMethod, EventListener.class);
        this.declaredEventTypes = this.resolveDeclaredEventTypes(method, ann);
        this.condition = ann != null ? ann.condition() : null;
        this.order = this.resolveOrder(method);
    }

    private List<ResolvableType> resolveDeclaredEventTypes(Method method, @Nullable EventListener ann) {
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
        return Collections.singletonList(ResolvableType.forMethodParameter(method, 0));
    }

    private int resolveOrder(Method method) {
        Order ann = AnnotatedElementUtils.findMergedAnnotation(method, Order.class);
        return ann != null ? ann.value() : 0;
    }

    void init(ApplicationContext applicationContext, EventExpressionEvaluator evaluator) {
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
            Class<?> eventClass = eventType.getRawClass();
            if (eventClass == null || !PayloadApplicationEvent.class.isAssignableFrom(eventClass) || !declaredEventType.isAssignableFrom(payloadType = eventType.as(PayloadApplicationEvent.class).getGeneric(new int[0]))) continue;
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

    public void processEvent(ApplicationEvent event) {
        Object[] args = this.resolveArguments(event);
        if (this.shouldHandle(event, args)) {
            Object result = this.doInvoke(args);
            if (result != null) {
                this.handleResult(result);
            } else {
                this.logger.trace("No result object given - no result to handle");
            }
        }
    }

    @Nullable
    protected Object[] resolveArguments(ApplicationEvent event) {
        ResolvableType declaredEventType = this.getResolvableType(event);
        if (declaredEventType == null) {
            return null;
        }
        if (this.method.getParameterCount() == 0) {
            return new Object[0];
        }
        Class<?> eventClass = declaredEventType.getRawClass();
        if ((eventClass == null || !ApplicationEvent.class.isAssignableFrom(eventClass)) && event instanceof PayloadApplicationEvent) {
            return new Object[]{((PayloadApplicationEvent)event).getPayload()};
        }
        return new Object[]{event};
    }

    protected void handleResult(Object result) {
        if (result.getClass().isArray()) {
            Object[] events;
            for (Object event : events = ObjectUtils.toObjectArray(result)) {
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
            Assert.notNull((Object)this.applicationContext, "ApplicationContext must not be null");
            this.applicationContext.publishEvent(event);
        }
    }

    private boolean shouldHandle(ApplicationEvent event, @Nullable Object[] args) {
        if (args == null) {
            return false;
        }
        String condition = this.getCondition();
        if (StringUtils.hasText(condition)) {
            Assert.notNull((Object)this.evaluator, "EventExpressionEvaluator must not be null");
            return this.evaluator.condition(condition, event, this.targetMethod, this.methodKey, args, this.applicationContext);
        }
        return true;
    }

    @Nullable
    protected Object doInvoke(Object ... args) {
        Object bean2 = this.getTargetBean();
        ReflectionUtils.makeAccessible(this.method);
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
        Assert.notNull((Object)this.applicationContext, "ApplicationContext must no be null");
        return this.applicationContext.getBean(this.beanName);
    }

    @Nullable
    protected String getCondition() {
        return this.condition;
    }

    protected String getDetailedErrorMessage(Object bean2, String message) {
        StringBuilder sb = new StringBuilder(message).append("\n");
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
            sb.append("[").append(i).append("] ");
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
            Class<?> eventClass = declaredEventType.getRawClass();
            if ((eventClass == null || !ApplicationEvent.class.isAssignableFrom(eventClass)) && payloadType != null && declaredEventType.isAssignableFrom(payloadType)) {
                return declaredEventType;
            }
            if (eventClass == null || !eventClass.isInstance(event)) continue;
            return declaredEventType;
        }
        return null;
    }

    public String toString() {
        return this.method.toGenericString();
    }
}

