/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.render;

public final class InvalidValueException
extends Exception {
    private static final long serialVersionUID = 1L;
    private String parameter;

    public InvalidValueException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return this.parameter;
    }
}

