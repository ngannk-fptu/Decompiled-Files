/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  javax.mail.internet.MimeMultipart
 */
package org.apache.axis.attachments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;

public class MimeMultipartDataSource
implements DataSource {
    public static final String CONTENT_TYPE = "multipart/mixed";
    private final String name;
    private final String contentType;
    private byte[] data;
    private ByteArrayOutputStream os;

    public MimeMultipartDataSource(String name, MimeMultipart data) {
        this.name = name;
        this.contentType = data == null ? CONTENT_TYPE : data.getContentType();
        this.os = new ByteArrayOutputStream();
        try {
            if (data != null) {
                data.writeTo((OutputStream)this.os);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public String getName() {
        return this.name;
    }

    public String getContentType() {
        return this.contentType;
    }

    public InputStream getInputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
            this.os.reset();
        }
        return new ByteArrayInputStream(this.data == null ? new byte[]{} : this.data);
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
            this.os.reset();
        }
        return this.os;
    }
}

