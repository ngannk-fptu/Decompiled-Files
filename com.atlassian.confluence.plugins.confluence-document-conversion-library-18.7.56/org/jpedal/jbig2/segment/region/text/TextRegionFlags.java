/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.text;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class TextRegionFlags
extends Flags {
    public static String SB_HUFF = "SB_HUFF";
    public static String SB_REFINE = "SB_REFINE";
    public static String LOG_SB_STRIPES = "LOG_SB_STRIPES";
    public static String REF_CORNER = "REF_CORNER";
    public static String TRANSPOSED = "TRANSPOSED";
    public static String SB_COMB_OP = "SB_COMB_OP";
    public static String SB_DEF_PIXEL = "SB_DEF_PIXEL";
    public static String SB_DS_OFFSET = "SB_DS_OFFSET";
    public static String SB_R_TEMPLATE = "SB_R_TEMPLATE";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(SB_HUFF, new Integer(n & 1));
        this.flags.put(SB_REFINE, new Integer(n >> 1 & 1));
        this.flags.put(LOG_SB_STRIPES, new Integer(n >> 2 & 3));
        this.flags.put(REF_CORNER, new Integer(n >> 4 & 3));
        this.flags.put(TRANSPOSED, new Integer(n >> 6 & 1));
        this.flags.put(SB_COMB_OP, new Integer(n >> 7 & 3));
        this.flags.put(SB_DEF_PIXEL, new Integer(n >> 9 & 1));
        int n2 = n >> 10 & 0x1F;
        if ((n2 & 0x10) != 0) {
            n2 |= 0xFFFFFFF0;
        }
        this.flags.put(SB_DS_OFFSET, new Integer(n2));
        this.flags.put(SB_R_TEMPLATE, new Integer(n >> 15 & 1));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

