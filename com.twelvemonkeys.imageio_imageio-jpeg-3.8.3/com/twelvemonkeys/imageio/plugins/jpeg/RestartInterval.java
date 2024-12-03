/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.io.DataInput;
import java.io.IOException;
import javax.imageio.IIOException;

class RestartInterval
extends Segment {
    final int interval;

    private RestartInterval(int n) {
        super(65501);
        this.interval = n;
    }

    public String toString() {
        return "DRI[" + this.interval + "]";
    }

    public static RestartInterval read(DataInput dataInput, int n) throws IOException {
        if (n != 4) {
            throw new IIOException("Unexpected length of DRI segment: " + n);
        }
        return new RestartInterval(dataInput.readUnsignedShort());
    }
}

