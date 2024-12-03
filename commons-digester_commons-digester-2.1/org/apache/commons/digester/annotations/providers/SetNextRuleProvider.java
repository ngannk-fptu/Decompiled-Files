/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import java.lang.reflect.Method;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.SetNext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SetNextRuleProvider
implements AnnotationRuleProvider<SetNext, Method, SetNextRule> {
    private String methodName;
    private String paramType;

    @Override
    public void init(SetNext annotation, Method element) {
        this.methodName = element.getName();
        this.paramType = element.getParameterTypes()[0].getName();
    }

    @Override
    public SetNextRule get() {
        return new SetNextRule(this.methodName, this.paramType);
    }
}

