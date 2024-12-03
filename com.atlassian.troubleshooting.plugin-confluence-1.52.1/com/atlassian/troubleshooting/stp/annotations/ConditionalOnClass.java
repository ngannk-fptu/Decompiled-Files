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
@Conditional(value={OnClassCondition.class})
public @interface ConditionalOnClass {
    public Class<?>[] value() default {};

    public static class OnClassCondition
    implements Condition {
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
            String[] names;
            for (String name : names = (String[])metadata.getAnnotationAttributes(ConditionalOnClass.class.getName(), true).get("value")) {
                try {
                    Class.forName(name);
                }
                catch (ClassNotFoundException e) {
                    return false;
                }
            }
            return true;
        }
    }
}

