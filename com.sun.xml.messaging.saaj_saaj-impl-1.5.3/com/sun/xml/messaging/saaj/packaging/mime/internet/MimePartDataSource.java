/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;

public final class MimePartDataSource
implements DataSource {
    private final MimeBodyPart part;

    public MimePartDataSource(MimeBodyPart part) {
        this.part = part;
    }

    public InputStream getInputStream() throws IOException {
        try {
            InputStream is = this.part.getContentStream();
            String encoding = this.part.getEncoding();
            if (encoding != null) {
                return MimeUtility.decode(is, encoding);
            }
            return is;
        }
        catch (MessagingException mex) {
            throw new IOException(mex.getMessage());
        }
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException();
    }

    public String getContentType() {
        return this.part.getContentType();
    }

    public String getName() {
        try {
            return this.part.getFileName();
        }
        catch (MessagingException mex) {
            return "";
        }
    }
}

