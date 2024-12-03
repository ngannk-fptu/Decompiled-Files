/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

final class JFXX
extends Application {
    public static final int JPEG = 16;
    public static final int INDEXED = 17;
    public static final int RGB = 19;
    final int extensionCode;
    final byte[] thumbnail;

    JFXX(int n, byte[] byArray) {
        super(65504, "JFXX", new byte[1 + (byArray != null ? byArray.length : 0)]);
        this.extensionCode = n;
        this.thumbnail = byArray;
    }

    @Override
    public String toString() {
        return String.format("APP0/JFXX extension (%s thumb size: %d)", this.extensionAsString(), this.thumbnail.length);
    }

    private String extensionAsString() {
        switch (this.extensionCode) {
            case 16: {
                return "JPEG";
            }
            case 17: {
                return "Indexed";
            }
            case 19: {
                return "RGB";
            }
        }
        return String.valueOf(this.extensionCode);
    }

    public static JFXX read(DataInput dataInput, int n) throws IOException {
        dataInput.readFully(new byte[5]);
        byte[] byArray = new byte[n - 2 - 5];
        dataInput.readFully(byArray);
        return new JFXX(byArray[0] & 0xFF, byArray.length - 1 > 0 ? Arrays.copyOfRange(byArray, 1, byArray.length - 1) : null);
    }
}

