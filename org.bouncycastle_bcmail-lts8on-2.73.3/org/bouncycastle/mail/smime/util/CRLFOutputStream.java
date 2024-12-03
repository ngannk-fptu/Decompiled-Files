/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mail.smime.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CRLFOutputStream
extends FilterOutputStream {
    protected int lastb = -1;
    protected static byte[] newline = new byte[2];

    public CRLFOutputStream(OutputStream outputstream) {
        super(outputstream);
    }

    @Override
    public void write(int i) throws IOException {
        if (i == 13) {
            this.out.write(newline);
        } else if (i == 10) {
            if (this.lastb != 13) {
                this.out.write(newline);
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
        CRLFOutputStream.newline[0] = 13;
        CRLFOutputStream.newline[1] = 10;
    }
}

