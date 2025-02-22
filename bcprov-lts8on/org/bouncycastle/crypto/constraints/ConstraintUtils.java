/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.constraints;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class ConstraintUtils {
    public static int bitsOfSecurityFor(BigInteger p) {
        return ConstraintUtils.bitsOfSecurityForFF(p.bitLength());
    }

    public static int bitsOfSecurityFor(ECCurve curve) {
        int sBits = (curve.getFieldSize() + 1) / 2;
        return sBits > 256 ? 256 : sBits;
    }

    public static int bitsOfSecurityForFF(int strength) {
        if (strength >= 2048) {
            return strength >= 3072 ? (strength >= 7680 ? (strength >= 15360 ? 256 : 192) : 128) : 112;
        }
        return strength >= 1024 ? 80 : 20;
    }
}

