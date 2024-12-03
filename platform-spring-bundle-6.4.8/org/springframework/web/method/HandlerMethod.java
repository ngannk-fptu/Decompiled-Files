/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

public class HandlerMethod {
    protected static final Log logger = LogFactory.getLog(HandlerMethod.class);
    private final Object bean;
    @Nullable
    private final BeanFactory beanFactory;
    @Nullable
    private final MessageSource messageSource;
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
    @Nullable
    private volatile List<Annotation[][]> interfaceParameterAnnotations;
    private final String description;

    public HandlerMethod(Object bean2, Method method) {
        this(bean2, method, null);
    }

    protected HandlerMethod(Object bean2, Method method, @Nullable MessageSource messageSource) {
        Assert.notNull(bean2, "Bean is required");
        Assert.notNull((Object)method, "Method is required");
        this.bean = bean2;
        this.beanFactory = null;
        this.messageSource = messageSource;
        this.beanType = ClassUtils.getUserClass(bean2);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(this.bridgedMethod);
        this.parameters = this.initMethodParameters();
        this.evaluateResponseStatus();
        this.description = HandlerMethod.initDescription(this.beanType, this.method);
    }

    public HandlerMethod(Object bean2, String methodName, Class<?> ... parameterTypes) throws NoSuchMethodException {
        Assert.notNull(bean2, "Bean is required");
        Assert.notNull((Object)methodName, "Method name is required");
        this.bean = bean2;
        this.beanFactory = null;
        this.messageSource = null;
        this.beanType = ClassUtils.getUserClass(bean2);
        this.method = bean2.getClass().getMethod(methodName, parameterTypes);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
        ReflectionUtils.makeAccessible(this.bridgedMethod);
        this.parameters = this.initMethodParameters();
        this.evaluateResponseStatus();
        this.description = HandlerMethod.initDescription(this.beanType, this.method);
    }

    public HandlerMethod(String beanName, BeanFactory beanFactory, Method method) {
        this(beanName, beanFactory, null, method);
    }

    public HandlerMethod(String beanName, BeanFactory beanFactory, @Nullable MessageSource messageSource, Method method) {
        Assert.hasText(beanName, "Bean name is required");
        Assert.notNull((Object)beanFactory, "BeanFactory is required");
        Assert.notNull((Object)method, "Method is required");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        this.messageSource = messageSource;
        Class<?> beanType = beanFactory.getType(beanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot resolve bean type for bean with name '" + beanName + "'");
        }
        this.beanType = ClassUtils.getUserClass(beanType);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(this.bridgedMethod);
        this.parameters = this.initMethodParameters();
        this.evaluateResponseStatus();
        this.description = HandlerMethod.initDescription(this.beanType, this.method);
    }

    protected HandlerMethod(HandlerMethod handlerMethod) {
        Assert.notNull((Object)handlerMethod, "HandlerMethod is required");
        this.bean = handlerMethod.bean;
        this.beanFactory = handlerMethod.beanFactory;
        this.messageSource = handlerMethod.messageSource;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.description = handlerMethod.description;
        this.resolvedFromHandlerMethod = handlerMethod.resolvedFromHandlerMethod;
    }

    private HandlerMethod(HandlerMethod handlerMethod, Object handler) {
        Assert.notNull((Object)handlerMethod, "HandlerMethod is required");
        Assert.notNull(handler, "Handler object is required");
        this.bean = handler;
        this.beanFactory = handlerMethod.beanFactory;
        this.messageSource = handlerMethod.messageSource;
        this.beanType = handlerMethod.beanType;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
        this.responseStatus = handlerMethod.responseStatus;
        this.responseStatusReason = handlerMethod.responseStatusReason;
        this.resolvedFromHandlerMethod = handlerMethod;
        this.description = handlerMethod.description;
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i2 = 0; i2 < count; ++i2) {
            result[i2] = new HandlerMethodParameter(i2);
        }
        return result;
    }

    private void evaluateResponseStatus() {
        ResponseStatus annotation = this.getMethodAnnotation(ResponseStatus.class);
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(this.getBeanType(), ResponseStatus.class);
        }
        if (annotation != null) {
            String reason = annotation.reason();
            String resolvedReason = StringUtils.hasText(reason) && this.messageSource != null ? this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale()) : reason;
            this.responseStatus = annotation.code();
            this.responseStatusReason = resolvedReason;
        }
    }

    private static String initDescription(Class<?> beanType, Method method) {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (Class<?> paramType : method.getParameterTypes()) {
            joiner.add(paramType.getSimpleName());
        }
        return beanType.getName() + "#" + method.getName() + joiner;
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
        return this.getBeanType().getName() + "#" + this.method.getName() + "[" + this.method.getParameterCount() + " args]";
    }

    private List<Annotation[][]> getInterfaceParameterAnnotations() {
        List<Annotation[][]> parameterAnnotations = this.interfaceParameterAnnotations;
        if (parameterAnnotations == null) {
            parameterAnnotations = new ArrayList<Annotation[][]>();
            for (Class<?> ifc : ClassUtils.getAllInterfacesForClassAsSet(this.method.getDeclaringClass())) {
                for (Method candidate : ifc.getMethods()) {
                    if (!this.isOverrideFor(candidate)) continue;
                    parameterAnnotations.add(candidate.getParameterAnnotations());
                }
            }
            this.interfaceParameterAnnotations = parameterAnnotations;
        }
        return parameterAnnotations;
    }

    private boolean isOverrideFor(Method candidate) {
        if (!candidate.getName().equals(this.method.getName()) || candidate.getParameterCount() != this.method.getParameterCount()) {
            return false;
        }
        Object[] paramTypes = this.method.getParameterTypes();
        if (Arrays.equals(candidate.getParameterTypes(), paramTypes)) {
            return true;
        }
        for (int i2 = 0; i2 < paramTypes.length; ++i2) {
            if (paramTypes[i2] == ResolvableType.forMethodParameter(candidate, i2, this.method.getDeclaringClass()).resolve()) continue;
            return false;
        }
        return true;
    }

    public boolean equals(@Nullable Object other) {
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
        return this.description;
    }

    @Nullable
    protected static Object findProvidedArgument(MethodParameter parameter, Object ... providedArgs) {
        if (!ObjectUtils.isEmpty(providedArgs)) {
            for (Object providedArg : providedArgs) {
                if (!parameter.getParameterType().isInstance(providedArg)) continue;
                return providedArg;
            }
        }
        return null;
    }

    protected static String formatArgumentError(MethodParameter param, String message) {
        return "Could not resolve parameter [" + param.getParameterIndex() + "] in " + param.getExecutable().toGenericString() + (StringUtils.hasText(message) ? ": " + message : "");
    }

    protected void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> targetBeanClass;
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass = targetBean.getClass())) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual controller bean class '" + targetBeanClass.getName() + "'. If the controller requires proxying (e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(this.formatInvokeError(text, args));
        }
    }

    protected String formatInvokeError(String text, Object[] args) {
        String formattedArgs = IntStream.range(0, args.length).mapToObj(i2 -> args[i2] != null ? "[" + i2 + "] [type=" + args[i2].getClass().getName() + "] [value=" + args[i2] + "]" : "[" + i2 + "] [null]").collect(Collectors.joining(",\n", " ", " "));
        return text + "\nController [" + this.getBeanType().getName() + "]\nMethod [" + this.getBridgedMethod().toGenericString() + "] with argument values:\n" + formattedArgs;
    }

    private class ReturnValueMethodParameter
    extends HandlerMethodParameter {
        @Nullable
        private final Class<?> returnValueType;

        public ReturnValueMethodParameter(Object returnValue) {
            super(-1);
            this.returnValueType = returnValue != null ? returnValue.getClass() : null;
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValueType = original.returnValueType;
        }

        @Override
        public Class<?> getParameterType() {
            return this.returnValueType != null ? this.returnValueType : super.getParameterType();
        }

        @Override
        public ReturnValueMethodParameter clone() {
            return new ReturnValueMethodParameter(this);
        }
    }

    protected class HandlerMethodParameter
    extends SynthesizingMethodParameter {
        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        protected HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }

        @Override
        @NonNull
        public Method getMethod() {
            return HandlerMethod.this.bridgedMethod;
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
        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                int index = this.getParameterIndex();
                if (index >= 0) {
                    for (Annotation[][] ifcAnns : HandlerMethod.this.getInterfaceParameterAnnotations()) {
                        Annotation[] paramAnns;
                        if (index >= ifcAnns.length || (paramAnns = ifcAnns[index]).length <= 0) continue;
                        ArrayList<Annotation> merged = new ArrayList<Annotation>(anns.length + paramAnns.length);
                        merged.addAll(Arrays.asList(anns));
                        for (Annotation paramAnn : paramAnns) {
                            boolean existingType = false;
                            for (Annotation ann : anns) {
                                if (ann.annotationType() != paramAnn.annotationType()) continue;
                                existingType = true;
                                break;
                            }
                            if (existingType) continue;
                            merged.add(this.adaptAnnotation(paramAnn));
                        }
                        anns = merged.toArray(new Annotation[0]);
                    }
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }

        @Override
        public HandlerMethodParameter clone() {
            return new HandlerMethodParameter(this);
        }
    }
}

