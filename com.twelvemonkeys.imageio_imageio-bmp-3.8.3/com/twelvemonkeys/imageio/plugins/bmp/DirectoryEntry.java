/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.imageio.IIOException;

abstract class DirectoryEntry {
    int width;
    int height;
    int colorCount;
    int planes;
    int bitCount;
    int size;
    int offset;

    DirectoryEntry() {
    }

    public static DirectoryEntry read(int n, DataInput dataInput) throws IOException {
        DirectoryEntry directoryEntry = DirectoryEntry.createEntry(n);
        directoryEntry.read(dataInput);
        return directoryEntry;
    }

    private static DirectoryEntry createEntry(int n) throws IIOException {
        switch (n) {
            case 1: {
                return new ICOEntry();
            }
            case 2: {
                return new CUREntry();
            }
        }
        throw new IIOException(String.format("Unknown DIB type: %s, expected: %s (ICO) or %s (CUR)", n, 1, 2));
    }

    protected void read(DataInput dataInput) throws IOException {
        int n = dataInput.readUnsignedByte();
        this.width = n == 0 ? 256 : n;
        int n2 = dataInput.readUnsignedByte();
        this.height = n2 == 0 ? 256 : n2;
        this.colorCount = dataInput.readUnsignedByte();
        dataInput.readUnsignedByte();
        this.planes = dataInput.readUnsignedShort();
        this.bitCount = dataInput.readUnsignedShort();
        this.size = dataInput.readInt();
        this.offset = dataInput.readInt();
    }

    void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(this.width % 256);
        dataOutput.writeByte(this.height % 256);
        dataOutput.writeByte(this.colorCount);
        dataOutput.writeByte(0);
        dataOutput.writeShort(1);
        dataOutput.writeShort(this.bitCount);
        dataOutput.writeInt(this.size);
        dataOutput.writeInt(this.offset);
    }

    public String toString() {
        return String.format("%s: width: %d, height: %d, colors: %d, planes: %d, bit count: %d, size: %d, offset: %d", this.getClass().getSimpleName(), this.width, this.height, this.colorCount, this.planes, this.bitCount, this.size, this.offset);
    }

    public int getBitCount() {
        return this.bitCount;
    }

    public int getColorCount() {
        return this.colorCount;
    }

    public int getHeight() {
        return this.height;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getPlanes() {
        return this.planes;
    }

    public int getSize() {
        return this.size;
    }

    public int getWidth() {
        return this.width;
    }

    static final class ICOEntry
    extends DirectoryEntry {
        private ICOEntry() {
        }

        ICOEntry(int n, int n2, ColorModel colorModel, int n3, int n4) {
            this.width = n;
            this.height = n2;
            this.colorCount = colorModel instanceof IndexColorModel ? ((IndexColorModel)colorModel).getMapSize() : 0;
            this.planes = 1;
            this.bitCount = colorModel.getPixelSize();
            this.size = n3;
            this.offset = n4;
        }
    }

    static class CUREntry
    extends DirectoryEntry {
        private int xHotspot;
        private int yHotspot;

        CUREntry() {
        }

        @Override
        protected void read(DataInput dataInput) throws IOException {
            super.read(dataInput);
            this.xHotspot = this.planes;
            this.yHotspot = this.bitCount;
            this.planes = 1;
            this.bitCount = 0;
        }

        public Point getHotspot() {
            return new Point(this.xHotspot, this.yHotspot);
        }
    }
}

