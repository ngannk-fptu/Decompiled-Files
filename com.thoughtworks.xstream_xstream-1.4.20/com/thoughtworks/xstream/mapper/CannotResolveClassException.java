/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.XStreamException;

public class CannotResolveClassException
extends XStreamException {
    public CannotResolveClassException(String className) {
        super(className);
    }

    public CannotResolveClassException(String className, Throwable cause) {
        super(className, cause);
    }
}

