/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

final class Scan
extends Segment {
    final int spectralSelStart;
    final int spectralSelEnd;
    final int approxHigh;
    final int approxLow;
    final Component[] components;

    Scan(Component[] componentArray, int n, int n2, int n3, int n4) {
        super(65498);
        this.components = componentArray;
        this.spectralSelStart = n;
        this.spectralSelEnd = n2;
        this.approxHigh = n3;
        this.approxLow = n4;
    }

    public String toString() {
        return String.format("SOS[spectralSelStart: %d, spectralSelEnd: %d, approxHigh: %d, approxLow: %d, components: %s]", this.spectralSelStart, this.spectralSelEnd, this.approxHigh, this.approxLow, Arrays.toString(this.components));
    }

    public static Scan read(ImageInputStream imageInputStream) throws IOException {
        int n = imageInputStream.readUnsignedShort();
        return Scan.read((DataInput)new SubImageInputStream(imageInputStream, (long)n), n);
    }

    public static Scan read(DataInput dataInput, int n) throws IOException {
        int n2;
        int n3;
        int n4;
        int n5 = dataInput.readUnsignedByte();
        int n6 = 6 + n5 * 2;
        if (n6 != n) {
            throw new IIOException(String.format("Unexpected SOS length: %d != %d", n, n6));
        }
        Component[] componentArray = new Component[n5];
        for (n4 = 0; n4 < n5; ++n4) {
            n3 = dataInput.readUnsignedByte();
            n2 = dataInput.readUnsignedByte();
            componentArray[n4] = new Component(n3, n2 & 0xF, n2 >> 4);
        }
        n4 = dataInput.readUnsignedByte();
        n3 = dataInput.readUnsignedByte();
        n2 = dataInput.readUnsignedByte();
        return new Scan(componentArray, n4, n3, n2 >> 4, n2 & 0xF);
    }

    public static final class Component {
        final int scanCompSel;
        final int acTabSel;
        final int dcTabSel;

        Component(int n, int n2, int n3) {
            this.scanCompSel = n;
            this.acTabSel = n2;
            this.dcTabSel = n3;
        }

        public String toString() {
            return String.format("scanCompSel: %d, acTabSel: %d, dcTabSel: %d", this.scanCompSel, this.acTabSel, this.dcTabSel);
        }
    }
}

