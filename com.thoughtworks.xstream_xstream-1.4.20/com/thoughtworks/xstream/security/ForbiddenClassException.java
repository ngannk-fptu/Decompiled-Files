/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.AbstractSecurityException;

public class ForbiddenClassException
extends AbstractSecurityException {
    public ForbiddenClassException(Class type) {
        super(type == null ? "null" : type.getName());
    }
}

