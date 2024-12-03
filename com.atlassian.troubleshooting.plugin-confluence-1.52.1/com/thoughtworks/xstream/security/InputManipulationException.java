/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.AbstractSecurityException;

public class InputManipulationException
extends AbstractSecurityException {
    private static final long serialVersionUID = 20210921L;

    public InputManipulationException(String message) {
        super(message);
    }
}

