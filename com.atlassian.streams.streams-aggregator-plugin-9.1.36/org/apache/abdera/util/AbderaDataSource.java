/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package org.apache.abdera.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import org.apache.abdera.model.Base;
import org.apache.abdera.util.MimeTypeHelper;

public final class AbderaDataSource
implements DataSource {
    private final byte[] data;
    private final String mimetype;
    private final String name;

    public AbderaDataSource(Base base) {
        this.data = this.read(base);
        this.mimetype = MimeTypeHelper.getMimeType(base);
        this.name = base.getClass().getName();
    }

    private byte[] read(Base base) {
        byte[] data = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            base.writeTo(out);
            data = out.toByteArray();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return data;
    }

    public String getContentType() {
        return this.mimetype;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.data);
    }

    public String getName() {
        return "Abdera Data Source::" + this.name;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
}

