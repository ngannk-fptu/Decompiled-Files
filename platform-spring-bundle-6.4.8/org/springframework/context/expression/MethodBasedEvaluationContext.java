/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.expression;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class MethodBasedEvaluationContext
extends StandardEvaluationContext {
    private final Method method;
    private final Object[] arguments;
    private final ParameterNameDiscoverer parameterNameDiscoverer;
    private boolean argumentsLoaded = false;

    public MethodBasedEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject);
        this.method = method;
        this.arguments = arguments;
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Override
    @Nullable
    public Object lookupVariable(String name) {
        Object variable = super.lookupVariable(name);
        if (variable != null) {
            return variable;
        }
        if (!this.argumentsLoaded) {
            this.lazyLoadArguments();
            this.argumentsLoaded = true;
            variable = super.lookupVariable(name);
        }
        return variable;
    }

    protected void lazyLoadArguments() {
        if (ObjectUtils.isEmpty(this.arguments)) {
            return;
        }
        String[] paramNames = this.parameterNameDiscoverer.getParameterNames(this.method);
        int paramCount = paramNames != null ? paramNames.length : this.method.getParameterCount();
        int argsCount = this.arguments.length;
        for (int i2 = 0; i2 < paramCount; ++i2) {
            Object[] value = null;
            if (argsCount > paramCount && i2 == paramCount - 1) {
                value = Arrays.copyOfRange(this.arguments, i2, argsCount);
            } else if (argsCount > i2) {
                value = this.arguments[i2];
            }
            this.setVariable("a" + i2, value);
            this.setVariable("p" + i2, value);
            if (paramNames == null || paramNames[i2] == null) continue;
            this.setVariable(paramNames[i2], value);
        }
    }
}

