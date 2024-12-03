/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.MimeMessage
 */
package org.springframework.mail.javamail;

import java.io.InputStream;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

public interface JavaMailSender
extends MailSender {
    public MimeMessage createMimeMessage();

    public MimeMessage createMimeMessage(InputStream var1) throws MailException;

    public void send(MimeMessage var1) throws MailException;

    public void send(MimeMessage ... var1) throws MailException;

    public void send(MimeMessagePreparator var1) throws MailException;

    public void send(MimeMessagePreparator ... var1) throws MailException;
}

