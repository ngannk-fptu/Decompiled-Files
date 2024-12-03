/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.business.insights.core.config.condition;

import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NonRefapp
implements Condition {
    private static final RefappOnly refappOnly = new RefappOnly();

    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !refappOnly.matches(context, metadata);
    }
}

