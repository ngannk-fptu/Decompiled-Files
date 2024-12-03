/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.Closure;
import java.util.Map;
import org.codehaus.groovy.runtime.callsite.BooleanReturningMethodInvoker;

public class BooleanClosureWrapper {
    private final BooleanReturningMethodInvoker bmi;
    private final Closure wrapped;
    private final int numberOfArguments;

    public BooleanClosureWrapper(Closure wrapped) {
        this.wrapped = wrapped;
        this.bmi = new BooleanReturningMethodInvoker("call");
        this.numberOfArguments = wrapped.getMaximumNumberOfParameters();
    }

    public boolean call(Object ... args) {
        return this.bmi.invoke(this.wrapped, args);
    }

    public <K, V> boolean callForMap(Map.Entry<K, V> entry) {
        if (this.numberOfArguments == 2) {
            return this.call(entry.getKey(), entry.getValue());
        }
        return this.call(entry);
    }
}

