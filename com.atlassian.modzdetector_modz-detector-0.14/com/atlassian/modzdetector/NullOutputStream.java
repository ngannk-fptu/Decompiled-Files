/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import java.io.IOException;
import java.io.OutputStream;

final class NullOutputStream
extends OutputStream {
    NullOutputStream() {
    }

    public final void write(int b) throws IOException {
    }
}

