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
import javax.activation.ActivationDataFlavor;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

public class multipart_mixed
extends handler_base {
    private static ActivationDataFlavor[] myDF = new ActivationDataFlavor[]{new ActivationDataFlavor(Multipart.class, "multipart/mixed", "Multipart")};

    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return myDF;
    }

    public Object getContent(DataSource ds) throws IOException {
        try {
            return new MimeMultipart(ds);
        }
        catch (MessagingException e) {
            IOException ioex = new IOException("Exception while constructing MimeMultipart");
            ioex.initCause(e);
            throw ioex;
        }
    }

    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (!(obj instanceof Multipart)) {
            throw new IOException("\"" + this.getDataFlavors()[0].getMimeType() + "\" DataContentHandler requires Multipart object, was given object of type " + obj.getClass().toString() + "; obj.cl " + obj.getClass().getClassLoader() + ", Multipart.cl " + Multipart.class.getClassLoader());
        }
        try {
            ((Multipart)obj).writeTo(os);
        }
        catch (MessagingException e) {
            IOException ioex = new IOException("Exception writing Multipart");
            ioex.initCause(e);
            throw ioex;
        }
    }
}

