/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.util;

public class PrimeCertaintyCalculator {
    private PrimeCertaintyCalculator() {
    }

    public static int getDefaultCertainty(int keySizeInBits) {
        return keySizeInBits <= 1024 ? 80 : 96 + 16 * ((keySizeInBits - 1) / 1024);
    }
}

