/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.GlyfDescript;
import org.apache.fontbox.ttf.TTFDataStream;

public class GlyfSimpleDescript
extends GlyfDescript {
    private static final Log LOG = LogFactory.getLog(GlyfSimpleDescript.class);
    private int[] endPtsOfContours;
    private byte[] flags;
    private short[] xCoordinates;
    private short[] yCoordinates;
    private final int pointCount;

    GlyfSimpleDescript() throws IOException {
        super((short)0, null);
        this.pointCount = 0;
    }

    GlyfSimpleDescript(short numberOfContours, TTFDataStream bais, short x0) throws IOException {
        super(numberOfContours, bais);
        if (numberOfContours == 0) {
            this.pointCount = 0;
            return;
        }
        this.endPtsOfContours = bais.readUnsignedShortArray(numberOfContours);
        int lastEndPt = this.endPtsOfContours[numberOfContours - 1];
        if (numberOfContours == 1 && lastEndPt == 65535) {
            this.pointCount = 0;
            return;
        }
        this.pointCount = lastEndPt + 1;
        this.flags = new byte[this.pointCount];
        this.xCoordinates = new short[this.pointCount];
        this.yCoordinates = new short[this.pointCount];
        int instructionCount = bais.readUnsignedShort();
        this.readInstructions(bais, instructionCount);
        this.readFlags(this.pointCount, bais);
        this.readCoords(this.pointCount, bais, x0);
    }

    @Override
    public int getEndPtOfContours(int i) {
        return this.endPtsOfContours[i];
    }

    @Override
    public byte getFlags(int i) {
        return this.flags[i];
    }

    @Override
    public short getXCoordinate(int i) {
        return this.xCoordinates[i];
    }

    @Override
    public short getYCoordinate(int i) {
        return this.yCoordinates[i];
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public int getPointCount() {
        return this.pointCount;
    }

    private void readCoords(int count, TTFDataStream bais, short x0) throws IOException {
        int i;
        short x = x0;
        short y = 0;
        for (i = 0; i < count; ++i) {
            if ((this.flags[i] & 0x10) != 0) {
                if ((this.flags[i] & 2) != 0) {
                    x = (short)(x + (short)bais.readUnsignedByte());
                }
            } else {
                x = (this.flags[i] & 2) != 0 ? (short)(x - (short)bais.readUnsignedByte()) : (short)(x + bais.readSignedShort());
            }
            this.xCoordinates[i] = x;
        }
        for (i = 0; i < count; ++i) {
            if ((this.flags[i] & 0x20) != 0) {
                if ((this.flags[i] & 4) != 0) {
                    y = (short)(y + (short)bais.readUnsignedByte());
                }
            } else {
                y = (this.flags[i] & 4) != 0 ? (short)(y - (short)bais.readUnsignedByte()) : (short)(y + bais.readSignedShort());
            }
            this.yCoordinates[i] = y;
        }
    }

    private void readFlags(int flagCount, TTFDataStream bais) throws IOException {
        for (int index = 0; index < flagCount; ++index) {
            this.flags[index] = (byte)bais.readUnsignedByte();
            if ((this.flags[index] & 8) == 0) continue;
            int repeats = bais.readUnsignedByte();
            for (int i = 1; i <= repeats; ++i) {
                if (index + i >= this.flags.length) {
                    LOG.error((Object)("repeat count (" + repeats + ") higher than remaining space"));
                    return;
                }
                this.flags[index + i] = this.flags[index];
            }
            index += repeats;
        }
    }
}

