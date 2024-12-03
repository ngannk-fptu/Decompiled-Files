/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

class NullOutputStream
extends OutputStream {
    NullOutputStream() {
    }

    @Override
    public void write(byte[] buf) throws IOException {
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
    }

    @Override
    public void write(int b) throws IOException {
    }
}

