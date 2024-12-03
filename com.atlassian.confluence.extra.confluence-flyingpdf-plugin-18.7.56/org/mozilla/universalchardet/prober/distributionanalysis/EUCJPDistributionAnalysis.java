/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober.distributionanalysis;

import org.mozilla.universalchardet.prober.distributionanalysis.JISDistributionAnalysis;

public class EUCJPDistributionAnalysis
extends JISDistributionAnalysis {
    public static final int HIGHBYTE_BEGIN = 161;
    public static final int HIGHBYTE_END = 254;
    public static final int LOWBYTE_BEGIN = 161;
    public static final int LOWBYTE_END = 254;

    @Override
    protected int getOrder(byte[] buf, int offset) {
        int highbyte = buf[offset] & 0xFF;
        if (highbyte >= 161) {
            int lowbyte = buf[offset + 1] & 0xFF;
            return 94 * (highbyte - 161) + lowbyte - 161;
        }
        return -1;
    }
}

