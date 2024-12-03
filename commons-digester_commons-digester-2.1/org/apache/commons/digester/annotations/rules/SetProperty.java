/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.rules;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.commons.digester.annotations.DigesterRule;
import org.apache.commons.digester.annotations.DigesterRuleList;
import org.apache.commons.digester.annotations.handlers.SetPropertiesLoaderHandler;
import org.apache.commons.digester.annotations.providers.SetPropertiesRuleProvider;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
@DigesterRule(reflectsRule=SetPropertiesRule.class, providedBy=SetPropertiesRuleProvider.class, handledBy=SetPropertiesLoaderHandler.class)
public @interface SetProperty {
    public String pattern();

    public String attributeName() default "";

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    @DigesterRuleList
    public static @interface List {
        public SetProperty[] value();
    }
}

