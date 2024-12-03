/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.AdobeDCT;
import com.twelvemonkeys.imageio.plugins.jpeg.EXIF;
import com.twelvemonkeys.imageio.plugins.jpeg.ICCProfile;
import com.twelvemonkeys.imageio.plugins.jpeg.JFIF;
import com.twelvemonkeys.imageio.plugins.jpeg.JFXX;
import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

class Application
extends Segment {
    final String identifier;
    final byte[] data;

    Application(int n, String string, byte[] byArray) {
        super(n);
        this.identifier = string;
        this.data = byArray;
    }

    InputStream data() {
        int n = this.identifier.length() + 1;
        return new ByteArrayInputStream(this.data, n, this.data.length - n);
    }

    public String toString() {
        return "APP" + (this.marker & 0xF) + "/" + this.identifier + "[length: " + this.data.length + "]";
    }

    public static Application read(int n, String string, DataInput dataInput, int n2) throws IOException {
        switch (n) {
            case 65504: {
                if ("JFIF".equals(string)) {
                    return JFIF.read(dataInput, n2);
                }
            }
            case 65505: {
                if ("JFXX".equals(string)) {
                    return JFXX.read(dataInput, n2);
                }
                if ("Exif".equals(string)) {
                    return EXIF.read(dataInput, n2);
                }
            }
            case 65506: {
                if ("ICC_PROFILE".equals(string)) {
                    return ICCProfile.read(dataInput, n2);
                }
            }
            case 65518: {
                if (!"Adobe".equals(string)) break;
                return AdobeDCT.read(dataInput, n2);
            }
        }
        byte[] byArray = new byte[Math.max(0, n2 - 2)];
        dataInput.readFully(byArray);
        return new Application(n, string, byArray);
    }
}

