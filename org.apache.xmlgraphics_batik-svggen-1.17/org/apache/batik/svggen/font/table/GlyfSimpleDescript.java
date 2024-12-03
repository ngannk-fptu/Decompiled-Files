/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import org.apache.batik.svggen.font.table.GlyfDescript;
import org.apache.batik.svggen.font.table.GlyfTable;

public class GlyfSimpleDescript
extends GlyfDescript {
    private int[] endPtsOfContours;
    private byte[] flags;
    private short[] xCoordinates;
    private short[] yCoordinates;
    private int count;

    public GlyfSimpleDescript(GlyfTable parentTable, short numberOfContours, ByteArrayInputStream bais) {
        super(parentTable, (short)numberOfContours, bais);
        this.endPtsOfContours = new int[numberOfContours];
        for (int i = 0; i < numberOfContours; ++i) {
            this.endPtsOfContours[i] = bais.read() << 8 | bais.read();
        }
        this.count = this.endPtsOfContours[numberOfContours - 1] + 1;
        this.flags = new byte[this.count];
        this.xCoordinates = new short[this.count];
        this.yCoordinates = new short[this.count];
        int instructionCount = bais.read() << 8 | bais.read();
        this.readInstructions(bais, instructionCount);
        this.readFlags(this.count, bais);
        this.readCoords(this.count, bais);
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
        return this.count;
    }

    @Override
    public int getContourCount() {
        return this.getNumberOfContours();
    }

    private void readCoords(int count, ByteArrayInputStream bais) {
        int i;
        short x = 0;
        short y = 0;
        for (i = 0; i < count; ++i) {
            if ((this.flags[i] & 0x10) != 0) {
                if ((this.flags[i] & 2) != 0) {
                    x = (short)(x + (short)bais.read());
                }
            } else {
                x = (this.flags[i] & 2) != 0 ? (short)(x + (short)(-((short)bais.read()))) : (short)(x + (short)(bais.read() << 8 | bais.read()));
            }
            this.xCoordinates[i] = x;
        }
        for (i = 0; i < count; ++i) {
            if ((this.flags[i] & 0x20) != 0) {
                if ((this.flags[i] & 4) != 0) {
                    y = (short)(y + (short)bais.read());
                }
            } else {
                y = (this.flags[i] & 4) != 0 ? (short)(y + (short)(-((short)bais.read()))) : (short)(y + (short)(bais.read() << 8 | bais.read()));
            }
            this.yCoordinates[i] = y;
        }
    }

    private void readFlags(int flagCount, ByteArrayInputStream bais) {
        try {
            for (int index = 0; index < flagCount; ++index) {
                this.flags[index] = (byte)bais.read();
                if ((this.flags[index] & 8) == 0) continue;
                int repeats = bais.read();
                for (int i = 1; i <= repeats; ++i) {
                    this.flags[index + i] = this.flags[index];
                }
                index += repeats;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("error: array index out of bounds");
        }
    }
}

