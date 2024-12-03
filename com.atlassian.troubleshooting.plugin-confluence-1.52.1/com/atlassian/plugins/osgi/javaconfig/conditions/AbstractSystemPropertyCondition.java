/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.plugins.osgi.javaconfig.conditions;

import com.atlassian.annotations.PublicApi;
import java.util.Objects;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@PublicApi
public abstract class AbstractSystemPropertyCondition
implements Condition {
    private final String propertyName;
    private final String trueValue;

    protected AbstractSystemPropertyCondition(String propertyName, String trueValue) {
        this.propertyName = Objects.requireNonNull(propertyName, "propertyName cannot be null");
        this.trueValue = Objects.requireNonNull(trueValue, "trueValue cannot be null");
    }

    public final boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Objects.equals(System.getProperty(this.propertyName), this.trueValue);
    }
}

