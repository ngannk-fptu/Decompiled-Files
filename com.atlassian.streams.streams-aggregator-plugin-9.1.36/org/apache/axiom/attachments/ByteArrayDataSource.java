/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axiom.ext.activation.SizeAwareDataSource;

public class ByteArrayDataSource
implements SizeAwareDataSource {
    private byte[] data;
    private String type;

    public ByteArrayDataSource(byte[] data, String type) {
        this.data = data;
        this.type = type;
    }

    public ByteArrayDataSource(byte[] data) {
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentType() {
        if (this.type == null) {
            return "application/octet-stream";
        }
        return this.type;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.data == null ? new byte[]{} : this.data);
    }

    public String getName() {
        return "ByteArrayDataSource";
    }

    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Not Supported");
    }

    public long getSize() {
        return this.data == null ? 0L : (long)this.data.length;
    }
}

