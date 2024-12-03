/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.lang.Nullable;

public interface AutowireCandidateResolver {
    default public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

    default public boolean isRequired(DependencyDescriptor descriptor) {
        return descriptor.isRequired();
    }

    default public boolean hasQualifier(DependencyDescriptor descriptor) {
        return false;
    }

    @Nullable
    default public Object getSuggestedValue(DependencyDescriptor descriptor) {
        return null;
    }

    @Nullable
    default public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
        return null;
    }

    default public AutowireCandidateResolver cloneIfNecessary() {
        return (AutowireCandidateResolver)BeanUtils.instantiateClass(this.getClass());
    }
}

