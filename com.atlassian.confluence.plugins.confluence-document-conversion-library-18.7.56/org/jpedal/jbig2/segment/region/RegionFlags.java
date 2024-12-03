/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.region;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class RegionFlags
extends Flags {
    public static String EXTERNAL_COMBINATION_OPERATOR = "EXTERNAL_COMBINATION_OPERATOR";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(EXTERNAL_COMBINATION_OPERATOR, new Integer(n & 7));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

