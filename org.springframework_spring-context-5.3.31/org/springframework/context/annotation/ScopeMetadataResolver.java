/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;

@FunctionalInterface
public interface ScopeMetadataResolver {
    public ScopeMetadata resolveScopeMetadata(BeanDefinition var1);
}

