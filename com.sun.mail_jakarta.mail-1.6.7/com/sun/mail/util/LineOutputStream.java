/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.util;

import com.sun.mail.util.ASCIIUtility;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LineOutputStream
extends FilterOutputStream {
    private boolean allowutf8;
    private static byte[] newline = new byte[2];

    public LineOutputStream(OutputStream out) {
        this(out, false);
    }

    public LineOutputStream(OutputStream out, boolean allowutf8) {
        super(out);
        this.allowutf8 = allowutf8;
    }

    public void writeln(String s) throws IOException {
        byte[] bytes = this.allowutf8 ? s.getBytes(StandardCharsets.UTF_8) : ASCIIUtility.getBytes(s);
        this.out.write(bytes);
        this.out.write(newline);
    }

    public void writeln() throws IOException {
        this.out.write(newline);
    }

    static {
        LineOutputStream.newline[0] = 13;
        LineOutputStream.newline[1] = 10;
    }
}

