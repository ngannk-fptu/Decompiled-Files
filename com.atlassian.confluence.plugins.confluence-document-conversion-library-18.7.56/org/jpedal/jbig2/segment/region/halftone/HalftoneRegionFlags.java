/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.halftone;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class HalftoneRegionFlags
extends Flags {
    public static String H_MMR = "H_MMR";
    public static String H_TEMPLATE = "H_TEMPLATE";
    public static String H_ENABLE_SKIP = "H_ENABLE_SKIP";
    public static String H_COMB_OP = "H_COMB_OP";
    public static String H_DEF_PIXEL = "H_DEF_PIXEL";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(H_MMR, new Integer(n & 1));
        this.flags.put(H_TEMPLATE, new Integer(n >> 1 & 3));
        this.flags.put(H_ENABLE_SKIP, new Integer(n >> 3 & 1));
        this.flags.put(H_COMB_OP, new Integer(n >> 4 & 7));
        this.flags.put(H_DEF_PIXEL, new Integer(n >> 7 & 1));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

