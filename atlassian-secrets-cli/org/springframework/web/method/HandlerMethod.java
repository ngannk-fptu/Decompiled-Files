/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

public class HandlerMethod {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Object bean;
    @Nullable
    private final BeanFactory beanFactory;
    private final Class<?> beanType;
    private final Method method;
    private final Method bridgedMethod;
    private final MethodParameter[] parameters;
    @Nullable
    private HttpStatus responseStatus;
    @Nullable
    private String responseStatusReason;
    @Nullable
    private HandlerMethod resolvedFromHandlerMethod;

    public HandlerMethod(Object bean2, Method method) {
        Assert.notNull(bean2, "Bean is required");
        Assert.notNull((Object)method, "Method is required");
        this.bean = bean2;
        this.beanFactory = null;
        this.beanType = ClassUtils.getUserClass(bean2);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = this.initMethodParameters();
        this.evaluateResponseStatus();
    }

    public HandlerMethod(Object bean2, String methodName, Class<?> ... parameterTypes) throws NoSuchMethodException {
        Assert.notNull(bean2, "Bean is required");
        Assert.notNull((Object)methodName, "Method name is required");
        this.bean = bean2;
        this.beanFactory = null;
        this.beanType = ClassUtils.getUserClass(bean2);
        this.method = bean2.getClass().getMethod(methodName, parameterTypes);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
        this.parameters = this.initMethodParameters();
        this.evaluateResponseStatus();
    }

    public HandlerMethod(String beanName, BeanFactory beanFactory, Method method) {
        Assert.hasText(beanName, "Bean name is required");
        Assert.notNull((Object)beanFactory, "BeanFactory is required");
        Assert.notNull((Object)method, "Method is required");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        Class<?> beanType = beanFactory.getType(beanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot resolve bean type for bean with name '" + beanName + "'");
        }
        this.beanType = ClassUtils.getUserClass(beanType);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = this.initMethodParameters();
        this.evaluateResponseStatus();
    }

    protected HandlerMethod(HandlerMethod handlerMethod) {
        Assert.notNull((Object)handlerMethod, "HandlerMethod is required");
        this.bean = handlerMethod.bean;
        this.beanFactory = handlerMethod.beanFactory;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod.resolvedFromHandlerMethod;
    }

    private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
        Assert.notNull((Object)handlerMethod, "HandlerMethod is required");
        Assert.notNull(handler, "Handler object is required");
        this.bean = handler;
        this.beanFactory = handlerMethod.beanFactory;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod;
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; ++i) {
            HandlerMethodParameter parameter = new HandlerMethodParameter(i);
            GenericTypeResolver.resolveParameterType(parameter, this.beanType);
            result[i] = parameter;
        }
        return result;
    }

    private void evaluateResponseStatus() {
        ResponseStatus annotation = this.getMethodAnnotation(ResponseStatus.class);
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(this.getBeanType(), ResponseStatus.class);
        }
        if (annotation != null) {
            this.responseStatus = annotation.code();
            this.responseStatusReason = annotation.reason();
        }
    }

    public Object getBean() {
        return this.bean;
    }

    public Method getMethod() {
        return this.method;
    }

    public Class<?> getBeanType() {
        return this.beanType;
    }

    protected Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    @Nullable
    protected HttpStatus getResponseStatus() {
        return this.responseStatus;
    }

    @Nullable
    protected String getResponseStatusReason() {
        return this.responseStatusReason;
    }

    public MethodParameter getReturnType() {
        return new HandlerMethodParameter(-1);
    }

    public MethodParameter getReturnValueType(@Nullable Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    public boolean isVoid() {
        return Void.TYPE.equals(this.getReturnType().getParameterType());
    }

    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
    }

    @Nullable
    public HandlerMethod getResolvedFromHandlerMethod() {
        return this.resolvedFromHandlerMethod;
    }

    public HandlerMethod createWithResolvedBean() {
        Object handler = this.bean;
        if (this.bean instanceof String) {
            Assert.state(this.beanFactory != null, "Cannot resolve bean name without BeanFactory");
            String beanName = (String)this.bean;
            handler = this.beanFactory.getBean(beanName);
        }
        return new HandlerMethod(this, handler);
    }

    public String getShortLogMessage() {
        int args = this.method.getParameterCount();
        return this.getBeanType().getName() + "#" + this.method.getName() + "[" + args + " args]";
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod otherMethod = (HandlerMethod)other;
        return this.bean.equals(otherMethod.bean) && this.method.equals(otherMethod.method);
    }

    public int hashCode() {
        return this.bean.hashCode() * 31 + this.method.hashCode();
    }

    public String toString() {
        return this.method.toGenericString();
    }

    private class ReturnValueMethodParameter
    extends HandlerMethodParameter {
        @Nullable
        private final Object returnValue;

        public ReturnValueMethodParameter(Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
        }

        @Override
        public Class<?> getParameterType() {
            return this.returnValue != null ? this.returnValue.getClass() : super.getParameterType();
        }

        @Override
        public ReturnValueMethodParameter clone() {
            return new ReturnValueMethodParameter(this);
        }
    }

    protected class HandlerMethodParameter
    extends SynthesizingMethodParameter {
        public HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        protected HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }

        @Override
        public Class<?> getContainingClass() {
            return HandlerMethod.this.getBeanType();
        }

        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.getMethodAnnotation(annotationType);
        }

        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.hasMethodAnnotation(annotationType);
        }

        @Override
        public HandlerMethodParameter clone() {
            return new HandlerMethodParameter(this);
        }
    }
}

