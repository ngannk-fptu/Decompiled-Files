/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import java.lang.reflect.Method;
import org.apache.commons.digester.SetTopRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.SetTop;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SetTopRuleProvider
implements AnnotationRuleProvider<SetTop, Method, SetTopRule> {
    private String methodName;
    private String paramType;

    @Override
    public void init(SetTop annotation, Method element) {
        this.methodName = element.getName();
        this.paramType = element.getParameterTypes()[0].getName();
    }

    @Override
    public SetTopRule get() {
        return new SetTopRule(this.methodName, this.paramType);
    }
}

