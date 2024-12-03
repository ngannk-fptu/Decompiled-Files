/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.audit.plugin.configuration.condition;

import com.atlassian.audit.plugin.configuration.condition.MethodFound;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MethodNotFound
implements Condition {
    private final MethodFound methodFound = new MethodFound();

    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return !this.methodFound.matches(conditionContext, annotatedTypeMetadata);
    }
}

