/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.ObjectCreate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ObjectCreateRuleProvider
implements AnnotationRuleProvider<ObjectCreate, Class<?>, ObjectCreateRule> {
    private Class<?> clazz;

    @Override
    public void init(ObjectCreate annotation, Class<?> element) {
        this.clazz = element;
    }

    @Override
    public ObjectCreateRule get() {
        return new ObjectCreateRule(this.clazz);
    }
}

