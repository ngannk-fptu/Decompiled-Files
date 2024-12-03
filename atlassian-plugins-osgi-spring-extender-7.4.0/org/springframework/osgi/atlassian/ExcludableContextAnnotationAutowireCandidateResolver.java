/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.config.DependencyDescriptor
 *  org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver
 */
package org.springframework.osgi.atlassian;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;

public class ExcludableContextAnnotationAutowireCandidateResolver
extends ContextAnnotationAutowireCandidateResolver {
    private Set<String> excludedBeanNames = new HashSet<String>();

    public ExcludableContextAnnotationAutowireCandidateResolver() {
        this.excludedBeanNames.add("org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry");
    }

    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (this.excludedBeanNames.contains(bdHolder.getBeanName())) {
            return false;
        }
        return super.isAutowireCandidate(bdHolder, descriptor);
    }
}

