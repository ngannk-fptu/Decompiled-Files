/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECNamedCurveParameterSpec
extends ECParameterSpec {
    private String name;

    public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n) {
        super(curve, G, n);
        this.name = name;
    }

    public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h) {
        super(curve, G, n, h);
        this.name = name;
    }

    public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed) {
        super(curve, G, n, h, seed);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

