/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.smtp;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.DefaultProvider;
import javax.mail.Provider;

@DefaultProvider
public class SMTPProvider
extends Provider {
    public SMTPProvider() {
        super(Provider.Type.TRANSPORT, "smtp", SMTPTransport.class.getName(), "Oracle", null);
    }
}

