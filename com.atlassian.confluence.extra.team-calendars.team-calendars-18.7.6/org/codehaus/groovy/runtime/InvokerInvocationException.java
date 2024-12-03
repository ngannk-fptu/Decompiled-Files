/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.GroovyRuntimeException;
import java.lang.reflect.InvocationTargetException;

public class InvokerInvocationException
extends GroovyRuntimeException {
    public InvokerInvocationException(InvocationTargetException e) {
        super(e.getTargetException());
    }

    public InvokerInvocationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        Throwable cause = this.getCause();
        return cause == null ? "java.lang.NullPointerException" : cause.toString();
    }
}

