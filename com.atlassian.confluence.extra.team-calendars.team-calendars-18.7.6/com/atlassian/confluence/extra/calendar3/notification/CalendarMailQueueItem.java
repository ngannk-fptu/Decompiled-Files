/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.Task
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.queue.AbstractMailQueueItem
 *  com.atlassian.mail.queue.SingleMailQueueItem
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMultipart
 */
package com.atlassian.confluence.extra.calendar3.notification;

import com.atlassian.core.task.Task;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.queue.AbstractMailQueueItem;
import com.atlassian.mail.queue.SingleMailQueueItem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class CalendarMailQueueItem
extends AbstractMailQueueItem
implements Serializable,
Task {
    private static final long serialVersionUID = 1L;
    private Email email;
    private SingleMailQueueItem delegateMailQueueItem;

    public CalendarMailQueueItem(Email email) {
        this.email = email;
        this.delegateMailQueueItem = new SingleMailQueueItem(email);
    }

    public void send() throws MailException {
        this.delegateMailQueueItem.send();
    }

    public void execute() throws Exception {
        this.delegateMailQueueItem.send();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        int numBodyParts;
        Multipart multipart = this.email.getMultipart();
        out.writeObject(this.email.getTo());
        out.writeObject(this.email.getSubject());
        out.writeObject(this.email.getFrom());
        out.writeObject(this.email.getFromName());
        out.writeObject(this.email.getMimeType());
        out.writeObject(this.email.getBody());
        try {
            numBodyParts = multipart.getCount();
        }
        catch (MessagingException e) {
            throw new IOException(e);
        }
        out.writeInt(numBodyParts);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (int i = 0; i < numBodyParts; ++i) {
            os.reset();
            try {
                multipart.getBodyPart(i).writeTo((OutputStream)os);
            }
            catch (MessagingException e) {
                throw new IOException(e);
            }
            out.writeObject(os.toByteArray());
        }
    }

    public String getSubject() {
        return this.email.getSubject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.email = new Email((String)in.readObject());
        this.email.setSubject((String)in.readObject());
        this.email.setFrom((String)in.readObject());
        this.email.setFromName((String)in.readObject());
        this.email.setMimeType((String)in.readObject());
        this.email.setBody((String)in.readObject());
        int numBodyParts = in.readInt();
        if (numBodyParts > 0) {
            MimeMultipart multipart = new MimeMultipart("related");
            for (int i = 0; i < numBodyParts; ++i) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])in.readObject());
                try {
                    MimeBodyPart mimeBodyPart = new MimeBodyPart((InputStream)inputStream);
                    multipart.addBodyPart((BodyPart)mimeBodyPart);
                    continue;
                }
                catch (MessagingException e) {
                    throw new IOException(e);
                }
            }
            this.email.setMultipart((Multipart)multipart);
        }
        this.delegateMailQueueItem = new SingleMailQueueItem(this.email);
    }

    public Email getEmail() {
        return this.email;
    }
}

