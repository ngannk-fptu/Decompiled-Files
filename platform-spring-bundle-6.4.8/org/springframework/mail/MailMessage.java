/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.mail;

import java.util.Date;
import org.springframework.mail.MailParseException;

public interface MailMessage {
    public void setFrom(String var1) throws MailParseException;

    public void setReplyTo(String var1) throws MailParseException;

    public void setTo(String var1) throws MailParseException;

    public void setTo(String ... var1) throws MailParseException;

    public void setCc(String var1) throws MailParseException;

    public void setCc(String ... var1) throws MailParseException;

    public void setBcc(String var1) throws MailParseException;

    public void setBcc(String ... var1) throws MailParseException;

    public void setSentDate(Date var1) throws MailParseException;

    public void setSubject(String var1) throws MailParseException;

    public void setText(String var1) throws MailParseException;
}

