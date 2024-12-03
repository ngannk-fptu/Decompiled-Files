/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.ArrayUtil;

final class GrowableByteArrayDataOutput
extends DataOutput {
    byte[] bytes;
    int length;

    GrowableByteArrayDataOutput(int cp) {
        this.bytes = new byte[ArrayUtil.oversize(cp, 1)];
        this.length = 0;
    }

    @Override
    public void writeByte(byte b) throws IOException {
        if (this.length >= this.bytes.length) {
            this.bytes = ArrayUtil.grow(this.bytes);
        }
        this.bytes[this.length++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        int newLength = this.length + len;
        if (newLength > this.bytes.length) {
            this.bytes = ArrayUtil.grow(this.bytes, newLength);
        }
        System.arraycopy(b, off, this.bytes, this.length, len);
        this.length = newLength;
    }
}

