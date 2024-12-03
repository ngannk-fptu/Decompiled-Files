/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset;

import com.opensymphony.module.propertyset.PropertyException;

public class PropertyImplementationException
extends PropertyException {
    protected Throwable original;

    public PropertyImplementationException() {
    }

    public PropertyImplementationException(String msg) {
        super(msg);
    }

    public PropertyImplementationException(String msg, Throwable original) {
        super(msg);
        this.original = original;
    }

    public PropertyImplementationException(Throwable original) {
        this(original.getLocalizedMessage(), original);
    }

    public Throwable getRootCause() {
        return this.original;
    }
}

