/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.util.Assert;

@Deprecated
public class SecuredAnnotationSecurityMetadataSource
extends AbstractFallbackMethodSecurityMetadataSource {
    private AnnotationMetadataExtractor annotationExtractor;
    private Class<? extends Annotation> annotationType;

    public SecuredAnnotationSecurityMetadataSource() {
        this(new SecuredAnnotationMetadataExtractor());
    }

    public SecuredAnnotationSecurityMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        Assert.notNull((Object)annotationMetadataExtractor, (String)"annotationMetadataExtractor cannot be null");
        this.annotationExtractor = annotationMetadataExtractor;
        this.annotationType = GenericTypeResolver.resolveTypeArgument(this.annotationExtractor.getClass(), AnnotationMetadataExtractor.class);
        Assert.notNull(this.annotationType, () -> this.annotationExtractor.getClass().getName() + " must supply a generic parameter for AnnotationMetadataExtractor");
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return this.processAnnotation(AnnotationUtils.findAnnotation(clazz, this.annotationType));
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        return this.processAnnotation(AnnotationUtils.findAnnotation((Method)method, this.annotationType));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    private Collection<ConfigAttribute> processAnnotation(Annotation annotation) {
        return annotation != null ? this.annotationExtractor.extractAttributes(annotation) : null;
    }

    static class SecuredAnnotationMetadataExtractor
    implements AnnotationMetadataExtractor<Secured> {
        SecuredAnnotationMetadataExtractor() {
        }

        @Override
        public Collection<ConfigAttribute> extractAttributes(Secured secured) {
            String[] attributeTokens = secured.value();
            ArrayList<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>(attributeTokens.length);
            for (String token : attributeTokens) {
                attributes.add(new SecurityConfig(token));
            }
            return attributes;
        }
    }
}

