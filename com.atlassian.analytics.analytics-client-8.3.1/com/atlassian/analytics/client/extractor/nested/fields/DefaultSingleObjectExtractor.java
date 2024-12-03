/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.v2.Hashed
 *  com.atlassian.analytics.api.annotations.v2.SecurityPermitted
 */
package com.atlassian.analytics.client.extractor.nested.fields;

import com.atlassian.analytics.api.annotations.v2.Hashed;
import com.atlassian.analytics.api.annotations.v2.SecurityPermitted;
import com.atlassian.analytics.client.extractor.nested.fields.AnalyticsFieldAnnotations;
import com.atlassian.analytics.client.extractor.nested.fields.AnnotatedInvocation;
import com.atlassian.analytics.client.extractor.nested.fields.SingleObjectExtractor;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DefaultSingleObjectExtractor
implements SingleObjectExtractor {
    @Override
    public Map<String, AnnotatedInvocation> extractSingleObject(Object object) {
        if (!this.isObjectPublic(object)) {
            return Collections.emptyMap();
        }
        Method[] declaredMethods = object.getClass().getMethods();
        LinkedHashMap<String, AnnotatedInvocation> extraction = new LinkedHashMap<String, AnnotatedInvocation>();
        Arrays.stream(declaredMethods).filter(this::isGetter).forEach(this.catchExceptions(method -> extraction.put(this.parseGetterNameToVariableName((Method)method), this.invokeAndReturnResultWithAnnotations(object, (Method)method))));
        return extraction;
    }

    private boolean isObjectPublic(Object object) {
        return (object.getClass().getModifiers() & 1) != 0;
    }

    private boolean isGetter(Method method) {
        return (method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterCount() == 0 && (method.getModifiers() & 1) != 0;
    }

    private String parseGetterNameToVariableName(Method method) {
        int prefixLength = method.getName().startsWith("is") ? 2 : 3;
        return Introspector.decapitalize(method.getName().substring(prefixLength));
    }

    private AnnotatedInvocation invokeAndReturnResultWithAnnotations(Object object, Method method) throws InvocationTargetException, IllegalAccessException {
        Object result = method.invoke(object, new Object[0]);
        EnumSet<AnalyticsFieldAnnotations> annotations = EnumSet.noneOf(AnalyticsFieldAnnotations.class);
        if (method.isAnnotationPresent(Hashed.class)) {
            annotations.add(AnalyticsFieldAnnotations.HASHED);
        }
        if (method.isAnnotationPresent(SecurityPermitted.class)) {
            annotations.add(AnalyticsFieldAnnotations.SECURITY_EXCEPTION);
        }
        return new AnnotatedInvocation(result, annotations);
    }

    private <T> Consumer<T> catchExceptions(ExceptionConsumer<T> function) {
        return t -> {
            try {
                function.accept(t);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static interface ExceptionConsumer<T> {
        public void accept(T var1) throws Exception;
    }
}

