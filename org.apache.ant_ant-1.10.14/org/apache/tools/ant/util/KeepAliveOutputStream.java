/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class KeepAliveOutputStream
extends FilterOutputStream {
    public KeepAliveOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void close() throws IOException {
    }

    public static PrintStream wrapSystemOut() {
        return KeepAliveOutputStream.wrap(System.out);
    }

    public static PrintStream wrapSystemErr() {
        return KeepAliveOutputStream.wrap(System.err);
    }

    private static PrintStream wrap(PrintStream ps) {
        return new PrintStream(new KeepAliveOutputStream(ps));
    }
}

