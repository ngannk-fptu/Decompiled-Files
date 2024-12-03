/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.plugins.osgi.javaconfig.annotations;

import com.atlassian.plugins.osgi.javaconfig.annotations.ConditionalOnClass;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

class ConditionalOnClassCondition
implements Condition {
    ConditionalOnClassCondition() {
    }

    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return Optional.ofNullable(annotatedTypeMetadata.getAnnotationAttributes(ConditionalOnClass.class.getTypeName(), true)).flatMap(attrs -> Optional.ofNullable(attrs.get("value"))).map(classNames -> Arrays.stream((String[])classNames)).orElse(Stream.empty()).noneMatch(this::isClassNotFound);
    }

    private boolean isClassNotFound(String className) {
        try {
            Class.forName(className);
            return false;
        }
        catch (ClassNotFoundException e) {
            return true;
        }
    }
}

