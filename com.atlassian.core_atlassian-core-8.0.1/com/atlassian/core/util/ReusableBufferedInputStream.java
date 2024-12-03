/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReusableBufferedInputStream
extends BufferedInputStream {
    public ReusableBufferedInputStream(InputStream inputStream) {
        super(inputStream);
        super.mark(Integer.MAX_VALUE);
    }

    @Override
    public void close() throws IOException {
        super.reset();
    }

    public void destroy() throws IOException {
        super.close();
    }
}

