/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.audit.plugin.configuration.condition;

import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NonJira
implements Condition {
    private final JiraOnly jiraOnly = new JiraOnly();

    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !this.jiraOnly.matches(context, metadata);
    }
}

