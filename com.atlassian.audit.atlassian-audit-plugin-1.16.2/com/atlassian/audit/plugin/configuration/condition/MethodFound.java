/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.audit.plugin.configuration.condition;

import com.atlassian.audit.plugin.configuration.condition.Method;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MethodFound
implements Condition {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public final boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
        Map annotationAttributes = metadata.getAnnotationAttributes(Method.class.getName());
        if (annotationAttributes == null) {
            this.log.debug("{} annotation expected in conjunction with this condition", Method.class);
            return false;
        }
        Class methodClass = (Class)annotationAttributes.get("of");
        String method = (String)annotationAttributes.get("method");
        try {
            methodClass.getMethod(method, new Class[0]);
        }
        catch (NoSuchMethodException ne) {
            this.log.debug("Method {} not found for class {}", (Object)method, (Object)methodClass);
            return false;
        }
        return true;
    }
}

