/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x9.X9ECParameters
 *  org.bouncycastle.math.ec.ECAlgorithms
 *  org.bouncycastle.math.ec.ECCurve
 *  org.bouncycastle.math.ec.ECPoint
 *  org.bouncycastle.math.field.FiniteField
 *  org.bouncycastle.math.field.Polynomial
 *  org.bouncycastle.math.field.PolynomialExtensionField
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its.jcajce;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

class ECUtil {
    ECUtil() {
    }

    static ECPoint convertPoint(org.bouncycastle.math.ec.ECPoint point) {
        point = point.normalize();
        return new ECPoint(point.getAffineXCoord().toBigInteger(), point.getAffineYCoord().toBigInteger());
    }

    public static EllipticCurve convertCurve(ECCurve curve, byte[] seed) {
        ECField field = ECUtil.convertField(curve.getField());
        BigInteger a = curve.getA().toBigInteger();
        BigInteger b = curve.getB().toBigInteger();
        return new EllipticCurve(field, a, b, null);
    }

    public static ECParameterSpec convertToSpec(X9ECParameters domainParameters) {
        return new ECParameterSpec(ECUtil.convertCurve(domainParameters.getCurve(), null), ECUtil.convertPoint(domainParameters.getG()), domainParameters.getN(), domainParameters.getH().intValue());
    }

    public static ECField convertField(FiniteField field) {
        if (ECAlgorithms.isFpField((FiniteField)field)) {
            return new ECFieldFp(field.getCharacteristic());
        }
        Polynomial poly = ((PolynomialExtensionField)field).getMinimalPolynomial();
        int[] exponents = poly.getExponentsPresent();
        int[] ks = Arrays.reverseInPlace((int[])Arrays.copyOfRange((int[])exponents, (int)1, (int)(exponents.length - 1)));
        return new ECFieldF2m(poly.getDegree(), ks);
    }
}

