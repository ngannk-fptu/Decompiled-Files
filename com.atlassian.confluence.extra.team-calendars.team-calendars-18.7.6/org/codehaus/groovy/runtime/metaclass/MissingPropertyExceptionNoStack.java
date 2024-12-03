/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.MissingPropertyException;

public class MissingPropertyExceptionNoStack
extends MissingPropertyException {
    public MissingPropertyExceptionNoStack(String propertyName, Class theClass) {
        super(propertyName, theClass);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}

