/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.support;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream
extends OutputStream {
    @Override
    public void write(int b) throws IOException {
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
    }
}

