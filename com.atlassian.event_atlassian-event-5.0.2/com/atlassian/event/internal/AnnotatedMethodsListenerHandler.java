/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.event.internal;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.internal.SingleParameterMethodListenerInvoker;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.event.spi.ListenerInvoker;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnnotatedMethodsListenerHandler
implements ListenerHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Class annotationClass;

    public AnnotatedMethodsListenerHandler() {
        this(EventListener.class);
    }

    public AnnotatedMethodsListenerHandler(Class annotationClass) {
        this.annotationClass = (Class)Preconditions.checkNotNull((Object)annotationClass);
    }

    @Override
    public List<ListenerInvoker> getInvokers(Object listener) {
        Map<Method, EventListenerAnnotationParams> validMethods = this.getValidMethods(Preconditions.checkNotNull((Object)listener));
        if (validMethods.isEmpty()) {
            this.log.debug("Couldn't find any valid listener methods on class <{}>", (Object)listener.getClass().getName());
        }
        return validMethods.entrySet().stream().map(entry -> new SingleParameterMethodListenerInvoker(listener, (Method)entry.getKey(), ((EventListenerAnnotationParams)entry.getValue()).scope, ((EventListenerAnnotationParams)entry.getValue()).order)).collect(Collectors.toCollection(LinkedList::new));
    }

    private Map<Method, EventListenerAnnotationParams> getValidMethods(Object listener) {
        LinkedHashMap<Method, EventListenerAnnotationParams> annotatedMethods = new LinkedHashMap<Method, EventListenerAnnotationParams>();
        for (Method method : listener.getClass().getMethods()) {
            if (!this.isValidMethod(method)) continue;
            annotatedMethods.put(method, this.getEventListenerAnnotationParams(method));
        }
        return annotatedMethods;
    }

    private EventListenerAnnotationParams getEventListenerAnnotationParams(Method method) {
        EventListener annotation = method.getAnnotation(EventListener.class);
        Optional<String> scope = Optional.ofNullable(annotation).map(EventListener::scope).filter(scopeName -> !"".equals(scopeName));
        int order = annotation != null ? annotation.order() : 0;
        return new EventListenerAnnotationParams(scope, order);
    }

    private boolean isValidMethod(Method method) {
        if (this.isAnnotated(method)) {
            if (this.hasOneAndOnlyOneParameter(method)) {
                return true;
            }
            throw new RuntimeException("Method <" + method + "> of class <" + method.getDeclaringClass() + "> is annotated with <" + this.annotationClass.getName() + "> but has 0 or more than 1 parameters! Listener methods MUST have 1 and only 1 parameter.");
        }
        return false;
    }

    private boolean isAnnotated(Method method) {
        return method.getAnnotation(this.annotationClass) != null;
    }

    private boolean hasOneAndOnlyOneParameter(Method method) {
        return method.getParameterTypes().length == 1;
    }

    private static class EventListenerAnnotationParams {
        final Optional<String> scope;
        final int order;

        private EventListenerAnnotationParams(Optional<String> scope, int order) {
            this.scope = scope;
            this.order = order;
        }
    }
}

