/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail;

import com.atlassian.mail.MailProtocol;

public class MailConstants {
    public static final MailProtocol DEFAULT_POP_PROTOCOL = MailProtocol.POP;
    public static final String DEFAULT_POP_PORT = "110";
    public static final String DEFAULT_SMTP_PORT = "25";
    public static final MailProtocol DEFAULT_SMTP_PROTOCOL = MailProtocol.SMTP;
    public static final long DEFAULT_TIMEOUT = 10000L;
    public static final String DEFAULT_IMAP_PORT = MailProtocol.IMAP.getDefaultPort();
    public static final MailProtocol DEFAULT_IMAP_PROTOCOL = MailProtocol.IMAP;
}

