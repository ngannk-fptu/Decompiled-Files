/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.fontbox.ttf.GlyphDescription;
import org.apache.fontbox.ttf.TTFDataStream;

public abstract class GlyfDescript
implements GlyphDescription {
    public static final byte ON_CURVE = 1;
    public static final byte X_SHORT_VECTOR = 2;
    public static final byte Y_SHORT_VECTOR = 4;
    public static final byte REPEAT = 8;
    public static final byte X_DUAL = 16;
    public static final byte Y_DUAL = 32;
    private int[] instructions;
    private final int contourCount;

    GlyfDescript(short numberOfContours, TTFDataStream bais) throws IOException {
        this.contourCount = numberOfContours;
    }

    @Override
    public void resolve() {
    }

    @Override
    public int getContourCount() {
        return this.contourCount;
    }

    public int[] getInstructions() {
        return this.instructions;
    }

    void readInstructions(TTFDataStream bais, int count) throws IOException {
        this.instructions = bais.readUnsignedByteArray(count);
    }
}

