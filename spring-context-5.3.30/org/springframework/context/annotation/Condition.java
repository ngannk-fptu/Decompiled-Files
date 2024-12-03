/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package org.springframework.context.annotation;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@FunctionalInterface
public interface Condition {
    public boolean matches(ConditionContext var1, AnnotatedTypeMetadata var2);
}

