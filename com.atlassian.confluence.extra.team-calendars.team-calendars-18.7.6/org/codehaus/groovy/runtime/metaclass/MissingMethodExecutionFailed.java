/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack;

public class MissingMethodExecutionFailed
extends MissingMethodExceptionNoStack {
    private Throwable cause;

    public MissingMethodExecutionFailed(String method, Class type, Object[] arguments, boolean isStatic, Throwable cause) {
        super(method, type, arguments, isStatic);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

