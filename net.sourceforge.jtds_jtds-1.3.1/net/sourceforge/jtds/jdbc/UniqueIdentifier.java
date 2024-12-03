/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import net.sourceforge.jtds.jdbc.Support;

public class UniqueIdentifier {
    private final byte[] bytes;

    public UniqueIdentifier(byte[] id) {
        this.bytes = id;
    }

    public byte[] getBytes() {
        return (byte[])this.bytes.clone();
    }

    public String toString() {
        byte[] tmp = this.bytes;
        if (this.bytes.length == 16) {
            tmp = new byte[this.bytes.length];
            System.arraycopy(this.bytes, 0, tmp, 0, this.bytes.length);
            tmp[0] = this.bytes[3];
            tmp[1] = this.bytes[2];
            tmp[2] = this.bytes[1];
            tmp[3] = this.bytes[0];
            tmp[4] = this.bytes[5];
            tmp[5] = this.bytes[4];
            tmp[6] = this.bytes[7];
            tmp[7] = this.bytes[6];
        }
        byte[] bb = new byte[1];
        StringBuilder buf = new StringBuilder(36);
        for (int i = 0; i < this.bytes.length; ++i) {
            bb[0] = tmp[i];
            buf.append(Support.toHex(bb));
            if (i != 3 && i != 5 && i != 7 && i != 9) continue;
            buf.append('-');
        }
        return buf.toString();
    }
}

