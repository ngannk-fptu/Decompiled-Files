/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.io.DataInput;
import java.io.IOException;

final class Unknown
extends Segment {
    final byte[] data;

    private Unknown(int n, byte[] byArray) {
        super(n);
        this.data = byArray;
    }

    public String toString() {
        return String.format("Unknown[%04x, length: %d]", this.marker, this.data.length);
    }

    public static Segment read(int n, int n2, DataInput dataInput) throws IOException {
        byte[] byArray = new byte[n2 - 2];
        dataInput.readFully(byArray);
        return new Unknown(n, byArray);
    }
}

