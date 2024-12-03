/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail;

public class MailException
extends Exception {
    public MailException() {
    }

    public MailException(String s) {
        super(s);
    }

    public MailException(Throwable throwable) {
        super(throwable);
    }

    public MailException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

