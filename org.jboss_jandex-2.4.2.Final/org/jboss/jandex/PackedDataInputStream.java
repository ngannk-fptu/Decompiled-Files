/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

class PackedDataInputStream
extends DataInputStream {
    static final int MAX_1BYTE = 127;

    public PackedDataInputStream(InputStream in) {
        super(in);
    }

    public int readPackedU32() throws IOException {
        byte b;
        int i = 0;
        do {
            b = this.readByte();
            i = i << 7 | b & 0x7F;
        } while ((b & 0x80) == 128);
        return i;
    }
}

