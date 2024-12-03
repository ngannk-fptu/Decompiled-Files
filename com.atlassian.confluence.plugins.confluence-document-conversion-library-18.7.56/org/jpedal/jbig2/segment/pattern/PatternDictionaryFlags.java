/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.pattern;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class PatternDictionaryFlags
extends Flags {
    public static String HD_MMR = "HD_MMR";
    public static String HD_TEMPLATE = "HD_TEMPLATE";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(HD_MMR, new Integer(n & 1));
        this.flags.put(HD_TEMPLATE, new Integer(n >> 1 & 3));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

