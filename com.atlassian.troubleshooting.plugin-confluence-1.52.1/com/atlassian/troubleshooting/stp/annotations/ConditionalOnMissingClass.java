/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.troubleshooting.stp.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Target(value={ElementType.TYPE, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Conditional(value={OnClassMissingCondition.class})
public @interface ConditionalOnMissingClass {
    public String[] value() default {};

    public static class OnClassMissingCondition
    implements Condition {
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
            String[] names;
            for (String name : names = (String[])metadata.getAnnotationAttributes(ConditionalOnMissingClass.class.getName(), true).get("value")) {
                try {
                    Class.forName(name);
                    return false;
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
            }
            return true;
        }
    }
}

