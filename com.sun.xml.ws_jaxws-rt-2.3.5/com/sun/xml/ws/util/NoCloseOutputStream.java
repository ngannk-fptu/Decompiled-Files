/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NoCloseOutputStream
extends FilterOutputStream {
    public NoCloseOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void close() throws IOException {
    }

    public void doClose() throws IOException {
        super.close();
    }
}

