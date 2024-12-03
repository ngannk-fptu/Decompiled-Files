/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

final class JFIF
extends Application {
    final int majorVersion;
    final int minorVersion;
    final int units;
    final int xDensity;
    final int yDensity;
    final int xThumbnail;
    final int yThumbnail;
    final byte[] thumbnail;

    JFIF(int n, int n2, int n3, int n4, int n5, int n6, int n7, byte[] byArray) {
        super(65504, "JFIF", new byte[14 + (byArray != null ? byArray.length : 0)]);
        this.majorVersion = n;
        this.minorVersion = n2;
        this.units = n3;
        this.xDensity = n4;
        this.yDensity = n5;
        this.xThumbnail = n6;
        this.yThumbnail = n7;
        this.thumbnail = byArray;
    }

    @Override
    public String toString() {
        return String.format("APP0/JFIF v%d.%02d %dx%d %s (%s)", this.majorVersion, this.minorVersion, this.xDensity, this.yDensity, this.unitsAsString(), this.thumbnailToString());
    }

    private String unitsAsString() {
        switch (this.units) {
            case 0: {
                return "(aspect only)";
            }
            case 1: {
                return "dpi";
            }
            case 2: {
                return "dpcm";
            }
        }
        return "(unknown unit)";
    }

    private String thumbnailToString() {
        if (this.xThumbnail == 0 || this.yThumbnail == 0) {
            return "no thumbnail";
        }
        return String.format("thumbnail: %dx%d", this.xThumbnail, this.yThumbnail);
    }

    public static JFIF read(DataInput dataInput, int n) throws IOException {
        if (n < 16) {
            throw new EOFException();
        }
        dataInput.readFully(new byte[5]);
        byte[] byArray = new byte[n - 2 - 5];
        dataInput.readFully(byArray);
        ByteBuffer byteBuffer = ByteBuffer.wrap(byArray);
        int n2 = byteBuffer.get() & 0xFF;
        int n3 = byteBuffer.get() & 0xFF;
        return new JFIF(byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF, byteBuffer.getShort() & 0xFFFF, byteBuffer.getShort() & 0xFFFF, n2, n3, JFIF.getBytes(byteBuffer, Math.min(byteBuffer.remaining(), n2 * n3 * 3)));
    }

    private static byte[] getBytes(ByteBuffer byteBuffer, int n) {
        if (n == 0) {
            return null;
        }
        byte[] byArray = new byte[n];
        byteBuffer.get(byArray);
        return byArray;
    }
}

