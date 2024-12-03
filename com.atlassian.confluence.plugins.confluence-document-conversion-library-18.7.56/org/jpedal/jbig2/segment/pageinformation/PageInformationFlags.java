/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.pageinformation;

import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Flags;

public class PageInformationFlags
extends Flags {
    public static String DEFAULT_PIXEL_VALUE = "DEFAULT_PIXEL_VALUE";
    public static String DEFAULT_COMBINATION_OPERATOR = "DEFAULT_COMBINATION_OPERATOR";

    public void setFlags(int n) {
        this.flagsAsInt = n;
        this.flags.put(DEFAULT_PIXEL_VALUE, new Integer(n >> 2 & 1));
        this.flags.put(DEFAULT_COMBINATION_OPERATOR, new Integer(n >> 3 & 3));
        if (JBIG2StreamDecoder.debug) {
            System.out.println(this.flags);
        }
    }
}

