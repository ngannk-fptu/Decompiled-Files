/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package org.apache.axis.attachments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import org.apache.axis.attachments.OctetStream;

public class OctetStreamDataSource
implements DataSource {
    public static final String CONTENT_TYPE = "application/octet-stream";
    private final String name;
    private byte[] data;
    private ByteArrayOutputStream os;

    public OctetStreamDataSource(String name, OctetStream data) {
        this.name = name;
        this.data = data == null ? null : data.getBytes();
        this.os = new ByteArrayOutputStream();
    }

    public String getName() {
        return this.name;
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public InputStream getInputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
        }
        return new ByteArrayInputStream(this.data == null ? new byte[]{} : this.data);
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
        }
        return new ByteArrayOutputStream();
    }
}

