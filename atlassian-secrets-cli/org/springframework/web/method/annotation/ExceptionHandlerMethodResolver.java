/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ExceptionHandlerMethodResolver {
    public static final ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS = method -> AnnotationUtils.findAnnotation(method, ExceptionHandler.class) != null;
    private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap<Class<? extends Throwable>, Method>(16);
    private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap<Class<? extends Throwable>, Method>(16);

    public ExceptionHandlerMethodResolver(Class<?> handlerType) {
        for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
            for (Class<? extends Throwable> exceptionType : this.detectExceptionMappings(method)) {
                this.addExceptionMapping(exceptionType, method);
            }
        }
    }

    private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
        ArrayList<Class<? extends Throwable>> result = new ArrayList<Class<? extends Throwable>>();
        this.detectAnnotationExceptionMappings(method, result);
        if (result.isEmpty()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                if (!Throwable.class.isAssignableFrom(paramType)) continue;
                result.add(paramType);
            }
        }
        if (result.isEmpty()) {
            throw new IllegalStateException("No exception types mapped to " + method);
        }
        return result;
    }

    protected void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
        ExceptionHandler ann = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
        Assert.state(ann != null, "No ExceptionHandler annotation");
        result.addAll(Arrays.asList(ann.value()));
    }

    private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
        Method oldMethod = this.mappedMethods.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException("Ambiguous @ExceptionHandler method mapped for [" + exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }

    public boolean hasExceptionMappings() {
        return !this.mappedMethods.isEmpty();
    }

    @Nullable
    public Method resolveMethod(Exception exception) {
        return this.resolveMethodByThrowable(exception);
    }

    @Nullable
    public Method resolveMethodByThrowable(Throwable exception) {
        Throwable cause;
        Method method = this.resolveMethodByExceptionType(exception.getClass());
        if (method == null && (cause = exception.getCause()) != null) {
            method = this.resolveMethodByExceptionType(cause.getClass());
        }
        return method;
    }

    @Nullable
    public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
        Method method = this.exceptionLookupCache.get(exceptionType);
        if (method == null) {
            method = this.getMappedMethod(exceptionType);
            this.exceptionLookupCache.put(exceptionType, method);
        }
        return method;
    }

    @Nullable
    private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
        ArrayList<Class<? extends Throwable>> matches = new ArrayList<Class<? extends Throwable>>();
        for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
            if (!mappedException.isAssignableFrom(exceptionType)) continue;
            matches.add(mappedException);
        }
        if (!matches.isEmpty()) {
            matches.sort(new ExceptionDepthComparator(exceptionType));
            return this.mappedMethods.get(matches.get(0));
        }
        return null;
    }
}

