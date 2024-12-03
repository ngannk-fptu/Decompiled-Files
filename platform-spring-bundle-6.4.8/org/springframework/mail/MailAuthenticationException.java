/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.mail;

import org.springframework.mail.MailException;

public class MailAuthenticationException
extends MailException {
    public MailAuthenticationException(String msg) {
        super(msg);
    }

    public MailAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MailAuthenticationException(Throwable cause) {
        super("Authentication failed", cause);
    }
}

