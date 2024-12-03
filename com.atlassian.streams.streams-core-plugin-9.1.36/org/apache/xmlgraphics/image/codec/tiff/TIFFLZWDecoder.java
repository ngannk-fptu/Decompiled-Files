/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

import org.apache.xmlgraphics.image.codec.util.PropertyUtil;

public class TIFFLZWDecoder {
    byte[][] stringTable;
    byte[] data;
    byte[] uncompData;
    int tableIndex;
    int bitsToGet = 9;
    int bytePointer;
    int dstIndex;
    int w;
    int predictor;
    int samplesPerPixel;
    int nextData;
    int nextBits;
    int[] andTable = new int[]{511, 1023, 2047, 4095};

    public TIFFLZWDecoder(int w, int predictor, int samplesPerPixel) {
        this.w = w;
        this.predictor = predictor;
        this.samplesPerPixel = samplesPerPixel;
    }

    public byte[] decode(byte[] data, byte[] uncompData, int h) {
        int code;
        if (data[0] == 0 && data[1] == 1) {
            throw new UnsupportedOperationException(PropertyUtil.getString("TIFFLZWDecoder0"));
        }
        this.initializeStringTable();
        this.data = data;
        this.uncompData = uncompData;
        this.bytePointer = 0;
        this.dstIndex = 0;
        this.nextData = 0;
        this.nextBits = 0;
        int oldCode = 0;
        while ((code = this.getNextCode()) != 257 && this.dstIndex != uncompData.length) {
            byte[] string;
            if (code == 256) {
                this.initializeStringTable();
                code = this.getNextCode();
                if (code == 257) break;
                this.writeString(this.stringTable[code]);
                oldCode = code;
                continue;
            }
            if (code < this.tableIndex) {
                string = this.stringTable[code];
                this.writeString(string);
                this.addStringToTable(this.stringTable[oldCode], string[0]);
                oldCode = code;
                continue;
            }
            string = this.stringTable[oldCode];
            string = this.composeString(string, string[0]);
            this.writeString(string);
            this.addStringToTable(string);
            oldCode = code;
        }
        if (this.predictor == 2) {
            for (int j = 0; j < h; ++j) {
                int count = this.samplesPerPixel * (j * this.w + 1);
                for (int i = this.samplesPerPixel; i < this.w * this.samplesPerPixel; ++i) {
                    int n = count;
                    uncompData[n] = (byte)(uncompData[n] + uncompData[count - this.samplesPerPixel]);
                    ++count;
                }
            }
        }
        return uncompData;
    }

    public void initializeStringTable() {
        this.stringTable = new byte[4096][];
        for (int i = 0; i < 256; ++i) {
            this.stringTable[i] = new byte[1];
            this.stringTable[i][0] = (byte)i;
        }
        this.tableIndex = 258;
        this.bitsToGet = 9;
    }

    public void writeString(byte[] string) {
        for (byte aString : string) {
            this.uncompData[this.dstIndex++] = aString;
        }
    }

    public void addStringToTable(byte[] oldString, byte newString) {
        int length = oldString.length;
        byte[] string = new byte[length + 1];
        System.arraycopy(oldString, 0, string, 0, length);
        string[length] = newString;
        this.stringTable[this.tableIndex++] = string;
        if (this.tableIndex == 511) {
            this.bitsToGet = 10;
        } else if (this.tableIndex == 1023) {
            this.bitsToGet = 11;
        } else if (this.tableIndex == 2047) {
            this.bitsToGet = 12;
        }
    }

    public void addStringToTable(byte[] string) {
        this.stringTable[this.tableIndex++] = string;
        if (this.tableIndex == 511) {
            this.bitsToGet = 10;
        } else if (this.tableIndex == 1023) {
            this.bitsToGet = 11;
        } else if (this.tableIndex == 2047) {
            this.bitsToGet = 12;
        }
    }

    public byte[] composeString(byte[] oldString, byte newString) {
        int length = oldString.length;
        byte[] string = new byte[length + 1];
        System.arraycopy(oldString, 0, string, 0, length);
        string[length] = newString;
        return string;
    }

    public int getNextCode() {
        try {
            this.nextData = this.nextData << 8 | this.data[this.bytePointer++] & 0xFF;
            this.nextBits += 8;
            if (this.nextBits < this.bitsToGet) {
                this.nextData = this.nextData << 8 | this.data[this.bytePointer++] & 0xFF;
                this.nextBits += 8;
            }
            int code = this.nextData >> this.nextBits - this.bitsToGet & this.andTable[this.bitsToGet - 9];
            this.nextBits -= this.bitsToGet;
            return code;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return 257;
        }
    }
}

