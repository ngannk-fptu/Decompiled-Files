/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.v7;

import org.codehaus.groovy.reflection.GroovyClassValue;

public class GroovyClassValueJava7<T>
extends ClassValue<T>
implements GroovyClassValue<T> {
    private final GroovyClassValue.ComputeValue<T> computeValue;

    public GroovyClassValueJava7(GroovyClassValue.ComputeValue<T> computeValue) {
        this.computeValue = computeValue;
    }

    @Override
    protected T computeValue(Class<?> type) {
        return this.computeValue.computeValue(type);
    }
}

