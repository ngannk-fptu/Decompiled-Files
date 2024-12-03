/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.smime.SMimeParserContext;

public class CanonicalOutputStream
extends FilterOutputStream {
    protected int lastb = -1;
    protected static byte[] newline = new byte[2];
    private final boolean is7Bit;

    public CanonicalOutputStream(SMimeParserContext parserContext, Headers headers, OutputStream outputstream) {
        super(outputstream);
        this.is7Bit = headers.getContentType() != null ? headers.getContentType() != null && !headers.getContentType().equals("binary") : parserContext.getDefaultContentTransferEncoding().equals("7bit");
    }

    @Override
    public void write(int i) throws IOException {
        if (this.is7Bit) {
            if (i == 13) {
                this.out.write(newline);
            } else if (i == 10) {
                if (this.lastb != 13) {
                    this.out.write(newline);
                }
            } else {
                this.out.write(i);
            }
        } else {
            this.out.write(i);
        }
        this.lastb = i;
    }

    @Override
    public void write(byte[] buf) throws IOException {
        this.write(buf, 0, buf.length);
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        for (int i = off; i != off + len; ++i) {
            this.write(buf[i]);
        }
    }

    public void writeln() throws IOException {
        this.out.write(newline);
    }

    static {
        CanonicalOutputStream.newline[0] = 13;
        CanonicalOutputStream.newline[1] = 10;
    }
}

