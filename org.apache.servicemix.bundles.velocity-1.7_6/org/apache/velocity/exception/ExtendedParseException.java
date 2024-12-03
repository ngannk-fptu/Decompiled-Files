/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.exception;

public interface ExtendedParseException {
    public String getTemplateName();

    public int getLineNumber();

    public int getColumnNumber();
}

