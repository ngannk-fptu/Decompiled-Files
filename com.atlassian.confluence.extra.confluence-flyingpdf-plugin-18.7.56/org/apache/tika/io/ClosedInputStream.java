/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.InputStream;

public class ClosedInputStream
extends InputStream {
    @Override
    public int read() {
        return -1;
    }
}

