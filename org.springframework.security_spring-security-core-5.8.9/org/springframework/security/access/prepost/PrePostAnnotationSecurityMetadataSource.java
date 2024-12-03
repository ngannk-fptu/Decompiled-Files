/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.ClassUtils
 */
package org.springframework.security.access.prepost;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PrePostInvocationAttributeFactory;
import org.springframework.util.ClassUtils;

@Deprecated
public class PrePostAnnotationSecurityMetadataSource
extends AbstractMethodSecurityMetadataSource {
    private final PrePostInvocationAttributeFactory attributeFactory;

    public PrePostAnnotationSecurityMetadataSource(PrePostInvocationAttributeFactory attributeFactory) {
        this.attributeFactory = attributeFactory;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        PostInvocationAttribute post;
        if (method.getDeclaringClass() == Object.class) {
            return Collections.emptyList();
        }
        PreFilter preFilter = this.findAnnotation(method, targetClass, PreFilter.class);
        PreAuthorize preAuthorize = this.findAnnotation(method, targetClass, PreAuthorize.class);
        PostFilter postFilter = this.findAnnotation(method, targetClass, PostFilter.class);
        PostAuthorize postAuthorize = this.findAnnotation(method, targetClass, PostAuthorize.class);
        if (preFilter == null && preAuthorize == null && postFilter == null && postAuthorize == null) {
            return Collections.emptyList();
        }
        String preFilterAttribute = preFilter != null ? preFilter.value() : null;
        String filterObject = preFilter != null ? preFilter.filterTarget() : null;
        String preAuthorizeAttribute = preAuthorize != null ? preAuthorize.value() : null;
        String postFilterAttribute = postFilter != null ? postFilter.value() : null;
        String postAuthorizeAttribute = postAuthorize != null ? postAuthorize.value() : null;
        ArrayList<ConfigAttribute> attrs = new ArrayList<ConfigAttribute>(2);
        PreInvocationAttribute pre = this.attributeFactory.createPreInvocationAttribute(preFilterAttribute, filterObject, preAuthorizeAttribute);
        if (pre != null) {
            attrs.add(pre);
        }
        if ((post = this.attributeFactory.createPostInvocationAttribute(postFilterAttribute, postAuthorizeAttribute)) != null) {
            attrs.add(post);
        }
        attrs.trimToSize();
        return attrs;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    private <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationClass) {
        Method specificMethod = ClassUtils.getMostSpecificMethod((Method)method, targetClass);
        Annotation annotation = AnnotationUtils.findAnnotation((Method)specificMethod, annotationClass);
        if (annotation != null) {
            this.logger.debug((Object)LogMessage.format((String)"%s found on specific method: %s", (Object)annotation, (Object)specificMethod));
            return (A)annotation;
        }
        if (specificMethod != method && (annotation = AnnotationUtils.findAnnotation((Method)method, annotationClass)) != null) {
            this.logger.debug((Object)LogMessage.format((String)"%s found on: %s", (Object)annotation, (Object)method));
            return (A)annotation;
        }
        annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(), annotationClass);
        if (annotation != null) {
            this.logger.debug((Object)LogMessage.format((String)"%s found on: %s", (Object)annotation, (Object)specificMethod.getDeclaringClass().getName()));
            return (A)annotation;
        }
        return null;
    }
}

