/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.validation;

public class ValidationException
extends Exception {
    private final String invalidXml;

    public ValidationException(Throwable cause) {
        super(cause);
        this.invalidXml = null;
    }

    public ValidationException(String invalidXml, Throwable cause) {
        super(cause);
        this.invalidXml = invalidXml;
    }

    public String getInvalidXml() {
        return this.invalidXml;
    }
}

