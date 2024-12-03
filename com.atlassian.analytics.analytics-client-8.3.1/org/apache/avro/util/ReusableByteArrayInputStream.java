/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.io.ByteArrayInputStream;

public class ReusableByteArrayInputStream
extends ByteArrayInputStream {
    public ReusableByteArrayInputStream() {
        super(new byte[0]);
    }

    public void setByteArray(byte[] buf, int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
        this.mark = offset;
    }
}

