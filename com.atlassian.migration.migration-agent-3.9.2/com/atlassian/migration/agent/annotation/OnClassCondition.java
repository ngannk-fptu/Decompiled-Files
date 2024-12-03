/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 *  org.springframework.util.MultiValueMap
 */
package com.atlassian.migration.agent.annotation;

import com.atlassian.migration.agent.annotation.ConditionalOnClass;
import com.atlassian.migration.agent.annotation.ConditionalOnMissingClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

public class OnClassCondition
implements Condition {
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        List<String> onClasses = this.getCandidates(metadata, ConditionalOnClass.class);
        if (onClasses != null) {
            return this.matchConditionClassFound(onClasses);
        }
        List<String> onMissingClasses = this.getCandidates(metadata, ConditionalOnMissingClass.class);
        if (onMissingClasses != null) {
            return !this.matchConditionClassFound(onMissingClasses);
        }
        return true;
    }

    private boolean matchConditionClassFound(List<String> onClasses) {
        return onClasses.stream().allMatch(this::isClassFound);
    }

    private boolean isClassFound(String className) {
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    private List<String> getCandidates(AnnotatedTypeMetadata metadata, Class<?> annotationType) {
        MultiValueMap attributes = metadata.getAllAnnotationAttributes(annotationType.getName(), true);
        if (attributes == null) {
            return null;
        }
        ArrayList<String> candidates = new ArrayList<String>();
        this.addAll(candidates, (List)attributes.get((Object)"value"));
        return candidates;
    }

    private void addAll(List<String> list, List<Object> itemsToAdd) {
        if (itemsToAdd != null) {
            for (Object item : itemsToAdd) {
                Collections.addAll(list, (String[])item);
            }
        }
    }
}

