/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.template;

public class TemplateRenderingException
extends RuntimeException {
    public TemplateRenderingException(String message) {
        super(message);
    }

    public TemplateRenderingException(String message, Throwable wrappedException) {
        super(message, wrappedException);
    }
}

