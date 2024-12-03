/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.smtp;

import com.sun.mail.smtp.SMTPSSLTransport;
import com.sun.mail.util.DefaultProvider;
import javax.mail.Provider;

@DefaultProvider
public class SMTPSSLProvider
extends Provider {
    public SMTPSSLProvider() {
        super(Provider.Type.TRANSPORT, "smtps", SMTPSSLTransport.class.getName(), "Oracle", null);
    }
}

