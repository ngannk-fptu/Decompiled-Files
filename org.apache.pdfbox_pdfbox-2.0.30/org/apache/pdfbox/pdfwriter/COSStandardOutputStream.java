/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfwriter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class COSStandardOutputStream
extends FilterOutputStream {
    public static final byte[] CRLF = new byte[]{13, 10};
    public static final byte[] LF = new byte[]{10};
    public static final byte[] EOL = new byte[]{10};
    private long position = 0L;
    private boolean onNewLine = false;

    public COSStandardOutputStream(OutputStream out) {
        super(out);
    }

    @Deprecated
    public COSStandardOutputStream(OutputStream out, int position) {
        super(out);
        this.position = position;
    }

    public COSStandardOutputStream(OutputStream out, long position) {
        super(out);
        this.position = position;
    }

    public long getPos() {
        return this.position;
    }

    public boolean isOnNewLine() {
        return this.onNewLine;
    }

    public void setOnNewLine(boolean newOnNewLine) {
        this.onNewLine = newOnNewLine;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.setOnNewLine(false);
        this.out.write(b, off, len);
        this.position += (long)len;
    }

    @Override
    public void write(int b) throws IOException {
        this.setOnNewLine(false);
        this.out.write(b);
        ++this.position;
    }

    public void writeCRLF() throws IOException {
        this.write(CRLF);
    }

    public void writeEOL() throws IOException {
        if (!this.isOnNewLine()) {
            this.write(EOL);
            this.setOnNewLine(true);
        }
    }

    public void writeLF() throws IOException {
        this.write(LF);
    }
}

