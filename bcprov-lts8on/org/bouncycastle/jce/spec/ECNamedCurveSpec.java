/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECNamedCurveSpec
extends ECParameterSpec {
    private String name;

    private static EllipticCurve convertCurve(ECCurve curve, byte[] seed) {
        ECField field = ECNamedCurveSpec.convertField(curve.getField());
        BigInteger a = curve.getA().toBigInteger();
        BigInteger b = curve.getB().toBigInteger();
        return new EllipticCurve(field, a, b, seed);
    }

    private static ECField convertField(FiniteField field) {
        if (ECAlgorithms.isFpField(field)) {
            return new ECFieldFp(field.getCharacteristic());
        }
        Polynomial poly = ((PolynomialExtensionField)field).getMinimalPolynomial();
        int[] exponents = poly.getExponentsPresent();
        int[] ks = Arrays.reverseInPlace(Arrays.copyOfRange(exponents, 1, exponents.length - 1));
        return new ECFieldF2m(poly.getDegree(), ks);
    }

    public ECNamedCurveSpec(String name, ECCurve curve, org.bouncycastle.math.ec.ECPoint g, BigInteger n) {
        super(ECNamedCurveSpec.convertCurve(curve, null), EC5Util.convertPoint(g), n, 1);
        this.name = name;
    }

    public ECNamedCurveSpec(String name, EllipticCurve curve, ECPoint g, BigInteger n) {
        super(curve, g, n, 1);
        this.name = name;
    }

    public ECNamedCurveSpec(String name, ECCurve curve, org.bouncycastle.math.ec.ECPoint g, BigInteger n, BigInteger h) {
        super(ECNamedCurveSpec.convertCurve(curve, null), EC5Util.convertPoint(g), n, h.intValue());
        this.name = name;
    }

    public ECNamedCurveSpec(String name, EllipticCurve curve, ECPoint g, BigInteger n, BigInteger h) {
        super(curve, g, n, h.intValue());
        this.name = name;
    }

    public ECNamedCurveSpec(String name, ECCurve curve, org.bouncycastle.math.ec.ECPoint g, BigInteger n, BigInteger h, byte[] seed) {
        super(ECNamedCurveSpec.convertCurve(curve, seed), EC5Util.convertPoint(g), n, h.intValue());
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

