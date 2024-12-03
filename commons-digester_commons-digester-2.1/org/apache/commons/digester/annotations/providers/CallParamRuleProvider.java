/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.reflect.MethodArgument;
import org.apache.commons.digester.annotations.rules.CallParam;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CallParamRuleProvider
implements AnnotationRuleProvider<CallParam, MethodArgument, CallParamRule> {
    private int index;

    @Override
    public void init(CallParam annotation, MethodArgument element) {
        this.index = element.getIndex();
    }

    @Override
    public CallParamRule get() {
        return new CallParamRule(this.index);
    }
}

