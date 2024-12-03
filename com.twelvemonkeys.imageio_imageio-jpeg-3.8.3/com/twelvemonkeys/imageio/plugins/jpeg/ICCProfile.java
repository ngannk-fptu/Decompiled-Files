/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import java.io.DataInput;
import java.io.IOException;

final class ICCProfile
extends Application {
    private ICCProfile(byte[] byArray) {
        super(65506, "ICC_PROFILE", byArray);
    }

    @Override
    public String toString() {
        return "ICC_PROFILE[" + this.data[12] + "/" + this.data[13] + " length: " + this.data.length + "]";
    }

    public static ICCProfile read(DataInput dataInput, int n) throws IOException {
        byte[] byArray = new byte[n - 2];
        dataInput.readFully(byArray);
        return new ICCProfile(byArray);
    }
}

