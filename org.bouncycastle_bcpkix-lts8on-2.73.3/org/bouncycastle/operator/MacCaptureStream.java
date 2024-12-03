/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.operator;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;

public class MacCaptureStream
extends OutputStream {
    private final OutputStream cOut;
    private final byte[] mac;
    int macIndex = 0;

    public MacCaptureStream(OutputStream cOut, int macLength) {
        this.cOut = cOut;
        this.mac = new byte[macLength];
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        if (len >= this.mac.length) {
            this.cOut.write(this.mac, 0, this.macIndex);
            this.macIndex = this.mac.length;
            System.arraycopy(buf, off + len - this.mac.length, this.mac, 0, this.mac.length);
            this.cOut.write(buf, off, len - this.mac.length);
        } else {
            for (int i = 0; i != len; ++i) {
                this.write(buf[off + i]);
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (this.macIndex == this.mac.length) {
            byte b1 = this.mac[0];
            System.arraycopy(this.mac, 1, this.mac, 0, this.mac.length - 1);
            this.mac[this.mac.length - 1] = (byte)b;
            this.cOut.write(b1);
        } else {
            this.mac[this.macIndex++] = (byte)b;
        }
    }

    public byte[] getMac() {
        return Arrays.clone((byte[])this.mac);
    }
}

