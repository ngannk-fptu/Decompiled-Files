/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class PackedDataOutputStream
extends DataOutputStream {
    static final int MAX_1BYTE = 127;
    static final int MAX_2BYTE = 16383;
    static final int MAX_3BYTE = 0x1FFFFF;
    static final int MAX_4BYTE = 0xFFFFFFF;

    public PackedDataOutputStream(OutputStream out) {
        super(out);
    }

    public void writePackedU32(int i) throws IOException {
        if ((i & 0xFFFFFF80) == 0) {
            this.writeByte(i);
        } else if ((i & 0xFFFFC000) == 0) {
            this.writeByte(i >>> 7 & 0x7F | 0x80);
            this.writeByte(i & 0x7F);
        } else if ((i & 0xFFE00000) == 0) {
            this.writeByte(i >>> 14 & 0x7F | 0x80);
            this.writeByte(i >>> 7 & 0x7F | 0x80);
            this.writeByte(i & 0x7F);
        } else if ((i & 0xF0000000) == 0) {
            this.writeByte(i >>> 21 & 0x7F | 0x80);
            this.writeByte(i >>> 14 & 0x7F | 0x80);
            this.writeByte(i >>> 7 & 0x7F | 0x80);
            this.writeByte(i & 0x7F);
        } else {
            this.writeByte(i >>> 28 & 0x7F | 0x80);
            this.writeByte(i >>> 21 & 0x7F | 0x80);
            this.writeByte(i >>> 14 & 0x7F | 0x80);
            this.writeByte(i >>> 7 & 0x7F | 0x80);
            this.writeByte(i & 0x7F);
        }
    }
}

