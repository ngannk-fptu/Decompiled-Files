/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axiom.attachments.PartImpl;
import org.apache.axiom.ext.activation.SizeAwareDataSource;

class PartDataSource
implements SizeAwareDataSource {
    private final PartImpl part;

    public PartDataSource(PartImpl part) {
        this.part = part;
    }

    public String getContentType() {
        return this.part.getDataSourceContentType();
    }

    public InputStream getInputStream() throws IOException {
        return this.part.getInputStream(true);
    }

    public String getName() {
        return this.part.getContentID();
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public long getSize() {
        return this.part.getSize();
    }
}

