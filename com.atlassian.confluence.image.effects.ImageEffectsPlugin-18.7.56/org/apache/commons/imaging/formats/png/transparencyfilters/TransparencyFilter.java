/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.transparencyfilters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFileParser;

public abstract class TransparencyFilter
extends BinaryFileParser {
    private final byte[] bytes;

    public TransparencyFilter(byte[] bytes) {
        this.bytes = (byte[])bytes.clone();
    }

    public abstract int filter(int var1, int var2) throws ImageReadException, IOException;

    public byte getByte(int offset) {
        return this.bytes[offset];
    }

    public int getLength() {
        return this.bytes.length;
    }
}

