/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NoCloseInputStream
extends FilterInputStream {
    public NoCloseInputStream(InputStream is) {
        super(is);
    }

    @Override
    public void close() throws IOException {
    }

    public void doClose() throws IOException {
        super.close();
    }
}

