/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.mail;

import org.springframework.mail.MailException;

public class MailParseException
extends MailException {
    public MailParseException(String msg) {
        super(msg);
    }

    public MailParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MailParseException(Throwable cause) {
        super("Could not parse mail", cause);
    }
}

