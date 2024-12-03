/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.pattern;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.image.JBIG2Bitmap;
import org.jpedal.jbig2.segment.Segment;
import org.jpedal.jbig2.segment.pattern.PatternDictionaryFlags;
import org.jpedal.jbig2.util.BinaryOperation;

public class PatternDictionarySegment
extends Segment {
    PatternDictionaryFlags patternDictionaryFlags = new PatternDictionaryFlags();
    private int width;
    private int height;
    private int grayMax;
    private JBIG2Bitmap[] bitmaps;
    private int size;

    public PatternDictionarySegment(JBIG2StreamDecoder jBIG2StreamDecoder) {
        super(jBIG2StreamDecoder);
    }

    public void readSegment() throws IOException, JBIG2Exception {
        this.readPatternDictionaryFlags();
        this.width = this.decoder.readByte();
        this.height = this.decoder.readByte();
        if (JBIG2StreamDecoder.debug) {
            System.out.println("pattern dictionary size = " + this.width + " , " + this.height);
        }
        short[] sArray = new short[4];
        this.decoder.readByte(sArray);
        this.grayMax = BinaryOperation.getInt32(sArray);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("grey max = " + this.grayMax);
        }
        boolean bl = this.patternDictionaryFlags.getFlagValue(PatternDictionaryFlags.HD_MMR) == 1;
        int n = this.patternDictionaryFlags.getFlagValue(PatternDictionaryFlags.HD_TEMPLATE);
        if (!bl) {
            this.arithmeticDecoder.resetGenericStats(n, null);
            this.arithmeticDecoder.start();
        }
        short[] sArray2 = new short[4];
        short[] sArray3 = new short[4];
        sArray2[0] = (short)(-this.width);
        sArray3[0] = 0;
        sArray2[1] = -3;
        sArray3[1] = -1;
        sArray2[2] = 2;
        sArray3[2] = -2;
        sArray2[3] = -2;
        sArray3[3] = -2;
        this.size = this.grayMax + 1;
        JBIG2Bitmap jBIG2Bitmap = new JBIG2Bitmap(this.size * this.width, this.height);
        jBIG2Bitmap.clear(0);
        jBIG2Bitmap.readBitmap(bl, n, false, false, null, sArray2, sArray3, this.segmentHeader.getSegmentDataLength() - 7);
        JBIG2Bitmap[] jBIG2BitmapArray = new JBIG2Bitmap[this.size];
        int n2 = 0;
        for (int i = 0; i < this.size; ++i) {
            jBIG2BitmapArray[i] = jBIG2Bitmap.getSlice(n2, 0, this.width, this.height);
            n2 += this.width;
        }
        this.setBitmaps(jBIG2BitmapArray);
    }

    private void setBitmaps(JBIG2Bitmap[] jBIG2BitmapArray) {
        this.bitmaps = jBIG2BitmapArray;
    }

    public JBIG2Bitmap[] getBitmaps() {
        return this.bitmaps;
    }

    private void readPatternDictionaryFlags() throws IOException {
        short s = this.decoder.readByte();
        this.patternDictionaryFlags.setFlags(s);
        if (JBIG2StreamDecoder.debug) {
            System.out.println("pattern Dictionary flags = " + s);
        }
    }

    public PatternDictionaryFlags getPatternDictionaryFlags() {
        return this.patternDictionaryFlags;
    }

    public int getSize() {
        return this.size;
    }
}

