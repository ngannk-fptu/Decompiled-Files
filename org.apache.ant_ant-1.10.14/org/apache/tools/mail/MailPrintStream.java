/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.mail;

import java.io.OutputStream;
import java.io.PrintStream;

class MailPrintStream
extends PrintStream {
    private int lastChar;

    public MailPrintStream(OutputStream out) {
        super(out, true);
    }

    @Override
    public void write(int b) {
        if (b == 10 && this.lastChar != 13) {
            this.rawWrite(13);
            this.rawWrite(b);
        } else if (b == 46 && this.lastChar == 10) {
            this.rawWrite(46);
            this.rawWrite(b);
        } else {
            this.rawWrite(b);
        }
        this.lastChar = b;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        for (int i = 0; i < len; ++i) {
            this.write(buf[off + i]);
        }
    }

    void rawWrite(int b) {
        super.write(b);
    }

    void rawPrint(String s) {
        for (char ch : s.toCharArray()) {
            this.rawWrite(ch);
        }
    }
}

