/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public final class LineInputStream
extends FilterInputStream {
    private char[] lineBuffer = null;

    public LineInputStream(InputStream in) {
        super(in);
    }

    public String readLine() throws IOException {
        int c1;
        InputStream in = this.in;
        char[] buf = this.lineBuffer;
        if (buf == null) {
            buf = this.lineBuffer = new char[128];
        }
        int room = buf.length;
        int offset = 0;
        while ((c1 = in.read()) != -1 && c1 != 10) {
            if (c1 == 13) {
                int c2 = in.read();
                if (c2 == 13) {
                    c2 = in.read();
                }
                if (c2 == 10) break;
                if (!(in instanceof PushbackInputStream)) {
                    in = this.in = new PushbackInputStream(in);
                }
                ((PushbackInputStream)in).unread(c2);
                break;
            }
            if (--room < 0) {
                buf = new char[offset + 128];
                room = buf.length - offset - 1;
                System.arraycopy(this.lineBuffer, 0, buf, 0, offset);
                this.lineBuffer = buf;
            }
            buf[offset++] = (char)c1;
        }
        if (c1 == -1 && offset == 0) {
            return null;
        }
        return String.copyValueOf(buf, 0, offset);
    }
}

