/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

final class EXIF
extends Application {
    EXIF(byte[] byArray) {
        super(65505, "Exif", byArray);
    }

    @Override
    public String toString() {
        return String.format("APP1/Exif, length: %d", this.data.length);
    }

    ImageInputStream exifData() {
        int n = this.identifier.length() + 2;
        return new ByteArrayImageInputStream(this.data, n, this.data.length - n);
    }

    public static EXIF read(DataInput dataInput, int n) throws IOException {
        if (n < 8) {
            throw new EOFException();
        }
        byte[] byArray = new byte[n - 2];
        dataInput.readFully(byArray);
        return new EXIF(byArray);
    }
}

