/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream
extends OutputStream {
    public static final NullOutputStream INSTANCE;
    @Deprecated
    public static final NullOutputStream NULL_OUTPUT_STREAM;

    @Deprecated
    public NullOutputStream() {
    }

    @Override
    public void write(byte[] b) throws IOException {
    }

    @Override
    public void write(byte[] b, int off, int len) {
    }

    @Override
    public void write(int b) {
    }

    static {
        NULL_OUTPUT_STREAM = INSTANCE = new NullOutputStream();
    }
}

