/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.business.insights.confluence.config;

import com.atlassian.confluence.search.v2.query.BooleanQuery;
import java.lang.reflect.Method;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NewConfluenceSearchQueryFilteringApiAvailableCondition
implements Condition {
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        for (Method m : BooleanQuery.Builder.class.getMethods()) {
            if (!m.getName().equals("addFilter")) continue;
            return true;
        }
        return false;
    }
}

