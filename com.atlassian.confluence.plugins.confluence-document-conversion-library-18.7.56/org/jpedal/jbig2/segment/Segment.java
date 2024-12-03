/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.ArithmeticDecoder;
import org.jpedal.jbig2.decoders.HuffmanDecoder;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.SegmentHeader;

public abstract class Segment {
    public static final int SYMBOL_DICTIONARY = 0;
    public static final int INTERMEDIATE_TEXT_REGION = 4;
    public static final int IMMEDIATE_TEXT_REGION = 6;
    public static final int IMMEDIATE_LOSSLESS_TEXT_REGION = 7;
    public static final int PATTERN_DICTIONARY = 16;
    public static final int INTERMEDIATE_HALFTONE_REGION = 20;
    public static final int IMMEDIATE_HALFTONE_REGION = 22;
    public static final int IMMEDIATE_LOSSLESS_HALFTONE_REGION = 23;
    public static final int INTERMEDIATE_GENERIC_REGION = 36;
    public static final int IMMEDIATE_GENERIC_REGION = 38;
    public static final int IMMEDIATE_LOSSLESS_GENERIC_REGION = 39;
    public static final int INTERMEDIATE_GENERIC_REFINEMENT_REGION = 40;
    public static final int IMMEDIATE_GENERIC_REFINEMENT_REGION = 42;
    public static final int IMMEDIATE_LOSSLESS_GENERIC_REFINEMENT_REGION = 43;
    public static final int PAGE_INFORMATION = 48;
    public static final int END_OF_PAGE = 49;
    public static final int END_OF_STRIPE = 50;
    public static final int END_OF_FILE = 51;
    public static final int PROFILES = 52;
    public static final int TABLES = 53;
    public static final int EXTENSION = 62;
    public static final int BITMAP = 70;
    protected SegmentHeader segmentHeader;
    protected HuffmanDecoder huffDecoder;
    protected ArithmeticDecoder arithmeticDecoder;
    protected JBIG2StreamDecoder decoder;

    public Segment(JBIG2StreamDecoder jBIG2StreamDecoder) {
        this.decoder = jBIG2StreamDecoder;
        try {
            this.huffDecoder = HuffmanDecoder.getInstance();
            this.arithmeticDecoder = ArithmeticDecoder.getInstance();
        }
        catch (JBIG2Exception jBIG2Exception) {
            jBIG2Exception.printStackTrace();
        }
    }

    protected short readATValue() throws IOException {
        short s = this.decoder.readByte();
        short s2 = s;
        if ((s2 & 0x80) != 0) {
            s = (short)(s | 0xFFFFFF00);
        }
        return s;
    }

    public SegmentHeader getSegmentHeader() {
        return this.segmentHeader;
    }

    public void setSegmentHeader(SegmentHeader segmentHeader) {
        this.segmentHeader = segmentHeader;
    }

    public abstract void readSegment() throws IOException, JBIG2Exception;
}

