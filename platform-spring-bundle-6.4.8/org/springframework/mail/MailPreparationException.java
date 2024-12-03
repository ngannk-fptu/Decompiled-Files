/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.mail;

import org.springframework.mail.MailException;

public class MailPreparationException
extends MailException {
    public MailPreparationException(String msg) {
        super(msg);
    }

    public MailPreparationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MailPreparationException(Throwable cause) {
        super("Could not prepare mail", cause);
    }
}

