/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

public class AnnotationScopeMetadataResolver
implements ScopeMetadataResolver {
    private final ScopedProxyMode defaultProxyMode;
    protected Class<? extends Annotation> scopeAnnotationType = Scope.class;

    public AnnotationScopeMetadataResolver() {
        this.defaultProxyMode = ScopedProxyMode.NO;
    }

    public AnnotationScopeMetadataResolver(ScopedProxyMode defaultProxyMode) {
        Assert.notNull((Object)defaultProxyMode, "'defaultProxyMode' must not be null");
        this.defaultProxyMode = defaultProxyMode;
    }

    public void setScopeAnnotationType(Class<? extends Annotation> scopeAnnotationType) {
        Assert.notNull(scopeAnnotationType, "'scopeAnnotationType' must not be null");
        this.scopeAnnotationType = scopeAnnotationType;
    }

    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        AnnotatedBeanDefinition annDef;
        AnnotationAttributes attributes;
        ScopeMetadata metadata = new ScopeMetadata();
        if (definition instanceof AnnotatedBeanDefinition && (attributes = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)(annDef = (AnnotatedBeanDefinition)definition).getMetadata(), this.scopeAnnotationType)) != null) {
            metadata.setScopeName(attributes.getString("value"));
            ScopedProxyMode proxyMode = (ScopedProxyMode)((Object)attributes.getEnum("proxyMode"));
            if (proxyMode == ScopedProxyMode.DEFAULT) {
                proxyMode = this.defaultProxyMode;
            }
            metadata.setScopedProxyMode(proxyMode);
        }
        return metadata;
    }
}

