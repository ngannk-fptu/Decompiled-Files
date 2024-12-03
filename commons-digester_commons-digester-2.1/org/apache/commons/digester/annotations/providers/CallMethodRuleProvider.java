/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations.providers;

import java.lang.reflect.Method;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.CallMethod;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CallMethodRuleProvider
implements AnnotationRuleProvider<CallMethod, Method, CallMethodRule> {
    private String methodName;
    private Class<?>[] parameterTypes;

    @Override
    public void init(CallMethod annotation, Method element) {
        this.methodName = element.getName();
        this.parameterTypes = element.getParameterTypes();
    }

    @Override
    public CallMethodRule get() {
        return new CallMethodRule(this.methodName, this.parameterTypes.length, this.parameterTypes);
    }
}

