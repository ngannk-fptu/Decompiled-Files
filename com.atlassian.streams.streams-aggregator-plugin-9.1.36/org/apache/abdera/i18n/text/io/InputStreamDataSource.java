/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package org.apache.abdera.i18n.text.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public final class InputStreamDataSource
implements DataSource {
    public static final String DEFAULT_TYPE = "application/octet-stream";
    private final InputStream in;
    private final String ctype;

    public InputStreamDataSource(InputStream in) {
        this(in, null);
    }

    public InputStreamDataSource(InputStream in, String ctype) {
        this.in = in;
        this.ctype = ctype != null ? ctype : DEFAULT_TYPE;
    }

    public String getContentType() {
        return this.ctype;
    }

    public String getName() {
        return null;
    }

    public InputStream getInputStream() throws IOException {
        return this.in;
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }
}

