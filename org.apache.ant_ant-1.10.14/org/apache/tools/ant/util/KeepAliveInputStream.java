/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class KeepAliveInputStream
extends FilterInputStream {
    public KeepAliveInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() throws IOException {
    }

    public static InputStream wrapSystemIn() {
        return new KeepAliveInputStream(System.in);
    }
}

