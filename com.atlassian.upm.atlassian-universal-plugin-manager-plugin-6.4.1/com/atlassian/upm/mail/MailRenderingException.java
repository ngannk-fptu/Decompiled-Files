/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.mail;

public class MailRenderingException
extends RuntimeException {
    public MailRenderingException() {
    }

    public MailRenderingException(String message) {
        super(message);
    }

    public MailRenderingException(Throwable t) {
        super(t);
    }

    public MailRenderingException(String message, Throwable t) {
        super(message, t);
    }
}

