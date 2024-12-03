/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.activation.DataSource
 */
package com.sun.mail.handlers;

import com.sun.mail.handlers.handler_base;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class message_rfc822
extends handler_base {
    private static ActivationDataFlavor[] ourDataFlavor = new ActivationDataFlavor[]{new ActivationDataFlavor(Message.class, "message/rfc822", "Message")};

    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return ourDataFlavor;
    }

    public Object getContent(DataSource ds) throws IOException {
        try {
            Session session;
            if (ds instanceof MessageAware) {
                MessageContext mc = ((MessageAware)ds).getMessageContext();
                session = mc.getSession();
            } else {
                session = Session.getDefaultInstance(new Properties(), null);
            }
            return new MimeMessage(session, ds.getInputStream());
        }
        catch (MessagingException me) {
            IOException ioex = new IOException("Exception creating MimeMessage in message/rfc822 DataContentHandler");
            ioex.initCause(me);
            throw ioex;
        }
    }

    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (!(obj instanceof Message)) {
            throw new IOException("\"" + this.getDataFlavors()[0].getMimeType() + "\" DataContentHandler requires Message object, was given object of type " + obj.getClass().toString() + "; obj.cl " + obj.getClass().getClassLoader() + ", Message.cl " + Message.class.getClassLoader());
        }
        Message m = (Message)obj;
        try {
            m.writeTo(os);
        }
        catch (MessagingException me) {
            IOException ioex = new IOException("Exception writing message");
            ioex.initCause(me);
            throw ioex;
        }
    }
}

