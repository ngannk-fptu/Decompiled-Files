/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.XStreamException;

public abstract class AbstractSecurityException
extends XStreamException {
    private static final long serialVersionUID = 20210921L;

    public AbstractSecurityException(String message) {
        super(message);
    }
}

