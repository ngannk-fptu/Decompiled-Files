/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

public interface MailSender {
    public void send(SimpleMailMessage var1) throws MailException;

    public void send(SimpleMailMessage ... var1) throws MailException;
}

