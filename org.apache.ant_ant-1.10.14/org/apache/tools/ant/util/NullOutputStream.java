/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.OutputStream;

public class NullOutputStream
extends OutputStream {
    public static NullOutputStream INSTANCE = new NullOutputStream();

    private NullOutputStream() {
    }

    @Override
    public void write(byte[] b) {
    }

    @Override
    public void write(byte[] b, int off, int len) {
    }

    @Override
    public void write(int i) {
    }
}

