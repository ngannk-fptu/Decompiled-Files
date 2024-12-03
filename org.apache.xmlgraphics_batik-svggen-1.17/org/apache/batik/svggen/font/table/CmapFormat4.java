/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.CmapFormat;

public class CmapFormat4
extends CmapFormat {
    public int language;
    private int segCountX2;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;
    private int[] endCode;
    private int[] startCode;
    private int[] idDelta;
    private int[] idRangeOffset;
    private int[] glyphIdArray;
    private int segCount;
    private int first;
    private int last;

    protected CmapFormat4(RandomAccessFile raf) throws IOException {
        super(raf);
        int i;
        this.format = 4;
        this.segCountX2 = raf.readUnsignedShort();
        this.segCount = this.segCountX2 / 2;
        this.endCode = new int[this.segCount];
        this.startCode = new int[this.segCount];
        this.idDelta = new int[this.segCount];
        this.idRangeOffset = new int[this.segCount];
        this.searchRange = raf.readUnsignedShort();
        this.entrySelector = raf.readUnsignedShort();
        this.rangeShift = raf.readUnsignedShort();
        this.last = -1;
        for (i = 0; i < this.segCount; ++i) {
            this.endCode[i] = raf.readUnsignedShort();
            if (this.endCode[i] <= this.last) continue;
            this.last = this.endCode[i];
        }
        raf.readUnsignedShort();
        for (i = 0; i < this.segCount; ++i) {
            this.startCode[i] = raf.readUnsignedShort();
            if (i != 0 && this.startCode[i] >= this.first) continue;
            this.first = this.startCode[i];
        }
        for (i = 0; i < this.segCount; ++i) {
            this.idDelta[i] = raf.readUnsignedShort();
        }
        for (i = 0; i < this.segCount; ++i) {
            this.idRangeOffset[i] = raf.readUnsignedShort();
        }
        int count = (this.length - 16 - this.segCount * 8) / 2;
        this.glyphIdArray = new int[count];
        for (int i2 = 0; i2 < count; ++i2) {
            this.glyphIdArray[i2] = raf.readUnsignedShort();
        }
    }

    @Override
    public int getFirst() {
        return this.first;
    }

    @Override
    public int getLast() {
        return this.last;
    }

    @Override
    public int mapCharCode(int charCode) {
        try {
            if (charCode < 0 || charCode >= 65534) {
                return 0;
            }
            for (int i = 0; i < this.segCount; ++i) {
                if (this.endCode[i] < charCode) continue;
                if (this.startCode[i] <= charCode) {
                    if (this.idRangeOffset[i] > 0) {
                        return this.glyphIdArray[this.idRangeOffset[i] / 2 + (charCode - this.startCode[i]) - (this.segCount - i)];
                    }
                    return (this.idDelta[i] + charCode) % 65536;
                }
                break;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("error: Array out of bounds - " + e.getMessage());
        }
        return 0;
    }

    @Override
    public String toString() {
        return new StringBuffer(80).append(super.toString()).append(", segCountX2: ").append(this.segCountX2).append(", searchRange: ").append(this.searchRange).append(", entrySelector: ").append(this.entrySelector).append(", rangeShift: ").append(this.rangeShift).append(", endCode: ").append(CmapFormat4.intToStr(this.endCode)).append(", startCode: ").append(CmapFormat4.intToStr(this.startCode)).append(", idDelta: ").append(CmapFormat4.intToStr(this.idDelta)).append(", idRangeOffset: ").append(CmapFormat4.intToStr(this.idRangeOffset)).toString();
    }

    private static String intToStr(int[] array) {
        int nSlots = array.length;
        StringBuffer workBuff = new StringBuffer(nSlots * 8);
        workBuff.append('[');
        for (int i = 0; i < nSlots; ++i) {
            workBuff.append(array[i]);
            if (i >= nSlots - 1) continue;
            workBuff.append(',');
        }
        workBuff.append(']');
        return workBuff.toString();
    }
}

