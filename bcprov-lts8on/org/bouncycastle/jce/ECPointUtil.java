/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce;

import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.math.ec.ECCurve;

public class ECPointUtil {
    public static ECPoint decodePoint(EllipticCurve curve, byte[] encoded) {
        int[] k;
        ECCurve c = null;
        c = curve.getField() instanceof ECFieldFp ? new ECCurve.Fp(((ECFieldFp)curve.getField()).getP(), curve.getA(), curve.getB(), null, null) : ((k = ((ECFieldF2m)curve.getField()).getMidTermsOfReductionPolynomial()).length == 3 ? new ECCurve.F2m(((ECFieldF2m)curve.getField()).getM(), k[2], k[1], k[0], curve.getA(), curve.getB(), null, null) : new ECCurve.F2m(((ECFieldF2m)curve.getField()).getM(), k[0], curve.getA(), curve.getB(), null, null));
        return EC5Util.convertPoint(c.decodePoint(encoded));
    }
}

