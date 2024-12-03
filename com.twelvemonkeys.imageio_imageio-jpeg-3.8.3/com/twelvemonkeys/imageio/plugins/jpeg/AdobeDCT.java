/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import java.io.DataInput;
import java.io.IOException;

final class AdobeDCT
extends Application {
    static final int Unknown = 0;
    static final int YCC = 1;
    static final int YCCK = 2;
    final int version;
    final int flags0;
    final int flags1;
    final int transform;

    private AdobeDCT(int n, int n2, int n3, int n4) {
        super(65518, "Adobe", new byte[]{65, 100, 111, 98, 101, 0, (byte)n, (byte)(n2 >> 8), (byte)(n2 & 0xFF), (byte)(n3 >> 8), (byte)(n3 & 0xFF), (byte)n4});
        this.version = n;
        this.flags0 = n2;
        this.flags1 = n3;
        this.transform = n4;
    }

    @Override
    public String toString() {
        return String.format("AdobeDCT[ver: %d.%02d, flags: %s %s, transform: %d]", this.version / 100, this.version % 100, Integer.toBinaryString(this.flags0), Integer.toBinaryString(this.flags1), this.transform);
    }

    public static AdobeDCT read(DataInput dataInput, int n) throws IOException {
        dataInput.skipBytes(6);
        return new AdobeDCT(dataInput.readUnsignedByte(), dataInput.readUnsignedShort(), dataInput.readUnsignedShort(), dataInput.readUnsignedByte());
    }
}

