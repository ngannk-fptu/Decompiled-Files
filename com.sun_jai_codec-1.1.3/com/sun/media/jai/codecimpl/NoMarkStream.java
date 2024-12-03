/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class NoMarkStream
extends FilterInputStream {
    NoMarkStream(InputStream in) {
        super(in);
    }

    public boolean markSupported() {
        return false;
    }

    public final void close() throws IOException {
    }
}

