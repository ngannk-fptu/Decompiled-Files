/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 */
package org.tuckey.web.filters.urlrewrite.gzip;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

public class FilterServletOutputStream
extends ServletOutputStream {
    private final OutputStream stream;

    public FilterServletOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    public void write(int b) throws IOException {
        this.stream.write(b);
    }

    public void write(byte[] b) throws IOException {
        this.stream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.stream.write(b, off, len);
    }
}

