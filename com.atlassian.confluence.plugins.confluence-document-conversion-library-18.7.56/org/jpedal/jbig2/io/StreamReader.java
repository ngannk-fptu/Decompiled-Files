/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.io;

import java.io.IOException;
import org.jpedal.jbig2.examples.pdf.PDFSegment;

public class StreamReader {
    private byte[] data;
    private int bitPointer = 7;
    private int bytePointer = 0;

    public StreamReader(byte[] byArray) {
        this.data = byArray;
    }

    public short readByte(PDFSegment pDFSegment) throws IOException {
        short s = (short)(this.data[this.bytePointer++] & 0xFF);
        if (pDFSegment != null) {
            pDFSegment.writeToHeader(s);
        }
        return s;
    }

    public void readByte(short[] sArray, PDFSegment pDFSegment) throws IOException {
        for (int i = 0; i < sArray.length; ++i) {
            sArray[i] = (short)(this.data[this.bytePointer++] & 0xFF);
        }
        if (pDFSegment != null) {
            pDFSegment.writeToHeader(sArray);
        }
    }

    public short readByte() throws IOException {
        short s = (short)(this.data[this.bytePointer++] & 0xFF);
        return s;
    }

    public void readByte(short[] sArray) throws IOException {
        for (int i = 0; i < sArray.length; ++i) {
            sArray[i] = (short)(this.data[this.bytePointer++] & 0xFF);
        }
    }

    public int readBit() throws IOException {
        short s = this.readByte();
        short s2 = (short)(1 << this.bitPointer);
        int n = (s & s2) >> this.bitPointer;
        --this.bitPointer;
        if (this.bitPointer == -1) {
            this.bitPointer = 7;
        } else {
            this.movePointer(-1);
        }
        return n;
    }

    public int readBits(int n) throws IOException {
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            n2 = n2 << 1 | this.readBit();
        }
        return n2;
    }

    public void movePointer(int n) {
        this.bytePointer += n;
    }

    public int readLong() throws IOException {
        short[] sArray = new short[4];
        this.readByte(sArray);
        return sArray[0] << 24 | sArray[1] << 16 | sArray[2] << 8 | sArray[3];
    }

    public int readWord() throws IOException {
        short[] sArray = new short[2];
        this.readByte(sArray);
        return sArray[0] << 8 | sArray[1];
    }

    public int getPointer() {
        return this.bitPointer;
    }

    public void consumeRemainingBits() throws IOException {
        if (this.bitPointer != 7) {
            this.readBits(this.bitPointer + 1);
        }
    }

    public boolean isFinished() throws IOException {
        return this.bytePointer == this.data.length;
    }
}

