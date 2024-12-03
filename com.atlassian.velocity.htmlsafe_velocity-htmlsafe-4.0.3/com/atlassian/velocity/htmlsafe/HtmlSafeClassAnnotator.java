/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.velocity.htmlsafe.introspection.MethodAnnotator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class HtmlSafeClassAnnotator
implements MethodAnnotator {
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static final Map<String, Set<String>> HTML_ENCODE_CLASS_METHODS = ImmutableMap.builder().put((Object)"com.opensymphony.util.TextUtils", (Object)ImmutableSet.of((Object)"htmlEncode")).put((Object)"org.apache.velocity.tools.generic.EscapeTool", (Object)ImmutableSet.of((Object)"html")).put((Object)"com.opensymphony.webwork.util.WebWorkUtil", (Object)ImmutableSet.of((Object)"htmlEncode")).put((Object)"com.opensymphony.webwork.util.VelocityWebWorkUtil", (Object)ImmutableSet.of((Object)"htmlEncode")).build();

    @Override
    public Collection<Annotation> getAnnotationsForMethod(Method method) {
        return this.isSafeMethod(method) ? HtmlSafeAnnotationUtils.HTML_SAFE_ANNOTATION_COLLECTION : ImmutableSet.of();
    }

    private boolean isSafeMethod(Method method) {
        Set<String> safeMethods = HTML_ENCODE_CLASS_METHODS.get(method.getDeclaringClass().getName());
        return safeMethods != null && safeMethods.contains(method.getName());
    }
}

