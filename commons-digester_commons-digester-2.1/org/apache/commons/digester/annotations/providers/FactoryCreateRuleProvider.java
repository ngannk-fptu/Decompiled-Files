/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.FactoryCreate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FactoryCreateRuleProvider
implements AnnotationRuleProvider<FactoryCreate, Class<?>, FactoryCreateRule> {
    private Class<?> factoryClass;
    private boolean ignoreCreateExceptions;

    @Override
    public void init(FactoryCreate annotation, Class<?> element) {
        this.factoryClass = annotation.factoryClass();
        this.ignoreCreateExceptions = annotation.ignoreCreateExceptions();
    }

    @Override
    public FactoryCreateRule get() {
        return new FactoryCreateRule(this.factoryClass, this.ignoreCreateExceptions);
    }
}

