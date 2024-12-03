/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.oauth2.common.web.loopsprevention;

import com.atlassian.plugins.osgi.javaconfig.conditions.product.BambooOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public final class SeraphEnabledCondition
implements Condition {
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return new JiraOnly().matches(context, metadata) || new ConfluenceOnly().matches(context, metadata) || new BambooOnly().matches(context, metadata) || new RefappOnly().matches(context, metadata);
    }
}

