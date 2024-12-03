/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.generic;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class GenericRegionFlags
extends Flags {
    public static String MMR = "MMR";
    public static String GB_TEMPLATE = "GB_TEMPLATE";
    public static String TPGDON = "TPGDON";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(MMR, new Integer(n & 1));
        this.flags.put(GB_TEMPLATE, new Integer(n >> 1 & 3));
        this.flags.put(TPGDON, new Integer(n >> 3 & 1));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

