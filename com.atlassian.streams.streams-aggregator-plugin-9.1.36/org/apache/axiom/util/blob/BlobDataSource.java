/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axiom.ext.activation.SizeAwareDataSource;
import org.apache.axiom.util.blob.Blob;

public class BlobDataSource
implements SizeAwareDataSource {
    private final Blob blob;
    private final String contentType;

    public BlobDataSource(Blob blob, String contentType) {
        this.blob = blob;
        this.contentType = contentType;
    }

    public InputStream getInputStream() throws IOException {
        return this.blob.getInputStream();
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getName() {
        return null;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public long getSize() {
        return this.blob.getLength();
    }
}

