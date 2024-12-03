/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.poi.util.Internal;

@Internal
public final class UnhandledDataStructure {
    private final byte[] _buf;

    public UnhandledDataStructure(byte[] buf, int offset, int length) {
        int offsetEnd = offset + length;
        if (offsetEnd > buf.length || offsetEnd < 0) {
            throw new IndexOutOfBoundsException("Buffer Length is " + buf.length + " but code is tried to read " + length + " from offset " + offset + " to " + offsetEnd);
        }
        if (offset < 0 || length < 0) {
            throw new IndexOutOfBoundsException("Offset and Length must both be >= 0, negative indicies are not permitted - code is tried to read " + length + " from offset " + offset);
        }
        this._buf = Arrays.copyOfRange(buf, offset, offsetEnd);
    }

    byte[] getBuf() {
        return this._buf;
    }
}

