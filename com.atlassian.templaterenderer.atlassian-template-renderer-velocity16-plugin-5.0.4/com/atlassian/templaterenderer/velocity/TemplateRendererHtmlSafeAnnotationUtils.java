/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.templaterenderer.annotations.HtmlSafe
 *  com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement
 */
package com.atlassian.templaterenderer.velocity;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.templaterenderer.annotations.HtmlSafe;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxedElement;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TemplateRendererHtmlSafeAnnotationUtils {
    public static final Annotation HTML_SAFE_ANNOTATION = HtmlSafeAnnotationFactory.getHtmlSafeAnnotation();
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static final Map<String, Boolean> htmlSafeToStringMethodByClassCache = new ConcurrentHashMap<String, Boolean>(1000);

    private TemplateRendererHtmlSafeAnnotationUtils() {
    }

    public static boolean hasHtmlSafeToStringMethod(Object value) {
        boolean result;
        Class<?> clazz = value.getClass();
        if (htmlSafeToStringMethodByClassCache.containsKey(clazz.getName())) {
            return htmlSafeToStringMethodByClassCache.get(clazz.getName());
        }
        try {
            result = clazz.getMethod("toString", new Class[0]).isAnnotationPresent(HtmlSafe.class);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Object does not have a toString method");
        }
        htmlSafeToStringMethodByClassCache.put(clazz.getName(), result);
        return result;
    }

    public static boolean isHtmlSafeValue(AnnotationBoxedElement value) {
        return TemplateRendererHtmlSafeAnnotationUtils.hasHtmlSafeToStringMethod(value.unbox()) || TemplateRendererHtmlSafeAnnotationUtils.containsAnnotationOfType(Arrays.asList(value.getAnnotations()), HtmlSafe.class);
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

        private static interface HtmlSafeAnnotationHolder {
            @HtmlSafe
            public Object holder();
        }
    }
}

