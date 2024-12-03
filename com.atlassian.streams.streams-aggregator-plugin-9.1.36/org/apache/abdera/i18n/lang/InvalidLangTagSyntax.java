/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.lang;

public class InvalidLangTagSyntax
extends RuntimeException {
    private static final long serialVersionUID = -2653819135178550519L;

    public InvalidLangTagSyntax() {
    }

    public InvalidLangTagSyntax(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLangTagSyntax(String message) {
        super(message);
    }

    public InvalidLangTagSyntax(Throwable cause) {
        super(cause);
    }
}

