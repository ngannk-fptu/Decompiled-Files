/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.rules;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.annotations.CreationRule;
import org.apache.commons.digester.annotations.DigesterRule;
import org.apache.commons.digester.annotations.DigesterRuleList;
import org.apache.commons.digester.annotations.providers.FactoryCreateRuleProvider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@CreationRule
@DigesterRule(reflectsRule=FactoryCreateRule.class, providedBy=FactoryCreateRuleProvider.class)
public @interface FactoryCreate {
    public Class<? extends AbstractObjectCreationFactory> factoryClass();

    public String pattern();

    public boolean ignoreCreateExceptions() default false;

    @Documented
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    @DigesterRuleList
    public static @interface List {
        public FactoryCreate[] value();
    }
}

