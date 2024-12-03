/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.rules;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.annotations.CreationRule;
import org.apache.commons.digester.annotations.DigesterRule;
import org.apache.commons.digester.annotations.DigesterRuleList;
import org.apache.commons.digester.annotations.providers.ObjectCreateRuleProvider;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@CreationRule
@DigesterRule(reflectsRule=ObjectCreateRule.class, providedBy=ObjectCreateRuleProvider.class)
public @interface ObjectCreate {
    public String pattern();

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    @DigesterRuleList
    public static @interface List {
        public ObjectCreate[] value();
    }
}

