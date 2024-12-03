/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.oauth2.common.config;

import com.atlassian.plugins.osgi.javaconfig.conditions.product.AbstractProductCondition;
import java.util.List;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MultipleProductCondition
implements Condition {
    private final List<AbstractProductCondition> productConditions;

    public MultipleProductCondition(List<AbstractProductCondition> productConditions) {
        this.productConditions = productConditions;
    }

    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return this.productConditions.stream().anyMatch(productCondition -> productCondition.matches(context, metadata));
    }
}

