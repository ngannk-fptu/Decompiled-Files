/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.IHasPosition;

public class ParserException
extends RuntimeException {
    private IHasPosition token;

    public ParserException(String message, IHasPosition token) {
        super(message);
        this.token = token;
    }

    public IHasPosition getLocation() {
        return this.token;
    }
}

