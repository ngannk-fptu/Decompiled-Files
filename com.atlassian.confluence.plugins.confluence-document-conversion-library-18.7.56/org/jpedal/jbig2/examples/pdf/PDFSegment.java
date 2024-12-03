/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.examples.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDFSegment {
    private ByteArrayOutputStream header = new ByteArrayOutputStream();
    private ByteArrayOutputStream data = new ByteArrayOutputStream();
    private int segmentDataLength;

    public void writeToHeader(short s) {
        this.header.write(s);
    }

    public void writeToHeader(short[] sArray) throws IOException {
        for (int i = 0; i < sArray.length; ++i) {
            this.header.write(sArray[i]);
        }
    }

    public void writeToData(short s) {
        this.data.write(s);
    }

    public ByteArrayOutputStream getHeader() {
        return this.header;
    }

    public ByteArrayOutputStream getData() {
        return this.data;
    }

    public void setDataLength(int n) {
        this.segmentDataLength = n;
    }

    public int getSegmentDataLength() {
        return this.segmentDataLength;
    }
}

