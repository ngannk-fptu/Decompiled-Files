/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;

@FunctionalInterface
public interface ScopeMetadataResolver {
    public ScopeMetadata resolveScopeMetadata(BeanDefinition var1);
}

