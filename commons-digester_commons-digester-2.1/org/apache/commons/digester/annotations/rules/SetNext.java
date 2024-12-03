/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.rules;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.annotations.DigesterRule;
import org.apache.commons.digester.annotations.handlers.MethodHandler;
import org.apache.commons.digester.annotations.providers.SetNextRuleProvider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@DigesterRule(reflectsRule=SetNextRule.class, providedBy=SetNextRuleProvider.class, handledBy=MethodHandler.class)
public @interface SetNext {
    public Class<?>[] value() default {};
}

