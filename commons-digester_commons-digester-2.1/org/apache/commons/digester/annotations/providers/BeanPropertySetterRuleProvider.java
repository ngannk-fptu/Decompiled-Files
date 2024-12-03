/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import java.lang.reflect.Field;
import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.BeanPropertySetter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BeanPropertySetterRuleProvider
implements AnnotationRuleProvider<BeanPropertySetter, Field, BeanPropertySetterRule> {
    private String name;

    @Override
    public void init(BeanPropertySetter annotation, Field element) {
        this.name = element.getName();
    }

    @Override
    public BeanPropertySetterRule get() {
        return new BeanPropertySetterRule(this.name);
    }
}

