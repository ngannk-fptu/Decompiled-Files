/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class MemoryEfficientByteArrayOutputStream
extends ByteArrayOutputStream {
    private static final int BEST_GUESS_SIZE = 512;
    private static int lastSize = 512;

    public MemoryEfficientByteArrayOutputStream(int size) {
        super(size);
    }

    public synchronized byte[] getBytes() {
        if (this.buf.length == this.size()) {
            return this.buf;
        }
        byte[] copy = new byte[this.size()];
        System.arraycopy(this.buf, 0, copy, 0, this.size());
        return copy;
    }

    public static MemoryEfficientByteArrayOutputStream serialize(Serializable serializable, int estimatedPayloadSize) throws IOException {
        MemoryEfficientByteArrayOutputStream outstr = new MemoryEfficientByteArrayOutputStream(estimatedPayloadSize);
        ObjectOutputStream objstr = new ObjectOutputStream(outstr);
        objstr.writeObject(serializable);
        objstr.close();
        return outstr;
    }

    public static MemoryEfficientByteArrayOutputStream serialize(Serializable serializable) throws IOException {
        MemoryEfficientByteArrayOutputStream outstr = new MemoryEfficientByteArrayOutputStream(lastSize);
        ObjectOutputStream objstr = new ObjectOutputStream(outstr);
        objstr.writeObject(serializable);
        objstr.close();
        lastSize = outstr.getBytes().length;
        return outstr;
    }
}

