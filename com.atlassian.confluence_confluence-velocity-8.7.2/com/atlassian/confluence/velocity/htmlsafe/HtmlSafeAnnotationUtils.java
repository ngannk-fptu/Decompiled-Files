/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement
 */
package com.atlassian.confluence.velocity.htmlsafe;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HtmlSafeAnnotationUtils {
    public static final Annotation HTML_SAFE_ANNOTATION = HtmlSafeAnnotationFactory.getHtmlSafeAnnotation();
    private static final Map<String, Boolean> htmlSafeToStringMethodByClassCache = new ConcurrentHashMap<String, Boolean>(1000);
    public static final Annotation ATLASSIAN_HTML_SAFE_ANNOTATION = HtmlSafeAnnotationFactory.getAtlassianHtmlSafeAnnotation();

    private HtmlSafeAnnotationUtils() {
    }

    public static boolean hasHtmlSafeToStringMethod(Object value) {
        boolean result;
        Class<?> clazz = value.getClass();
        if (htmlSafeToStringMethodByClassCache.containsKey(clazz.getName())) {
            return htmlSafeToStringMethodByClassCache.get(clazz.getName());
        }
        try {
            Method method = clazz.getMethod("toString", new Class[0]);
            result = method.isAnnotationPresent(HtmlSafe.class) || method.isAnnotationPresent(com.atlassian.velocity.htmlsafe.HtmlSafe.class);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Object does not have a toString method");
        }
        htmlSafeToStringMethodByClassCache.put(clazz.getName(), result);
        return result;
    }

    public static boolean isHtmlSafeValue(AnnotationBoxedElement value) {
        List<Annotation> annotations = Arrays.asList(value.getAnnotations());
        return HtmlSafeAnnotationUtils.hasHtmlSafeToStringMethod(value.unbox()) || HtmlSafeAnnotationUtils.containsAnnotationOfType(annotations, HtmlSafe.class) || HtmlSafeAnnotationUtils.containsAnnotationOfType(annotations, com.atlassian.velocity.htmlsafe.HtmlSafe.class);
    }

    public static boolean containsAnnotationOfType(Collection<Annotation> annotations, Class<? extends Annotation> annotationType) {
        for (Annotation annotation : annotations) {
            if (!annotation.annotationType().equals(annotationType)) continue;
            return true;
        }
        return false;
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

        static Annotation getAtlassianHtmlSafeAnnotation() {
            try {
                return AtlassianHtmlSafeAnnotationHolder.class.getMethod("holder", new Class[0]).getAnnotation(com.atlassian.velocity.htmlsafe.HtmlSafe.class);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        private static interface AtlassianHtmlSafeAnnotationHolder {
            @com.atlassian.velocity.htmlsafe.HtmlSafe
            public Object holder();
        }

        private static interface HtmlSafeAnnotationHolder {
            @HtmlSafe
            public Object holder();
        }
    }
}

