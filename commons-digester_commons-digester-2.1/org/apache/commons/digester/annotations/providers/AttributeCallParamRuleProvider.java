/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.reflect.MethodArgument;
import org.apache.commons.digester.annotations.rules.AttributeCallParam;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AttributeCallParamRuleProvider
implements AnnotationRuleProvider<AttributeCallParam, MethodArgument, CallParamRule> {
    private String attribute;
    private int index;

    @Override
    public void init(AttributeCallParam annotation, MethodArgument element) {
        this.attribute = annotation.attribute();
        this.index = element.getIndex();
    }

    @Override
    public CallParamRule get() {
        return new CallParamRule(this.index, this.attribute);
    }
}

