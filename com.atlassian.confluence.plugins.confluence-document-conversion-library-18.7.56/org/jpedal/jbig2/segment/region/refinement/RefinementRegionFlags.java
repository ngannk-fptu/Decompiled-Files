/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region.refinement;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class RefinementRegionFlags
extends Flags {
    public static String GR_TEMPLATE = "GR_TEMPLATE";
    public static String TPGDON = "TPGDON";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(GR_TEMPLATE, new Integer(n & 1));
        this.flags.put(TPGDON, new Integer(n >> 1 & 1));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

