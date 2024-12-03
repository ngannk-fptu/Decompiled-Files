/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import java.io.IOException;
import java.io.InputStream;

public class NullInputStream
extends InputStream {
    @Override
    public int read() throws IOException {
        return -1;
    }

    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public long skip(long l) throws IOException {
        return 0L;
    }
}

