/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.text;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class TextRegionHuffmanFlags
extends Flags {
    public static String SB_HUFF_FS = "SB_HUFF_FS";
    public static String SB_HUFF_DS = "SB_HUFF_DS";
    public static String SB_HUFF_DT = "SB_HUFF_DT";
    public static String SB_HUFF_RDW = "SB_HUFF_RDW";
    public static String SB_HUFF_RDH = "SB_HUFF_RDH";
    public static String SB_HUFF_RDX = "SB_HUFF_RDX";
    public static String SB_HUFF_RDY = "SB_HUFF_RDY";
    public static String SB_HUFF_RSIZE = "SB_HUFF_RSIZE";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(SB_HUFF_FS, new Integer(n & 3));
        this.flags.put(SB_HUFF_DS, new Integer(n >> 2 & 3));
        this.flags.put(SB_HUFF_DT, new Integer(n >> 4 & 3));
        this.flags.put(SB_HUFF_RDW, new Integer(n >> 6 & 3));
        this.flags.put(SB_HUFF_RDH, new Integer(n >> 8 & 3));
        this.flags.put(SB_HUFF_RDX, new Integer(n >> 10 & 3));
        this.flags.put(SB_HUFF_RDY, new Integer(n >> 12 & 3));
        this.flags.put(SB_HUFF_RSIZE, new Integer(n >> 14 & 1));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

