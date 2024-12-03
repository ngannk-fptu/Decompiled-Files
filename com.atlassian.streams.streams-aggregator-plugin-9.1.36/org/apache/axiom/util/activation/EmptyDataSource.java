/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axiom.ext.activation.SizeAwareDataSource;

public class EmptyDataSource
implements SizeAwareDataSource {
    public static final EmptyDataSource INSTANCE = new EmptyDataSource("application/octet-stream");
    private static final InputStream emptyInputStream = new InputStream(){

        public int read() throws IOException {
            return -1;
        }
    };
    private final String contentType;

    public EmptyDataSource(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getName() {
        return null;
    }

    public long getSize() {
        return 0L;
    }

    public InputStream getInputStream() throws IOException {
        return emptyInputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
}

