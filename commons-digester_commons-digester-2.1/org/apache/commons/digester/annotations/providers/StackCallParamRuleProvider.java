/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.reflect.MethodArgument;
import org.apache.commons.digester.annotations.rules.StackCallParam;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class StackCallParamRuleProvider
implements AnnotationRuleProvider<StackCallParam, MethodArgument, CallParamRule> {
    private int paramIndex;
    private int stackIndex;

    @Override
    public void init(StackCallParam annotation, MethodArgument element) {
        this.paramIndex = element.getIndex();
        this.stackIndex = annotation.stackIndex();
    }

    @Override
    public CallParamRule get() {
        return new CallParamRule(this.paramIndex, this.stackIndex);
    }
}

