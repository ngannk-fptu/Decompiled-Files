/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

public class FieldNotRequestedException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FieldNotRequestedException(String msg) {
        super(msg);
    }

    public FieldNotRequestedException(String msg, Exception ex) {
        super(msg, ex);
    }
}

