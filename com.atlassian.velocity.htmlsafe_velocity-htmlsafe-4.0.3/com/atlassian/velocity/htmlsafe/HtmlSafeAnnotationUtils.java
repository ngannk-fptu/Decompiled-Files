/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class HtmlSafeAnnotationUtils {
    public static final Annotation HTML_SAFE_ANNOTATION = HtmlSafeAnnotationFactory.getHtmlSafeAnnotation();
    public static final Set<Annotation> HTML_SAFE_ANNOTATION_COLLECTION = ImmutableSet.of((Object)HTML_SAFE_ANNOTATION);
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Caches by class name whether toString() method is annotated with HtmlSafe or not")
    private static final Map<String, Boolean> htmlSafeToStringMethodByClassCache = new ConcurrentHashMap<String, Boolean>(1000);

    private HtmlSafeAnnotationUtils() {
    }

    public static boolean hasHtmlSafeToStringMethod(Object value) {
        Class<?> clazz = value.getClass();
        String className = clazz.getName();
        Boolean result = htmlSafeToStringMethodByClassCache.get(className);
        if (result == null) {
            try {
                result = clazz.getMethod("toString", new Class[0]).isAnnotationPresent(HtmlSafe.class);
                htmlSafeToStringMethodByClassCache.put(className, result);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("Object does not have a toString method");
            }
        }
        return result;
    }

    public static boolean isHtmlSafeValue(AnnotationBoxedElement<?> value) {
        return HtmlSafeAnnotationUtils.hasHtmlSafeToStringMethod(value.unbox()) || HtmlSafeAnnotationUtils.containsAnnotationOfType(value.getAnnotationCollection(), HtmlSafe.class);
    }

    public static boolean containsAnnotationOfType(Collection<Annotation> annotations, Class<? extends Annotation> annotationType) {
        for (Annotation annotation : annotations) {
            if (!annotation.annotationType().equals(annotationType)) continue;
            return true;
        }
        return false;
    }

    public static boolean endsWithHtmlIgnoreCase(String name) {
        char c;
        int pos = name.length();
        if (pos < 4) {
            return false;
        }
        if ((c = name.charAt(--pos)) != 'l' && c != 'L') {
            return false;
        }
        if ((c = name.charAt(--pos)) != 'm' && c != 'M') {
            return false;
        }
        if ((c = name.charAt(--pos)) != 't' && c != 'T') {
            return false;
        }
        return (c = name.charAt(--pos)) == 'h' || c == 'H';
    }

    private static class HtmlSafeAnnotationFactory {
        private HtmlSafeAnnotationFactory() {
        }

        static Annotation getHtmlSafeAnnotation() {
            try {
                return HtmlSafeAnnotationHolder.class.getMethod("holder", new Class[0]).getAnnotation(HtmlSafe.class);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        private static interface HtmlSafeAnnotationHolder {
            @HtmlSafe
            public Object holder();
        }
    }
}

